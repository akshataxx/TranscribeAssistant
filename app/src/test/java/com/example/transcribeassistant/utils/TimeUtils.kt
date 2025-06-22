package com.example.transcribeassistant.utils

import org.junit.Test
import org.junit.Assert.assertEquals
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class TimeUtilsTest {

    private fun daysAgo(days: Long): Instant {
        return LocalDate.now().minusDays(days)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
    }

    private fun monthsAgo(months: Long): Instant {
        return LocalDate.now().minusMonths(months)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
    }

    private fun yearsAgo(years: Long): Instant {
        return LocalDate.now().minusYears(years)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
    }

    @Test
    fun returnsDaysAgoForLessThan30Days() {
        assertEquals("1 day ago", TimeUtils.timeAgo(daysAgo(1)))
        assertEquals("15 days ago", TimeUtils.timeAgo(daysAgo(15)))
        assertEquals("29 days ago", TimeUtils.timeAgo(daysAgo(29)))
    }

    @Test
    fun returnsMonthsAgoFor30DaysToLessThan12Months() {
        assertEquals("1 month ago", TimeUtils.timeAgo(monthsAgo(1)))
        assertEquals("6 months ago", TimeUtils.timeAgo(monthsAgo(6)))
        assertEquals("11 months ago", TimeUtils.timeAgo(monthsAgo(11)))
    }

    @Test
    fun returnsYearsAgoFor12MonthsAndBeyond() {
        assertEquals("1 year ago", TimeUtils.timeAgo(yearsAgo(1)))
        assertEquals("5 years ago", TimeUtils.timeAgo(yearsAgo(5)))
    }


    @Test
    fun formatsSecondsUnderOneMinute() {
        assertEquals("00:30", TimeUtils.formatDuration(30))
        assertEquals("00:59", TimeUtils.formatDuration(59))
    }

    @Test
    fun formatsExactlyOneMinute() {
        assertEquals("01:00", TimeUtils.formatDuration(60))
    }

    @Test
    fun formatsJustBelowTwoMinutes() {
        assertEquals("01:59", TimeUtils.formatDuration(119))
    }

    @Test
    fun formatsTwoMinutesAndBeyond() {
        assertEquals("02:00", TimeUtils.formatDuration(120))
        assertEquals("05:15", TimeUtils.formatDuration(315))
    }
}
