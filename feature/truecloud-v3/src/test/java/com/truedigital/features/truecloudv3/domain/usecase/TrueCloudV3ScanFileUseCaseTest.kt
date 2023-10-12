package com.truedigital.features.truecloudv3.domain.usecase

import android.database.Cursor
import android.net.Uri
import com.truedigital.core.provider.ContextDataProvider
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3ScanFileUseCaseTest {

    private val contextDataProvider: ContextDataProvider = mockk(relaxed = true)
    private lateinit var scanFileUseCase: ScanFileUseCase

    @BeforeEach
    fun setUp() {
        scanFileUseCase = ScanFileUseCaseImpl(
            contextDataProvider = contextDataProvider
        )
    }

    @Test
    fun `test scanFile Found success`() = runTest {
        val uri = mockk<Uri>(relaxed = true)
        val contentList =
            arrayListOf("image_backup", "video_backup", "audio_backup", "other_backup")
        val path = "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        val cursor: Cursor = mockk(relaxed = true)
        every {
            contextDataProvider.getDataContext().contentResolver.query(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns cursor
        every { cursor.moveToFirst() } returns true
        every { cursor.moveToNext() } returnsMany listOf(true, true, false)
        every { cursor.getColumnIndexOrThrow(any()) } returnsMany listOf(
            0, // MediaStore.MediaColumns.DATA
            1 // MediaStore.MediaColumns.DATE_MODIFIED
        )
        every { cursor.getString(0) } returns path
        every { cursor.getLong(1) } returns 1655678955L

        // act
        val result = scanFileUseCase.execute(contentList, 1655678900L).toList()

        // Verify the expected behavior
        coVerify {
            contextDataProvider.getDataContext().contentResolver.query(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
        // Assert the result
        assertEquals(1, result.size)
    }
}
