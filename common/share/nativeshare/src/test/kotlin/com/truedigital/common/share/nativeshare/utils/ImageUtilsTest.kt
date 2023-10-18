package com.truedigital.common.share.nativeshare.utils

import android.content.Context
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ImageUtilsTest {

    private val context: Context = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `Get image file from url`() {
        assertNull(
            getImageFileFromUrl(context, "")
        )
    }

    @Test
    fun `Save bitmap`() {
    }
}
