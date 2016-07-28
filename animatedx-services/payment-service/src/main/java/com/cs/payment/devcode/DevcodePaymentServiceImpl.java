package com.cs.payment.devcode;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.bonus.Bonus;
import com.cs.bonus.BonusService;
import com.cs.bonus.BonusStatus;
import com.cs.bonus.PlayerBonus;
import com.cs.bonus.TriggerEvent;
import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.Money;
import com.cs.payment.Provider;
import com.cs.payment.ProviderRepository;
import com.cs.payment.QDCPaymentTransaction;
import com.cs.persistence.NotFoundException;
import com.cs.player.Player;
import com.cs.player.PlayerService;
import com.cs.player.Wallet;
import com.cs.player.WalletRepository;
import com.cs.promotion.PlayerCriteria;
import com.cs.promotion.PromotionService;
import com.cs.promotion.PromotionTrigger;
import com.cs.session.PlayerSessionService;
import com.cs.util.CalendarUtils;
import com.cs.util.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cs.payment.DCEventType.AUTHORIZE;
import static com.cs.payment.DCEventType.CANCEL;
import static com.cs.payment.DCEventType.TRANSFER;
import static com.cs.payment.DCEventType.getComplementaryEvents;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class DevcodePaymentServiceImpl implements DevcodePaymentService {

    private final Logger logger = LoggerFactory.getLogger(DevcodePaymentServiceImpl.class);

    private final AuditService auditService;
    private final BonusService bonusService;
    private final DCPaymentTransactionRepository dcPaymentTransactionRepository;
    private final PlayerService playerService;
    private final ProviderRepository providerRepository;
    private final PlayerSessionService playerSessionService;
    private final PromotionService promotionService;
    private final WalletRepository walletRepository;

    @Autowired
    public DevcodePaymentServiceImpl(final AuditService auditService, final BonusService bonusService, final PlayerService playerService,
                                     final ProviderRepository providerRepository, final DCPaymentTransactionRepository dcPaymentTransactionRepository,
                                     final PlayerSessionService playerSessionService, final PromotionService promotionService, final WalletRepository walletRepository) {
        this.auditService = auditService;
        this.bonusService = bonusService;
        this.playerService = playerService;
        this.providerRepository = providerRepository;
        this.dcPaymentTransactionRepository = dcPaymentTransactionRepository;
        this.playerSessionService = playerSessionService;
        this.promotionService = promotionService;
        this.walletRepository = walletRepository;
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public Player verifyPlayer(final String playerIdString, final String sessionId) {
        final Player player;
        try {
            player = playerService.getPlayer(Long.parseLong(playerIdString));
        } catch (final NumberFormatException | NotFoundException ex) {
            throw new DCInvalidPlayerException(playerIdString);
        }

        if (!playerSessionService.isPlayerSessionValidForPayment(player, sessionId)) {
            throw new DCInvalidSessionException(playerIdString, sessionId);
        }

        logger.info("Verifying player {} with session id {}", player.getId(), sessionId);
        return player;
    }

    @Transactional(propagation = REQUIRED, noRollbackFor = {DCPlayerDetailException.class})
    @Override
    public DCPaymentTransaction authorize(final String playerIdString, final DCPaymentTransaction dcPaymentTransaction) {
        final DCPaymentTransaction duplicatedTransaction = dcPaymentTransactionRepository.findByTransactionId(dcPaymentTransaction.getTransactionId());
        if (duplicatedTransaction != null && duplicatedTransaction.getAuthorizationCode() != null) {
            // Already authorized
            return duplicatedTransaction;
        }

        final UUID authorizationCode = UUID.randomUUID();
        dcPaymentTransaction.setAuthorizationCode(authorizationCode.toString());
        final DCPaymentTransaction persistedDcPaymentTransaction = dcPaymentTransactionRepository.save(setPlayerAndProvider(playerIdString, dcPaymentTransaction));

        // TODO if amount == 0
        if (dcPaymentTransaction.getAmount().isPositive()) {
            return authorizeDeposit(persistedDcPaymentTransaction);
        }

        return authorizeWithdraw(persistedDcPaymentTransaction, persistedDcPaymentTransaction.getAmount().abs());
    }

    private DCPaymentTransaction setPlayerAndProvider(final String playerIdString, final DCPaymentTransaction dcPaymentTransaction) {
        try {
            final Player player = playerService.getPlayer(Long.parseLong(playerIdString));
            dcPaymentTransaction.setPlayer(player);
            dcPaymentTransaction.setLevel(player.getLevel().getLevel());
            final Provider provider = providerRepository.findOne(Provider.DEVCODE);
            dcPaymentTransaction.setProvider(provider);
        } catch (final NumberFormatException | NotFoundException ex) {
            throw new DCInvalidPlayerException(playerIdString);
        }

        return dcPaymentTransaction;
    }

    private DCPaymentTransaction authorizeDeposit(final DCPaymentTransaction dcPaymentTransaction) {
        logger.info("Authorizing deposit request for player {} with amount {}", dcPaymentTransaction.getPlayer().getId(),
                    dcPaymentTransaction.getAmount().getEuroValueInBigDecimal());
        return dcPaymentTransaction;
    }

    private DCPaymentTransaction authorizeWithdraw(final DCPaymentTransaction dcPaymentTransaction, final Money amount) {
        final Player player = dcPaymentTransaction.getPlayer();
        final Wallet wallet = player.getWallet();

        verifySufficientFunds(amount, player);
        reserveMoney(wallet, amount);
        reserveBonusMoneyOnWithdrawal(player, dcPaymentTransaction);

        logger.info("Authorizing withdrawal request for player {} with amount {}", player.getId(), dcPaymentTransaction.getAmount().getEuroValueInBigDecimal());
        return dcPaymentTransaction;
    }

    private void verifySufficientFunds(final Money amount, final Player player) {
        if (player.getWallet().getMoneyBalance().isLessThan(amount)) {
            throw new DCTransactionAmountException(player.getId(), String.format("Player's money balance: %f is less than authorize (withdraw) amount: %f",
                                                                                 player.getWallet().getMoneyBalance().getEuroValueInBigDecimal(),
                                                                                 amount.getEuroValueInBigDecimal()));
        }
    }

    private void reserveMoney(final Wallet wallet, final Money amount) {
        logger.info("Reserving {} for player {}", amount, wallet.getPlayer().getId());
        wallet.setMoneyBalance(wallet.getMoneyBalance().subtract(amount));
        wallet.setReservedBalance(wallet.getReservedBalance().add(amount));
        walletRepository.save(wallet);
    }

    private void reserveBonusMoneyOnWithdrawal(final Player player, final DCPaymentTransaction dcPaymentTransaction) {
        final List<PlayerBonus> voidablePlayerBonuses = bonusService.getVoidablePlayerBonuses(player);

        if (voidablePlayerBonuses.isEmpty()) {
            logger.debug("No voidable player bonuses for player {}", player.getId());
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
            voidablePlayerBonus.setDcPaymentTransaction(dcPaymentTransaction);
            bonusService.updatePlayerBonus(voidablePlayerBonus);
            final Bonus bonus = voidablePlayerBonus.getPk().getBonus();
            voidedBonuses.add(new Pair<>(bonus.getId(), bonus.getName()));
        }

        final String message = String.format("Reserving bonuses %s due to withdraw request", voidedBonuses);
        auditService.trackPlayerActivityWithDescription(player, PlayerActivityType.BONUS_RESERVED_ON_WITHDRAW, message);
        logger.info("Player {} withdraw request {} reserved all bonus money of {} player-bonus to be voided on Notification", player.getId(),
                    dcPaymentTransaction.getId(), voidablePlayerBonuses.size());
    }

    @Override
    public DCPaymentTransaction transfer(final String playerIdString, final DCPaymentTransaction dcPaymentTransaction) {
        final DCPaymentTransaction authorizedTransaction = dcPaymentTransactionRepository.findByTransactionId(dcPaymentTransaction.getTransactionId());
        if (authorizedTransaction != null && authorizedTransaction.getDcEventType() == TRANSFER) {
            // already processed
            return authorizedTransaction;
        }

        // Sometimes PaymentIQ must do a transfer without a previous authorize and will therefore in such circumstances not provide an authorization code.
        // So, only validate if the authorization code is set
        if (dcPaymentTransaction.getAuthorizationCode() != null) {
            if (authorizedTransaction == null || !dcPaymentTransaction.getAuthorizationCode().equals(authorizedTransaction.getAuthorizationCode())) {
                logger.error("Invalid authorization code for payment {}", dcPaymentTransaction);
                throw new DCInvalidAuthorizationCodeException(playerIdString, dcPaymentTransaction.getAuthorizationCode());
            }
        }

        final DCPaymentTransaction updatedTransaction = updateTransferTransaction(playerIdString, authorizedTransaction, dcPaymentTransaction);

        if (dcPaymentTransaction.getAmount().isPositive()) {
            return transferDeposit(updatedTransaction);
        } else {
            return transferWithdraw(updatedTransaction, dcPaymentTransaction.getAmount().abs());
        }
    }

    private DCPaymentTransaction updateTransferTransaction(final String playerIdString, @Nullable final DCPaymentTransaction authorizedTransaction,
                                                           final DCPaymentTransaction dcPaymentTransaction) {
        if (authorizedTransaction == null) {
            return dcPaymentTransactionRepository.save(setPlayerAndProvider(playerIdString, dcPaymentTransaction));
        }

        authorizedTransaction.setDcEventType(TRANSFER);
        authorizedTransaction.setTransactionProvider(dcPaymentTransaction.getTransactionProvider());
        authorizedTransaction.setBonusCode(dcPaymentTransaction.getBonusCode());
        authorizedTransaction.setStatusCode(dcPaymentTransaction.getStatusCode());
        authorizedTransaction.setPspStatusCode(dcPaymentTransaction.getPspStatusCode());
        authorizedTransaction.setFee(dcPaymentTransaction.getFee());
        authorizedTransaction.setFeeCurrency(dcPaymentTransaction.getFeeCurrency());
        authorizedTransaction.setPspAmount(dcPaymentTransaction.getPspAmount());
        authorizedTransaction.setPspCurrency(dcPaymentTransaction.getPspCurrency());
        authorizedTransaction.setAttributes(dcPaymentTransaction.getAttributes());
        authorizedTransaction.setModifiedDate(new Date());

        return dcPaymentTransactionRepository.save(authorizedTransaction);
    }

    private DCPaymentTransaction transferDeposit(final DCPaymentTransaction dcPaymentTransaction) {
        final Player player = dcPaymentTransaction.getPlayer();
        final Wallet wallet = player.getWallet();
        final Money depositAmount = dcPaymentTransaction.getAmount();
        wallet.setMoneyBalance(wallet.getMoneyBalance().add(depositAmount));
        increaseAccumulatedDeposits(wallet, dcPaymentTransaction);
        walletRepository.save(wallet);

        // if there is an authorize then give deposit bonus
        if (dcPaymentTransaction.getAuthorizationCode() != null) {
            useDepositBonus(player, depositAmount, dcPaymentTransaction.getBonusCode());
        }

        handleDepositPromotions(player, depositAmount);

        logger.info("Player {} deposited {}", player.getId(), depositAmount);
        return dcPaymentTransaction;
    }

    private void increaseAccumulatedDeposits(final Wallet wallet, final DCPaymentTransaction dcPaymentTransaction) {
        logger.info("Player {} accumulated deposits value was increased by {} euros due to successful transfer (deposit) {}",
                    wallet.getPlayer().getId(), dcPaymentTransaction.getAmount().getEuroValueInBigDecimal(), dcPaymentTransaction.getTransactionId());

        wallet.setAccumulatedDeposit(wallet.getAccumulatedDeposit().add(dcPaymentTransaction.getAmount()));
        walletRepository.save(wallet);
    }

    private void useDepositBonus(final Player player, final Money amount, @Nullable final String bonusIdString) {
        if (bonusIdString == null) {
            return;
        }

        final Long bonusId;
        try {
            bonusId = Long.parseLong(bonusIdString);
        } catch (final NumberFormatException e) {
            logger.error("Could not convert deposit bonus code {} to bonus id to be used by Player {}", bonusIdString, player.getId());
            return;
        }

        final Bonus bonus = bonusService.getBonus(bonusId);
        if (bonus == null) {
            logger.error("Could not find deposit bonus wit id {} to be used by Player {}", bonusId, player.getId());
            return;
        }

        final PlayerCriteria playerCriteria = new PlayerCriteria();
        playerCriteria.setAmount(amount);

        // if deposit bonus is level deposit bonus then grant it first
        if (bonus.isLevelDepositBonus(player)) {
            bonusService.grantBonusToPlayer(player, bonus);
        }

        bonusService.useBonus(player, bonus, playerCriteria);
    }

    private void handleDepositPromotions(final Player player, final Money depositAmount) {
        final PlayerCriteria playerCriteria = new PlayerCriteria();
        playerCriteria.setAmount(depositAmount);
        promotionService.assignPromotions(player, PromotionTrigger.DEPOSIT, playerCriteria);
        final List<Bonus> availableBonuses = bonusService.getAvailableBonuses(player, TriggerEvent.DEPOSIT);
        bonusService.useBonuses(player, availableBonuses, playerCriteria);
    }

    private DCPaymentTransaction transferWithdraw(final DCPaymentTransaction dcPaymentTransaction, final Money amount) {
        final Wallet wallet = dcPaymentTransaction.getPlayer().getWallet();
        increaseAccumulatedWithdrawal(wallet, amount);

        if (dcPaymentTransaction.getAuthorizationCode() != null) {
            finalizeWithdrawalFromReservedBalance(wallet, amount);
        } else {
            // not previously authorized
            finalizeWithdrawFromMoneyBalance(wallet, amount);
        }
        voidReservedBonusMoneyOnWithdrawal(dcPaymentTransaction);

        logger.info("Player {} withdrew {}", wallet.getPlayer(), amount);
        return dcPaymentTransaction;
    }

    private void increaseAccumulatedWithdrawal(final Wallet wallet, final Money amount) {
        logger.info("Player {} accumulated withdrawal value was increased by {} euros due to successful transfer (withdraw)",
                    wallet.getPlayer().getId(), amount.getEuroValueInBigDecimal());

        wallet.setAccumulatedWithdrawal(wallet.getAccumulatedWithdrawal().add(amount));
        walletRepository.save(wallet);
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
            logger.info("Removed {} from player {} reserved balance, now {}", amount, wallet.getPlayer().getId(), wallet.getReservedBalance());
        }

        walletRepository.save(wallet);
    }

    private void voidReservedBonusMoneyOnWithdrawal(final DCPaymentTransaction dcPaymentTransaction) {
        final Player player = dcPaymentTransaction.getPlayer();
        logger.info("Player {} successful withdraw voided all reserved bonus money of player-bonuses linked to transfer (withdraw) transaction {} and those are " +
                    "reserved from previous awaiting withdrawals.", player.getId(), dcPaymentTransaction.getId());

        final BonusStatus toStatus = BonusStatus.VOIDED;
        final List<PlayerBonus> playerBonuses = bonusService.getPlayerBonusesTiedToPayment(player, dcPaymentTransaction);
        final List<PlayerBonus> olderPlayerBonuses = bonusService.getOldReservedPlayerBonuses(player, dcPaymentTransaction.getCreatedDate());
        playerBonuses.addAll(olderPlayerBonuses);

        final Date now = new Date();
        for (final PlayerBonus playerBonus : playerBonuses) {
            // Only touch unfinished bonuses
            if (!playerBonus.getStatus().isCompleted()) {
                if (playerBonus.getStatus() != BonusStatus.RESERVED) {
                    logger.error("Player {} Player-bonus {} has invalid status {} when trying to void, modified to {}.",
                                 player.getId(), playerBonus.getId(), playerBonus.getStatus(), toStatus);
                }
                playerBonus.setDcPaymentTransaction(dcPaymentTransaction); // updates old reserved player bonuses
                playerBonus.setStatus(toStatus);
                playerBonus.setCompletionDate(now);
                bonusService.updatePlayerBonus(playerBonus);
                logger.info("Player {} Player-bonus {} status changed to {} for transfer (withdraw) transaction {} .", player.getId(), playerBonus.getId(), toStatus,
                            dcPaymentTransaction.getId());
            }
        }
    }

    private void finalizeWithdrawFromMoneyBalance(final Wallet wallet, final Money amount) {
        wallet.setMoneyBalance(wallet.getMoneyBalance().subtract(amount));
        if (wallet.getMoneyBalance().isNegative()) {
            logger.error("Player {} money balance has negative amount {}", wallet.getPlayer().getId(), amount);
            alertBackOffice();
        }
        walletRepository.save(wallet);
    }

    @Override
    public DCPaymentTransaction cancel(final String playerIdString, final DCPaymentTransaction dcPaymentTransaction) {
        final DCPaymentTransaction authorizedTransaction = dcPaymentTransactionRepository.findByTransactionId(dcPaymentTransaction.getTransactionId());
        if (authorizedTransaction != null && authorizedTransaction.getDcEventType() == CANCEL) {
            // Already cancelled
            return authorizedTransaction;
        }

        if (authorizedTransaction == null) {
            logger.error("No authorized transaction found for payment {}", dcPaymentTransaction);
            throw new DCInvalidAuthorizationCodeException(playerIdString, dcPaymentTransaction.getAuthorizationCode());
        }

        if (dcPaymentTransaction.getAuthorizationCode() == null || !dcPaymentTransaction.getAuthorizationCode().equals(authorizedTransaction.getAuthorizationCode())) {
            logger.error("Invalid authorization code for payment {}", dcPaymentTransaction);
            throw new DCInvalidAuthorizationCodeException(playerIdString, dcPaymentTransaction.getAuthorizationCode());
        }

        final DCPaymentTransaction updatedTransaction = updateCancelTransaction(authorizedTransaction, dcPaymentTransaction);

        if (dcPaymentTransaction.getAmount().isGreaterOrEqualThan(Money.ZERO)) {
            return dcPaymentTransaction;
        } else {
            return cancelWithdraw(dcPaymentTransaction.getAmount().abs(), updatedTransaction);
        }
    }

    private DCPaymentTransaction updateCancelTransaction(final DCPaymentTransaction authorizedTransaction, final DCPaymentTransaction dcPaymentTransaction) {
        authorizedTransaction.setStatusCode(dcPaymentTransaction.getStatusCode());
        authorizedTransaction.setPspStatusCode(dcPaymentTransaction.getPspStatusCode());
        authorizedTransaction.setDcEventType(CANCEL);
        authorizedTransaction.setStatusCode(dcPaymentTransaction.getStatusCode());
        authorizedTransaction.setModifiedDate(new Date());
        return dcPaymentTransactionRepository.save(authorizedTransaction);
    }

    private DCPaymentTransaction cancelWithdraw(final Money amount, final DCPaymentTransaction linkedAuthorize) {
        final Player player = linkedAuthorize.getPlayer();

        rollbackWithdrawalFromReservedBalance(player.getWallet(), amount);
        logger.info("CANCEL (withdraw) releases all reserved bonus money of player-bonuses linked to withdraw transaction {} for Player {}",
                    linkedAuthorize.getId(), player.getId());
        releaseBonusMoneyOnAbortedWithdrawal(player, linkedAuthorize);

        logger.info("Withdrawal with id  {} was canceled, {} euros moved from reserved balance to money balance for Player {}",
                    linkedAuthorize.getId(), amount.getEuroValueInBigDecimal(), player.getId());
        return linkedAuthorize;
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

    private void releaseBonusMoneyOnAbortedWithdrawal(final Player player, final DCPaymentTransaction dcPaymentTransaction) {
        final BonusStatus toStatus = BonusStatus.INACTIVE;

        final List<PlayerBonus> playerBonuses = bonusService.getPlayerBonusesTiedToPayment(player, dcPaymentTransaction);
        final DCPaymentTransaction nextAwaitingWithdrawal = findNextAwaitingWithdrawal(player, dcPaymentTransaction.getCreatedDate());

        final Date now = new Date();
        for (final PlayerBonus playerBonus : playerBonuses) {
            // Only touch unfinished bonuses
            if (!playerBonus.getStatus().isCompleted()) {
                if (playerBonus.getStatus() != BonusStatus.RESERVED) {
                    logger.error("Player {} Player-bonus {} has invalid status {} when trying to void, modified to {}.",
                                 player.getId(), playerBonus.getId(), playerBonus.getStatus(), toStatus);
                }

                if (nextAwaitingWithdrawal != null) {
                    playerBonus.setDcPaymentTransaction(nextAwaitingWithdrawal);
                    logger.info("Player {} Player-bonus {} payment transaction changed to {} for authorize (withdraw) transaction {} .", player.getId(),
                                playerBonus.getId(), nextAwaitingWithdrawal.getId(), dcPaymentTransaction.getId());
                } else {
                    playerBonus.setStatus(toStatus);
                    logger.info("Player {} Player-bonus {} status changed to {} for authorize (withdraw) transaction {} .", player.getId(), playerBonus.getId(), toStatus,
                                dcPaymentTransaction.getId());
                }

                playerBonus.setCompletionDate(now);
                bonusService.updatePlayerBonus(playerBonus);
            }
        }

        bonusService.getActivePlayerBonus(player);
    }

    @Nullable
    private DCPaymentTransaction findNextAwaitingWithdrawal(final Player player, final Date from) {
        final List<DCPaymentTransaction> dcPaymentTransactions = getAwaitingWithdrawalsAfter(player, from);
        DCPaymentTransaction nextAwaitingWithdrawal = null;
        if (dcPaymentTransactions.size() >= 1) {
            nextAwaitingWithdrawal = dcPaymentTransactions.get(0);
        }
        return nextAwaitingWithdrawal;
    }

    private List<DCPaymentTransaction> getAwaitingWithdrawalsAfter(final Player player, final Date from) {
        final List<DCPaymentTransaction> transactions = dcPaymentTransactionRepository.findWithdrawRequestsAfter(player, AUTHORIZE, Money.ZERO, from);
        final List<DCPaymentTransaction> awaitingWithdrawals = new ArrayList<>();
        for (final DCPaymentTransaction dcPaymentTransaction : transactions) {
            if (dcPaymentTransactionRepository.findComplementaryRequestsAfter(player, getComplementaryEvents(), Money.ZERO,
                                                                              dcPaymentTransaction.getCreatedDate(),
                                                                              dcPaymentTransaction.getAuthorizationCode()).isEmpty()) {
                awaitingWithdrawals.add(dcPaymentTransaction);
            }
        }

        return awaitingWithdrawals;
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public DCPaymentTransaction getStatus(final String transactionId) {
        return dcPaymentTransactionRepository.findByTransactionId(transactionId);
    }

    private void alertBackOffice() {
        // TODO alert back-office
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Map<BigInteger, DCPaymentSummary> getAffiliatePlayersPaymentsSummary(final Date startDate, final Date endDate) {
        final Map<BigInteger, DCPaymentSummary> map = new HashMap<>();
        final List<Object[]> list = dcPaymentTransactionRepository.getAffiliatePlayersPaymentsSummary(startDate, endDate);
        for (final Object[] tuple : list) {
            final BigInteger playerId = (BigInteger) tuple[0];
            final Money totalDeposit = Money.getMoneyFromCents((BigDecimal) tuple[1]);
            final Money totalWithdraw = Money.getMoneyFromCents((BigDecimal) tuple[2]);
            final long numberOfTransactions = ((BigInteger) tuple[3]).longValue();

            map.put(playerId, new DCPaymentSummary(totalDeposit, totalWithdraw, numberOfTransactions));
        }
        return map;
    }

    @Override
    public Page<DCPaymentTransaction> getPayments(final Long playerId, final Date startDate, final Date endDate, final Integer page, final Integer size) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        final QSort sort = new QSort(new OrderSpecifier<>(Order.DESC, QDCPaymentTransaction.dCPaymentTransaction.createdDate));
        final PageRequest pageRequest = new PageRequest(page, size, sort);
        final BooleanExpression query = getDCPaymentTransactionsQuery(playerId, startDateTrimmed, endDateTrimmed);

        return dcPaymentTransactionRepository.findAll(query, pageRequest);
    }

    private BooleanExpression getDCPaymentTransactionsQuery(final Long playerId, final Date startDate, final Date endDate) {
        final QDCPaymentTransaction qdcPaymentTransaction = QDCPaymentTransaction.dCPaymentTransaction;
        BooleanExpression query = qdcPaymentTransaction.createdDate.between(startDate, endDate);
        if (playerId != null) {
            query = query.and(qdcPaymentTransaction.player.id.eq(playerId));
        }
        return query;
    }
}
