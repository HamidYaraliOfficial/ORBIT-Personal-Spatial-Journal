package com.orbit.spatialjournal.ai

import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.core.model.MemoryType
import org.junit.Test
import java.time.LocalDate

class NaturalLanguageQueryParserTest {

    private val parser = NaturalLanguageQueryParser()
    private val anchor = LocalDate.of(2024, 8, 15)

    @Test
    fun `parses last summer in English to a June to August range of the prior year`() {
        val filters = parser.parse("where was I last summer", "en", anchor)
        assertThat(filters.fromDate).isNotNull()
        assertThat(filters.toDate).isNotNull()
        assertThat(filters.fromDate!! < filters.toDate!!).isTrue()
    }

    @Test
    fun `parses Persian last summer phrase`() {
        val filters = parser.parse("تابستان پارسال کجا بودم", "fa", anchor)
        assertThat(filters.fromDate).isNotNull()
    }

    @Test
    fun `parses Chinese last summer phrase`() {
        val filters = parser.parse("去年夏天我在哪里", "zh", anchor)
        assertThat(filters.fromDate).isNotNull()
    }

    @Test
    fun `detects voice note type across languages`() {
        assertThat(parser.parse("find all voice notes about project X", "en", anchor).types).contains(MemoryType.VOICE)
        assertThat(parser.parse("تمام ویس‌های مربوط به پروژه X را پیدا کن", "fa", anchor).types).contains(MemoryType.VOICE)
    }

    @Test
    fun `plain keyword query with no recognized phrase keeps text as-is`() {
        val filters = parser.parse("coffee shop receipt", "en", anchor)
        assertThat(filters.query).contains("coffee")
        assertThat(filters.fromDate).isNull()
    }

    @Test
    fun `today phrase resolves to a single day range`() {
        val filters = parser.parse("today", "en", anchor)
        val oneDayMillis = 24 * 60 * 60 * 1000L
        assertThat(filters.toDate!! - filters.fromDate!!).isLessThan(oneDayMillis + 1)
    }
}
