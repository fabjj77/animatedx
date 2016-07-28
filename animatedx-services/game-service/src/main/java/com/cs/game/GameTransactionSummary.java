package com.cs.game;

import com.cs.payment.Money;

import javax.annotation.Nonnull;

/**
 * @author Hadi Movaghar
 */
public class GameTransactionSummary {

    @Nonnull
    private final Money totalMoneyWin;

    @Nonnull
    private final Money totalMoneyBet;

    @Nonnull
    private final Money totalBonusWin;

    @Nonnull
    private final Money totalBonusBet;

    @Nonnull
    private final Long numberOfTransactions;

    public GameTransactionSummary() {
        totalMoneyWin = Money.ZERO;
        totalMoneyBet = Money.ZERO;
        totalBonusWin = Money.ZERO;
        totalBonusBet = Money.ZERO;
        numberOfTransactions = 0L;
    }

    public GameTransactionSummary(@Nonnull final Money totalMoneyWin, @Nonnull final Money totalMoneyBet, @Nonnull final Money totalBonusWin,
                                  @Nonnull final Money totalBonusBet, @Nonnull final Long numberOfTransactions) {
        this.totalMoneyWin = totalMoneyWin;
        this.totalMoneyBet = totalMoneyBet;
        this.totalBonusWin = totalBonusWin;
        this.totalBonusBet = totalBonusBet;
        this.numberOfTransactions = numberOfTransactions;
    }

    @Nonnull
    public Double getTotalMoneyBet() {
        return totalMoneyBet.getEuroValueInDouble();
    }

    @Nonnull
    public Long getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public Double getGrossRevenue() {
        return totalMoneyWin.subtract(totalMoneyBet).getEuroValueInDouble();
    }
}
