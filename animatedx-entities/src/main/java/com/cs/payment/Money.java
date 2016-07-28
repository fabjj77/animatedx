package com.cs.payment;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Hadi Movaghar
 */
public class Money implements Comparable<Money>, Serializable {

    private static final long serialVersionUID = 1L;

    private final Long cents;
    // Lazy BigDecimal representation of the value in full euros
    private BigDecimal euros;

    public static final Money ZERO = new Money();
    public static final Money ONE = new Money(1L);
    public static final Money TEN = new Money(10L);

    private Money() {
        cents = 0L;
    }

    /**
     * The parameter to send to this constructor is supposed to be cents and not euros
     */
    public Money(final Long cents) {
        this.cents = cents;
    }

    /**
     * The parameter to send to this constructor is supposed to be cents and not euros
     */
    public Money(final Double cents) {
        this.cents = Math.round(cents * 100);
    }

    /**
     * The parameter to send to this constructor is supposed to be cents and not euros
     */
    public Money(final BigDecimal cents) {
        this.cents = Math.round(cents.doubleValue() * 100);
    }

    /**
     * The parameter to send to this constructor is supposed to be euros and not cents
     */
    public Money(final String euros) {
        this(new BigDecimal(euros));
    }

    public static Money getMoneyInCentsFromEuro(final BigDecimal value) {
        return new Money(value);
    }

    public static Money getMoneyFromCents(final BigDecimal value) {
        return new Money(value.longValue());
    }

    public static Money valueOf(final Long cents) {
        return new Money(cents);
    }

    public Long getCents() {
        return cents;
    }

    public Money add(final Money money) {
        return new Money(cents + money.cents);
    }

    public Money subtract(final Money money) {
        return new Money(cents - money.cents);
    }

    public Money multiply(final Double value) {
        return valueOf((long) (cents * value));
    }

    // TODO review this used in bonus fraction
    public Money multiply(final BigDecimal fraction) {
        return new Money(Math.round(cents * fraction.doubleValue()));
    }

    public boolean isLessThan(final Money money) {
        return cents < money.cents;
    }

    public boolean isGreaterThan(final Money money) {
        return cents > money.cents;
    }

    public boolean isLessOrEqualThan(final Money money) {
        return cents <= money.cents;
    }

    public boolean isGreaterOrEqualThan(final Money money) {
        return cents >= money.cents;
    }

    public Money abs() {
        return new Money(Math.abs(cents));
    }

    public double doubleValue() {
        return cents;
    }

    public double getEuroValueInDouble() {
        return cents / 100.0;
    }

    public Money calculateBonusMoney(final Money bonusAmount, final Money moneyAmount) {
        if (bonusAmount.cents + moneyAmount.cents == 0) {
            return Money.ZERO;
        }

        if (moneyAmount.isZero()) {
            return new Money(cents);
        }

        final double fraction = bonusAmount.cents * 1.00D / (moneyAmount.cents + bonusAmount.cents);
        return new Money(Math.round(cents * fraction));
    }

    public BigDecimal getEuroValueInBigDecimal() {
        if (euros == null) {
            euros = new BigDecimal(cents).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
        }
        return euros;
    }

    public Money min(final Money val) {
        return compareTo(val) <= 0 ? this : val;
    }

    public boolean isNegative() {
        return compareTo(ZERO) == -1;
    }

    public boolean isZero() {
        return compareTo(ZERO) == 0;
    }

    public boolean isPositive() {
        return compareTo(ZERO) == 1;
    }

    @Override
    public int compareTo(@Nonnull final Money input) {
        if (cents < input.cents) {
            return -1;
        } else if (cents > input.cents) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Money that = (Money) o;

        return Objects.equal(cents, that.cents);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cents);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                // Use the double representation for efficiency
                .addValue(getEuroValueInDouble())
                .toString();
    }
}
