package com.truedigital.core.extensions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

interface ListExtensionTestCase {
    @Test
    fun `When list string is null and require Non Null is false should return empty list string`()

    @Test
    fun `When list string is null and require Non Null is true should return list string only have empty string`()

    @Test
    fun `When empty list string and require Non Null is false should return empty list string`()

    @Test
    fun `When empty list string and require Non Null is true should return list string only have empty string`()

    @Test
    fun `When list string first index is empty string and require Non Null is false should return same list string`()

    @Test
    fun `When list string first index is empty string and require Non Null is true should return same list string`()

    @Test
    fun `When list string first index is empty and require Non Null is false should return same list string`()

    @Test
    fun `When list string first index is empty and require Non Null is true should return same list string`()

    @Test
    fun `When list string last index is empty and require Non Null is false should return same list string`()

    @Test
    fun `When list string last index is empty and require Non Null is true should return same list string`()

    @Test
    fun `When list string middle index is empty and require Non Null is false should return same list string`()

    @Test
    fun `When list string middle index is empty and require Non Null is true should return same list string`()

    @Test
    fun `When list is null should don't do in scope`()

    @Test
    fun `When list is empty but type data is nullable should don't do in scope`()

    @Test
    fun `When list is have empty string but type data is nullable should do in scope`()

    @Test
    fun `When list is empty should don't do in scope`()

    @Test
    fun `When list is have empty string should do in scope`()

    @Test
    fun `When list is have data should do in scope`()

    @Test
    fun `When list is null should do in scope`()

    @Test
    fun `When list is empty should do in scope`()

    @Test
    fun `When list is not empty should don't do in scope`()
}

class ListExtensionTestImpl : ListExtensionTestCase {
    @Test
    override fun `When list string is null and require Non Null is false should return empty list string`() {
        val listString: List<String>? = null
        assertEquals(listString.validateStringValue(false), listOf())
    }

    @Test
    override fun `When list string is null and require Non Null is true should return list string only have empty string`() {
        val listString: List<String>? = null
        assertEquals(listString.validateStringValue(true), listOf(""))
    }

    @Test
    override fun `When empty list string and require Non Null is false should return empty list string`() {
        val listString = listOf<String>()
        assertEquals(listString.validateStringValue(false), listOf())
    }

    @Test
    override fun `When empty list string and require Non Null is true should return list string only have empty string`() {
        val listString = listOf<String>()
        assertEquals(listString.validateStringValue(true), listOf(""))
    }

    @Test
    override fun `When list string first index is empty string and require Non Null is false should return same list string`() {
        val listString = listOf("")
        assertEquals(listString.validateStringValue(false), listOf(""))
    }

    @Test
    override fun `When list string first index is empty string and require Non Null is true should return same list string`() {
        val listString = listOf("")
        assertEquals(listString.validateStringValue(true), listOf(""))
    }

    @Test
    override fun `When list string first index is empty and require Non Null is false should return same list string`() {
        val listString = listOf("", "a")
        assertEquals(listString.validateStringValue(false), listOf("", "a"))
    }

    @Test
    override fun `When list string first index is empty and require Non Null is true should return same list string`() {
        val listString = listOf("", "a", "b")
        assertEquals(listString.validateStringValue(true), listOf("", "a", "b"))
    }

    @Test
    override fun `When list string last index is empty and require Non Null is false should return same list string`() {
        val listString = listOf("a", "b", "")
        assertEquals(listString.validateStringValue(false), listOf("a", "b", ""))
    }

    @Test
    override fun `When list string last index is empty and require Non Null is true should return same list string`() {
        val listString = listOf("a", "b", "")
        assertEquals(listString.validateStringValue(true), listOf("a", "b", ""))
    }

    @Test
    override fun `When list string middle index is empty and require Non Null is false should return same list string`() {
        val listString = listOf("a", "", "b")
        assertEquals(listString.validateStringValue(false), listOf("a", "", "b"))
    }

    @Test
    override fun `When list string middle index is empty and require Non Null is true should return same list string`() {
        val listString = listOf("a", "", "b")
        assertEquals(listString.validateStringValue(true), listOf("a", "", "b"))
    }

    @Test
    override fun `When list is null should don't do in scope`() {
        val listString: List<String>? = null
        var isNotEmpty = false
        listString?.ifNotEmpty {
            isNotEmpty = true
        }
        assertEquals(false, isNotEmpty)
    }

    @Test
    override fun `When list is empty but type data is nullable should don't do in scope`() {
        val listString: List<String?> = listOf()
        var isNotEmpty = false
        listString.ifNotEmpty {
            isNotEmpty = true
        }
        assertEquals(false, isNotEmpty)
    }

    @Test
    override fun `When list is have empty string but type data is nullable should do in scope`() {
        val listString: List<String?> = listOf("")
        var isNotEmpty = false
        listString.ifNotEmpty {
            isNotEmpty = true
        }
        assertEquals(true, isNotEmpty)
    }

    @Test
    override fun `When list is empty should don't do in scope`() {
        val listString = listOf<String>()
        var isNotEmpty = false
        listString.ifNotEmpty {
            isNotEmpty = true
        }
        assertEquals(false, isNotEmpty)
    }

    @Test
    override fun `When list is have empty string should do in scope`() {
        val listString = listOf("")
        var isNotEmpty = false
        listString.ifNotEmpty {
            isNotEmpty = true
        }
        assertEquals(true, isNotEmpty)
    }

    @Test
    override fun `When list is have data should do in scope`() {
        val listString = listOf("a")
        var isNotEmpty = false
        listString.ifNotEmpty {
            isNotEmpty = true
        }
        assertEquals(true, isNotEmpty)
    }

    @Test
    override fun `When list is null should do in scope`() {
        val listString: List<String>? = null
        var isDoInScope = false
        listString.doOnNullOrEmpty {
            isDoInScope = true
        }
        assertEquals(true, isDoInScope)
    }

    @Test
    override fun `When list is empty should do in scope`() {
        val listString = listOf<String>()
        var isDoInScope = false
        listString.doOnNullOrEmpty {
            isDoInScope = true
        }
        assertEquals(true, isDoInScope)
    }

    @Test
    override fun `When list is not empty should don't do in scope`() {
        val listString = listOf("a")
        var isDoInScope = false
        listString.doOnNullOrEmpty {
            isDoInScope = true
        }
        assertEquals(false, isDoInScope)
    }
}
