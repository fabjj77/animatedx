package com.cs.player;

import java.math.BigDecimal;

/**
 * @author Hadi Movaghar
 */
public enum TimeUnit {
    DAY(1), WEEK(7), MONTH(30);

    private final int timeInDays;

    TimeUnit(final int timeInDays) {
        this.timeInDays = timeInDays;
    }

    public int getTimeValue() {
        return timeInDays;
    }

    public static BigDecimal getTimeUnitConversionValue(final TimeUnit fromUnit, final TimeUnit toUnit) {
        return BigDecimal.valueOf(toUnit.getTimeValue() * 1.0 / fromUnit.getTimeValue());
    }
}
