package com.cs.payment.adyen;

import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.PaymentMethod;
import com.cs.payment.RecurringContract;
import com.cs.player.Player;

import com.adyen.modification.ModificationResult;
import com.adyen.payout.ModifyResponse;
import com.adyen.payout.StoreDetailResponse;
import com.adyen.payout.SubmitResponse;
import com.adyen.recurring.RecurringDetailsResult;

import javax.annotation.Nullable;

/**
 * @author Joakim Gottzén
 */
public interface AdyenService {

    SubmitResponse withdraw(final Player player, final RecurringContract recurringContract, final String targetReference, final Money amount, final Long reference)
            throws AdyenException;

    ModifyResponse confirmWithdrawal(@Nullable final String originalReference)
            throws AdyenException;

    ModifyResponse declineWithdrawal(@Nullable final String originalReference)
            throws AdyenException;

    StoreDetailResponse storeBankAccount(final Player player, final String ownerName, final String bankName, final String iban, final String bic,
                                         final RecurringContract recurringContract)
            throws AdyenException;

    RecurringDetailsResult getWithdrawalDetails(final Player player, final RecurringContract recurringContract)
            throws AdyenException;

    ModificationResult refundDeposit(final Player player, final String originalReference, final Money amount, final EventCode eventCode, final Long reference)
            throws AdyenException;

    ModificationResult cancelDeposit(final Player player, final String originalReference, final Money amount, final EventCode eventCode, final Long reference)
            throws AdyenException;

    String createAdyenDepositUrl(final Money amount, final String skin, final PaymentMethod paymentMethodType, final Player player, final String merchantReference);
}
