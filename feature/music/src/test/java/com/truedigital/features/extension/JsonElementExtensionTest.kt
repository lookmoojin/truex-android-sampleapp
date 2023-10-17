package com.truedigital.features.extension

import com.google.gson.JsonParser
import com.truedigital.features.music.extensions.toSettingModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonElementExtensionTest {

    @Test
    fun toSettingModel_dataMatchWithSettingModel_returnSettingModel() {
        // Given
        val mockStreamUrl = "streamUrl"
        val mockSettingJson = JsonParser.parseString("{\"stream_url\":\"$mockStreamUrl\" }")

        // When
        val result = mockSettingJson.toSettingModel()

        // Then
        assertEquals(mockStreamUrl, result?.streamUrl)
    }

    @Test
    fun toSettingModel_dataDoNotMatchWithSettingModel_returnNull() {
        // Given
        val mockSettingJson = JsonParser.parseString("")

        // When
        val result = mockSettingJson.toSettingModel()

        // Then
        assertNull(result)
    }
}
