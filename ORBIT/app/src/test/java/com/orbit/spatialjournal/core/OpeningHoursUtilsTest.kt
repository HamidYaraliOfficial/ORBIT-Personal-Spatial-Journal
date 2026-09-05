package com.orbit.spatialjournal.core

import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.core.model.OpeningHoursEntry
import com.orbit.spatialjournal.core.util.OpeningHoursUtils
import org.junit.Test
import java.time.LocalDateTime

class OpeningHoursUtilsTest {

    private val mondayToFriday9to18 = (1..5).map {
        OpeningHoursEntry(isoDayOfWeek = it, openMinuteOfDay = 9 * 60, closeMinuteOfDay = 18 * 60)
    }
    private val weekendClosed = listOf(6, 7).map { OpeningHoursEntry(isoDayOfWeek = it, closedAllDay = true) }
    private val weekHours = mondayToFriday9to18 + weekendClosed

    @Test
    fun `open during business hours reports correct time until close`() {
        // Monday 2024-01-01, 14:00
        val now = LocalDateTime.of(2024, 1, 1, 14, 0)
        val status = OpeningHoursUtils.computeStatus(weekHours, now)
        assertThat(status.isOpenNow).isTrue()
        assertThat(status.minutesUntilNextChange).isEqualTo(4 * 60L) // closes at 18:00
    }

    @Test
    fun `closed before opening reports time until open`() {
        val now = LocalDateTime.of(2024, 1, 1, 7, 0) // Monday 07:00
        val status = OpeningHoursUtils.computeStatus(weekHours, now)
        assertThat(status.isOpenNow).isFalse()
        assertThat(status.minutesUntilNextChange).isEqualTo(2 * 60L) // opens at 09:00
    }

    @Test
    fun `closed on weekend finds next monday opening`() {
        val saturday = LocalDateTime.of(2024, 1, 6, 10, 0) // Saturday
        val status = OpeningHoursUtils.computeStatus(weekHours, saturday)
        assertThat(status.isOpenNow).isFalse()
        assertThat(status.minutesUntilNextChange).isNotNull()
    }

    @Test
    fun `segment crossing midnight stays open past 00 00`() {
        val barHours = listOf(OpeningHoursEntry(isoDayOfWeek = 5, openMinuteOfDay = 20 * 60, closeMinuteOfDay = 2 * 60)) // Fri 20:00 - Sat 02:00
        val fridayNight = LocalDateTime.of(2024, 1, 5, 23, 30) // Friday 23:30
        val status = OpeningHoursUtils.computeStatus(barHours, fridayNight)
        assertThat(status.isOpenNow).isTrue()
    }

    @Test
    fun `segment crossing midnight is still open just after midnight`() {
        val barHours = listOf(OpeningHoursEntry(isoDayOfWeek = 5, openMinuteOfDay = 20 * 60, closeMinuteOfDay = 2 * 60))
        val saturdayEarly = LocalDateTime.of(2024, 1, 6, 1, 0) // Saturday 01:00, still within Friday's segment
        val status = OpeningHoursUtils.computeStatus(barHours, saturdayEarly)
        assertThat(status.isOpenNow).isTrue()
        assertThat(status.minutesUntilNextChange).isEqualTo(60L)
    }

    @Test
    fun `empty hours returns not open with no next change`() {
        val status = OpeningHoursUtils.computeStatus(emptyList(), LocalDateTime.now())
        assertThat(status.isOpenNow).isFalse()
        assertThat(status.nextChangeAtEpochMillis).isNull()
    }
}
