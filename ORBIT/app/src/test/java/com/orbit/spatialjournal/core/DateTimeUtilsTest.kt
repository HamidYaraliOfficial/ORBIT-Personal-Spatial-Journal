package com.orbit.spatialjournal.core

import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.core.util.DateTimeUtils
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/** Covers the "Timezone Change" and "Travel Across Timezones" test scenarios from the spec:
 * day/week boundaries must be computed in a single, explicit zone rather than silently using
 * whatever the device zone happens to be at query time, or a trip that crosses the
 * international date line could show a memory on the wrong day. */
class DateTimeUtilsTest {

    @Test
    fun `start and end of day bracket the whole day in a fixed zone`() {
        val zone = ZoneId.of("Asia/Tehran")
        val noon = DateTimeUtils.toEpochMillis(LocalDate.of(2024, 6, 15).atTime(12, 0), zone)
        val start = DateTimeUtils.startOfDay(noon, zone)
        val end = DateTimeUtils.endOfDay(noon, zone)
        assertThat(start).isLessThan(noon)
        assertThat(end).isGreaterThan(noon)
        assertThat(end - start).isEqualTo(24 * 60 * 60 * 1000L - 1)
    }

    @Test
    fun `same instant can fall on different calendar days in different zones`() {
        // 23:30 in Tokyo is already the next day compared to 08:30 UTC on the same instant check.
        val tokyoLateNight = DateTimeUtils.toEpochMillis(LocalDate.of(2024, 3, 10).atTime(23, 30), ZoneId.of("Asia/Tokyo"))
        val utcSameInstant = DateTimeUtils.toLocalDateTime(tokyoLateNight, ZoneOffset.UTC).toLocalDate()
        val tokyoSameInstant = DateTimeUtils.toLocalDateTime(tokyoLateNight, ZoneId.of("Asia/Tokyo")).toLocalDate()
        assertThat(tokyoSameInstant).isNotEqualTo(utcSameInstant)
    }

    @Test
    fun `isSameCalendarDay respects the given zone`() {
        val zone = ZoneId.of("America/Los_Angeles")
        val morning = DateTimeUtils.toEpochMillis(LocalDate.of(2024, 1, 1).atTime(1, 0), zone)
        val night = DateTimeUtils.toEpochMillis(LocalDate.of(2024, 1, 1).atTime(23, 0), zone)
        assertThat(DateTimeUtils.isSameCalendarDay(morning, night, zone)).isTrue()
    }

    @Test
    fun `start of week always lands on Monday`() {
        val wednesday = DateTimeUtils.toEpochMillis(LocalDate.of(2024, 5, 15).atTime(10, 0)) // a Wednesday
        val weekStart = DateTimeUtils.startOfWeek(wednesday)
        val dayOfWeek = DateTimeUtils.toLocalDateTime(weekStart).dayOfWeek
        assertThat(dayOfWeek.value).isEqualTo(1) // Monday
    }
}
