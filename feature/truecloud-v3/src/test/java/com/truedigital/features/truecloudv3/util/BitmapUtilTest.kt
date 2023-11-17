package com.truedigital.features.truecloudv3.util

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import com.truedigital.share.mock.utils.BuildUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class BitmapUtilTest {

    private val THUMBNAIL_QUALITY = 100

    lateinit var bitmapUtil: BitmapUtil

    @BeforeEach
    fun setup() {
        bitmapUtil = BitmapUtilImpl()
    }

    @Test
    fun testGetImageThumbnail() {
        // arrange
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 29)
        val mockFile = mockk<File>()
        val bitmap = mockkClass(Bitmap::class)
        mockkStatic(ThumbnailUtils::class)
        every { mockFile.length() } returns 500000L
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        coEvery {
            ThumbnailUtils.createImageThumbnail(
                mockFile,
                any(),
                any()
            )
        } returns bitmap
        coEvery {
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                THUMBNAIL_QUALITY, any()
            )
        } returns true
        coEvery {
            bitmap.byteCount
        } returns 100

        // act
        val response = bitmapUtil.createImageThumbnail(mockFile)

        // assert
        assertEquals(bitmap, response)
    }

    @Test
    fun testGetVideoThumbnail() {
        // arrange
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 29)
        val mockFile = mockk<File>()
        val bitmap = mockkClass(Bitmap::class)
        mockkStatic(ThumbnailUtils::class)
        every { mockFile.length() } returns 500000L
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        coEvery {
            ThumbnailUtils.createVideoThumbnail(
                mockFile,
                any(),
                any()
            )
        } returns bitmap
        coEvery {
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                THUMBNAIL_QUALITY, any()
            )
        } returns true
        coEvery {
            bitmap.byteCount
        } returns 100

        // act
        val response = bitmapUtil.createVideoThumbnail(mockFile)

        // assert
        assertEquals(bitmap, response)
    }
}
