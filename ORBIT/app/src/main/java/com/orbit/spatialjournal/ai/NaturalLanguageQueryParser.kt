package com.orbit.spatialjournal.ai

import com.orbit.spatialjournal.core.model.SearchFilters
import com.orbit.spatialjournal.core.model.MemoryType
import com.orbit.spatialjournal.core.util.DateTimeUtils
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lightweight, fully on-device keyword parser behind the Natural Language Memory Search box.
 * It intentionally does NOT call any cloud LLM: it recognizes a curated set of relative-date
 * and content-type phrases in English, Persian and Chinese, and turns them into a
 * [SearchFilters] the existing FTS-backed SearchRepository can execute. This keeps "natural
 * language search" private and instant, at the cost of only understanding a fixed vocabulary.
 */
@Singleton
class NaturalLanguageQueryParser @Inject constructor() {

    fun parse(rawQuery: String, languageTag: String, now: LocalDate = LocalDate.now()): SearchFilters {
        val q = rawQuery.trim().lowercase()
        var from: Long? = null
        var to: Long? = null
        val types = mutableSetOf<MemoryType>()
        var remainder = q

        val zone = ZoneId.systemDefault()

        fun consume(phrases: List<String>, action: () -> Unit) {
            for (phrase in phrases) {
                if (remainder.contains(phrase)) {
                    action()
                    remainder = remainder.replace(phrase, " ")
                }
            }
        }

        // ---- Relative date phrases (en / fa / zh) ----
        consume(listOf("last summer", "تابستان پارسال", "تابستان گذشته", "去年夏天")) {
            val year = if (now.monthValue < 6) now.year - 1 else now.year - 1
            from = DateTimeUtils.toEpochMillis(LocalDate.of(year, Month.JUNE, 1).atStartOfDay(), zone)
            to = DateTimeUtils.toEpochMillis(LocalDate.of(year, Month.AUGUST, 31).atTime(23, 59), zone)
        }
        consume(listOf("last winter", "زمستان پارسال", "زمستان گذشته", "去年冬天")) {
            val year = now.year - 1
            from = DateTimeUtils.toEpochMillis(LocalDate.of(year, Month.DECEMBER, 1).atStartOfDay(), zone)
            to = DateTimeUtils.toEpochMillis(LocalDate.of(year + 1, Month.FEBRUARY, 28).atTime(23, 59), zone)
        }
        consume(listOf("last year", "پارسال", "سال قبل", "去年")) {
            from = DateTimeUtils.toEpochMillis(LocalDate.of(now.year - 1, 1, 1).atStartOfDay(), zone)
            to = DateTimeUtils.toEpochMillis(LocalDate.of(now.year - 1, 12, 31).atTime(23, 59), zone)
        }
        consume(listOf("this year", "امسال", "今年")) {
            from = DateTimeUtils.toEpochMillis(LocalDate.of(now.year, 1, 1).atStartOfDay(), zone)
            to = DateTimeUtils.toEpochMillis(now.atTime(23, 59), zone)
        }
        consume(listOf("last month", "ماه گذشته", "ماه قبل", "上个月")) {
            val firstOfThisMonth = now.withDayOfMonth(1)
            val firstOfLastMonth = firstOfThisMonth.minusMonths(1)
            from = DateTimeUtils.toEpochMillis(firstOfLastMonth.atStartOfDay(), zone)
            to = DateTimeUtils.toEpochMillis(firstOfThisMonth.minusDays(1).atTime(23, 59), zone)
        }
        consume(listOf("last week", "هفته گذشته", "هفته قبل", "上周")) {
            from = DateTimeUtils.toEpochMillis(now.minusWeeks(1).atStartOfDay(), zone)
            to = DateTimeUtils.toEpochMillis(now.atTime(23, 59), zone)
        }
        consume(listOf("yesterday", "دیروز", "昨天")) {
            val y = now.minusDays(1)
            from = DateTimeUtils.toEpochMillis(y.atStartOfDay(), zone)
            to = DateTimeUtils.toEpochMillis(y.atTime(23, 59), zone)
        }
        consume(listOf("today", "امروز", "今天")) {
            from = DateTimeUtils.toEpochMillis(now.atStartOfDay(), zone)
            to = DateTimeUtils.toEpochMillis(now.atTime(23, 59), zone)
        }

        // ---- Content-type phrases ----
        consume(listOf("voice", "voice note", "صوتی", "ویس", "语音")) { types += MemoryType.VOICE }
        consume(listOf("photo", "photos", "عکس", "照片")) { types += MemoryType.PHOTO }
        consume(listOf("video", "videos", "ویدیو", "视频")) { types += MemoryType.VIDEO }
        consume(listOf("note", "notes", "یادداشت", "笔记")) { types += MemoryType.NOTE }
        consume(listOf("journal", "ژورنال", "日记")) { types += MemoryType.JOURNAL }

        // ---- Filler question words we don't need in the free-text remainder ----
        val filler = listOf(
            "where was i", "where have i been", "what memories do i have in",
            "last time i was here", "show me", "find all", "کجا بودم", "چه خاطراتی دارم",
            "آخرین بار چه زمانی اینجا بودم", "تمام", "مربوط به", "نمایش بده", "پیدا کن",
            "我在哪", "上次来这里是什么时候", "显示", "找到", "所有"
        )
        filler.forEach { remainder = remainder.replace(it, " ") }

        return SearchFilters(
            query = remainder.trim(),
            types = types,
            fromDate = from,
            toDate = to
        )
    }
}
