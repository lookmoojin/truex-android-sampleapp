package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Context
import android.net.Uri
import com.canhub.cropper.CropImageView
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File
import java.util.UUID

@ExtendWith(InstantTaskExecutorExtension::class)
class TrueCloudV3PhotoEditorTransformViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val fileProvider = mockk<FileProvider>(relaxed = true)
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
    private val context = mockk<Context>()
    private val mockFile = mockk<File>()
    private lateinit var viewModel: TrueCloudV3PhotoEditorTransformViewModel

    @BeforeEach
    fun setUp() {
        viewModel = TrueCloudV3PhotoEditorTransformViewModel(
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

        mockkStatic(UUID::class)
        every { UUID.randomUUID().toString() } returns "uuid"
    }

    @Test
    fun `test onConfirmClick success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onGenerateUri)
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        val mockFile = mockk<File>()
        val mockFileData = mockk<File>()
        every {
            fileProvider.getFile(any())
        } returns mockFile
        every { mockFile.exists() } returns false
        every { mockFile.mkdir() } returns true
        every { mockFile.path } returns "path/"
        every {
            fileProvider.getFile(any(), any())
        } returns mockFileData
        every { mockFileData.exists() } returns true
        every { mockFileData.absolutePath } returns "/test/path"

        // act
        viewModel.onConfirmClick()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onCropImageComplete error`() = runTest {
        // arrange
        val onShowErrorObserver = TestObserver.test(viewModel.onShowError)
        val onCropCompleteObserver = TestObserver.test(viewModel.onCropComplete)
        val mockCropResult = mockk<CropImageView.CropResult>()
        every { mockCropResult.error } returns Exception("Exception")

        // act
        viewModel.onCropImageComplete(mockCropResult)

        // assert
        onShowErrorObserver.assertHasValue()
        onCropCompleteObserver.assertNoValue()
    }

    @Test
    fun `test onCropImageComplete success`() = runTest {
        // arrange
        val onShowErrorObserver = TestObserver.test(viewModel.onShowError)
        val onCropCompleteObserver = TestObserver.test(viewModel.onCropComplete)
        val mockUri = mockk<Uri>()
        val mockCropResult = mockk<CropImageView.CropResult>()
        every { mockCropResult.error } returns null
        every { mockCropResult.uriContent } returns mockUri

        // act
        viewModel.onCropImageComplete(mockCropResult)

        // assert
        onShowErrorObserver.assertNoValue()
        onCropCompleteObserver.assertHasValue()
    }

    @Test
    fun `test onFlipImage`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onFlipImage)

        // act
        viewModel.onFlipImage()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onRotateImage`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onRotateImage)

        // act
        viewModel.onRotateImage()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onSetRotation`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetRotation)

        // act
        viewModel.onSetRotation(30)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onCropReset`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onCropReset)

        // act
        viewModel.onCropReset()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onSetCrop`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetCrop)

        // act
        viewModel.onSetCrop(1 to 1)

        // assert
        testObserver.assertHasValue()
    }
}
