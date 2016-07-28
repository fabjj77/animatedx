package com.cs.payment.devcode;

import com.cs.payment.Money;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;

/**
 * @author Hadi Movaghar
 */
public class DCPaymentSummary {

    @Nonnull
    private final Money totalDeposit;

    @Nonnull
    private final Money totalWithdraw;

    @Nonnull
    private final Long numberOfTransactions;

    public DCPaymentSummary() {
        totalDeposit = Money.ZERO;
        totalWithdraw = Money.ZERO;
        numberOfTransactions = 0L;
    }

    public DCPaymentSummary(@Nonnull final Money totalDeposit, @Nonnull final Money totalWithdraw, @Nonnull final Long numberOfTransactions) {
        this.totalDeposit = totalDeposit;
        this.totalWithdraw = totalWithdraw;
        this.numberOfTransactions = numberOfTransactions;
    }

    @Nonnull
    public Money getTotalDeposit() {
        return totalDeposit;
    }

    @Nonnull
    public Money getTotalWithdraw() {
        return totalWithdraw;
    }

    @Nonnull
    public Long getNumberOfTransactions() {
        return numberOfTransactions;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("totalDeposit", totalDeposit)
                .add("totalWithdraw", totalWithdraw)
                .add("numberOfTransactions", numberOfTransactions)
                .toString();
    }
}
