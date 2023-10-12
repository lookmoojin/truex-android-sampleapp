package com.truedigital.features.truecloudv3.extension

import android.content.Context
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3BytesExtensionTest {
    private val mockContext: Context = mockk(relaxed = true)

    @Test
    fun `test formatBinarySize MB correct`() {
        // arrange
        val mockResponse = " MB"
        val bytesize = 1048576L
        // act
        val response = bytesize.formatBinarySize(mockContext)

        // assert
        assertEquals(mockResponse, response)
    }

    @Test
    fun `test formatBinarySize GB correct`() {
        // arrange
        val mockResponse = " GB"
        val bytesize = 1073741824L
        // act
        val response = bytesize.formatBinarySize(mockContext)

        // assert
        assertEquals(mockResponse, response)
    }

    @Test
    fun `test formatBinarySize KB correct`() {
        // arrange
        val mockResponse = " KB"
        val bytesize = 1024L
        // act
        val response = bytesize.formatBinarySize(mockContext)

        // assert
        assertEquals(mockResponse, response)
    }

    @Test
    fun `test formatBinarySize TB correct`() {
        // arrange
        val mockResponse = " TB"
        val bytesize = 1073741824L * 1024
        // act
        val response = bytesize.formatBinarySize(mockContext)

        // assert
        assertEquals(mockResponse, response)
    }
}
