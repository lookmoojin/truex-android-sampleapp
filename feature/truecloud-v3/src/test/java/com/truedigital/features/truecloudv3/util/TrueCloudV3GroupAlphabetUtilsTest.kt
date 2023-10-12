package com.truedigital.features.truecloudv3.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class TrueCloudV3GroupAlphabetUtilsTest {

    @Test
    fun `test TrueCloudV3GroupAlphabetUtilsTest getFirstAlphabet case correct`() {
        // arrange
        val mockResponse = "Panya N"

        // act
        val response = TrueCloudV3GroupAlphabetUtils.getFirstAlphabet(mockResponse)

        // assert
        assertEquals("P", response)
    }

    @Test
    fun `test TrueCloudV3GroupAlphabetUtilsTest getFirstAlphabet case incorrect`() {
        // arrange
        val mockResponse = "Panya N"

        // act
        val response = TrueCloudV3GroupAlphabetUtils.getFirstAlphabet(mockResponse)

        // assert
        assertNotEquals("N", response)
    }

    @Test
    fun `test TrueCloudV3GroupAlphabetUtilsTest getFirstAlphabet case thai`() {
        // arrange
        val mockResponse = "ปัญญา"

        // act
        val response = TrueCloudV3GroupAlphabetUtils.getFirstAlphabet(mockResponse)

        // assert
        assertEquals("ป", response)
    }

    @Test
    fun `test TrueCloudV3GroupAlphabetUtilsTest getFirstAlphabet case number`() {
        // arrange
        val mockResponse = "56"

        // act
        val response = TrueCloudV3GroupAlphabetUtils.getFirstAlphabet(mockResponse)

        // assert
        assertEquals("#", response)
    }

    @Test
    fun `test TrueCloudV3GroupAlphabetUtilsTest getFirstAlphabet case $#*`() {
        // arrange
        val mockResponse = "$#*"

        // act
        val response = TrueCloudV3GroupAlphabetUtils.getFirstAlphabet(mockResponse)

        // assert
        assertEquals("#", response)
    }
}
