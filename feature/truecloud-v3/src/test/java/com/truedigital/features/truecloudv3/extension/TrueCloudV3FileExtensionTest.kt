package com.truedigital.features.truecloudv3.extension

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFalse

class TrueCloudV3FileExtensionTest {
    @Test
    fun testFileIsCompleteCaseSize10() {
        var file = mockk<File>(relaxed = true)
        every {
            file.length()
        } returns 10
        every {
            file.path
        } returns "path/test.exc"

        // act
        val response = file.isComplete()

        // assert
        assertFalse(response)
    }
}
