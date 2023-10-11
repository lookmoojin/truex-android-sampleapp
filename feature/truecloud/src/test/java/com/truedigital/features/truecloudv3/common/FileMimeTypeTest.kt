package com.truedigital.features.truecloudv3.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FileMimeTypeTest {

    @Test
    fun `test FileMimeTypeTest return correct`() {
        // arrange
        val expect = FileMimeType.IMAGE
        // act
        val response = FileMimeType.IMAGE.name

        // assert
        assertEquals(expect.name, response)
    }

    @Test
    fun `test FileMimeTypeManager contact return correct`() {
        // arrange
        val expect = FileMimeType.CONTACT
        // act
        val response = FileMimeTypeManager.getMimeType(FileMimeType.CONTACT.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test FileMimeTypeManager audio return correct`() {
        // arrange
        val expect = FileMimeType.AUDIO
        // act
        val response = FileMimeTypeManager.getMimeType(FileMimeType.AUDIO.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test FileMimeTypeManager video return correct`() {
        // arrange
        val expect = FileMimeType.VIDEO
        // act
        val response = FileMimeTypeManager.getMimeType(FileMimeType.VIDEO.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test FileMimeTypeManager other return correct`() {
        // arrange
        val expect = FileMimeType.OTHER
        // act
        val response = FileMimeTypeManager.getMimeType(FileMimeType.OTHER.type)

        // assert
        assertEquals(expect, response)
    }

    @Test
    fun `test FileMimeTypeManager image return correct`() {
        // arrange
        val expect = FileMimeType.IMAGE
        // act
        val response = FileMimeTypeManager.getMimeType(FileMimeType.IMAGE.type)

        // assert
        assertEquals(expect, response)
    }
    @Test
    fun `test FileMimeTypeTest res return correct`() {
        // arrange
        val expect = FileMimeType.IMAGE
        // act
        val response = FileMimeType.IMAGE.res

        // assert
        assertEquals(expect.res, response)
    }

    @Test
    fun `test FileMimeTypeTest resGrid return correct`() {
        // arrange
        val expect = FileMimeType.IMAGE
        // act
        val response = FileMimeType.IMAGE.resGrid

        // assert
        assertEquals(expect.resGrid, response)
    }

    @Test
    fun `test FileMimeTypeTest type return correct`() {
        // arrange
        val expect = FileMimeType.IMAGE
        // act
        val response = FileMimeType.IMAGE.type

        // assert
        assertEquals(expect.type, response)
    }
}
