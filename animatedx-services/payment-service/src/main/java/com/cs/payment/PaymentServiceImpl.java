package com.cs.payment;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivity;
import com.cs.audit.PlayerActivityType;
import com.cs.audit.UserActivityType;
import com.cs.avatar.Level;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.BonusStatus;
import com.cs.bonus.PlayerBonus;
import com.cs.job.ScheduledJob;
import com.cs.payment.adyen.AdyenException;
import com.cs.payment.adyen.AdyenService;
import com.cs.payment.adyen.AdyenTransactionNotification;
import com.cs.payment.credit.CreditTransactionRepository;
import com.cs.payment.transaction.PaymentTransactionFacade;
import com.cs.persistence.CommunicationException;
import com.cs.persistence.Country;
import com.cs.persistence.NotFoundException;
import com.cs.player.BlockedPlayerException;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.player.PlayerUuid;
import com.cs.player.PlayerUuidRepository;
import com.cs.player.QWallet;
import com.cs.player.Wallet;
import com.cs.player.WalletRepository;
import com.cs.promotion.PlayerCriteria;
import com.cs.security.AccessDeniedException;
import com.cs.user.SecurityRole;
import com.cs.user.SecurityRole.Access;
import com.cs.user.User;
import com.cs.util.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.adyen.modification.ModificationResult;
import com.adyen.payout.ModifyResponse;
import com.adyen.payout.StoreDetailResponse;
import com.adyen.payout.SubmitResponse;
import com.adyen.recurring.BankAccount;
import com.adyen.recurring.Card;
import com.adyen.recurring.RecurringDetail;
import com.adyen.recurring.RecurringDetailsResult;
import com.google.common.collect.Iterables;
import com.mysema.query.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class PaymentServiceImpl implements PaymentService {

    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    // TODO in the case of error (return) add to queue, alert back-office (insert transaction in database? as un-served)
    private static final String STORE_BANK_ACCOUNT_INFO_SUCCESS_RESULT = "Success";
    private static final String PAYOUT_SUBMIT_ACKNOWLEDGEMENT = "[payout-submit-received]";
    private static final String PAYOUT_CONFIRM_ACKNOWLEDGEMENT = "[payout-confirm-received]";
    private static final String PAYOUT_DECLINE_ACKNOWLEDGEMENT = "[payout-decline-received]";
    private static final String TEMPORARY_PROVIDER_REFERENCE = "TEMPORARY_REFERENCE";

    @Value("#{new com.cs.payment.Money('${payment.minimum-balance-for-limited-player-to-withdraw}')}")
    private Money minimumBalanceForLimitedPlayerToWithdraw;

    @Value("${bonus.bonus-conversion-goal-multiplier}")
    private Double bonusConversionGoalMultiplier;

    private final AdyenService adyenService;
    private final AuditService auditService;
    private final BonusService bonusService;
    private final CreditTransactionRepository creditTransactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PlayerService playerService;
    private final PlayerUuidRepository playerUuidRepository;
    private final ProviderRepository providerRepository;
    private final PaymentTransactionFacade paymentTransactionFacade;
    private final WalletRepository walletRepository;

    @Autowired
    public PaymentServiceImpl(final AdyenService adyenService, final AuditService auditService, final BonusService bonusService,
                              final CreditTransactionRepository creditTransactionRepository, final PaymentMethodRepository paymentMethodRepository,
                              final PlayerService playerService, final PlayerUuidRepository playerUuidRepository, final ProviderRepository providerRepository,
                              final PaymentTransactionFacade paymentTransactionFacade, final WalletRepository walletRepository) {
        this.adyenService = adyenService;
        this.auditService = auditService;
        this.bonusService = bonusService;
        this.creditTransactionRepository = creditTransactionRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.playerService = playerService;
        this.playerUuidRepository = playerUuidRepository;
        this.providerRepository = providerRepository;
        this.paymentTransactionFacade = paymentTransactionFacade;
        this.walletRepository = walletRepository;
    }

    @Nullable
    @Override
    public PaymentTransaction processDeposit(final AdyenTransactionNotification transaction) {
        final PlayerUuid playerUuid = playerUuidRepository.findOne(transaction.getMerchantReference());
        if (playerUuid == null) {
            logger.error("Transaction {} with reference {} has invalid merchant reference (uuid) {}",
                         transaction.getEventCode(), transaction.getProviderReference(), transaction.getMerchantReference());
            alertBackOffice();
            return null;
        }

        // check uuid, if (usedDate != null) then a failed payment transaction must be available
        if (playerUuid.getUsedDate() != null) {
            final PaymentTransaction paymentTransaction = paymentTransactionFacade.getPayment(transaction.getProviderReference(), transaction.getEventCode(),
                                                                                              PaymentStatus.FAILURE);
            if (paymentTransaction == null) {
                logger.error("Transaction {} with reference {} has invalid merchant reference (uuid) {}",
                             transaction.getEventCode(), transaction.getProviderReference(), transaction.getMerchantReference());
                alertBackOffice();
                return null;
            }
        }

        final Player player = playerUuid.getPlayer();

        if (player.getCurrency() != transaction.getCurrency()) {
            logger.error("Transaction {} with reference {} has mismatched currency: {}",
                         transaction.getEventCode(), transaction.getProviderReference(), transaction.getCurrency());
            alertBackOffice();
            return null;
        }

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        final PaymentTransaction paymentTransaction = paymentTransactionFacade.insertPayment(player, transaction, provider);

        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            final Wallet wallet = player.getWallet();
            wallet.setMoneyBalance(wallet.getMoneyBalance().add(transaction.getAmount()));
            increaseAccumulatedDeposits(wallet, transaction.getAmount(), transaction.getEventCode());
            useDepositBonus(player, transaction, playerUuid);
            walletRepository.save(wallet);
            final String message = String.format("Deposit amount %f euros with method %s with provider reference %s",
                                                 transaction.getAmount().getEuroValueInBigDecimal(), transaction.getPaymentMethod(), transaction.getProviderReference());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.DEPOSIT_MONEY, message);
        }

        playerUuid.setUsedDate(new Date());
        playerUuidRepository.save(playerUuid);

        alertFrontEnd();

        return paymentTransaction;
    }

    private void useDepositBonus(final Player player, final AdyenTransactionNotification transaction, final PlayerUuid playerUuid) {
        final Long bonusId = playerUuid.getBonusId();
        if (bonusId == null) {
            logger.debug("Player {} did not get deposit bonus because of selected bonus id is null", player.getId());
            return;
        }

        final Bonus bonus = bonusService.getBonus(bonusId);
        if (bonus == null) {
            logger.warn("Could not find deposit bonus wit id {} to be used by Player {}", bonusId, player.getId());
            return;
        }

        final PlayerCriteria playerCriteria = new PlayerCriteria();
        playerCriteria.setAmount(transaction.getAmount());

        // if deposit bonus is level deposit bonus then grant it first
        if (bonus.isLevelDepositBonus(player)) {
            bonusService.grantBonusToPlayer(player, bonus);
        }

        bonusService.useBonus(player, bonus, playerCriteria);
    }

    private Date getDateByHoursBeforeNow(final Integer hours) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -hours);
        return calendar.getTime();
    }

    @Override
    public void processRefund(final AdyenTransactionNotification notification) {
        processRefundCancel(notification, PaymentStatus.REFUNDED);
    }

    @Override
    public void processCancel(final AdyenTransactionNotification notification) {
        processRefundCancel(notification, PaymentStatus.CANCELED);
    }

    private void processRefundCancel(final AdyenTransactionNotification notification, final PaymentStatus paymentStatus) {
        final PaymentTransaction originalDeposit = paymentTransactionFacade.getPayment(notification.getOriginalReference(), EventCode.AUTHORISATION,
                                                                                       PaymentStatus.SUCCESS);

        if (!isNotificationAgainstOriginalDepositValid(notification, originalDeposit)) {
            return;
        }

        final Player player = originalDeposit.getPlayer();
        final Wallet wallet = player.getWallet();

        final PaymentTransaction transaction = paymentTransactionFacade.getPayment(notification.getProviderReference());

        if (transaction == null) {
            logger.warn("Transaction {} with reference {} contains an un-known provider reference", notification.getEventCode(), notification.getProviderReference());

            final Provider provider = providerRepository.findOne(Provider.ADYEN);
            paymentTransactionFacade.insertPayment(player, notification.getProviderReference(), notification.getOriginalReference(), notification.getAmount(),
                                                   notification.getCurrency(), notification.getPaymentMethod(), notification.getPaymentStatus(),
                                                   provider, notification.getEventCode());

            if (notification.getPaymentStatus() == PaymentStatus.SUCCESS) {
                finalizeWithdrawFromMoneyBalance(wallet, notification.getAmount());
                originalDeposit.setPaymentStatus(paymentStatus);
                paymentTransactionFacade.savePayment(originalDeposit);
                decreaseAccumulatedDeposits(wallet, notification.getAmount(), notification.getEventCode());
            } else {
                rollbackWithdrawFromMoneyBalance(wallet, notification.getAmount());
            }
        } else {
            if (!isRefundCancelNotificationAgainstTransactionValid(notification, transaction)) {
                return;
            }

            transaction.setProcessDate(new Date());

            if (notification.getPaymentStatus() == PaymentStatus.SUCCESS) {
                finalizeWithdrawalFromReservedBalance(wallet, originalDeposit.getAmount());
                originalDeposit.setPaymentStatus(paymentStatus);
                paymentTransactionFacade.savePayment(originalDeposit);
                transaction.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentTransactionFacade.savePayment(transaction);
                decreaseAccumulatedDeposits(wallet, notification.getAmount(), notification.getEventCode());
                return;
            }

            // else paymentStatus.equals(FAILURE)
            rollbackWithdrawalFromReservedBalance(wallet, originalDeposit.getAmount());
            paymentTransactionFacade.savePayment(transaction);
        }
    }

    private boolean isNotificationAgainstOriginalDepositValid(final AdyenTransactionNotification transaction, final PaymentTransaction originalDeposit) {
        final EventCode eventCode = transaction.getEventCode();
        final boolean isCancellation = isCancellation(eventCode);

        if (originalDeposit == null) {
            logger.error("{} Transaction {} with reference {} is not permitted since original deposit is invalid.",
                         eventCode, eventCode, transaction.getProviderReference());
            alertBackOffice();
            return false;
        }

        final Money originalDepositAmount = originalDeposit.getAmount();
        final Money transactionAmount = transaction.getAmount();
        if (isCancellation && !originalDepositAmount.equals(transactionAmount)) {
            logger.error("{} Transaction {} with reference {} has mismatched amount: {} , {}", eventCode, eventCode, transaction.getProviderReference(),
                         transactionAmount, originalDepositAmount);
            alertBackOffice();
            return false;
        }

        if (isCancellation && originalDeposit.getCurrency() != transaction.getCurrency()) {
            logger.error("{} Transaction {} with reference {} has mismatched currency: {}",
                         eventCode, eventCode, transaction.getProviderReference(), transaction.getCurrency());
            alertBackOffice();
            return false;
        }

        return true;
    }

    private boolean isCancellation(final EventCode eventCode) {
        return eventCode != EventCode.CANCELLATION && eventCode != EventCode.CANCEL_OR_REFUND;
    }

    private boolean isRefundCancelNotificationAgainstTransactionValid(final AdyenTransactionNotification notification, final PaymentTransaction transaction) {
        final EventCode eventCode = transaction.getEventCode();
        final boolean isCancellation = isCancellation(eventCode);

        if (!notification.getOriginalReference().equals(transaction.getOriginalReference())) {
            logger.error("Transaction {} with reference {} contains invalid original reference {}",
                         notification.getEventCode(), notification.getProviderReference(), notification.getOriginalReference());
            alertBackOffice();
            return false;
        }

        // already checked through notification duplicate
        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            logger.error("Transaction {} with reference {} was already processed", notification.getEventCode(),
                         notification.getProviderReference());
            alertBackOffice();
            return false;
        }

        if (isCancellation && !transaction.getAmount().equals(notification.getAmount())) {
            logger.error("Transaction {} with reference {} has mismatched amount {}, {}", notification.getEventCode(),
                         notification.getProviderReference(), notification.getAmount(), transaction.getAmount());
            alertBackOffice();
            return false;
        }

        return true;
    }

    @Override
    public void processRefundReversed(final AdyenTransactionNotification notification) {
        final PaymentTransaction originalPaymentTransaction =
                paymentTransactionFacade.getSucceededOrRefundedPayment(notification.getOriginalReference());
        if (!isNotificationAgainstOriginalDepositValid(notification, originalPaymentTransaction)) {
            return;
        }

        final Player player = originalPaymentTransaction.getPlayer();
        final Wallet wallet = player.getWallet();

        if (notification.getEventCode() == EventCode.REFUND_FAILED && notification.getPaymentStatus() == PaymentStatus.FAILURE
            || notification.getEventCode() == EventCode.REFUNDED_REVERSED && notification.getPaymentStatus() == PaymentStatus.SUCCESS) {
            // won't be received
            logger.error("Transaction {} with reference {} contains invalid success field.",
                         notification.getEventCode(), notification.getProviderReference());
            return;
        }

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        paymentTransactionFacade.insertPayment(player, notification, provider);

        final PaymentTransaction refundTransaction = paymentTransactionFacade.getPayment(notification.getOriginalReference());

        if (refundTransaction == null) {
            if (originalPaymentTransaction.getPaymentStatus() == PaymentStatus.REFUNDED) {
                // back money to player
                rollbackWithdrawFromMoneyBalance(wallet, notification.getAmount());
                originalPaymentTransaction.setPaymentStatus(PaymentStatus.SUCCESS);
                increaseAccumulatedDeposits(wallet, notification.getAmount(), notification.getEventCode());
                paymentTransactionFacade.savePayment(originalPaymentTransaction);
            }
        } else if (originalPaymentTransaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            // release reserved money
            // refund message must be AWAITING (if is true then original must be REFUNDED)
            if (refundTransaction.getPaymentStatus() == PaymentStatus.AWAITING_PAYMENT) {
                rollbackWithdrawalFromReservedBalance(wallet, notification.getAmount());
                originalPaymentTransaction.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentTransactionFacade.savePayment(originalPaymentTransaction);
                refundTransaction.setPaymentStatus(PaymentStatus.FAILURE);
                paymentTransactionFacade.savePayment(refundTransaction);
            } else {
                logger.error("Transaction {} with reference {} corresponds to invalid refund message with status {}",
                             notification.getEventCode(), notification.getProviderReference(), refundTransaction.getPaymentStatus());
                alertBackOffice();
            }
        } else if (originalPaymentTransaction.getPaymentStatus() == PaymentStatus.REFUNDED) {
            // back money to player
            // refund message must be SUCCESS
            if (refundTransaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
                rollbackWithdrawFromMoneyBalance(wallet, notification.getAmount());
                originalPaymentTransaction.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentTransactionFacade.savePayment(originalPaymentTransaction);
                refundTransaction.setPaymentStatus(PaymentStatus.FAILURE);
                paymentTransactionFacade.savePayment(refundTransaction);
                increaseAccumulatedDeposits(wallet, notification.getAmount(), notification.getEventCode());
            } else {
                logger.error("Transaction {} with reference {} corresponds to invalid refund message with reference {}",
                             notification.getEventCode(), notification.getProviderReference(), refundTransaction.getProviderReference());
                alertBackOffice();
            }
        }
    }

    @Override
    public void processPaymentCancellation(final AdyenTransactionNotification transaction) {
        final PaymentTransaction originalPaymentTransaction = paymentTransactionFacade.getPayment(transaction.getOriginalReference(), EventCode.AUTHORISATION,
                                                                                                  PaymentStatus.SUCCESS);
        if (!isNotificationAgainstOriginalDepositValid(transaction, originalPaymentTransaction)) {
            return;
        }

        final Player player = originalPaymentTransaction.getPlayer();
        final Wallet wallet = player.getWallet();

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        paymentTransactionFacade.insertPayment(player, transaction, provider);

        // CAPTURE_FAILED, CANCEL_OR_REFUND
        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            finalizeWithdrawFromMoneyBalance(wallet, transaction.getAmount());
            decreaseAccumulatedDeposits(wallet, transaction.getAmount(), transaction.getEventCode());
            originalPaymentTransaction.setPaymentStatus(PaymentStatus.FAILURE);
            paymentTransactionFacade.savePayment(originalPaymentTransaction);
            logger.error("Unexpected transaction {} with reference {} was received and processed.", transaction.getEventCode(), transaction.getProviderReference());
        }
        alertBackOffice();
    }

    @Override
    public void processPaymentCapture(final AdyenTransactionNotification transaction) {
        final PaymentTransaction originalPaymentTransaction = paymentTransactionFacade.getPayment(transaction.getOriginalReference(), EventCode.AUTHORISATION,
                                                                                                  PaymentStatus.SUCCESS);
        if (!isNotificationAgainstOriginalDepositValid(transaction, originalPaymentTransaction)) {
            return;
        }

        final Player player = originalPaymentTransaction.getPlayer();

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        paymentTransactionFacade.insertPayment(player, transaction, provider);

        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            // payment already has been done, alert back-office
            logger.error("Unexpected transaction {} with reference {} was received.", transaction.getEventCode(), transaction.getProviderReference());
        }
        alertBackOffice();
    }

    @Override
    public void defendChargeBack(final AdyenTransactionNotification transaction) {
        final PaymentTransaction originalPaymentTransaction = paymentTransactionFacade.getPayment(transaction.getOriginalReference(), EventCode.AUTHORISATION,
                                                                                                  PaymentStatus.SUCCESS);
        if (!isNotificationAgainstOriginalDepositValid(transaction, originalPaymentTransaction)) {
            // alert back-office
            alertBackOffice();
            return;
        }
        final Player player = originalPaymentTransaction.getPlayer();
        final Wallet wallet = player.getWallet();

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        paymentTransactionFacade.insertPayment(player, transaction, provider);

        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            finalizeWithdrawFromMoneyBalance(wallet, transaction.getAmount());
            originalPaymentTransaction.setPaymentStatus(PaymentStatus.CHARGEBACKED);
            paymentTransactionFacade.savePayment(originalPaymentTransaction);
            // TODO
            // back-office must do decreaseAccumulatedDeposits(wallet, notification.getAmount(), notification.getEventCode())
            // depending on the result of defend
            alertBackOffice();
        }
    }

    @Override
    public void processChargeBack(final AdyenTransactionNotification transaction) {
        final PaymentTransaction originalPaymentTransaction = paymentTransactionFacade.getPayment(transaction.getOriginalReference(), EventCode.AUTHORISATION,
                                                                                                  PaymentStatus.SUCCESS);
        if (!isNotificationAgainstOriginalDepositValid(transaction, originalPaymentTransaction)) {
            // alert back-office
            alertBackOffice();
            return;
        }

        final Player player = originalPaymentTransaction.getPlayer();
        final Wallet wallet = player.getWallet();

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        paymentTransactionFacade.insertPayment(player, transaction, provider);

        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            finalizeWithdrawFromMoneyBalance(wallet, transaction.getAmount());
            originalPaymentTransaction.setPaymentStatus(PaymentStatus.CHARGEBACKED);
            paymentTransactionFacade.savePayment(originalPaymentTransaction);
            decreaseAccumulatedDeposits(wallet, transaction.getAmount(), transaction.getEventCode());
        }
    }

    @Override
    public void processChargeBackReversed(final AdyenTransactionNotification transaction) {
        final PaymentTransaction originalPaymentTransaction = paymentTransactionFacade.getPayment(transaction.getOriginalReference(), EventCode.AUTHORISATION,
                                                                                                  PaymentStatus.CHARGEBACKED);
        if (!isNotificationAgainstOriginalDepositValid(transaction, originalPaymentTransaction)) {
            return;
        }

        final Player player = originalPaymentTransaction.getPlayer();
        final Wallet wallet = player.getWallet();

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        paymentTransactionFacade.insertPayment(player, transaction, provider);

        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            rollbackWithdrawFromMoneyBalance(wallet, transaction.getAmount());
            originalPaymentTransaction.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentTransactionFacade.savePayment(originalPaymentTransaction);
            increaseAccumulatedDeposits(wallet, transaction.getAmount(), transaction.getEventCode());
        }
    }

    @Override
    public void processReports(final AdyenTransactionNotification transaction) {
        // TODO alert back-office for info
        alertBackOffice();
    }

    @Nullable
    @Override
    public PaymentTransaction processWithdraw(final AdyenTransactionNotification notification) {
        final PaymentTransaction transaction = paymentTransactionFacade.getPayment(notification.getTransactionId());
        if (!isWithdrawNotificationAgainstTransactionValid(notification, transaction)) {
            alertBackOffice();
            return null;
        }

        final Player player = transaction.getPlayer();
        final Wallet wallet = player.getWallet();

        // confirmed from Adyen BO
        if (transaction.getProviderReference().equals(TEMPORARY_PROVIDER_REFERENCE)) {
            transaction.setProviderReference(notification.getProviderReference());
        }
        transaction.setEventDate(notification.getEventDate());
        transaction.setProcessDate(new Date());

        final PaymentTransaction updatedTransaction;
        if (notification.getPaymentStatus() == PaymentStatus.SUCCESS) {
            finalizeWithdrawalFromReservedBalance(wallet, notification.getAmount());
            transaction.setPaymentStatus(PaymentStatus.SUCCESS);
            voidReservedBonusMoneyOnWithdrawal(player, transaction);
            final String message = String.format("Withdraw amount %f euros with method %s with provider reference %s and withdraw reference %s",
                                                 transaction.getAmount().getEuroValueInBigDecimal(), transaction.getPaymentMethod(),
                                                 transaction.getProviderReference(), transaction.getWithdrawReference());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.WITHDRAW_MONEY, message);
            increaseAccumulatedWithdrawal(wallet, notification.getAmount(), notification.getEventCode());
            updatedTransaction = paymentTransactionFacade.savePayment(transaction);
        } else {
            logger.info("Player {} un-successful withdraw releases all reserved bonus money of player-bonuses linked to withdraw transaction {}",
                        player.getId(), transaction.getId());
            releaseBonusMoneyOnAbortedWithdrawal(player, transaction);
            rollbackWithdrawalFromReservedBalance(wallet, notification.getAmount());
            transaction.setPaymentStatus(PaymentStatus.FAILURE);
            transaction.setReason(notification.getReason());
            updatedTransaction = paymentTransactionFacade.savePayment(transaction);
        }

        return updatedTransaction;
    }

    @Override
    public void processDeclinedWithdraw(final AdyenTransactionNotification notification) {
        final PaymentTransaction transaction = paymentTransactionFacade.getPayment(notification.getTransactionId());
        if (!isWithdrawNotificationAgainstTransactionValid(notification, transaction)) {
            alertBackOffice();
            return;
        }

        final Player player = transaction.getPlayer();
        final Wallet wallet = player.getWallet();

        // Declined from Adyen BO
        if (transaction.getProviderReference().equals(TEMPORARY_PROVIDER_REFERENCE)) {
            transaction.setProviderReference(notification.getProviderReference());
        }
        transaction.setEventDate(notification.getEventDate());
        transaction.setProcessDate(new Date());

        if (notification.getPaymentStatus() == PaymentStatus.SUCCESS) {
            rollbackWithdrawalFromReservedBalance(wallet, notification.getAmount());
            logger.info("Player {} DECLINED withdraw releases all reserved bonus money of player-bonuses linked to withdraw transaction {}",
                        player.getId(), transaction.getId());
            releaseBonusMoneyOnAbortedWithdrawal(player, transaction);
            transaction.setPaymentStatus(PaymentStatus.DECLINED);
            paymentTransactionFacade.savePayment(transaction);

            logger.info("Withdrawal with reference {} was declined, {} euros moved from reserved balance to money balance for Player {}",
                        transaction.getWithdrawReference(), transaction.getAmount().getEuroValueInBigDecimal(), player.getId());

            final String message = String.format("Withdraw amount %f euros with method %s with provider reference %s and original reference %s was declined",
                                                 transaction.getAmount().getEuroValueInBigDecimal(), transaction.getPaymentMethod(),
                                                 transaction.getProviderReference(), transaction.getOriginalReference());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.WITHDRAW_MONEY_DECLINED, message);
        } else {
            logger.error("{} Notification {} with success field {} was received", notification.getEventCode(),
                         notification.getProviderReference(), notification.getPaymentMethod());
        }
    }

    private boolean isWithdrawNotificationAgainstTransactionValid(final AdyenTransactionNotification notification, final PaymentTransaction transaction) {
        if (transaction == null) {
            logger.error("Withdraw with provider reference {} has invalid transaction id {}", notification.getProviderReference(), notification.getTransactionId());
            return false;
        }

        if (!notification.getOriginalReference().equals(transaction.getOriginalReference())) {
            logger.error("Withdraw with provider reference {} has invalid original reference {}", notification.getProviderReference(),
                         notification.getOriginalReference());
            return false;
        }

        // transaction.getProviderReference() may be equal to "Temporal Ref", if confirmed by adyen BO
        if (!transaction.getProviderReference().equals(TEMPORARY_PROVIDER_REFERENCE) && !transaction.getProviderReference().equals(notification.getProviderReference())) {
            logger.error("Withdraw with provider reference {} has invalid provider reference {}", notification.getProviderReference(),
                         notification.getProviderReference());
            return false;
        }

        if (transaction.getCurrency() != notification.getCurrency()) {
            logger.error("Withdraw with provider reference {} and original reference {} has has mismatched currency {}", notification.getProviderReference(),
                         notification.getOriginalReference(), notification.getCurrency());
            return false;
        }

        if (!transaction.getAmount().equals(notification.getAmount())) {
            logger.error("Withdraw with provider reference {} and original reference {} has has mismatched amount {}", notification.getProviderReference(),
                         notification.getOriginalReference(), notification.getAmount());
            return false;
        }

        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            logger.error("Withdraw with provider reference {}  with original reference {} already received", notification.getProviderReference(),
                         notification.getOriginalReference());
            return false;
        }

        return true;
    }

    private void finalizeWithdrawalFromReservedBalance(final Wallet wallet, final Money amount) {
        final Money reservedBalance = wallet.getReservedBalance();

        if (reservedBalance.isLessThan(amount)) {
            final String message = String.format("Player %d has inconsistent wallet; reserved balance %s, amount in withdrawal %s, money balance %s",
                                                 wallet.getPlayer().getId(), reservedBalance, amount, wallet.getMoneyBalance());
            logger.error(message);
            auditService.trackPlayerActivityWithDescription(wallet.getPlayer(), PlayerActivityType.WALLET_ERROR, message);
            alertBackOffice();

            // Due to inconsistencies in the system, the player has less in the reserved balance than the declined withdrawal
            wallet.setReservedBalance(Money.ZERO);
            wallet.setMoneyBalance(wallet.getMoneyBalance().add(reservedBalance).subtract(amount));
            if (wallet.getMoneyBalance().isNegative()) {
                logger.error("Player {} has inconsistent wallet; reserved balance {}, amount in withdrawal {}, money balance {}", wallet.getPlayer().getId(),
                             reservedBalance, amount, wallet.getMoneyBalance());
                alertBackOffice();
            }
        } else {
            wallet.setReservedBalance(reservedBalance.subtract(amount));
        }

        walletRepository.save(wallet);
    }

    private void finalizeWithdrawFromMoneyBalance(final Wallet wallet, final Money amount) {
        wallet.setMoneyBalance(wallet.getMoneyBalance().subtract(amount));
        if (wallet.getMoneyBalance().isNegative()) {
            logger.error("Player {} money balance has negative amount {}", wallet.getPlayer().getId(), amount);
            alertBackOffice();
        }
        walletRepository.save(wallet);
    }

    private void rollbackWithdrawalFromReservedBalance(final Wallet wallet, final Money amount) {
        final Money reservedBalance = wallet.getReservedBalance();

        if (reservedBalance.isLessThan(amount)) {
            final String message = String.format("Player %d has inconsistent wallet; reserved balance %s, amount in withdrawal %s, money balance %s",
                                                 wallet.getPlayer().getId(), reservedBalance, amount, wallet.getMoneyBalance());
            logger.error(message);
            auditService.trackPlayerActivityWithDescription(wallet.getPlayer(), PlayerActivityType.WALLET_ERROR, message);
            alertBackOffice();

            // Due to inconsistencies in the system, the player has less in the reserved balance than the declined withdrawal
            wallet.setReservedBalance(reservedBalance.subtract(amount));
            wallet.setMoneyBalance(wallet.getMoneyBalance().add(reservedBalance));
        } else {
            wallet.setReservedBalance(reservedBalance.subtract(amount));
            wallet.setMoneyBalance(wallet.getMoneyBalance().add(amount));
        }

        walletRepository.save(wallet);
    }

    private void rollbackWithdrawFromMoneyBalance(final Wallet wallet, final Money amount) {
        wallet.setMoneyBalance(wallet.getMoneyBalance().add(amount));
        walletRepository.save(wallet);
    }

    /**
     * A duplicate notification is one where the eventCode and providerReference fields are the same If a duplicate is received with the paymentStatus field set to true
     * it overrules the previous notification. In all other cases you do not need to act on duplicate notifications.
     */
    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public boolean isNotificationDuplicated(final String providerReference, final EventCode eventCode, final PaymentStatus paymentStatus) {
        final PaymentTransaction paymentTransaction = paymentTransactionFacade.getPayment(providerReference, eventCode, paymentStatus);
        return paymentTransaction != null && (paymentTransaction.getPaymentStatus() == PaymentStatus.SUCCESS || paymentTransaction.getPaymentStatus() == paymentStatus);
    }

    @Transactional(propagation = REQUIRED, noRollbackFor = {CommunicationException.class})
    @Override
    public void refundDeposit(final String originalReference, final Long playerId, final User user) {
        refundOrCancelDeposit(originalReference, playerId, user, EventCode.REFUND, Operation.REFUND);
    }

    @Transactional(propagation = REQUIRED, noRollbackFor = {CommunicationException.class})
    @Override
    public void cancelDeposit(final String originalReference, final Long playerId, final User user) {
        refundOrCancelDeposit(originalReference, playerId, user, EventCode.CANCELLATION, Operation.CANCEL);
    }

    private void refundOrCancelDeposit(final String originalReference, final Long playerId, final User user, final EventCode eventCode, final Operation operation) {
        final Player player = playerService.getPlayer(playerId);

        final PaymentTransaction originalDeposit = paymentTransactionFacade.getPayment(originalReference, EventCode.AUTHORISATION, PaymentStatus.SUCCESS);
        if (originalDeposit == null) {
            logger.error("{} was requested on invalid original deposit with reference {}", eventCode, originalReference);
            throw new InvalidOriginalPaymentException(originalReference);
        }

        if (!originalDeposit.getOperations().contains(operation)) {
            logger.error("{} is not allowed for the original deposit reference {} for player {} tried by user {}", eventCode, originalReference, playerId, user.getId());
            throw new InvalidOriginalPaymentException(operation);
        }

        final Wallet wallet = player.getWallet();
        final Money amount = originalDeposit.getAmount();
        if (wallet.getMoneyBalance().isLessThan(amount)) {
            logger.error("Player {} money balance: {} is less than {} amount: {} (original reference {}) refund tried by user {}",
                         playerId, wallet.getMoneyBalance(), eventCode, amount, originalReference, user.getId());
            throw new PaymentAmountException(String.format("Player's money balance: %s is less than %s amount: %s", wallet.getMoneyBalance(), eventCode, amount));
        }

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        final PaymentTransaction transaction =
                paymentTransactionFacade.insertPayment(player, TEMPORARY_PROVIDER_REFERENCE, originalReference, amount, originalDeposit.getCurrency(),
                                                       originalDeposit.getPaymentMethod(), PaymentStatus.AWAITING_PAYMENT, provider, eventCode);
        final ModificationResult modificationResult;
        try {
            if (eventCode == EventCode.REFUND) {
                modificationResult = adyenService.refundDeposit(player, originalReference, amount, eventCode, transaction.getId());
                auditService.trackUserActivity(user, UserActivityType.REFUND_DEPOSIT,
                                               String.format("Refund deposit with reference %s to Player %s", originalDeposit.getProviderReference(), playerId));
            } else {
                modificationResult = adyenService.cancelDeposit(player, originalReference, amount, eventCode, transaction.getId());
                auditService.trackUserActivity(user, UserActivityType.CANCEL_DEPOSIT,
                                               String.format("Canceled deposit with reference %s to Player %s", originalDeposit.getProviderReference(), playerId));
            }
        } catch (final AdyenException e) {
            logger.error("Error while sending {} request by user {} for player {}", eventCode, user.getId(), playerId, e);
            transaction.setPaymentStatus(PaymentStatus.SENDING_FAILURE);
            paymentTransactionFacade.savePayment(transaction);
            throw new CommunicationException(e.getMessage(), e);
        }

        // modificationResult.getResponse() is ignored since in case of paymentStatus this will be [cancel-received] / [refund-received].
        // In case of an error, a SOAP Fault will be received.

        reserveMoney(wallet, amount);

        // update transaction
        transaction.setProviderReference(modificationResult.getPspReference().getValue());
        paymentTransactionFacade.savePayment(transaction);
    }

    @Transactional(propagation = REQUIRED, noRollbackFor = {CommunicationException.class, WithdrawalFailedException.class})
    @Override
    public void withdraw(final Long playerId, final String payoutTargetReference, final String paymentMethod, final Money amount, final String password) {
        final Player player = playerService.getPlayer(playerId);
        final Wallet wallet = player.getWallet();

        playerService.validatePassword(player, password);

        if (player.getBlockType().isLimited(player.getBlockEndDate()) && wallet.getMoneyBalance().isLessThan(minimumBalanceForLimitedPlayerToWithdraw)) {
            logger.info("Player {} tried to withdraw money while was blocked {} and money balance was {}",
                        player.getId(), player.getBlockType(), wallet.getMoneyBalance());
            throw new BlockedPlayerException("Player is blocked " + player.getBlockType());
        }

        verifySufficientFunds(amount, wallet);

        final Provider provider = providerRepository.findOne(Provider.ADYEN);
        final PaymentTransaction transaction =
                paymentTransactionFacade.insertWithdrawal(player, TEMPORARY_PROVIDER_REFERENCE, payoutTargetReference, amount, player.getCurrency(),
                                                          paymentMethod, PaymentStatus.AWAITING_APPROVAL, provider);

        final SubmitResponse submitResponse;
        try {
            submitResponse = adyenService.withdraw(player, RecurringContract.PAYOUT, payoutTargetReference, amount, transaction.getId());
        } catch (final AdyenException e) {
            logger.error("Error while sending sendWithdrawRequest request: ", e);
            transaction.setPaymentStatus(PaymentStatus.SENDING_FAILURE);
            paymentTransactionFacade.savePayment(transaction);
            final String message = String.format("Failed Withdraw request of amount %f euros with method %s for reason of %s",
                                                 amount.getEuroValueInBigDecimal(), paymentMethod, e.getMessage());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.WITHDRAW_MONEY_REQUEST_FAILED, message);
            throw new CommunicationException(e.getMessage(), e);
        }

        logger.info("Received submitResponse from Adyen, PSP reference is {}", submitResponse.getPspReference());
        transaction.setOriginalReference(submitResponse.getPspReference());
        paymentTransactionFacade.savePayment(transaction);

        if (!submitResponse.getResultCode().equalsIgnoreCase(PAYOUT_SUBMIT_ACKNOWLEDGEMENT)) {
            logger.warn("Sending payout request failed for Player {} and amount {} on payout reference {} since for the reason of {}",
                        player.getId(), amount, payoutTargetReference, submitResponse.getRefusalReason());
            transaction.setReason(submitResponse.getRefusalReason());
            transaction.setPaymentStatus(PaymentStatus.FAILURE);
            paymentTransactionFacade.savePayment(transaction);
            final String message = String.format("Failed Withdraw request of amount %f euros with method %s for reason of %s",
                                                 amount.getEuroValueInBigDecimal(), paymentMethod, submitResponse.getRefusalReason());
            auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.WITHDRAW_MONEY_REQUEST_FAILED, message);

            throw new WithdrawalFailedException(message);
        }

        reserveMoney(wallet, amount);
        reserveBonusMoneyOnWithdrawal(player, transaction);

        logger.info("Made withdrawal request for player {} with amount {} and method {}", player.getId(), amount, paymentMethod);
        final String message = String.format("Successful Withdraw request of amount %s euros with method %s", amount.getEuroValueInBigDecimal(), paymentMethod);
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.WITHDRAW_MONEY_REQUEST, message);
    }

    private void verifySufficientFunds(final Money amount, final Wallet wallet) {
        if (wallet.getMoneyBalance().isLessThan(amount)) {
            throw new PaymentAmountException(String.format("Player's money balance: %s is less than sendWithdrawRequest amount: %s", wallet.getMoneyBalance(), amount));
        }
    }

    private void reserveMoney(final Wallet wallet, final Money amount) {
        wallet.setMoneyBalance(wallet.getMoneyBalance().subtract(amount));
        wallet.setReservedBalance(wallet.getReservedBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Override
    public Map<String, String> confirmWithdrawals(final List<String> originalReferences, final User user) {
        final Iterable<PaymentTransaction> awaitingWithdraws = paymentTransactionFacade.getPaymentsAwaitingApproval(originalReferences);

        if (Iterables.isEmpty(awaitingWithdraws)) {
            logger.error("No awaiting withdraw was found for confirmation, requested references: {}", originalReferences);
            throw new NotFoundException("Withdraw references not available.");
        }

        final Map<String, String> confirmedWithdrawals = new HashMap<>();
        for (final PaymentTransaction transaction : awaitingWithdraws) {
            logger.info("Confirming withdrawal for player {} of amount {}", transaction.getPlayer().getId(), transaction.getAmount());
            try {
                final String status = confirmWithdrawal(transaction, user);
                confirmedWithdrawals.put(transaction.getOriginalReference(), status);
            } catch (final CommunicationException | InsufficientBalanceException e) {
                logger.error("Error on confirming withdraw request with reference {} : Error Message: {}", transaction.getProviderReference(), e.getMessage());
                confirmedWithdrawals.put(transaction.getOriginalReference(), e.getMessage());
            }
        }

        return confirmedWithdrawals;
    }

    private String confirmWithdrawal(final PaymentTransaction transaction, final User user) {
        final Player player = transaction.getPlayer();

        verifySufficientReservedFunds(player, transaction.getAmount());

        final ModifyResponse modifyResponse;
        try {
            modifyResponse = adyenService.confirmWithdrawal(transaction.getOriginalReference());
        } catch (final AdyenException e) {
            logger.error("Error when confirming withdrawal with Adyen: ", e);
            throw new CommunicationException(e.getMessage(), e);
        }

        transaction.setProviderReference(modifyResponse.getPspReference());
        transaction.setPaymentStatus(PaymentStatus.AWAITING_NOTIFICATION);
        transaction.setWithdrawConfirmDate(new Date());

        if (!modifyResponse.getResponse().equalsIgnoreCase(PAYOUT_CONFIRM_ACKNOWLEDGEMENT)) {
            logger.error("Error confirming withdrawal {}: {}", transaction.getProviderReference(), modifyResponse.getResponse());
            return modifyResponse.getResponse();
        }

        paymentTransactionFacade.savePayment(transaction);

        final String message = String.format("Withdraw amount %f euros with method %s with provider reference %s and original reference %s was confirmed",
                                             transaction.getAmount().getEuroValueInBigDecimal(), transaction.getPaymentMethod(),
                                             transaction.getProviderReference(), transaction.getOriginalReference());
        auditService.trackUserActivity(user, UserActivityType.CONFIRM_WITHDRAW, message);
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.WITHDRAW_MONEY_CONFIRMED, message);

        return modifyResponse.getPspReference();
    }

    private void verifySufficientReservedFunds(final Player player, final Money amount) {
        final Money reservedBalance = player.getWallet().getReservedBalance();
        if (reservedBalance.isLessThan(amount)) {
            logger.error("Player {} has insufficient reserved funds {} for amount {}", player.getId(), reservedBalance, amount);
            throw new InsufficientBalanceException(reservedBalance, amount);
        }
    }

    @Override
    public Map<String, String> declineWithdrawals(final List<String> originalReferences, final User user) {
        final Iterable<PaymentTransaction> awaitingWithdraws = paymentTransactionFacade.getPaymentsAwaitingApproval(originalReferences);

        if (Iterables.isEmpty(awaitingWithdraws)) {
            logger.error("No awaiting withdraw was found for declining, requested references: {}", originalReferences);
            throw new NotFoundException("Withdraw references not available.");
        }

        final Map<String, String> declinedWithdrawals = new HashMap<>();
        for (final PaymentTransaction transaction : awaitingWithdraws) {
            try {
                logger.info("Declining withdrawal for player {} of amount {}", transaction.getPlayer().getId(), transaction.getAmount());
                final String status = declineWithdrawal(transaction, user);
                declinedWithdrawals.put(transaction.getOriginalReference(), status);
            } catch (final CommunicationException e) {
                logger.error("Error on declining withdraw request with reference {} : Error Message: {}", transaction.getProviderReference(), e.getMessage());
                declinedWithdrawals.put(transaction.getOriginalReference(), e.getMessage());
            }
        }

        return declinedWithdrawals;
    }

    private String declineWithdrawal(final PaymentTransaction transaction, final User user) {
        final Player player = transaction.getPlayer();

        final ModifyResponse modifyResponse;
        try {
            modifyResponse = adyenService.declineWithdrawal(transaction.getOriginalReference());
        } catch (final AdyenException e) {
            logger.error("Error when declining withdrawal with Adyen: ", e);
            throw new CommunicationException(e.getMessage(), e);
        }
        transaction.setProviderReference(modifyResponse.getPspReference());
        transaction.setPaymentStatus(PaymentStatus.AWAITING_NOTIFICATION);
        transaction.setWithdrawConfirmDate(new Date());

        if (!modifyResponse.getResponse().equalsIgnoreCase(PAYOUT_DECLINE_ACKNOWLEDGEMENT)) {
            logger.error("Error declining withdrawal {}: {}", transaction.getProviderReference(), modifyResponse.getResponse());
            return modifyResponse.getResponse();
        }

        paymentTransactionFacade.savePayment(transaction);

        final String message = String.format("Withdraw amount %f euros with method %s with provider reference %s and withdraw reference %s was declined",
                                             transaction.getAmount().getEuroValueInBigDecimal(), transaction.getPaymentMethod(),
                                             transaction.getProviderReference(), transaction.getWithdrawReference());
        auditService.trackUserActivity(user, UserActivityType.DECLINE_WITHDRAW, message);
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.WITHDRAW_MONEY_DECLINED, message);

        return modifyResponse.getPspReference();
    }

    private void reserveBonusMoneyOnWithdrawal(final Player player, final PaymentTransaction paymentTransaction) {
        final List<PlayerBonus> voidablePlayerBonuses = bonusService.getVoidablePlayerBonuses(player);

        if (voidablePlayerBonuses.isEmpty()) {
            logger.info("No voidable player bonuses for player {}", player.getId());
            return;
        }

        final List<Pair<Long, String>> voidedBonuses = new ArrayList<>();

        for (final PlayerBonus voidablePlayerBonus : voidablePlayerBonuses) {
            if (voidablePlayerBonus.getStatus() == BonusStatus.ACTIVE) {
                logger.info("Removed active player-bonus {} from player {} wallet on withdraw request.", voidablePlayerBonus.getId(), player.getId());
                final Wallet wallet = player.getWallet();
                wallet.setActivePlayerBonus(null);
                walletRepository.save(wallet);
                break;
            }
        }

        for (final PlayerBonus voidablePlayerBonus : voidablePlayerBonuses) {
            voidablePlayerBonus.setStatus(BonusStatus.RESERVED);
            voidablePlayerBonus.setPaymentTransaction(paymentTransaction);
            bonusService.updatePlayerBonus(voidablePlayerBonus);
            final Bonus bonus = voidablePlayerBonus.getPk().getBonus();
            voidedBonuses.add(new Pair<>(bonus.getId(), bonus.getName()));
        }

        final String message = String.format("Reserving bonuses %s due to withdraw request", voidedBonuses);
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.BONUS_RESERVED_ON_WITHDRAW, message);
        logger.info("Player {} withdraw request {} reserved all bonus money of {} player-bonus to be voided on Notification", player.getId(),
                    paymentTransaction.getId(), voidablePlayerBonuses.size());
    }

    private void voidReservedBonusMoneyOnWithdrawal(final Player player, final PaymentTransaction paymentTransaction) {
        logger.info("Player {} successful withdraw voided all reserved bonus money of player-bonuses linked to withdraw transaction {} and those are reserved from " +
                    "previous awaiting withdrawals.",
                    player.getId(), paymentTransaction.getId()
        );

        final BonusStatus toStatus = BonusStatus.VOIDED;
        final List<PlayerBonus> playerBonuses = bonusService.getPlayerBonusesTiedToPayment(player, paymentTransaction);
        final List<PlayerBonus> olderPlayerBonuses = bonusService.getOldReservedPlayerBonuses(player, paymentTransaction.getCreatedDate());
        playerBonuses.addAll(olderPlayerBonuses);

        final Date now = new Date();
        for (final PlayerBonus playerBonus : playerBonuses) {
            // Only touch unfinished bonuses
            if (!playerBonus.getStatus().isCompleted()) {
                if (playerBonus.getStatus() != BonusStatus.RESERVED) {
                    logger.error("Player {} Player-bonus {} has invalid status {} when trying to void, modified to {}.",
                                 player.getId(), playerBonus.getId(), playerBonus.getStatus(), toStatus);
                }
                playerBonus.setPaymentTransaction(paymentTransaction); // updates old reserved player bonuses
                playerBonus.setStatus(toStatus);
                playerBonus.setCompletionDate(now);
                bonusService.updatePlayerBonus(playerBonus);
                logger.info("Player {} Player-bonus {} status changed to {} for {} withdraw transaction {} .", player.getId(), playerBonus.getId(), toStatus,
                            paymentTransaction.getPaymentStatus(), paymentTransaction.getId());
            }
        }
    }

    private void releaseBonusMoneyOnAbortedWithdrawal(final Player player, final PaymentTransaction paymentTransaction) {
        final BonusStatus toStatus = BonusStatus.INACTIVE;

        final List<PlayerBonus> playerBonuses = bonusService.getPlayerBonusesTiedToPayment(player, paymentTransaction);
        final PaymentTransaction nextAwaitingWithdrawal = findNextAwaitingWithdrawal(player, paymentTransaction.getCreatedDate());

        final Date now = new Date();
        for (final PlayerBonus playerBonus : playerBonuses) {
            // Only touch unfinished bonuses
            if (!playerBonus.getStatus().isCompleted()) {
                if (playerBonus.getStatus() != BonusStatus.RESERVED) {
                    logger.error("Player {} Player-bonus {} has invalid status {} when trying to void, modified to {}.",
                                 player.getId(), playerBonus.getId(), playerBonus.getStatus(), toStatus);
                }

                if (nextAwaitingWithdrawal != null) {
                    playerBonus.setPaymentTransaction(nextAwaitingWithdrawal);
                    logger.info("Player {} Player-bonus {} payment transaction changed to {} for {} withdraw transaction {} .", player.getId(), playerBonus.getId(),
                                nextAwaitingWithdrawal.getId(), paymentTransaction.getPaymentStatus(), paymentTransaction.getId());
                } else {
                    playerBonus.setStatus(toStatus);
                    logger.info("Player {} Player-bonus {} status changed to {} for {} withdraw transaction {} .", player.getId(), playerBonus.getId(), toStatus,
                                paymentTransaction.getPaymentStatus(), paymentTransaction.getId());
                }

                playerBonus.setCompletionDate(now);
                bonusService.updatePlayerBonus(playerBonus);
            }
        }

        bonusService.getActivePlayerBonus(player);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Nullable
    private PaymentTransaction findNextAwaitingWithdrawal(final Player player, final Date from) {
        final List<PaymentTransaction> paymentTransactions = paymentTransactionFacade.getAwaitingWithdrawalsAfter(player, from);
        PaymentTransaction nextAwaitingWithdrawal = null;
        if (paymentTransactions.size() >= 1) {
            nextAwaitingWithdrawal = paymentTransactions.get(0);
        }
        return nextAwaitingWithdrawal;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<PayoutDetailReference> sendPayoutInformationRequest(final Player player) {

        final RecurringDetailsResult recurringDetailsResult;
        try {
            recurringDetailsResult = adyenService.getWithdrawalDetails(player, RecurringContract.PAYOUT);
        } catch (final AdyenException e) {
            logger.error("Error while sending retrieve sendWithdrawRequest detail request", e);
            throw new CommunicationException(e.getMessage(), e);
        }

        final List<PayoutDetailReference> payoutDetailReferenceList = new ArrayList<>();

        if (recurringDetailsResult.getDetails() != null && recurringDetailsResult.getDetails().getRecurringDetail() != null) {
            for (final RecurringDetail recurringDetail : recurringDetailsResult.getDetails().getRecurringDetail()) {
                final PayoutDetailReference reference = new PayoutDetailReference(recurringDetail.getRecurringDetailReference(), recurringDetail.getVariant(),
                                                                                  recurringDetail.getName(), recurringDetail.getCreationDate().getTime());
                final Card card = recurringDetail.getCard();
                final BankAccount account = recurringDetail.getBank();
                if (card != null && card.getNumber() != null) {
                    reference.setCardNumber(card.getNumber().substring(Math.max(card.getNumber().length() - 4, 0)));
                } else if (account != null && account.getBankAccountNumber() != null) {
                    reference.setAccountNumber(account.getBankAccountNumber().substring(Math.max(account.getBankAccountNumber().length() - 4, 0)));
                } else if (account != null && account.getIban() != null) {
                    reference.setIban(account.getIban().substring(Math.max(account.getIban().length() - 4, 0)));
                }
                payoutDetailReferenceList.add(reference);
            }
        }

        return payoutDetailReferenceList;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public boolean isPlayerAllowedToStoreBankAccount(final List<PayoutDetailReference> references) {
        for (final PayoutDetailReference reference : references) {
            if (reference.getVariant().startsWith("bankTransfer")) {
                return false;
            }
        }

        return true;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Page<PaymentTransaction> getAwaitingWithdrawals(final Integer page, final Integer size) {
        return paymentTransactionFacade.getAwaitingWithdrawals(page, size);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<PaymentTransaction> getPaymentsContainingOperation(final Long playerId, final Operation operation) {
        final Player player = playerService.getPlayer(playerId);
        final List<PaymentTransaction> appliedList = new ArrayList<>();
        final List<PaymentTransaction> list = paymentTransactionFacade.getPayments(player, EventCode.AUTHORISATION, PaymentStatus.SUCCESS);
        for (final PaymentTransaction paymentTransaction : list) {
            if (paymentTransaction.getOperations().contains(operation)) {
                appliedList.add(paymentTransaction);
            }
        }
        return appliedList;
    }

    private void alertBackOffice() {
        // TODO alert back-office
    }

    private void alertFrontEnd() {
        // TODO push through web socket
    }

    @Override
    public DepositDetails prepareDeposit(final Long playerId, final Money amount, final String skin, final String paymentMethodName, @Nullable final Long bonusId) {
        final PaymentMethod paymentMethod = paymentMethodRepository.findByMethodName(paymentMethodName);
        if (paymentMethod == null) {
            logger.error("Could not found Payment Method with name {} during deposit {} euros by Player {}",
                         paymentMethodName, amount.getEuroValueInBigDecimal(), playerId);
            throw new NotFoundException("Payment Method not found with name " + paymentMethodName);
        }

        if (!paymentMethod.isDepositAmountValid(amount)) {
            logger.warn("Player {} tried to deposit {} euros using {}", playerId, amount.getEuroValueInBigDecimal(), paymentMethod.getName());
            throw new PaymentAmountException(paymentMethod.getMaxLimit().toString(),
                                             "Maximum deposit amount for " + paymentMethod.getName() + " is: " + paymentMethod.getMaxLimit());
        }
        final Player player = playerService.getPlayer(playerId);
        final PlayerUuid playerUuid = playerService.createUuidForDeposit(player);
        playerUuid.setBonusId(bonusId);
        playerUuidRepository.save(playerUuid);

        final String url = adyenService.createAdyenDepositUrl(amount, skin, paymentMethod, player, playerUuid.getUuid());

        final String message = String.format("Deposit request of amount %f euros with method %s", amount.getEuroValueInBigDecimal(), paymentMethod.getMethodName());
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.DEPOSIT_MONEY_REQUEST, message);

        return new DepositDetails(url, paymentMethod.isEmbeddedFrame());
    }

    @Override
    public Wallet updatePlayerWallet(final Long id, final User user, final Wallet wallet) {
        final SecurityRole securityRole = user.getRoles().get(0).getPk().getRole().getName();

        final Player player = playerService.getPlayer(id);
        final Wallet playerWallet = player.getWallet();
        final Money requestedMoneyBalance = wallet.getMoneyBalance();
        final Money moneyDelta;
        //noinspection ConstantConditions
        if (requestedMoneyBalance != null) {
            moneyDelta = requestedMoneyBalance.subtract(playerWallet.getMoneyBalance());
        } else {
            moneyDelta = Money.ZERO;
        }

        final Integer requestCreditsBalance = wallet.getCreditsBalance();
        final Integer creditsDelta;
        //noinspection ConstantConditions
        if (requestCreditsBalance != null) {
            creditsDelta = requestCreditsBalance - playerWallet.getCreditsBalance();
        } else {
            creditsDelta = 0;
        }

        logger.info("Received a request to update wallet for player: {} from user: {} with money amount: {}, credits amount: {}",
                    player.getId(), user.getId(), moneyDelta, creditsDelta);

        if (!moneyDelta.isZero()) {
            if (securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_ADMIN) || securityRole.getAccesses().contains(Access.PLAYER_PAYMENTS_WRITE)) {
                final EventCode eventCode = moneyDelta.isPositive() ? EventCode.BACK_OFFICE_MONEY_DEPOSIT : EventCode.BACK_OFFICE_MONEY_WITHDRAW;
                createBlingCityPayment(player, moneyDelta.abs(), ProviderReferences.BLING_CITY_MONEY, eventCode);
                playerWallet.setMoneyBalance(requestedMoneyBalance);
                final String message = String.format("Updated wallet for player: %s from user: %s with money amount: %s", player.getId(), user.getId(), moneyDelta);
                auditService.trackUserActivity(user, UserActivityType.UPDATE_PLAYER_WALLET, message);
                logger.info("Updated wallet for player: {} from user: {} with money amount: {}", player.getId(), user.getId(), moneyDelta);
            } else {
                logger.info("User: {} tried to update player: {} with money amount: {}", user.getId(), player.getId(), moneyDelta);
                final String message = String.format("User: %s tried to update player: %s with money amount: %s", player.getId(), user.getId(), moneyDelta);
                auditService.trackUserActivity(user, UserActivityType.UPDATE_PLAYER_WALLET, message);
                throw new AccessDeniedException("User not allowed to execute this action");
            }
        }

        if (creditsDelta != 0) {
            final boolean isAdmin = securityRole.getAccesses().contains(Access.PLAYER_CREDITS_ADMIN);
            final boolean isWrite = securityRole.getAccesses().contains(Access.PLAYER_CREDITS_WRITE);
            if (isAdmin || isWrite) {
                if (!isAdmin && creditsDelta > 0) {
                    logger.warn("User {} not allowed to increase", user.getId());
                    throw new AccessDeniedException("User not allowed to execute this action");
                }
                playerWallet.setCreditsBalance(requestCreditsBalance);

                final CreditTransaction creditTransaction = new CreditTransaction();
                creditTransaction.setPlayer(player);
                creditTransaction.setLevel(player.getLevel().getLevel());
                creditTransaction.setCurrency(player.getCurrency());
                creditTransaction.setCredit(requestCreditsBalance);
                creditTransaction.setUser(user);
                creditTransaction.setCreatedDate(new Date());
                creditTransaction.setCreditTransactionType(creditsDelta > 0 ? CreditTransactionType.BACK_OFFICE_CREDIT_DEPOSIT :
                                                                   CreditTransactionType.BACK_OFFICE_CREDIT_WITHDRAW);

                creditTransactionRepository.save(creditTransaction);

                final String message = String.format("Updated wallet for player: %s from user: %s with credit amount: %s",
                                                     player.getId(), user.getId(), requestCreditsBalance);
                auditService.trackUserActivity(user, UserActivityType.UPDATE_PLAYER_WALLET, message);
                logger.info("Updated wallet for player: {} from user: {} with credit amount: {}",
                            player.getId(), user.getId(), requestCreditsBalance);
            } else {
                logger.info("User: {} tried to update player: {} with credit amount: {}", player.getId(), user.getId(), requestCreditsBalance);
                final String message = String.format("User: %s tried to update player: %s with credit amount: %s", player.getId(), user.getId(), requestCreditsBalance);
                auditService.trackUserActivity(user, UserActivityType.UPDATE_PLAYER_WALLET, message);
                throw new AccessDeniedException("User not allowed to execute this action");
            }
        }

        if (!moneyDelta.isZero() || creditsDelta != 0) {
            walletRepository.save(playerWallet);
        }

        return playerWallet;
    }

    @Override
    public PlayerBonus updatePlayerBonus(final Long playerBonusId, final User user, final Money requestedBonusBalance) {
        final Pair<Pair<Money, EventCode>, PlayerBonus> pair = bonusService.updatePlayerBonus(playerBonusId, user, requestedBonusBalance);
        final PlayerBonus playerBonus = pair.getRight();
        final Pair<Money, EventCode> moneyEventCodePair = pair.getLeft();
        createBlingCityPayment(playerBonus.getPk().getPlayer(), moneyEventCodePair.getLeft().abs(), ProviderReferences.BLING_CITY_BONUS, moneyEventCodePair.getRight());
        return playerBonus;
    }

    @Override
    public PlayerBonus createAdjustableBonus(final Player player, final User user, final Money amount, final Money maxRedemptionAmount, final Date validFrom,
                                             final Date validTo) {
        final PlayerBonus playerBonus = bonusService.addAdjustableBonus(player, user, amount, maxRedemptionAmount, validFrom, validTo);
        createBlingCityPayment(player, amount, ProviderReferences.BLING_CITY_BONUS, EventCode.BACK_OFFICE_BONUS_DEPOSIT);
        return playerBonus;
    }

    @Override
    public PaymentTransaction createBlingCityPayment(final Player player, final Money amount, final String providerReference, final EventCode eventCode) {
        final Provider blingCity = providerRepository.findOne(Provider.BLING_CITY);

        final Date now = new Date();
        final PaymentTransaction payment = new PaymentTransaction();
        payment.setPlayer(player);
        payment.setCurrency(player.getCurrency());
        payment.setProvider(blingCity);
        payment.setEventDate(now);
        payment.setCreatedDate(now);
        payment.setProviderReference(providerReference);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setAmount(amount);
        payment.setEventCode(eventCode);
        payment.setLevel(player.getLevel().getLevel());

        logger.info("Creating payment to player {}, with amount {} and type {}", player.getId(), amount, eventCode);

        return paymentTransactionFacade.savePayment(payment);
    }

    @Override
    public PaymentTransaction createBonusConversionDeposit(final Player player, final Money amount) {
        return createBlingCityPayment(player, amount, ProviderReferences.BLING_CITY_BONUS_CONVERSION, EventCode.BONUS_CONVERSION);
    }

    @Override
    public void storeBankAccountForWithdrawal(final Player player, final String iban, final String bic, final String bankName, final String ownerName) {
        final String withdrawReference = storeBankAccount(player, iban, bic, bankName, ownerName);
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.STORE_BANK_ACCOUNT_INFO,
                                                        String.format("Bank account info was registered; withdraw reference on Adyen: %s", withdrawReference));
    }

    @Override
    public void backOfficeStoreBankAccountForWithdrawal(final User user, final Long playerId, final String iban, final String bic, final String bankName,
                                                        final String ownerName) {
        final Player player = playerService.getPlayer(playerId);
        final String withdrawReference = storeBankAccount(player, iban, bic, bankName, ownerName);
        auditService.trackUserActivity(user, UserActivityType.STORE_BANK_ACCOUNT_INFO, String.format("Player %d bank account info was registered; withdraw reference " +
                                                                                                     "on Adyen: %s", playerId, withdrawReference));
    }

    private String storeBankAccount(final Player player, final String iban, final String bic, final String bankName, final String ownerName) {
        final StoreDetailResponse storeDetailResponse;
        try {
            storeDetailResponse = adyenService.storeBankAccount(player, ownerName, bankName, iban, bic, RecurringContract.PAYOUT);
        } catch (final AdyenException e) {
            throw new CommunicationException(e.getMessage(), e);
        }

        // TODO success as a constant
        if (STORE_BANK_ACCOUNT_INFO_SUCCESS_RESULT.equalsIgnoreCase(storeDetailResponse.getResultCode())) {
            logger.info("Successfully stored bank account info on Adyen for player {}, provider reference is {} withdraw reference is {}",
                        player.getId(), storeDetailResponse.getPspReference(), storeDetailResponse.getRecurringDetailReference());
            return storeDetailResponse.getRecurringDetailReference();
        } else {
            logger.error("Error while storing bank account info on Adyen for player {}, result code {}",
                         player.getId(), storeDetailResponse.getResultCode());
            throw new IncorrectBankAccountException("Incorrect bank account information " + storeDetailResponse.getResultCode());
        }
    }

    private void increaseAccumulatedDeposits(final Wallet wallet, final Money amount, final EventCode eventCode) {
        logger.warn("Player {} accumulated deposits value was increased by {} euros due to successful {} Notification",
                    wallet.getPlayer().getId(), amount.getEuroValueInBigDecimal(), eventCode);

        wallet.setAccumulatedDeposit(wallet.getAccumulatedDeposit().add(amount));
        walletRepository.save(wallet);
    }

    private void decreaseAccumulatedDeposits(final Wallet wallet, final Money amount, final EventCode eventCode) {
        logger.warn("Player {} accumulated deposits value was decreased by {} euros due to successful {} Notification",
                    wallet.getPlayer().getId(), amount.getEuroValueInBigDecimal(), eventCode);

        wallet.setAccumulatedDeposit(wallet.getAccumulatedDeposit().subtract(amount));
        walletRepository.save(wallet);
    }

    private void increaseAccumulatedWithdrawal(final Wallet wallet, final Money amount, final EventCode eventCode) {
        logger.warn("Player {} accumulated withdrawal value was increased by {} euros due to successful {} Notification",
                    wallet.getPlayer().getId(), amount.getEuroValueInBigDecimal(), eventCode);

        wallet.setAccumulatedWithdrawal(wallet.getAccumulatedWithdrawal().add(amount));
        walletRepository.save(wallet);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<PaymentMethod> getAllowedPaymentMethodsByCountry(final Country country) {
        final List<PaymentMethod> paymentMethods = paymentMethodRepository.findByEnabled(true);
        final List<PaymentMethod> allowedMethods = new ArrayList<>();
        for (final PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod.isPaymentMethodAllowedInCountry(country)) {
                allowedMethods.add(paymentMethod);
            }
        }
        return allowedMethods;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Page<Wallet> getWalletsWeeklyTurnover(final Integer page, final Integer size) {
        final PageRequest pageRequest = new PageRequest(page, size);
        final Predicate predicate = QWallet.wallet.accumulatedWeeklyTurnover.gt(Money.ZERO);
        return walletRepository.findAll(predicate, pageRequest);
    }

    @ScheduledJob
    @Override
    public boolean giveCashback(final Wallet wallet) {
        final Player player = wallet.getPlayer();
        boolean updatedWallet = false;

        try {
            final Money accumulatedWeeklyTurnover = wallet.getAccumulatedWeeklyTurnover();
            if (player.isCashbackEligible()) {
                updatedWallet = true;

                //Calculate cashback
                final BigDecimal cashbackPercentage = player.getLevel().getCashbackPercentage().divide(new BigDecimal(100));
                final Money cashbackToGive = accumulatedWeeklyTurnover.multiply(cashbackPercentage);
                wallet.setMoneyBalance(wallet.getMoneyBalance().add(cashbackToGive));
                wallet.setAccumulatedCashback(wallet.getAccumulatedCashback().add(cashbackToGive));
                walletRepository.save(wallet);

                createBlingCityPayment(player, cashbackToGive, ProviderReferences.BLING_CITY_CASHBACK, EventCode.CASHBACK);
                insertCashbackToPlayerActivity(player, accumulatedWeeklyTurnover, cashbackToGive, player.getLevel());

                logger.info("Player {} received cashback amount: {} with cashbackPercentage: {} from accumulated weekly turnover {}", player.getId(), cashbackToGive,
                            cashbackPercentage, accumulatedWeeklyTurnover);
            }
            resetAccumulatedWeeklyTurnover(wallet);
        } catch (final RuntimeException e) {
            logger.error("Error giving player cashback: ", e);
        }

        return updatedWallet;
    }

    private void insertCashbackToPlayerActivity(final Player player, final Money accumulatedWeeklyTurnover, final Money cashbackToGive, final Level level) {
        final PlayerActivity playerActivity = new PlayerActivity(player, PlayerActivityType.GIVE_CASHBACK, new Date());
        playerActivity.setDescription(String.format("Given %s cashback from %s accumulated weekly turnover based on level %d",
                                                    cashbackToGive, accumulatedWeeklyTurnover, level.getLevel()));
        auditService.trackPlayerActivity(playerActivity);
    }

    private void resetAccumulatedWeeklyTurnover(final Wallet wallet) {
        wallet.setAccumulatedWeeklyTurnover(Money.ZERO);
        logger.debug("Reset weekly turnover for Player {}", wallet.getPlayer().getId());
        walletRepository.save(wallet);
    }

    @ScheduledJob
    @Override
    public void resetMonthlyTurnover() {
        final Integer count = walletRepository.setAccumulatedMontyTurnoverToZero(Money.ZERO);
        logger.info("Reset monthly turnover for {} players", count);
    }
}
