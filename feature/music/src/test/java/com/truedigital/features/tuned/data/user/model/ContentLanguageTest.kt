package com.truedigital.features.tuned.data.user.model

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Objects
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ContentLanguageTest {

    @Test
    fun testContentLanguage_getData() {
        val mockContentLanguage = ContentLanguage(
            code = "code",
            localDisplayName = "localDisplayName"
        )

        assertEquals("code", mockContentLanguage.code)
        assertEquals("localDisplayName", mockContentLanguage.localDisplayName)
    }

    @Test
    fun testContentLanguage_setData() {
        val mockContentLanguage = ContentLanguage(
            code = "",
            localDisplayName = ""
        )
        mockContentLanguage.code = "code"
        mockContentLanguage.localDisplayName = "localDisplayName"

        assertEquals("code", mockContentLanguage.code)
        assertEquals("localDisplayName", mockContentLanguage.localDisplayName)
    }

    @Test
    fun testContentLanguage_toString() {
        val mockContentLanguage = ContentLanguage(
            code = "code",
            localDisplayName = "name"
        )

        assertEquals(
            String.format("ContentLanguage(code=CODE, localDisplayName=name"),
            mockContentLanguage.toString()
        )
    }

    @Test
    fun testContentLanguage_equalsNull_returnFalse() {
        val mockContentLanguage = ContentLanguage(
            code = "code",
            localDisplayName = "name"
        )

        assertFalse(mockContentLanguage.equals(null))
    }

    @Test
    fun testContentLanguage_equalsData_returnTrue() {
        val mockContentLanguage = ContentLanguage(
            code = "code",
            localDisplayName = "name"
        )
        val mockContentLanguageEqual = ContentLanguage(
            code = "code",
            localDisplayName = "name"
        )

        assertTrue(mockContentLanguage == mockContentLanguageEqual)
    }

    @Test
    fun testContentLanguage_equalsSameData_returnTrue() {
        val mockContentLanguage = ContentLanguage(
            code = "code",
            localDisplayName = "name"
        )

        assertTrue(mockContentLanguage == mockContentLanguage)
    }

    @Test
    fun testContentLanguage_hashCode_returnTrue() {
        val mockContentLanguage = ContentLanguage(
            code = "code",
            localDisplayName = "name"
        )

        assertEquals(
            Objects.hash("CODE", "name"),
            mockContentLanguage.hashCode()
        )
    }
}
