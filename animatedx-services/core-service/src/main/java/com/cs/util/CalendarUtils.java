package com.cs.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Joakim Gottzén
 */
public final class CalendarUtils {
    public static final int MONTHS_IN_YEAR = 12;
    public static final int DAYS_IN_WEEK = 7;
    public static final int MILLISECOND_IN_A_SECOND = 1000;
    public static final int SECONDS_IN_AN_HOUR = 3600;
    public static final int SECONDS_IN_A_DAY = 86400;
    public static final int MINUTES_IN_AN_HOUR = 60;
    public static final int MILLISECOND_IN_AN_HOUR = 3600000;

    /**
     * This is a singleton.
     */
    private CalendarUtils() {
    }

    /**
     * Adjusts the given calendar to the start of the period as indicated by the given field. This delegates to startOfDay, -Week, -Month, -Year as appropriate.
     *
     * @param calendar the calendar to adjust.
     * @param field    the period to adjust, allowed are Calendar.DAY_OF_MONTH, -.MONTH, -.WEEK and -.YEAR.
     *
     * @throws IllegalArgumentException if the {@code field} is not supported.
     */
    public static void startOf(final Calendar calendar, final int field)
            throws IllegalArgumentException {
        switch (field) {
            case Calendar.DAY_OF_MONTH:
                startOfDay(calendar);
                break;
            case Calendar.MONTH:
                startOfMonth(calendar);
                break;
            case Calendar.WEEK_OF_YEAR:
                startOfWeek(calendar);
                break;
            case Calendar.YEAR:
                startOfYear(calendar);
                break;
            default:
                throw new IllegalArgumentException("unsupported field: " + field);
        }
    }

    /**
     * Adjust the given calendar to the first millisecond of the current day, that is all time fields cleared.
     *
     * @param calendar calendar to adjust.
     */
    public static void startOfDay(final Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.getTimeInMillis();
    }

    /**
     * Adjust the given calendar to the first millisecond of the given date, that is all time fields cleared. The Date of the adjusted Calendar is returned.
     *
     * @param calendar calendar to adjust.
     * @param date     the date to use.
     *
     * @return the start of the day of the given date
     */
    public static Date startOfDay(final Calendar calendar, final Date date) {
        calendar.setTime(date);
        startOfDay(calendar);
        return calendar.getTime();
    }

    /**
     * Adjust the given calendar to the last millisecond of the specified date.
     *
     * @param calendar calendar to adjust.
     */
    public static void endOfDay(final Calendar calendar) {
        calendar.add(Calendar.DATE, 1);
        startOfDay(calendar);
        calendar.add(Calendar.MILLISECOND, -1);
    }

    /**
     * Adjust the given calendar to the last millisecond of the given date. that is all time fields cleared. The Date of the adjusted Calendar is returned.
     *
     * @param calendar calendar to adjust.
     * @param date     the date to use.
     *
     * @return the end of the day of the given date
     */
    public static Date endOfDay(final Calendar calendar, final Date date) {
        calendar.setTime(date);
        endOfDay(calendar);
        return calendar.getTime();
    }

    /**
     * Adjusts the calendar to the start of the current week. That is, first day of the week with all time fields cleared.
     *
     * @param calendar the calendar to adjust.
     */
    public static void startOfWeek(final Calendar calendar) {
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        startOfDay(calendar);
    }

    /**
     * Adjusts the calendar to the start of the current month, that is, first day of the month with all time fields cleared.
     *
     * @param calendar the calendar to adjust.
     */
    public static void startOfMonth(final Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startOfDay(calendar);
    }

    /**
     * Adjusts the calendar to the end of the current month, that is the last day of the month with all time-fields at max.
     *
     * @param calendar the calendar to adjust.
     */
    public static void endOfMonth(final Calendar calendar) {
        calendar.add(Calendar.MONTH, 1);
        startOfMonth(calendar);
        calendar.add(Calendar.MILLISECOND, -1);
    }

    /**
     * Adjusts the given Calendar to the start of the year.
     *
     * @param calendar the calendar to adjust.
     */
    public static void startOfYear(final Calendar calendar) {
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        startOfMonth(calendar);
    }

    /**
     * Checks if the {@code date} is today.
     *
     * @param date the date to check.
     *
     * @return {@code true} if the {@code date} is today.
     */
    public static boolean isToday(final Date date) {
        if (date == null) {
            return false;
        }
        final GregorianCalendar today = new GregorianCalendar();
        final GregorianCalendar that = new GregorianCalendar();
        that.setTime(date);
        startOfDay(today);
        startOfDay(that);

        return today.equals(that);
    }

    /**
     * Checks whether {@code otherDate} is the day after {@code firstDate}.
     *
     * @param firstDate the first date.
     * @param otherDate the date check.
     *
     * @return {@code true} if {@code otherDate} is the day after {@code firstDate}.
     */
    public static boolean isNextDay(final Date firstDate, final Date otherDate) {
        if (firstDate == null || otherDate == null) {
            return false;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(otherDate);
        startOfDay(calendar);
        final Date normalizedOtherDate = calendar.getTime();
        calendar.setTime(firstDate);
        startOfDay(calendar);
        calendar.add(Calendar.DATE, 1);
        final Date normalizedFirstDate = calendar.getTime();

        return normalizedFirstDate.equals(normalizedOtherDate);
    }

    /**
     * Returns a boolean indicating if the calendar is set to the start of a period  as defined by the given field. This delegates to startOfDay, -Week, -Month, -Year as
     * appropriate. The calendar is unchanged.
     *
     * @param calendar the calendar to test.
     * @param field    the period to adjust, allowed are Calendar.DAY_OF_MONTH, -.MONTH, -.WEEK and YEAR.
     *
     * @return {@code true} if the calendar is the start of the field.
     *
     * @throws IllegalArgumentException if the {@code field} is not supported.
     */
    public static boolean isStartOf(final Calendar calendar, final int field)
            throws IllegalArgumentException {
        switch (field) {
            case Calendar.DAY_OF_MONTH:
                return isStartOfDay(calendar);
            case Calendar.WEEK_OF_YEAR:
                return isStartOfWeek(calendar);
            case Calendar.MONTH:
                return isStartOfMonth(calendar);
            case Calendar.YEAR:
                return isStartOfYear(calendar);
            default:
                throw new IllegalArgumentException("unsupported field: " + field);
        }
    }

    /**
     * Returns a boolean indicating if the given calendar represents the start of a day (in the calendar's time zone). The calendar is unchanged.
     *
     * @param calendar the calendar to check.
     *
     * @return {@code true} if the calendar's time is the start of the day, {@code false} otherwise.
     */
    public static boolean isStartOfDay(final Calendar calendar) {
        final Calendar temp = (Calendar) calendar.clone();
        temp.add(Calendar.MILLISECOND, -1);
        return temp.get(Calendar.DATE) != calendar.get(Calendar.DATE);
    }

    /**
     * Returns a boolean indicating if the given calendar represents the end of a day (in the calendar's time zone). The calendar is unchanged.
     *
     * @param calendar the calendar to check.
     *
     * @return true if the calendar's time is the end of the day, false otherwise.
     */
    public static boolean isEndOfDay(final Calendar calendar) {
        final Calendar temp = (Calendar) calendar.clone();
        temp.add(Calendar.MILLISECOND, 1);
        return temp.get(Calendar.DATE) != calendar.get(Calendar.DATE);
    }

    /**
     * Returns a boolean indicating if the given calendar represents the start of a month (in the calendar's time zone). Returns true, if the time is the start of the
     * first day of the month, false otherwise. The calendar is unchanged.
     *
     * @param calendar the calendar to check.
     *
     * @return true if the calendar's time is the start of the first day of the month, false otherwise.
     */
    public static boolean isStartOfWeek(final Calendar calendar) {
        final Calendar temp = (Calendar) calendar.clone();
        temp.add(Calendar.MILLISECOND, -1);
        return temp.get(Calendar.WEEK_OF_YEAR) != calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Returns a boolean indicating if the given calendar represents the start of a month (in the calendar's time zone). Returns true, if the time is the start of the
     * first day of the month, false otherwise. The calendar is unchanged.
     *
     * @param calendar the calendar to check.
     *
     * @return true if the calendar's time is the start of the first day of the month, false otherwise.
     */
    public static boolean isStartOfMonth(final Calendar calendar) {
        final Calendar temp = (Calendar) calendar.clone();
        temp.add(Calendar.MILLISECOND, -1);
        return temp.get(Calendar.MONTH) != calendar.get(Calendar.MONTH);
    }

    /**
     * Returns a boolean indicating if the given calendar represents the start of a year (in the calendar's time zone). Returns true, if the time is the start of the
     * first day of the year, false otherwise. The calendar is unchanged.
     *
     * @param calendar the calendar to check.
     *
     * @return true if the calendar's time is the start of the first day of the month, false otherwise.
     */
    public static boolean isStartOfYear(final Calendar calendar) {
        final Calendar temp = (Calendar) calendar.clone();
        temp.add(Calendar.MILLISECOND, -1);
        return temp.get(Calendar.YEAR) != calendar.get(Calendar.YEAR);
    }

    /**
     * Checks the given dates for being equal.
     *
     * @param current one of the dates to compare
     * @param date    the other of the dates to compare
     *
     * @return {@code true} if the two given dates both are {@code null} or both are not {@code null} and equal, {@code false} otherwise.
     */
    public static boolean areEqual(final Date current, final Date date) {
        return (date == null) && (current == null) || date != null && date.equals(current);
    }

    /**
     * Returns a boolean indicating whether the given Date is the same day as the day in the calendar. Calendar and date are unchanged by the check.
     *
     * @param today the Calendar representing a date, must not be null.
     * @param now   the date to compare to, must not be null
     *
     * @return true if the calendar and date represent the same day in the given calendar.
     */
    public static boolean isSameDay(final Date today, final Date now) {
        final Calendar temp = Calendar.getInstance();
        temp.setTime(today);
        startOfDay(temp);
        final Date start = temp.getTime();
        temp.setTime(now);
        startOfDay(temp);
        return start.equals(temp.getTime());
    }

    /**
     * Returns a boolean indicating whether the given Date is in the same period as the Date in the calendar, as defined by the calendar field. Calendar and date are
     * unchanged by the check.
     *
     * @param today the Calendar representing a date, must not be null.
     * @param now   the date to compare to, must not be null
     * @param field the field to test.
     *
     * @return true if the calendar and date represent the same day in the given calendar.
     */
    public static boolean isSame(final Calendar today, final Date now, final int field) {
        final Calendar temp = (Calendar) today.clone();
        startOf(temp, field);
        final Date start = temp.getTime();
        temp.setTime(now);
        startOf(temp, field);
        return start.equals(temp.getTime());
    }
}
