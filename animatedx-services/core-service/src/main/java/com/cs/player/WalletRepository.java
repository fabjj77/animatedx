package com.cs.player;

import com.cs.payment.Money;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Omid Alaepour
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>, QueryDslPredicateExecutor<Wallet> {

    @Modifying
    @Query("update Wallet w set w.accumulatedMonthlyTurnover = ?1 where w.accumulatedMoneyTurnover > ?1")
    Integer setAccumulatedMontyTurnoverToZero(final Money zero);

    @Modifying
    @Query("update Wallet w set w.accumulatedDailyBet = ?1, w.accumulatedDailyLoss = ?1 where w.accumulatedDailyBet > ?1 or w.accumulatedDailyLoss > ?1")
    Integer resetAccumulateDailyBetsAndLosses(final Money zero);

    @Modifying
    @Query("update Wallet w set w.accumulatedWeeklyBet = ?1, w.accumulatedWeeklyLoss = ?1 where w.accumulatedWeeklyBet > ?1 or w.accumulatedWeeklyLoss > ?1")
    Integer resetAccumulateWeeklyBetsAndLosses(final Money zero);

    @Modifying
    @Query("update Wallet w set w.accumulatedMonthlyBet = ?1, w.accumulatedMonthlyLoss = ?1 where w.accumulatedMonthlyBet > ?1 or w.accumulatedMonthlyLoss > ?1")
    Integer resetAccumulateMonthlyBetsAndLosses(final Money zero);
}
