package com.cs.payment;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Hadi Movaghar
 */
public class PaymentSummary {

    @Nonnull
    private final Money totalDeposit;

    @Nonnull
    private final Money totalWithdraw;

    public PaymentSummary() {
        totalDeposit = Money.ZERO;
        totalWithdraw = Money.ZERO;
    }

    public PaymentSummary(@Nonnull final Money totalDeposit, @Nonnull final Money totalWithdraw) {
        this.totalDeposit = totalDeposit;
        this.totalWithdraw = totalWithdraw;
    }

    public Double getAdjustments() {
        return 0.0;
    }

    @Nullable
    public Integer getAdjustmentsTypeId() {
        return null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("totalDeposit", totalDeposit)
                .add("totalWithdraw", totalWithdraw)
                .toString();
    }
}
