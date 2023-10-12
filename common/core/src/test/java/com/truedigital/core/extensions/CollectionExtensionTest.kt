package com.truedigital.core.extensions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CollectionExtensionTest {

    @Test
    fun `test getOrDefaultCompat with existing key`() {
        // Given
        val map: Map<String, Int> = mapOf(
            "foo" to 42
        )

        val defaultValue = 0

        // When
        val result = map.getOrElse("foo") { defaultValue }

        // Then
        assertEquals(42, result)
    }

    @Test
    fun `test getOrDefaultCompat with non-existing key`() {
        // Given
        val map: Map<String, Int> = mapOf(
            "foo" to 42
        )

        val defaultValue = 0

        // When
        val result = map.getOrElse("bar") { defaultValue }

        // Then
        assertEquals(defaultValue, result)
    }
}
