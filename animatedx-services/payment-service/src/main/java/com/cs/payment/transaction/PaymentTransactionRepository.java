package com.cs.payment.transaction;

import com.cs.payment.EventCode;
import com.cs.payment.PaymentStatus;
import com.cs.payment.PaymentTransaction;
import com.cs.player.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Omid Alaepour
 */
@Repository
interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long>, QueryDslPredicateExecutor<PaymentTransaction> {

    PaymentTransaction findByProviderReference(final String providerReference);

    PaymentTransaction findByProviderReferenceAndEventCodeAndPaymentStatus(final String providerReference, final EventCode eventCode, final PaymentStatus paymentStatus);

    List<PaymentTransaction> findByPlayerAndEventCodeAndPaymentStatus(final Player player, final EventCode eventCode, final PaymentStatus paymentStatus);

    @Query("select pt from PaymentTransaction pt where pt.player = ?1 and pt.eventCode = ?2 and pt.paymentStatus in ?3 and pt.createdDate > ?4 order by pt.createdDate " +
           "asc")
    List<PaymentTransaction> findAwaitingPaymentsAfter(final Player player, final EventCode eventCode, final Collection<PaymentStatus> paymentStatuses, final Date date);

    @Query(value = "select count(p) from PaymentTransaction p where p.player = ?1 and p.eventCode = ?2 and p.paymentStatus = ?3 " +
                   "and p.createdDate between ?4 and ?5 group by p.paymentMethod")
    List<Integer> countDifferentPaymentMethod(final Player player, final EventCode eventCode, final PaymentStatus paymentStatus, final Date startDate,
                                              final Date endDate);

    @Query(value = "select player_id, sum(deposit), sum(withdraw) from (select player_id, event_code, if(event_code='AUTHORISATION',  sum(amount), 0) deposit, " +
                   "if(event_code='REFUND_WITH_DATA', sum(amount), 0) withdraw from payment_transactions where status = 'SUCCESS' and event_code in ('AUTHORISATION' , " +
                   "'REFUND_WITH_DATA') and created_date >= ?1 and created_date <= ?2 group by player_id, event_code) a group by player_id",
           nativeQuery = true)
    List<Object[]> getPlayersDepositAndWithdrawBetween(final Date startDate, final Date endDate);

    @Query(value = "select player_id, sum(deposit), sum(withdraw) " +
                   "from" +
                   "(select player_id," +
                   "if(event_code in ('BACK_OFFICE_MONEY_DEPOSIT', 'CASHBACK', 'BONUS_CONVERSION'), sum(amount), 0) deposit," +
                   "if(event_code='BACK_OFFICE_MONEY_WITHDRAW', sum(amount), 0) withdraw " +
                   "from payment_transactions " +
                   "where player_id in (select player_id from players_affiliates)" +
                   "and status = 'SUCCESS' and event_code in ('CASHBACK', 'BONUS_CONVERSION' , 'BACK_OFFICE_MONEY_DEPOSIT', 'BACK_OFFICE_MONEY_WITHDRAW')" +
                   "and created_date >= ?1 and created_date <= ?2 " +
                   "group by player_id, event_code) a " +
                   "group by player_id",
           nativeQuery = true)
    List<Object[]> getAffiliatePlayersPaymentSummary(final Date startDate, final Date endDate);
}
