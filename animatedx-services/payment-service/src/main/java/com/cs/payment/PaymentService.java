package com.cs.payment;

import com.cs.bonus.PlayerBonus;
import com.cs.payment.adyen.AdyenTransactionNotification;
import com.cs.persistence.Country;
import com.cs.player.Player;
import com.cs.player.Wallet;
import com.cs.user.User;

import org.springframework.data.domain.Page;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Hadi Movaghar
 */
public interface PaymentService {

    @Nullable
    PaymentTransaction processDeposit(final AdyenTransactionNotification transaction);

    void processRefund(final AdyenTransactionNotification transaction);

    void processCancel(final AdyenTransactionNotification transaction);

    void processRefundReversed(final AdyenTransactionNotification transaction);

    void processPaymentCancellation(final AdyenTransactionNotification transaction);

    void processPaymentCapture(final AdyenTransactionNotification transaction);

    void defendChargeBack(final AdyenTransactionNotification transaction);

    void processChargeBack(final AdyenTransactionNotification transaction);

    void processChargeBackReversed(final AdyenTransactionNotification transaction);

    void processReports(final AdyenTransactionNotification transaction);

    @Nullable
    PaymentTransaction processWithdraw(final AdyenTransactionNotification transaction);

    void processDeclinedWithdraw(final AdyenTransactionNotification notification);

    boolean isNotificationDuplicated(final String providerReference, final EventCode eventCode, final PaymentStatus paymentStatus);

    void refundDeposit(final String originalReference, final Long playerId, final User user);

    void cancelDeposit(final String originalReference, final Long playerId, final User user);

    void withdraw(final Long playerId, final String payoutTargetReference, final String paymentMethod, final Money amount, final String password);

    Page<PaymentTransaction> getAwaitingWithdrawals(final Integer page, final Integer size);

    Map<String, String> confirmWithdrawals(final List<String> withdrawReferences, final User user);

    Map<String, String> declineWithdrawals(final List<String> withdrawReferences, final User user);

    List<PayoutDetailReference> sendPayoutInformationRequest(final Player player);

    List<PaymentTransaction> getPaymentsContainingOperation(final Long playerId, final Operation operation);

    DepositDetails prepareDeposit(final Long playerId, final Money amount, final String skin, final String paymentMethodName, @Nullable final Long bonusId);

    Wallet updatePlayerWallet(Long id, User user, Wallet wallet);

    PaymentTransaction createBonusConversionDeposit(final Player player, final Money amount);

    void storeBankAccountForWithdrawal(final Player player, final String iban, final String bic, final String bankName, final String ownerName);

    void backOfficeStoreBankAccountForWithdrawal(final User user, final Long playerId, final String iban, final String bic, final String bankName,
                                                 final String ownerName);

    boolean isPlayerAllowedToStoreBankAccount(final List<PayoutDetailReference> references);

    List<PaymentMethod> getAllowedPaymentMethodsByCountry(final Country country);

    Page<Wallet> getWalletsWeeklyTurnover(final Integer page, final Integer size);

    boolean giveCashback(final Wallet wallet);

    void resetMonthlyTurnover();

    PlayerBonus updatePlayerBonus(final Long playerBonusId, final User user, final Money requestedBonusBalance);

    PlayerBonus createAdjustableBonus(final Player player, final User user, final Money amount, final Money maxRedemptionAmount, final Date validFrom,
                                      final Date validTo);

    PaymentTransaction createBlingCityPayment(final Player player, final Money amount, final String providerReference, final EventCode eventCode);
}
