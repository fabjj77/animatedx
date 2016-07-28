package com.cs.payment.credit;

import com.cs.payment.Money;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;

/**
 * @author Hadi Movaghar
 */
public class CreditSummary {

    @Nonnull
    private final Money realMoney;

    @Nonnull
    private final Money bonusMoney;

    @Nonnull
    private final Integer credits;

    public CreditSummary() {
        realMoney = Money.ZERO;
        bonusMoney = Money.ZERO;
        credits = 0;
    }

    public CreditSummary(@Nonnull final Money realMoney, @Nonnull final Money bonusMoney, @Nonnull final Integer credits) {
        this.realMoney = realMoney;
        this.bonusMoney = bonusMoney;
        this.credits = credits;
    }

    @Nonnull
    public Money getRealMoney() {
        return realMoney;
    }

    @Nonnull
    public Money getBonusMoney() {
        return bonusMoney;
    }

    @Nonnull
    public Integer getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("realMoney", realMoney)
                .add("bonusMoney", bonusMoney)
                .add("credits", credits)
                .toString();
    }
}
