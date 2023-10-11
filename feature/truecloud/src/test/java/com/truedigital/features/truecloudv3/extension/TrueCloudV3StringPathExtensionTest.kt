package com.truedigital.features.truecloudv3.extension

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3StringPathExtensionTest {

    @Test
    fun testGetNotExistsPath() {
        val path = "ABC"
        val response = path.getNotExistsPath()

        assertEquals(path, response)
    }

    @Test
    fun testGetNotExistsPathEmpty() {
        val path = ""
        val response = path.getNotExistsPath()

        assertEquals(path, response)
    }

    @Test
    fun testGetNotExistsPathIsExists() {
        val path = "/storage/emulated/0/Download/abc.jpg"
        val response = path.getNotExistsPath()
        assertEquals(path, response)
    }
}
