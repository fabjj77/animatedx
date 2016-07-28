package com.cs.promotion;

import com.google.common.base.Objects;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import java.io.Serializable;

import static javax.persistence.EnumType.STRING;

/**
 * @author Hadi Movaghar
 */
@Embeddable
public class TimeCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "required_time_unit")
    @Nullable
    @Enumerated(STRING)
    private RecurringTimeUnit recurringTimeUnit;

    @Column(name = "required_recurring_time")
    @Nullable
    private Integer recurringTime;

    public TimeCriteria() {
    }

    @Nullable
    public RecurringTimeUnit getRecurringTimeUnit() {
        return recurringTimeUnit;
    }

    public void setRecurringTimeUnit(@Nullable final RecurringTimeUnit recurringTimeUnit) {
        this.recurringTimeUnit = recurringTimeUnit;
    }

    @Nullable
    public Integer getRecurringTime() {
        return recurringTime;
    }

    public void setRecurringTime(@Nullable final Integer recurringTime) {
        this.recurringTime = recurringTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TimeCriteria that = (TimeCriteria) o;

        return Objects.equal(recurringTimeUnit, that.recurringTimeUnit) &&
               Objects.equal(recurringTime, that.recurringTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(recurringTimeUnit, recurringTime);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(recurringTimeUnit)
                .addValue(recurringTime)
                .toString();
    }
}
