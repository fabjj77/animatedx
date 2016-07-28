package com.cs.payment.credit;

import com.cs.payment.CreditTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Omid Alaepour
 */
@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long>, QueryDslPredicateExecutor<CreditTransaction> {

    @Query(value = "select player_id, sum(real_money), sum(bonus_money), sum(credit) " +
                   "FROM credit_transactions " +
                   "where transaction_type = 'CONVERSION' and created_date >= ?1 and created_date <= ?2 and player_id in (select player_id from players_affiliates) " +
                   "group by player_id", nativeQuery = true)
    List<Object[]> getAffiliatePlayersConvertedCreditsSummary(Date startDate, Date endDate);
}
