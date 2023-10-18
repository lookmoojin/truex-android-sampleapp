package com.truedigital.core.extensions

import com.google.gson.Gson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsonExtensionTest {

    @Test
    fun `test fromJson should parse json correctly`() {
        // Arrange
        val gson = Gson()
        val json = """{"name": "John", "age": 30}"""
        val expected = Person("John", 30)

        // Act
        val result = gson.fromJson<Person>(json)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `test isJSONValid should return true for valid json`() {
        // Arrange
        val json = """{"name": "John", "age": 30}"""

        // Act
        val result = json.isJSONValid()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `test isJSONValid should return false for invalid json`() {
        // Arrange
        val json = """{"name": "John", "age": }"""

        // Act
        val result = json.isJSONValid()

        // Assert
        assertFalse(result)
    }

    // Helper data class for testing
    data class Person(val name: String, val age: Int)
}
