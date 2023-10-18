package com.truedigital.foundation.extension

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ListExtensionTest {

    private val mockList01 = listOf(1, 2, 3)
    private val mockList02 = listOf(2, 3, 4)
    private val mockList03 = listOf(2, 3, 4, 5)

    @Test
    fun testIsEqual_listDataIsEqual_returnTrue() {
        val result = mockList01.isEqual(mockList01)
        assertTrue(result)
    }

    @Test
    fun testIsEqual_listDataIsNotEqual_listSizeIsEqual_returnFalse() {
        val result = mockList01.isEqual(mockList02)
        assertFalse(result)
    }

    @Test
    fun testIsEqual_listDataIsNotEqual_listSizeIsNotEqual_returnFalse() {
        val result = mockList01.isEqual(mockList03)
        assertFalse(result)
    }
}
