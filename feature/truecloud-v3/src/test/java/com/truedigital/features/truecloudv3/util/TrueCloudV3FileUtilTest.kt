package com.truedigital.features.truecloudv3.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

internal class TrueCloudV3FileUtilTest {
    private lateinit var trueCloudV3FileUtil: TrueCloudV3FileUtil

    @BeforeEach
    fun setup() {
        trueCloudV3FileUtil = TrueCloudV3FileUtilImpl()
    }

    @Test
    fun testFileUtilSchemeContent() {
        // arrange
        val context = mockk<Context>(relaxed = true)
        val uri = mockk<Uri>(relaxed = true)
        val mockFile = mockk<File>(relaxed = true)
        val contentResolver: ContentResolver = mockk()
        every { contentResolver.getType(any()) } returns "Type"
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(mockFile) } returns uri
        every { mockFile.toUri() } returns uri
        every { uri.getScheme() } returns "content"
        every { context.contentResolver } returns contentResolver
        // act
        val response = trueCloudV3FileUtil.getMimeType(uri, contentResolver)

        // assert
        assertEquals("Type", response)
    }

    @Test
    fun `test TrueCloudV3FileUtilgetMimeType no content`() {
        // arrange
        val uri: Uri = mockk(relaxed = true)
        val contentResolver: ContentResolver = mockk()
        every { contentResolver.insert(any(), any()) } returns uri
        every { contentResolver.getType(any()) } returns "Type"
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"
        // act
        val response = trueCloudV3FileUtil.getMimeType(uri, contentResolver)

        // assert
        assertEquals("mockMimeType", response)
    }
}
