package com.truedigital.core.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DoubleExtensionTest {
    @Test
    fun `When convert number to short string format should return correct format`() {
        val numberA = 2.0
        val numberB = 19.02
        val numberC = 1252.05
        val numberD = 25250.00
        val numberE = 100000.00
        val numberF = 1520000.00

        val strNumberA = numberA.convertToCurrencyUnitFormat()
        val strNumberB = numberB.convertToCurrencyUnitFormat()
        val strNumberC = numberC.convertToCurrencyUnitFormat()
        val strNumberD = numberD.convertToCurrencyUnitFormat()
        val strNumberE = numberE.convertToCurrencyUnitFormat()
        val strNumberF = numberF.convertToCurrencyUnitFormat()

        assertEquals("2", strNumberA)
        assertEquals("19", strNumberB)
        assertEquals("1.2K", strNumberC)
        assertEquals("25.2K", strNumberD)
        assertEquals("100K", strNumberE)
        assertEquals("1.5M", strNumberF)
    }

    @Test
    fun `When convert number to string format should return correct format`() {
        val numberA = 2.0
        val numberB = 19.05
        val numberC = 1252.05
        val numberD = 2525000.059
        val numberE = 10000000
        val numberF = 152000000

        val strNumberA = numberA.convertToCurrencyFormat()
        val strNumberB = numberB.convertToCurrencyFormat()
        val strNumberC = numberC.convertToCurrencyFormat()
        val strNumberD = numberD.convertToCurrencyFormat()
        val strNumberE = numberE.convertToCurrencyFormat()
        val strNumberF = numberF.convertToCurrencyFormat()

        assertEquals("2.00", strNumberA)
        assertEquals("19.05", strNumberB)
        assertEquals("1,252.05", strNumberC)
        assertEquals("2,525,000.06", strNumberD)
        assertEquals("10,000,000", strNumberE)
        assertEquals("152,000,000", strNumberF)
    }

    @Test
    fun `Given smaller number, when convertNearestNumber then should return correct number`() {
        val mockSmallerNumber = 55.55f
        val mockSize = 10f

        val number = mockSmallerNumber.convertNearestNumber(mockSize)

        assertEquals(60.0f, number)
    }

    @Test
    fun `Given larger number, when convertNearestNumber then should return correct number`() {
        val mockSmallerNumber = 54.55f
        val mockSize = 10f

        val number = mockSmallerNumber.convertNearestNumber(mockSize)

        assertEquals(50.0f, number)
    }
}
