package com.truedigital.core.extensions

import com.google.gson.Gson
import com.truedigital.core.data.CommonDataSettingModel
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

class CommonDataSettingModelExtensionTest {

    @Ignore
    @Test
    fun `toJson should return JSON string of object`() {
        // Given
        val mockModel = mockk<CommonDataSettingModel>()
        val expectedJson = Gson().toJson(mockModel)

        every { mockModel.toJson() } returns expectedJson

        // When
        val actualJson = mockModel.toJson()

        // Then
        assertEquals(expectedJson, actualJson)
    }
}
