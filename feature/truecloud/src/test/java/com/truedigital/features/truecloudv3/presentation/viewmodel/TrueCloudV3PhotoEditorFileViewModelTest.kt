package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File
import java.io.FileOutputStream

@ExtendWith(InstantTaskExecutorExtension::class)
class TrueCloudV3PhotoEditorFileViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val fileProvider = mockk<FileProvider>(relaxed = true)
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
    private val context = mockk<Context>()
    private val mockFile = mockk<File>()
    private val mockBitmap = mockk<Bitmap>(relaxed = true)
    private val mockUri = mockk<Uri>()
    private lateinit var viewModel: TrueCloudV3PhotoEditorFileViewModel

    @BeforeEach
    fun setUp() {
        viewModel = TrueCloudV3PhotoEditorFileViewModel(
            coroutineDispatcher = coroutineDispatcher,
            fileProvider = fileProvider,
            contextDataProviderWrapper = contextDataProviderWrapper,
        )

        every {
            context.cacheDir
        } returns mockFile

        every {
            mockFile.absolutePath
        } returns "/cacheDir/absolutePath"

        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns mockUri
        mockkStatic(Bitmap::class)
        every { Bitmap.createBitmap(any(), any(), any()) } returns mockBitmap
        every { mockBitmap.compress(any(), any(), any()) } returns true
    }

    @Test
    fun `test onConfirmClick success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onGenerateUri)
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        val mockFile = mockk<File>()
        val mockFileData = File.createTempFile("mockFileData", "mockFileData")
        mockkConstructor(FileOutputStream::class)
        every {
            fileProvider.getFile(any())
        } returns mockFile
        every { mockFile.exists() } returns false
        every { mockFile.mkdir() } returns true
        every { mockFile.path } returns "path/"
        every {
            fileProvider.getFile(any(), any())
        } returns mockFileData
        every { anyConstructed<FileOutputStream>().close() } just runs
        every { anyConstructed<FileOutputStream>().flush() } just runs

        // act
        viewModel.onConfirmClick(mockBitmap)

        // assert
        testObserver.assertHasValue()
    }
}
