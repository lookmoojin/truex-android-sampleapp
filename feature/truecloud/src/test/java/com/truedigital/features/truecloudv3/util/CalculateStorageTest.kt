package com.truedigital.features.truecloudv3.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal interface CalculateStorageTestCase {
    fun `test getStoragePercentage 60 percent`()
    fun `test getStoragePercentage 0 percent`()
    fun `test getStoragePercentage 100 percent`()
}

internal class CalculateStorageTest : CalculateStorageTestCase {

    @Test
    override fun `test getStoragePercentage 60 percent`() {
        // arrange
        val mockResponse = 60

        // act
        val response = CalculateStorage.getStoragePercentage(600, 1000)

        // assert
        assertEquals(mockResponse, response)
    }

    @Test
    override fun `test getStoragePercentage 0 percent`() {
        // arrange
        val mockResponse = 0

        // act
        val response = CalculateStorage.getStoragePercentage(0, 1000)

        // assert
        assertEquals(mockResponse, response)
    }

    @Test
    override fun `test getStoragePercentage 100 percent`() {
        // arrange
        val mockResponse = 100

        // act
        val response = CalculateStorage.getStoragePercentage(1000, 1000)

        // assert
        assertEquals(mockResponse, response)
    }
}
