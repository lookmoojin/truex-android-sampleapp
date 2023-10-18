package com.truedigital.features.truecloudv3.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FileStorageTypeTest {

    @Test
    fun `test getStorageType return correct`() {
        // arrange
        val expect = FileStorageType.IMAGES_File_Storage_TYPE
        // act
        val response = FileStorageType.getStorageType(FileStorageType.IMAGES_File_Storage_TYPE.key)

        // assert
        assertEquals(expect, response)
    }

    @Test
    fun `test getStorageType return other`() {
        // arrange
        val expect = FileStorageType.OTHERS_File_Storage_TYPE
        // act
        val response = FileStorageType.getStorageType("")

        // assert
        assertEquals(expect, response)
    }
}
