package com.orbit.spatialjournal.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * All Memory timestamps are stored as UTC epoch millis. Every conversion to a human
 * calendar day/hour goes through here so day boundaries stay consistent even when the
 * user travels across timezones (see TripBuilder + tests).
 */
object DateTimeUtils {

    fun nowMillis(): Long = System.currentTimeMillis()

    fun toLocalDateTime(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime =
        Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDateTime()

    fun toEpochMillis(dateTime: LocalDateTime, zoneId: ZoneId = ZoneId.systemDefault()): Long =
        dateTime.atZone(zoneId).toInstant().toEpochMilli()

    fun startOfDay(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Long =
        toLocalDateTime(epochMillis, zoneId).toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli()

    fun endOfDay(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Long =
        startOfDay(epochMillis, zoneId) + 24 * 60 * 60 * 1000L - 1

    fun startOfWeek(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        val date = toLocalDateTime(epochMillis, zoneId).toLocalDate()
        val monday = date.minusDays((date.dayOfWeek.value - 1).toLong())
        return monday.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun startOfMonth(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        val date = toLocalDateTime(epochMillis, zoneId).toLocalDate().withDayOfMonth(1)
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun startOfYear(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        val date = LocalDate.of(toLocalDateTime(epochMillis, zoneId).year, 1, 1)
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun isSameCalendarDay(a: Long, b: Long, zoneId: ZoneId = ZoneId.systemDefault()): Boolean =
        toLocalDateTime(a, zoneId).toLocalDate() == toLocalDateTime(b, zoneId).toLocalDate()

    fun formatShortDate(epochMillis: Long, pattern: String = "yyyy-MM-dd"): String =
        DateTimeFormatter.ofPattern(pattern).format(toLocalDateTime(epochMillis))

    fun formatTime(epochMillis: Long, pattern: String = "HH:mm"): String =
        DateTimeFormatter.ofPattern(pattern).format(toLocalDateTime(epochMillis))

    fun minutesOfDay(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Int {
        val t = toLocalDateTime(epochMillis, zoneId).toLocalTime()
        return t.hour * 60 + t.minute
    }
}
