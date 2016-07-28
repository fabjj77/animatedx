package com.cs.player;

import com.cs.payment.Money;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * @author Hadi Movaghar
 */
public class Limit implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    @Nonnull
    private LimitationType limitationType;

    @XmlElement
    @Nonnull
    private TimeUnit timeUnit;

    @XmlElement
    @Nonnull
    private Money amount;

    public Limit() {
    }

    public Limit(@Nonnull final LimitationType limitationType, @Nonnull final TimeUnit timeUnit, @Nonnull final Money amount) {
        this.limitationType = limitationType;
        this.timeUnit = timeUnit;
        this.amount = amount;
    }

    @Nonnull
    public LimitationType getLimitationType() {
        return limitationType;
    }

    public void setLimitationType(@Nonnull final LimitationType limitationType) {
        this.limitationType = limitationType;
    }

    @Nonnull
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(@Nonnull final TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Nonnull
    public Money getAmount() {
        return amount;
    }

    public void setAmount(@Nonnull final Money amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Limit that = (Limit) o;

        return Objects.equal(limitationType, that.limitationType) &&
               Objects.equal(timeUnit, that.timeUnit) &&
               Objects.equal(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(limitationType, timeUnit, amount);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(limitationType)
                .addValue(timeUnit)
                .addValue(amount)
                .toString();
    }
}
