package com.cs.messaging.sftp;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Joakim Gottzén
 */
public class PlayerActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("UnusedDeclaration")
    public static final int CUSTOMER_PRODUCT_ID = 0;
    public static final int CASINO_PRODUCT_ID = 1;

    public static final int NORMAL_ADJUSTMENT = 1;
    @SuppressWarnings("UnusedDeclaration")
    public static final int CONTRIBUTION_ADJUSTMENT = 6;

    @Nonnull
    private final Long customerId;
    @Nonnull
    private final Date activityDate;
    @Nonnull
    private final Integer productId;
    @Nonnull
    private final Double grossRevenue;
    @Nonnull
    private final Double bonuses;
    @Nonnull
    private final Double adjustments;
    @Nonnull
    private final Double deposits;
    @Nonnull
    private final Integer blings;
    @Nonnull
    private final Double turnover;
    @Nonnull
    private final Double withdrawals;
    @Nonnull
    private final Integer transactions;
    @Nonnull
    private final Integer adjustmentTypeId;

    public PlayerActivity(@Nonnull final Long customerId, @Nonnull final Date activityDate, @Nonnull final Integer productId, @Nonnull final Double grossRevenue,
                          @Nonnull final Double bonuses, @Nonnull final Double adjustments, @Nonnull final Double deposits, @Nonnull final Integer blings,
                          @Nonnull final Double turnover, @Nonnull final Double withdrawals, @Nonnull final Integer transactions,
                          @Nullable final Integer adjustmentTypeId) {
        this.customerId = customerId;
        this.activityDate = activityDate;
        this.productId = productId;
        this.grossRevenue = grossRevenue;
        this.bonuses = bonuses;
        this.adjustments = adjustments;
        this.deposits = deposits;
        this.blings = blings;
        this.turnover = turnover;
        this.withdrawals = withdrawals;
        this.transactions = transactions;
        if (adjustmentTypeId != null) {
            this.adjustmentTypeId = adjustmentTypeId;
        } else {
            this.adjustmentTypeId = NORMAL_ADJUSTMENT;
        }
    }

    @Nonnull
    public Long getCustomerId() {
        return customerId;
    }

    @Nonnull
    public Date getActivityDate() {
        return activityDate;
    }

    @Nonnull
    public Integer getProductId() {
        return productId;
    }

    @Nonnull
    public Double getGrossRevenue() {
        return grossRevenue;
    }

    @Nonnull
    public Double getBonuses() {
        return bonuses;
    }

    @Nonnull
    public Double getAdjustments() {
        return adjustments;
    }

    @Nonnull
    public Double getDeposits() {
        return deposits;
    }

    @Nonnull
    public Integer getBlings() {
        return blings;
    }

    @Nonnull
    public Double getTurnover() {
        return turnover;
    }

    @Nonnull
    public Double getWithdrawals() {
        return withdrawals;
    }

    @Nonnull
    public Integer getTransactions() {
        return transactions;
    }

    @Nonnull
    public Integer getAdjustmentTypeId() {
        return adjustmentTypeId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlayerActivity that = (PlayerActivity) o;

        return Objects.equal(customerId, that.customerId) &&
               Objects.equal(activityDate, that.activityDate) &&
               Objects.equal(productId, that.productId) &&
               Objects.equal(grossRevenue, that.grossRevenue) &&
               Objects.equal(adjustments, that.adjustments) &&
               Objects.equal(deposits, that.deposits) &&
               Objects.equal(blings, that.blings) &&
               Objects.equal(turnover, that.turnover) &&
               Objects.equal(withdrawals, that.withdrawals) &&
               Objects.equal(transactions, that.transactions) &&
               Objects.equal(adjustmentTypeId, that.adjustmentTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerId, activityDate, productId, grossRevenue, adjustments, deposits, blings, turnover, withdrawals, transactions, adjustmentTypeId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("customerId", customerId)
                .add("activityDate", activityDate)
                .add("productId", productId)
                .add("grossRevenue", grossRevenue)
                .add("bonuses", bonuses)
                .add("adjustments", adjustments)
                .add("deposits", deposits)
                .add("blings", blings)
                .add("turnover", turnover)
                .add("withdrawals", withdrawals)
                .add("transactions", transactions)
                .add("adjustmentTypeId", adjustmentTypeId)
                .toString();
    }
}
