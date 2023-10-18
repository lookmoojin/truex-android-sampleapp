package com.truedigital.features.truecloudv3.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StorageTypeTest {
    @Test
    fun testObjectType() {
        val storageTypeFile = StorageType.FILE
        val storageTypeFolder = StorageType.FOLDER
        assertEquals(StorageType.FILE, storageTypeFile)
        assertEquals(StorageType.FOLDER, storageTypeFolder)
    }
}
