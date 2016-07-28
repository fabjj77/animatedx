package com.cs.payment.devcode;

import com.cs.payment.DCEventType;
import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.Money;
import com.cs.player.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
@Repository
interface DCPaymentTransactionRepository extends JpaRepository<DCPaymentTransaction, Long>, QueryDslPredicateExecutor<DCPaymentTransaction> {

    DCPaymentTransaction findByTransactionId(final String transactionId);

    @Query("select pt from DCPaymentTransaction pt where pt.player = ?1 and pt.dcEventType = ?2 and pt.amount < ?3 and pt.createdDate > ?4 order by pt.createdDate asc")
    List<DCPaymentTransaction> findWithdrawRequestsAfter(final Player player, final DCEventType dcEventType, final Money zero, final Date date);

    @Query("select pt from DCPaymentTransaction pt where pt.player = ?1 and pt.dcEventType in ?2 and pt.amount < ?3 and pt.createdDate > ?4 and pt.authorizationCode = " +
           "?5 or pt.authorizationCode = null")
    List<DCPaymentTransaction> findComplementaryRequestsAfter(final Player player, final Collection<DCEventType> dcEventTypes, final Money zero, final Date date,
                                                              final String authorizationCode);

    @Query(value = "select player_id, sum(case when amount > 0 then amount else 0 end), sum(case when amount < 0 then amount else 0 end), count(id) " +
                   "from devcode_transactions dt " +
                   "where success = '1' and event_type = 'TRANSFER' and created_date >= ?1 and created_date <= ?2 and player_id in (select player_id from " +
                   "players_affiliates)" +
                   "group by player_id", nativeQuery = true)
    List<Object[]> getAffiliatePlayersPaymentsSummary(final Date startDate, final Date endDate);
}

