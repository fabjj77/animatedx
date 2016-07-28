package com.cs.player;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class LimitDto {
    @XmlElement
    @Nonnull
    @NotNull(message = "limitDto.limitationType.notNull")
    private LimitationType limitationType;

    @XmlElement
    @Nonnull
    @NotNull(message = "limitDto.timeUnit.notNull")
    private TimeUnit timeUnit;

    @XmlElement
    @Nonnull
    @DecimalMin(value = "0", message = "limitDto.amount.min")
    private BigDecimal amount;

    @XmlElement
    @Nullable
    private Integer percent;

    @XmlElement
    private Date expirationDate;

    @SuppressWarnings("UnusedDeclaration")
    public LimitDto() {
    }

    public LimitDto(@Nonnull final LimitationType limitationType, @Nonnull final TimeUnit timeUnit, @Nonnull final BigDecimal amount, @Nullable final Integer percent,
                    @Nullable final Date expirationDate) {
        this.limitationType = limitationType;
        this.timeUnit = timeUnit;
        this.amount = amount;
        this.percent = percent;
        this.expirationDate = expirationDate;
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
    public BigDecimal getAmount() {
        return amount;
    }

    @Nullable
    public Integer getPercent() {
        return percent;
    }

    public void setPercent(@Nullable final Integer percent) {
        this.percent = percent;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LimitDto that = (LimitDto) o;

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
