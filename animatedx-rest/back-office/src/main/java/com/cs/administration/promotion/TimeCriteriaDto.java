package com.cs.administration.promotion;

import com.cs.promotion.RecurringTimeUnit;
import com.cs.promotion.TimeCriteria;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.persistence.EnumType.STRING;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class TimeCriteriaDto {
    @XmlElement
    @Nonnull
    @Enumerated(STRING)
    private RecurringTimeUnit recurringTimeUnit;

    @XmlElement
    @Nonnull
    @Min(value = 1, message = "timeCriteriaDto.recurringTime.min")
    private Integer recurringTime;

    public TimeCriteriaDto() {
    }

    public TimeCriteriaDto(@Nullable final TimeCriteria timeCriteria) {
        if (timeCriteria != null) {
            recurringTime = timeCriteria.getRecurringTime();
            recurringTimeUnit = timeCriteria.getRecurringTimeUnit();
        }
    }

    public TimeCriteria asTimeCriteria() {
        final TimeCriteria timeCriteria = new TimeCriteria();
        timeCriteria.setRecurringTimeUnit(recurringTimeUnit);
        timeCriteria.setRecurringTime(recurringTime);
        return timeCriteria;
    }

    @Nonnull
    public RecurringTimeUnit getRecurringTimeUnit() {
        return recurringTimeUnit;
    }

    public void setRecurringTimeUnit(@Nonnull final RecurringTimeUnit recurringTimeUnit) {
        this.recurringTimeUnit = recurringTimeUnit;
    }

    @Nonnull
    public Integer getRecurringTime() {
        return recurringTime;
    }

    public void setRecurringTime(@Nonnull final Integer recurringTime) {
        this.recurringTime = recurringTime;
    }
}
