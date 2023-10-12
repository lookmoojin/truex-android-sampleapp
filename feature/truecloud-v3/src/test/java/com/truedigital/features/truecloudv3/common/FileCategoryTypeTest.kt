package com.truedigital.features.truecloudv3.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class FileCategoryTypeTest {

    @Test
    fun `test FileCategoryTypeTest return correct`() {
        // arrange
        val expect = FileCategoryType.IMAGE
        // act
        val response = FileCategoryType.IMAGE.mediaType

        // assert
        assertEquals(expect.mediaType, response)
    }
    @Test
    fun `test getCategoryType IMAGE return correct`() {
        // arrange
        val expect = FileCategoryType.IMAGE
        // act
        val response = FileCategoryTypeManager.getCategoryType(FileCategoryType.IMAGE.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test getCategoryType AUDIO return correct`() {
        // arrange
        val expect = FileCategoryType.AUDIO
        // act
        val response = FileCategoryTypeManager.getCategoryType(FileCategoryType.AUDIO.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test getCategoryType VIDEO return correct`() {
        // arrange
        val expect = FileCategoryType.VIDEO
        // act
        val response = FileCategoryTypeManager.getCategoryType(FileCategoryType.VIDEO.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test getCategoryType CONTACT return correct`() {
        // arrange
        val expect = FileCategoryType.CONTACT
        // act
        val response = FileCategoryTypeManager.getCategoryType(FileCategoryType.CONTACT.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test getCategoryType OTHER return correct`() {
        // arrange
        val expect = FileCategoryType.OTHER
        // act
        val response = FileCategoryTypeManager.getCategoryType(FileCategoryType.OTHER.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test getCategoryType UNSUPPORTED_FORMAT return correct`() {
        // arrange
        val expect = FileCategoryType.UNSUPPORTED_FORMAT
        // act
        val response = FileCategoryTypeManager.getCategoryType(FileCategoryType.UNSUPPORTED_FORMAT.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test getCategoryType RECENT return correct`() {
        // arrange
        val expect = FileCategoryType.RECENT
        // act
        val response = FileCategoryTypeManager.getCategoryType(FileCategoryType.RECENT.type)

        // assert
        assertEquals(expect, response)
    }
}
