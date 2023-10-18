package com.truedigital.core.extensions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntegerExtensionTest {
    @Test
    fun `test IntegerExtension decToHex int return string hex`() {
        assertEquals(175.decToHex(), "af")
    }

    @Test
    fun `test IntegerExtension decToHex 0 return string 0`() {
        assertEquals(0.decToHex(), "0")
    }

    @Test
    fun `test IntegerExtension convertToNumberFormat 1234 return string 1,234`() {
        assertEquals(1234.convertToNumberFormat(), "1,234")
    }

    @Test
    fun `test IntegerExtension convertToNumberFormat 123 return string 123`() {
        assertEquals(123.convertToNumberFormat(), "123")
    }

    @Test
    fun `test IntegerExtension convertToNumberFormat null return string null`() {
        assertEquals(null.convertToNumberFormat(), "null")
    }

    @Test
    fun `test IntegerExtension fromMinuteToHour 123 return 2 hr 3 min`() {
        assertEquals(123.fromMinuteToHour("hr", "min"), "2 hr 3 min")
    }

    @Test
    fun `test IntegerExtension fromMinuteToHour 59 return 59 min`() {
        assertEquals(59.fromMinuteToHour("hr", "min"), "59 min")
    }

    @Test
    fun `test IntegerExtension fromMinuteToHour 120 return 2 hr`() {
        assertEquals(120.fromMinuteToHour("hr", "min"), "2 hr")
    }

    @Test
    fun `test IntegerExtension fromMilliSecondToMinute 120000 return 2`() {
        assertEquals(120000.fromMilliSecondToMinute(), 2)
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo 185392 return 185and3K`() {
        assertEquals(185392.convertToCountViewVideo(), "185.3K")
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo 1935955 return 1and9M`() {
        assertEquals(1935955.convertToCountViewVideo(), "1.9M")
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo 1935955998 return 1and9B`() {
        assertEquals(1935955998.convertToCountViewVideo(), "1.9B")
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo minus185392 return 185and3K`() {
        assertEquals((-185392).convertToCountViewVideo(), "-185.3K")
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo minus 1935955 return 1and9M`() {
        assertEquals((-1935955).convertToCountViewVideo(), "-1.9M")
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo minus 1935955998 return 1and9B`() {
        assertEquals((-1935955998).convertToCountViewVideo(), "-1.9B")
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo 235 return 235`() {
        assertEquals(235.convertToCountViewVideo(), "235")
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo 99999999 return 99and9B`() {
        assertEquals(99999999.convertToCountViewVideo(), "99.9M")
    }

    @Test
    fun `test IntegerExtension convertToCountViewVideo 999999999 return 999and9B`() {
        assertEquals(999999999.convertToCountViewVideo(), "999.9M")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 1234567890 return 1point2B`() {
        assertEquals((1234567890).convertToEngagementFormat(), "1.2B")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 99900000 return 99point9M`() {
        assertEquals((99900000).convertToEngagementFormat(), "99.9M")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 9999999 return 9point9M`() {
        assertEquals((9999999).convertToEngagementFormat(), "9.9M")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 999999 return 999point9K`() {
        assertEquals((999999).convertToEngagementFormat(), "999.9K")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 99999 return 99point9K`() {
        assertEquals((99999).convertToEngagementFormat(), "99.9K")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 9999 return 9999`() {
        assertEquals((9999).convertToEngagementFormat(), "9999")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 999 return 999`() {
        assertEquals((999).convertToEngagementFormat(), "999")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 99 return 99`() {
        assertEquals((99).convertToEngagementFormat(), "99")
    }

    @Test
    fun `test IntegerExtension convertToEngagementFormat 0 return 0`() {
        assertEquals((0).convertToEngagementFormat(), "0")
    }
}
