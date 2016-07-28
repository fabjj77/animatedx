package com.cs.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public abstract class BasicWalletDto {

    @XmlElement
    @Nullable
    private BigDecimal moneyBalance;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedCashBack;

    @XmlElement
    @Nonnull
    private BigDecimal reservedBalance;

    @XmlElement
    @Nullable
    private Integer creditsBalance;

    @XmlElement
    @Nonnull
    private BigDecimal levelProgress;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedWeeklyTurnover;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedMonthlyTurnover;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedDailyLoss;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedWeeklyLoss;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedMonthlyLoss;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedDailyBet;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedWeeklyBet;

    @XmlElement
    @Nonnull
    private BigDecimal accumulatedMonthlyBet;

    @XmlElement
    @Nullable
    private BigDecimal nextLevelPercentage;

    @SuppressWarnings("UnusedDeclaration")
    protected BasicWalletDto() {
    }

    protected BasicWalletDto(final Wallet wallet) {
        moneyBalance = wallet.getMoneyBalance().getEuroValueInBigDecimal();
        accumulatedCashBack = wallet.getAccumulatedCashback().getEuroValueInBigDecimal();
        creditsBalance = wallet.getCreditsBalance();
        levelProgress = wallet.getLevelProgress().getEuroValueInBigDecimal();
        nextLevelPercentage = wallet.getNextLevelPercentage();
        reservedBalance = wallet.getReservedBalance().getEuroValueInBigDecimal();
        accumulatedWeeklyTurnover = wallet.getAccumulatedWeeklyTurnover().getEuroValueInBigDecimal();
        accumulatedMonthlyTurnover = wallet.getAccumulatedMonthlyTurnover().getEuroValueInBigDecimal();
        accumulatedDailyLoss = wallet.getAccumulatedDailyLoss().getEuroValueInBigDecimal();
        accumulatedWeeklyLoss = wallet.getAccumulatedWeeklyLoss().getEuroValueInBigDecimal();
        accumulatedMonthlyLoss = wallet.getAccumulatedMonthlyLoss().getEuroValueInBigDecimal();
        accumulatedDailyBet = wallet.getAccumulatedDailyBet().getEuroValueInBigDecimal();
        accumulatedWeeklyBet = wallet.getAccumulatedWeeklyBet().getEuroValueInBigDecimal();
        accumulatedMonthlyBet = wallet.getAccumulatedMonthlyBet().getEuroValueInBigDecimal();
    }

    @Nullable
    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    @Nullable
    public Integer getCreditsBalance() {
        return creditsBalance;
    }
}
