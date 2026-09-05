package com.orbit.spatialjournal.core.util

import com.orbit.spatialjournal.core.model.OpeningHoursEntry
import com.orbit.spatialjournal.core.model.OpeningStatus
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Computes "open now / closes in Xh Ym" and "closed / opens in Xh Ym" from a place's
 * user-entered weekly opening hours. Hours are entered once by the user (see
 * OpeningHoursEditor); everything else here is derived, not guessed.
 *
 * Handles the two tricky real-world cases explicitly:
 *  - a segment that crosses midnight (e.g. a bar open 20:00-02:00)
 *  - a day marked fully closed
 */
object OpeningHoursUtils {

    fun computeStatus(
        hours: List<OpeningHoursEntry>,
        now: LocalDateTime = LocalDateTime.now(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): OpeningStatus {
        if (hours.isEmpty()) {
            return OpeningStatus(isOpenNow = false, currentSegmentEndsAtEpochMillis = null,
                nextChangeAtEpochMillis = null, minutesUntilNextChange = null)
        }

        val byDay = hours.associateBy { it.isoDayOfWeek }
        val nowEpoch = DateTimeUtils.toEpochMillis(now, zoneId)

        // 1) Is "now" inside today's segment, or inside yesterday's segment that spilled past midnight?
        val today = now.dayOfWeek
        val yesterday = today.minus(1)
        val nowMinutes = now.hour * 60 + now.minute

        byDay[today.isoValueSafe()]?.let { entry ->
            if (!entry.closedAllDay && entry.openMinuteOfDay != null && entry.closeMinuteOfDay != null) {
                val crossesMidnight = entry.closeMinuteOfDay <= entry.openMinuteOfDay
                val closesToday = if (crossesMidnight) 24 * 60 else entry.closeMinuteOfDay
                if (nowMinutes in entry.openMinuteOfDay until closesToday) {
                    val endEpoch = DateTimeUtils.toEpochMillis(
                        now.toLocalDate().atStartOfDay().plusMinutes(closesToday.toLong()), zoneId
                    )
                    return OpeningStatus(true, endEpoch, endEpoch, minutesBetween(nowEpoch, endEpoch))
                }
            }
        }

        byDay[yesterday.isoValueSafe()]?.let { entry ->
            if (!entry.closedAllDay && entry.openMinuteOfDay != null && entry.closeMinuteOfDay != null) {
                val crossesMidnight = entry.closeMinuteOfDay <= entry.openMinuteOfDay
                if (crossesMidnight && nowMinutes < entry.closeMinuteOfDay) {
                    val endEpoch = DateTimeUtils.toEpochMillis(
                        now.toLocalDate().atStartOfDay().plusMinutes(entry.closeMinuteOfDay.toLong()), zoneId
                    )
                    return OpeningStatus(true, endEpoch, endEpoch, minutesBetween(nowEpoch, endEpoch))
                }
            }
        }

        // 2) Closed now — find the next opening within the next 14 days.
        for (dayOffset in 0..14) {
            val candidateDate = now.toLocalDate().plusDays(dayOffset.toLong())
            val entry = byDay[candidateDate.dayOfWeek.isoValueSafe()] ?: continue
            if (entry.closedAllDay || entry.openMinuteOfDay == null) continue

            val openDateTime = candidateDate.atStartOfDay().plusMinutes(entry.openMinuteOfDay.toLong())
            if (openDateTime.isAfter(now)) {
                val openEpoch = DateTimeUtils.toEpochMillis(openDateTime, zoneId)
                return OpeningStatus(false, null, openEpoch, minutesBetween(nowEpoch, openEpoch))
            }
        }

        return OpeningStatus(false, null, null, null)
    }

    private fun DayOfWeek.isoValueSafe(): Int = this.value // 1..7, Monday..Sunday

    private fun minutesBetween(fromEpoch: Long, toEpoch: Long): Long =
        ChronoUnit.MINUTES.between(
            java.time.Instant.ofEpochMilli(fromEpoch),
            java.time.Instant.ofEpochMilli(toEpoch)
        )
}
