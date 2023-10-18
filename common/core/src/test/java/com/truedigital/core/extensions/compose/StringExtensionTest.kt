package com.truedigital.core.extensions.compose

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

class StringExtensionTest {

    @Test
    fun `test hexToJetpackColor with valid hex`() {
        // Given
        val hexString = "#FF0000"
        val expectedColor = Color(android.graphics.Color.parseColor(hexString))

        // When
        val resultColor = hexString.hexToJetpackColor()

        // Then
        assertEquals(expectedColor, resultColor)
    }

    @Ignore
    @Test
    fun `test hexToJetpackColor with invalid hex`() {
        // Given
        val hexString = "invalid_hex"
        val expectedColor = Color.White

        // When
        val resultColor = hexString.hexToJetpackColor()

        // Then
        assertEquals(expectedColor, resultColor)
    }
}
