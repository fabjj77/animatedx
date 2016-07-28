package com.cs.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * @author Joakim Gottzén
 */
@SuppressWarnings({"ConstantConditions", "MagicConstant"})
public class CalendarUtilsTest {
    private Calendar todayGerman;
    private Calendar todayUS;
    private Calendar midJune;

    @Before
    public void setUp()
            throws Exception {
        todayGerman = Calendar.getInstance(Locale.GERMAN);
        todayUS = Calendar.getInstance(Locale.US);
        midJune = Calendar.getInstance(Locale.GERMAN);
        midJune.set(Calendar.DAY_OF_MONTH, 14);
        midJune.set(Calendar.MONTH, Calendar.JUNE);
        midJune.getTimeInMillis();
    }

    @Test
    public void testSameByDayField() {
        final Date now = todayGerman.getTime();
        CalendarUtils.endOfDay(todayGerman);
        final Date end = todayGerman.getTime();

        assertThat(CalendarUtils.isSame(todayGerman, now, Calendar.DAY_OF_MONTH), is(true));
        assertThat(end, is(equalTo(todayGerman.getTime())));

        todayGerman.add(Calendar.DAY_OF_MONTH, 1);
        assertThat(CalendarUtils.isSame(todayGerman, now, Calendar.DAY_OF_MONTH), is(false));
    }

    @Test
    public void testStartOfYearField() {
        CalendarUtils.startOf(midJune, Calendar.YEAR);

        assertThat(CalendarUtils.isStartOfYear(midJune), is(true));
        assertThat(CalendarUtils.isStartOf(midJune, Calendar.YEAR), is(true));
    }

    @Test
    public void testStartOfDayField() {
        CalendarUtils.startOf(midJune, Calendar.DAY_OF_MONTH);

        assertThat(CalendarUtils.isStartOfDay(midJune), is(true));
        assertThat(CalendarUtils.isStartOf(midJune, Calendar.DAY_OF_MONTH), is(true));
    }

    @Test
    public void testStartOfMonthField() {
        CalendarUtils.startOf(midJune, Calendar.MONTH);

        assertThat(CalendarUtils.isStartOfMonth(midJune), is(true));
        assertThat(CalendarUtils.isStartOf(midJune, Calendar.MONTH), is(true));
    }

    @Test
    public void testStartOfWeekField() {
        CalendarUtils.startOf(midJune, Calendar.WEEK_OF_YEAR);

        assertThat(CalendarUtils.isStartOfWeek(midJune), is(true));
        assertThat(CalendarUtils.isStartOf(midJune, Calendar.WEEK_OF_YEAR), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartOfInvalidField() {
        CalendarUtils.startOf(midJune, Calendar.ERA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsStartOfInvalidField() {
        CalendarUtils.isStartOf(midJune, Calendar.ERA);
    }

    @Test
    public void testStartOfYear() {
        final int year = midJune.get(Calendar.YEAR);
        CalendarUtils.startOfYear(midJune);

        assertThat(midJune.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(CalendarUtils.isStartOfMonth(midJune), is(true));
        assertThat(midJune.get(Calendar.YEAR), is(year));
    }

    @Test
    public void testIsStartOfYear() {
        CalendarUtils.startOfYear(midJune);

        assertThat(CalendarUtils.isStartOfYear(midJune), is(true));

        midJune.add(Calendar.MILLISECOND, -1);
        final Date changed = midJune.getTime();

        assertThat(CalendarUtils.isStartOfYear(midJune), is(false));
        assertThat("calendar must be unchanged", midJune.getTime(), is(equalTo(changed)));
    }

    /**
     * test to characterize start of week behaviour is we are in a calendar with minimalDays > 1.
     */
    @Test
    public void testWeekOfYearInDecember() {
        // a date before the first week of the month
        todayGerman.set(2007, Calendar.DECEMBER, 1);
        final Date firstOfDecember = todayGerman.getTime();
        CalendarUtils.startOfWeek(todayGerman);
        todayGerman.setTime(firstOfDecember);
        CalendarUtils.endOfMonth(todayGerman);

        // we crossed the year boundary
        assertThat(todayGerman.get(Calendar.WEEK_OF_YEAR), is(1));
    }

    /**
     * test to characterize start of week behaviour is we are in a calendar with minimalDays > 1.
     */
    @Test
    public void testStartOfWeekBeforeFirstWeekOfMonth() {
        // a date before the first week of the month
        todayGerman.set(2008, Calendar.FEBRUARY, 1);

        assertThat(todayGerman.get(Calendar.WEEK_OF_MONTH), is(0));

        CalendarUtils.startOfWeek(todayGerman);

        assertThat(todayGerman.get(Calendar.MONTH), is(Calendar.JANUARY));
    }

    /**
     * test to characterize start of week behaviour is we are in a calendar with minimalDays > 1.
     */
    @Test
    public void testStartOfWeekBeforeFirstWeekOfYear() {
        // a date before the first week of the year
        todayGerman.set(2010, Calendar.JANUARY, 1);

        assertThat(todayGerman.get(Calendar.WEEK_OF_MONTH), is(0));
        assertThat(todayGerman.get(Calendar.WEEK_OF_YEAR), is(53));

        CalendarUtils.startOfWeek(todayGerman);

        assertThat(todayGerman.get(Calendar.MONTH), is(Calendar.DECEMBER));
    }

    @Test
    public void testSameDay() {
        final Date now = todayGerman.getTime();
        CalendarUtils.endOfDay(todayGerman);
        final Date end = todayGerman.getTime();

        assertThat(CalendarUtils.isSameDay(todayGerman.getTime(), now), is(true));
        assertThat(todayGerman.getTime(), is(end));

        todayGerman.add(Calendar.DAY_OF_MONTH, 1);

        assertThat(CalendarUtils.isSameDay(todayGerman.getTime(), now), is(false));
    }

    @Test
    public void testIsToday() {
        final Date now = todayGerman.getTime();
        CalendarUtils.endOfDay(todayGerman);
        final Date end = todayGerman.getTime();

        assertThat(CalendarUtils.isToday(now), is(true));
        assertThat(CalendarUtils.isToday(null), is(false));
        assertThat(todayGerman.getTime(), is(end));

        todayGerman.add(Calendar.DAY_OF_MONTH, 1);

        assertThat(CalendarUtils.isSameDay(todayGerman.getTime(), now), is(false));
    }

    @Test
    public void isNextDay() {
        final Date today = todayGerman.getTime();
        todayGerman.add(Calendar.DATE, 1);
        final Date tomorrow = todayGerman.getTime();

        assertThat(CalendarUtils.isNextDay(today, tomorrow), is(true));

        todayGerman.add(Calendar.DATE, 1);
        final Date notTomorrow = todayGerman.getTime();

        assertThat(CalendarUtils.isNextDay(today, notTomorrow), is(false));
    }

    @Test
    public void testAreEqual() {
        assertThat(CalendarUtils.areEqual(null, null), is(true));

        final Calendar calendar = Calendar.getInstance();
        final Date now = calendar.getTime();

        assertThat(CalendarUtils.areEqual(now, null), is(false));
        assertThat(CalendarUtils.areEqual(null, now), is(false));
        assertThat(CalendarUtils.areEqual(now, now), is(true));

        calendar.add(Calendar.HOUR_OF_DAY, 1);

        assertThat(CalendarUtils.areEqual(now, calendar.getTime()), is(false));
    }

    @Test
    public void testIsStartOfWeek() {
        CalendarUtils.startOfWeek(midJune);

        assertThat(CalendarUtils.isStartOfWeek(midJune), is(true));

        midJune.add(Calendar.MILLISECOND, -1);
        final Date date = midJune.getTime();

        assertThat(CalendarUtils.isStartOfWeek(midJune), is(false));
        assertThat("calendar must be unchanged", midJune.getTime(), is(date));
    }

    @Test
    public void testStartOfWeekFromMiddle() {
        final int day = Calendar.WEDNESDAY;
        todayGerman.set(Calendar.DAY_OF_WEEK, day);
        final int week = todayGerman.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayGerman);

        assertThat(todayGerman.get(Calendar.WEEK_OF_YEAR), is(week));
        assertThat(todayGerman.getFirstDayOfWeek(), is(todayGerman.get(Calendar.DAY_OF_WEEK)));
    }

    @Test
    public void testStartOfWeekFromFirst() {
        todayGerman.set(Calendar.DAY_OF_WEEK, todayGerman.getFirstDayOfWeek());
        final int week = todayGerman.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayGerman);

        assertThat(todayGerman.get(Calendar.WEEK_OF_YEAR), is(week));
        assertThat(todayGerman.getFirstDayOfWeek(), is(todayGerman.get(Calendar.DAY_OF_WEEK)));
    }

    @Test
    public void testStartOfWeekFromLast() {
        todayGerman.set(Calendar.DAY_OF_WEEK, todayGerman.getFirstDayOfWeek());
        final int week = todayGerman.get(Calendar.WEEK_OF_YEAR);
        todayGerman.add(Calendar.DATE, 6);
        // sanity

        assertThat(todayGerman.get(Calendar.WEEK_OF_YEAR), is(week));

        CalendarUtils.startOfWeek(todayGerman);

        assertThat(todayGerman.get(Calendar.WEEK_OF_YEAR), is(week));
        assertThat(todayGerman.getFirstDayOfWeek(), is(todayGerman.get(Calendar.DAY_OF_WEEK)));
    }

    @Test
    public void testStartOfWeekFromFirstJan() {
        todayGerman.set(Calendar.MONTH, Calendar.JANUARY);
        todayGerman.set(Calendar.DATE, 1);
        if (todayGerman.get(Calendar.DAY_OF_WEEK) == todayGerman.getFirstDayOfWeek()) {
            todayGerman.add(Calendar.YEAR, -1);
        }
        final int week = todayGerman.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayGerman);

        assertThat(todayGerman.get(Calendar.WEEK_OF_YEAR), is(week));
        assertThat(todayGerman.getFirstDayOfWeek(), is(todayGerman.get(Calendar.DAY_OF_WEEK)));
    }

    @Test
    public void testStartOfWeekUS() {
        final int day = Calendar.WEDNESDAY;

        assertThat(day, is(not(todayUS.getFirstDayOfWeek())));

        final int week = todayUS.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayUS);

        assertThat(todayUS.get(Calendar.WEEK_OF_YEAR), is(week));
    }

    @Test
    public void testIsStartOfMonth() {
        // want to be in the middle of a year
        final int month = 5;
        todayGerman.set(Calendar.MONTH, month);
        CalendarUtils.startOfMonth(todayGerman);
        final Date start = todayGerman.getTime();

        assertThat(CalendarUtils.isStartOfMonth(todayGerman), is(true));
        // sanity: calendar must not be changed
        assertThat(todayGerman.getTime(), is(start));

        todayGerman.add(Calendar.MILLISECOND, 1);

        assertThat(CalendarUtils.isStartOfMonth(todayGerman), is(false));
    }

    @Test
    public void testEndOfMonth() {
        // want to be in the middle of a year
        final int month = midJune.get(Calendar.MONTH);
        CalendarUtils.endOfMonth(midJune);

        assertThat(midJune.get(Calendar.MONTH), is(month));

        midJune.add(Calendar.MILLISECOND, 1);

        assertThat(midJune.get(Calendar.MONTH), is(month + 1));
    }

    @Test
    public void testStartOfMonth() {
        // want to be in the middle of a year
        final int month = midJune.get(Calendar.MONTH);
        CalendarUtils.startOfMonth(midJune);

        assertThat(midJune.get(Calendar.MONTH), is(month));

        midJune.add(Calendar.MILLISECOND, -1);

        assertThat(midJune.get(Calendar.MONTH), is(month - 1));
    }

    @Test
    public void testEndOfDay() {
        // want to be in the middle of a month
        final int day = midJune.get(Calendar.DAY_OF_MONTH);
        CalendarUtils.endOfDay(midJune);

        assertThat(midJune.get(Calendar.DATE), is(day));

        midJune.add(Calendar.MILLISECOND, 1);

        assertThat(midJune.get(Calendar.DATE), is(day + 1));
    }

    @Test
    public void testEndOfDayWithReturn() {
        final Date date = midJune.getTime();
        final Date start = CalendarUtils.endOfDay(midJune, date);

        assertThat(CalendarUtils.isEndOfDay(midJune), is(true));
        assertThat(midJune.getTime(), is(start));
    }

    @Test
    public void testStartOfDay() {
        // want to be in the middle of a month
        final int day = midJune.get(Calendar.DAY_OF_MONTH);
        CalendarUtils.startOfDay(midJune);

        assertThat(midJune.get(Calendar.DATE), is(day));

        midJune.add(Calendar.MILLISECOND, -1);

        assertThat(midJune.get(Calendar.DATE), is(day - 1));
    }

    @Test
    public void testStartOfDayWithReturn() {
        final Date date = midJune.getTime();
        final Date start = CalendarUtils.startOfDay(midJune, date);

        assertThat(CalendarUtils.isStartOfDay(midJune), is(true));
        assertThat(midJune.getTime(), is(start));
    }

    @Test
    public void testStartOfDayUnique() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        CalendarUtils.startOfMonth(calendar);

        // sanity
        assertThat(CalendarUtils.isStartOfDay(calendar), is(true));
        assertNotStartOfDayInTimeZones(calendar, "GMT+");
        assertNotStartOfDayInTimeZones(calendar, "GMT-");
    }

    private static void assertNotStartOfDayInTimeZones(final Calendar calendar, final String id) {
        for (int i = 1; i < 13; i++) {
            calendar.setTimeZone(TimeZone.getTimeZone(id + i));
            assertThat(CalendarUtils.isStartOfDay(calendar), is(false));
        }
    }

    @Test
    public void testStartOfMonthUnique() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        CalendarUtils.startOfMonth(calendar);

        // sanity
        assertThat(CalendarUtils.isStartOfMonth(calendar), is(true));
        assertNotStartOfMonthInTimeZones(calendar, "GMT+");
        assertNotStartOfMonthInTimeZones(calendar, "GMT-");
    }

    private static void assertNotStartOfMonthInTimeZones(final Calendar calendar, final String id) {
        for (int i = 1; i < 13; i++) {
            calendar.setTimeZone(TimeZone.getTimeZone(id + i));
            assertThat(CalendarUtils.isStartOfMonth(calendar), is(false));
        }
    }

    /**
     * sanity ...
     */
    @Test
    public void testNextMonthCal() {
        todayGerman.set(Calendar.MONTH, Calendar.JANUARY);
        Date date = todayGerman.getTime();
        for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
            final int month = todayGerman.get(Calendar.MONTH);
            CalendarUtils.startOfMonth(todayGerman);

            assertThat(todayGerman.get(Calendar.MONTH), is(month));

            CalendarUtils.endOfMonth(todayGerman);

            assertThat(todayGerman.get(Calendar.MONTH), is(month));
            // restore original and add
            todayGerman.setTime(date);
            todayGerman.add(Calendar.MONTH, 1);
            date = todayGerman.getTime();

            if (i < Calendar.DECEMBER) {
                assertThat(todayGerman.get(Calendar.MONTH), is(month + 1));
            } else {
                assertThat(todayGerman.get(Calendar.MONTH), is(Calendar.JANUARY));
            }
        }
    }

    @Test
    public void testFlushedStartOfWeek() {
        CalendarUtils.startOfWeek(midJune);
        assertFlushed(midJune);
    }

    @Test
    public void testFlushedStartOfDay() {
        CalendarUtils.startOfDay(midJune);
        assertFlushed(midJune);
    }

    @Test
    public void testFlushedStartOfMonth() {
        CalendarUtils.startOfMonth(midJune);
        assertFlushed(midJune);
    }

    @Test
    public void testFlushedStartOfYear() {
        CalendarUtils.startOfYear(midJune);
        assertFlushed(midJune);
    }

    @Test
    public void testFlushedEndOfDay() {
        CalendarUtils.endOfDay(midJune);
        assertFlushed(midJune);
    }

    @Test
    public void testFlushedEndOfMonth() {
        CalendarUtils.endOfMonth(midJune);
        assertFlushed(midJune);
    }

    @Test
    public void testFlushedInitially() {
        assertFlushed(todayGerman);
        assertFlushed(todayUS);
        assertFlushed(midJune);
    }

    private static void assertFlushed(final Calendar calendar) {
        assertThat("must be flushed but was: " + calendar, isFlushed(calendar));
    }

    /**
     * Returns a boolean to indicate whether the given calendar is flushed. <p>
     * <p/>
     * The only way to guarantee a flushed state is to let client code call getTime or getTimeInMillis. See
     * <p/>
     * <a href=http://forums.java.net/jive/thread.jspa?threadID=74472&tstart=0>Despairing in Calendar</a>
     * <p/>
     * Note: this if for testing only and not entirely safe!
     *
     * @param calendar the calendar to check
     *
     * @return {@code true} if the calendar is flushed, {@code false} otherwise.
     */
    private static boolean isFlushed(final Calendar calendar) {
        return !calendar.toString().contains("time=?");
    }
}
