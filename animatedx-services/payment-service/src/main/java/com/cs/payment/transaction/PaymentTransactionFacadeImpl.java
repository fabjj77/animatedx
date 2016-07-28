package com.cs.payment.transaction;

import com.cs.payment.Currency;
import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.PaymentStatus;
import com.cs.payment.PaymentSummary;
import com.cs.payment.PaymentTransaction;
import com.cs.payment.Provider;
import com.cs.payment.QPaymentTransaction;
import com.cs.payment.adyen.AdyenTransactionNotification;
import com.cs.player.Player;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cs.payment.EventCode.AUTHORISATION;
import static com.cs.payment.EventCode.REFUND;
import static com.cs.payment.EventCode.REFUND_WITH_DATA;
import static com.cs.payment.PaymentStatus.CHARGEBACKED;
import static com.cs.payment.PaymentStatus.REFUNDED;
import static com.cs.payment.PaymentStatus.SUCCESS;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class PaymentTransactionFacadeImpl implements PaymentTransactionFacade {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    public PaymentTransactionFacadeImpl(final PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Nullable
    @Override
    public PaymentTransaction getPayment(@Nonnull final Long id) {
        return paymentTransactionRepository.findOne(id);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Nullable
    @Override
    public PaymentTransaction getPayment(final String providerReference) {
        return paymentTransactionRepository.findByProviderReference(providerReference);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Nullable
    @Override
    public PaymentTransaction getPayment(final String providerReference, final EventCode eventCode, final PaymentStatus paymentStatus) {
        return paymentTransactionRepository.findByProviderReferenceAndEventCodeAndPaymentStatus(providerReference, eventCode, paymentStatus);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Nullable
    @Override
    public List<PaymentTransaction> getAwaitingWithdrawalsAfter(final Player player, final Date date) {
        return paymentTransactionRepository.findAwaitingPaymentsAfter(player, REFUND_WITH_DATA, PaymentStatus.awaitingStatuses(), date);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public PaymentTransaction getSucceededOrRefundedPayment(final String providerReference) {
        PaymentTransaction paymentTransaction =
                paymentTransactionRepository.findByProviderReferenceAndEventCodeAndPaymentStatus(providerReference, AUTHORISATION, SUCCESS);
        if (paymentTransaction == null) {
            paymentTransaction = paymentTransactionRepository.findByProviderReferenceAndEventCodeAndPaymentStatus(providerReference, AUTHORISATION, REFUNDED);
        }
        return paymentTransaction;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<PaymentTransaction> getPayments(final Player player, final EventCode eventCode, final PaymentStatus paymentStatus) {
        return paymentTransactionRepository.findByPlayerAndEventCodeAndPaymentStatus(player, eventCode, paymentStatus);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Page<PaymentTransaction> getPayments(final Long playerId, final EventCode eventCode, final PaymentStatus paymentStatus, final Date startDate,
                                                final Date endDate, final Integer page, final Integer size) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        final QSort sort = new QSort(new OrderSpecifier<>(Order.DESC, QPaymentTransaction.paymentTransaction.createdDate));
        final PageRequest pageRequest = new PageRequest(page, size, sort);
        final BooleanExpression query = getPaymentTransactionsQuery(playerId, eventCode, paymentStatus, startDateTrimmed, endDateTrimmed);
        return paymentTransactionRepository.findAll(query, pageRequest);
    }

    private BooleanExpression getPaymentTransactionsQuery(final Long playerId, final EventCode eventCode, final PaymentStatus paymentStatus, final Date startDate,
                                                          final Date endDate) {
        final Calendar instance = Calendar.getInstance();
        final Date startDateTrimmed = CalendarUtils.startOfDay(instance, startDate);
        final Date endDateTrimmed = CalendarUtils.endOfDay(instance, endDate);

        final QPaymentTransaction qPaymentTransaction = QPaymentTransaction.paymentTransaction;
        BooleanExpression query = qPaymentTransaction.createdDate.between(startDateTrimmed, endDateTrimmed);
        if (playerId != null) {
            query = query.and(qPaymentTransaction.player.id.eq(playerId));
        }
        if (paymentStatus != null && eventCode == null) {
            query = query.and(qPaymentTransaction.paymentStatus.eq(paymentStatus));
        } else if (eventCode == AUTHORISATION) {
            query = query.and(qPaymentTransaction.eventCode.eq(AUTHORISATION)).and(qPaymentTransaction.paymentStatus.in(SUCCESS, REFUNDED, CHARGEBACKED));
        } else if (eventCode == REFUND_WITH_DATA) {
            query = query.and(qPaymentTransaction.eventCode.eq(REFUND_WITH_DATA)).and(qPaymentTransaction.paymentStatus.eq(SUCCESS));
        } else if (eventCode == REFUND) {
            query = query.and(qPaymentTransaction.eventCode.eq(REFUND)).and(qPaymentTransaction.paymentStatus.eq(SUCCESS));
        }
        return query;
    }


    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Iterable<PaymentTransaction> getPayments(final Long playerId, final EventCode eventCode, final PaymentStatus paymentStatus, final Date startDate,
                                                    final Date endDate) {
        final BooleanExpression query = getPaymentTransactionsQuery(playerId, eventCode, paymentStatus, startDate, endDate);
        return paymentTransactionRepository.findAll(query);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Page<PaymentTransaction> getAwaitingWithdrawals(final Integer page, final Integer size) {
        final QPaymentTransaction payment = QPaymentTransaction.paymentTransaction;
        final PageRequest pageRequest = new PageRequest(page, size, new QSort(new OrderSpecifier<>(Order.DESC, payment.createdDate)));
        final BooleanExpression predicate = payment.eventCode.eq(EventCode.REFUND_WITH_DATA).and(payment.paymentStatus.eq(PaymentStatus.AWAITING_APPROVAL));
        return paymentTransactionRepository.findAll(predicate, pageRequest);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Iterable<PaymentTransaction> getPaymentsAwaitingApproval(final List<String> originalReferences) {
        final QPaymentTransaction qTransaction = QPaymentTransaction.paymentTransaction;
        final BooleanExpression predicate = qTransaction.originalReference.in(originalReferences).and(qTransaction.paymentStatus.eq(PaymentStatus.AWAITING_APPROVAL));
        return paymentTransactionRepository.findAll(predicate);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public List<Integer> countDifferentPaymentMethod(final Player player, final EventCode eventCode, final PaymentStatus paymentStatus, final Date startDate,
                                                     final Date endDate) {
        return paymentTransactionRepository.countDifferentPaymentMethod(player, EventCode.AUTHORISATION, PaymentStatus.SUCCESS, startDate, endDate);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Long countRepeatedDeposits(final Player player, final Date startDate) {
        final QPaymentTransaction payment = QPaymentTransaction.paymentTransaction;
        final BooleanExpression predicate = payment.player.eq(player).and(payment.eventCode.eq(EventCode.AUTHORISATION))
                .and(payment.paymentStatus.eq(PaymentStatus.SUCCESS)).and(payment.createdDate.after(startDate));
        return paymentTransactionRepository.count(predicate);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Long countPaymentsAboveAmount(final Player player, final Money amount, final EventCode eventCode, final Date startDate) {
        final BooleanExpression predicate = paymentByPlayerAndAmountCreatedDateFrom(player, amount, EventCode.AUTHORISATION, startDate);
        return paymentTransactionRepository.count(predicate);
    }

    private BooleanExpression paymentByPlayerAndAmountCreatedDateFrom(final Player player, final Money amount, final EventCode eventCode, final Date from) {
        final QPaymentTransaction payment = QPaymentTransaction.paymentTransaction;
        return payment.player.eq(player).and(payment.eventCode.eq(eventCode)).and(payment.paymentStatus.eq(PaymentStatus.SUCCESS))
                .and(payment.amount.goe(amount)).and(payment.createdDate.after(from));
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Money getPaymentsAmount(final Long playerId, final EventCode eventCode, final Date startDate, final Date endDate) {
        final BooleanExpression query = getPaymentTransactionsQuery(playerId, eventCode, PaymentStatus.SUCCESS, startDate, endDate);
        Money sum = Money.ZERO;

        for (final PaymentTransaction paymentTransaction : paymentTransactionRepository.findAll(query)) {
            sum = sum.add(paymentTransaction.getAmount());
        }

        return sum;
    }

    @Override
    public PaymentTransaction insertPayment(final Player player, final AdyenTransactionNotification transaction, final Provider provider) {
        final PaymentTransaction payment =
                new PaymentTransaction(player, provider, transaction.getEventDate(), transaction.getCurrency(), transaction.getProviderReference(),
                                       transaction.getOriginalReference(), transaction.getPaymentStatus(), transaction.getPaymentMethod(), transaction.getOperations(),
                                       transaction.getReason(), transaction.getAmount(), transaction.getEventCode(), transaction.getMerchantReference(), new Date());
        return paymentTransactionRepository.save(payment);
    }

    @Override
    public PaymentTransaction insertWithdrawal(final Player player, final String providerReference, final String withdrawReference, final Money amount,
                                               final Currency currency, final String paymentMethod, final PaymentStatus status, final Provider provider) {
        return paymentTransactionRepository.save(new PaymentTransaction(player, provider, new Date(), currency, providerReference, status, paymentMethod, amount,
                                                                        EventCode.REFUND_WITH_DATA, withdrawReference));
    }

    @Override
    public PaymentTransaction insertPayment(final Player player, final String providerReference, final String originalProviderReference, final Money amount,
                                            final Currency currency, @Nullable final String paymentMethod, final PaymentStatus status, final Provider provider,
                                            final EventCode eventCode) {
        return paymentTransactionRepository.save(new PaymentTransaction(player, provider, new Date(), currency, providerReference, originalProviderReference, status,
                                                                        paymentMethod, amount, eventCode));
    }

    @Override
    public PaymentTransaction savePayment(final PaymentTransaction transaction) {
        return paymentTransactionRepository.save(transaction);
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Map<BigInteger, Pair<Money, Money>> getPlayersDepositWithdraw(final Date startDateTrimmed, final Date endDateTrimmed) {
        final Map<BigInteger, Pair<Money, Money>> map = new HashMap<>();
        final List<Object[]> list = paymentTransactionRepository.getPlayersDepositAndWithdrawBetween(startDateTrimmed, endDateTrimmed);
        for (final Object[] tuple : list) {
            map.put((BigInteger)tuple[0], new Pair<>(new Money((BigDecimal) tuple[1]), new Money((BigDecimal) tuple[2])));
        }
        return map;
    }

    @Transactional(propagation = SUPPORTS, readOnly = true)
    @Override
    public Map<BigInteger, PaymentSummary> getAffiliatePlayersPaymentSummary(final Date startDateTrimmed, final Date endDateTrimmed) {
        final Map<BigInteger, PaymentSummary> map = new HashMap<>();
        final List<Object[]> list = paymentTransactionRepository.getAffiliatePlayersPaymentSummary(startDateTrimmed, endDateTrimmed);
        for (final Object[] tuple : list) {
            final BigInteger playerId = (BigInteger) tuple[0];
            final Money totalDeposit = Money.getMoneyFromCents((BigDecimal) tuple[1]);
            final Money totalWithdraw = Money.getMoneyFromCents((BigDecimal) tuple[2]);

            map.put(playerId, new PaymentSummary(totalDeposit, totalWithdraw));
        }
        return map;
    }
}
