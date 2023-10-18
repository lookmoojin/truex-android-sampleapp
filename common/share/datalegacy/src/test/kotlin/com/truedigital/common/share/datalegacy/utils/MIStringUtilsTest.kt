package com.truedigital.common.share.datalegacy.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class MIStringUtilsTest {

    @Test
    @DisplayName("humanReadableByteCount should return expected result")
    fun humanReadableByteCount_shouldReturnExpectedResult() {
        val bytes = 1024L * 1024L * 1024L * 2L // 2 GB
        val expected = "2.0 GB"
        assertEquals(expected, MIStringUtils.humanReadableByteCount(bytes))
    }

    @Test
    @DisplayName("trimLastString should return original string if it doesn't end with /")
    fun trimLastString_shouldReturnOriginalString() {
        val original = "abcde"
        val expected = "abcde"
        assertEquals(expected, MIStringUtils.trimLastString(original))
    }

    @Test
    @DisplayName("trimLastString should remove last character if it is /")
    fun trimLastString_shouldRemoveLastCharacter() {
        val original = "abcde/"
        val expected = "abcde"
        assertEquals(expected, MIStringUtils.trimLastString(original))
    }

    @Test
    @DisplayName("getTimeStringFromInt should return expected result")
    fun getTimeStringFromInt_shouldReturnExpectedResult() {
        val time = 1000 * 60 * 3 + 1000 * 20 // 3 minutes and 20 seconds
        val expected = "3:20"
        assertEquals(expected, MIStringUtils.getTimeStringFromInt(time))
    }

    @Test
    @DisplayName("getMinuteFromInt should return expected result")
    fun getMinuteFromInt_shouldReturnExpectedResult() {
        val time = 1000 * 60 * 3 + 1000 * 20 // 3 minutes and 20 seconds
        val expected = 3
        assertEquals(expected, MIStringUtils.getMinuteFromInt(time))
    }

    @Test
    @DisplayName("getFileExtension should return expected result")
    fun getFileExtension_shouldReturnExpectedResult() {
        val fileName = "abcde.txt"
        val fileExt = "application/pdf"
        val expected = "abcde.txt.application/pdf"
        assertEquals(expected, MIStringUtils.getFileExtension(fileName, fileExt))
    }

    @Test
    @DisplayName("getFileExtension should append file extension if file name has no extension")
    fun getFileExtension_shouldAppendFileExtension() {
        val fileName = "abcde"
        val fileExt = "application/pdf"
        val expected = "abcde.application/pdf"
        assertEquals(expected, MIStringUtils.getFileExtension(fileName, fileExt))
    }

    @Test
    @DisplayName("getFileExtension should use fileExt parameter if fileName is null or empty")
    fun getFileExtension_shouldUseFileExtParameter() {
        val fileName1 = null
        val fileExt1 = "application/pdf"
        val expected1 = ".application/pdf"
        assertEquals(expected1, MIStringUtils.getFileExtension(fileName1, fileExt1))

        val fileName2 = ""
        val fileExt2 = "application/pdf"
        val expected2 = ".application/pdf"
        assertEquals(expected2, MIStringUtils.getFileExtension(fileName2, fileExt2))
    }

    @Test
    @DisplayName("isNullOrEmpty should return true if input is null or empty")
    fun isNullOrEmpty_shouldReturnTrueForNullOrEmptyInput() {
        val input1: String? = null
        assertTrue(MIStringUtils.isNullOrEmpty(input1))
    }

    @Test
    fun testGetSecondFromInt() {
        // Given
        val milliseconds = 65123

        // When
        val seconds = MIStringUtils.getSecondFromInt(milliseconds)

        // Then
        assertEquals(5, seconds)
    }

    @Test
    fun testGetSecondFromIntWithZero() {
        // Given
        val milliseconds = 60000

        // When
        val seconds = MIStringUtils.getSecondFromInt(milliseconds)

        // Then
        assertEquals(0, seconds)
    }
}
