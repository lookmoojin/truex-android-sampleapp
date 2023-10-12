package com.truedigital.core.extensions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DateLongExtensionTest {
    @Test
    fun `test DateLongExtension isOneDayLeft more than one day return false`() {
        assertEquals(123456789L.isOneDayLeft(), false)
    }

    @Test
    fun `test DateLongExtension isOneDayLeft less than one day return true`() {
        assertEquals(1234L.isOneDayLeft(), true)
    }

    @Test
    fun `test DateLongExtension convertChristianYearToBuddhistYear return string`() {
        assertEquals(1638182599000.convertChristianYearToBuddhistYear("dd-MM-YYYY"), "29-11-2564")
    }

    @Test
    fun `test DateLongExtension convertToSecond return string`() {
        assertEquals(1638182599000.convertToSecond(), "1638182599")
    }
}
