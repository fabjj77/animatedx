package com.cs.payment.transaction;

import com.cs.payment.Currency;
import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.PaymentStatus;
import com.cs.payment.PaymentSummary;
import com.cs.payment.PaymentTransaction;
import com.cs.payment.Provider;
import com.cs.payment.adyen.AdyenTransactionNotification;
import com.cs.player.Player;
import com.cs.util.Pair;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This is used as the facade for all database access for the {@code PaymentTransaction} entity, to keep the transaction demarcation in proper order.
 *
 * @author Hadi Movaghar
 */
public interface PaymentTransactionFacade {

    PaymentTransaction getPayment(@Nonnull final Long id);

    PaymentTransaction getPayment(final String providerReference);

    PaymentTransaction getPayment(final String providerReference, final EventCode eventCode, final PaymentStatus paymentStatus);

    List<PaymentTransaction> getAwaitingWithdrawalsAfter(final Player player, final Date date);

    PaymentTransaction getSucceededOrRefundedPayment(final String providerReference);

    List<PaymentTransaction> getPayments(final Player player, final EventCode eventCode, final PaymentStatus paymentStatus);

    Page<PaymentTransaction> getPayments(final Long playerId, final EventCode eventCode, final PaymentStatus paymentStatus, final Date startDate, final Date endDate,
                                         final Integer page, final Integer size);

    Iterable<PaymentTransaction> getPayments(final Long playerId, final EventCode eventCode, final PaymentStatus paymentStatus, final Date startDate,
                                             final Date endDate);

    Page<PaymentTransaction> getAwaitingWithdrawals(final Integer page, final Integer size);

    Iterable<PaymentTransaction> getPaymentsAwaitingApproval(final List<String> originalReferences);

    List<Integer> countDifferentPaymentMethod(final Player player, final EventCode eventCode, final PaymentStatus paymentStatus, final Date startDate,
                                              final Date endDate);

    Long countRepeatedDeposits(final Player player, final Date startDate);

    Long countPaymentsAboveAmount(final Player player, final Money amount, final EventCode eventCode, final Date startDate);

    Money getPaymentsAmount(final Long playerId, final EventCode eventCode, final Date startDate, final Date endDate);

    PaymentTransaction insertPayment(final Player player, final AdyenTransactionNotification adyenTransactionNotification, final Provider provider);

    PaymentTransaction insertPayment(final Player player, final String providerReference, final String originalProviderReference, final Money amount,
                                     final Currency currency, @Nullable final String paymentMethod, final PaymentStatus status, final Provider provider,
                                     final EventCode eventCode);

    PaymentTransaction insertWithdrawal(final Player player, final String providerReference, final String withdrawReference, final Money amount, final Currency currency,
                                        final String paymentMethod, final PaymentStatus status, final Provider provider);

    PaymentTransaction savePayment(PaymentTransaction transaction);

    Map<BigInteger, Pair<Money, Money>> getPlayersDepositWithdraw(final Date startDate, final Date endDate);

    Map<BigInteger, PaymentSummary> getAffiliatePlayersPaymentSummary(final Date startDate, final Date endDate);
}
