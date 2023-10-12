package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.exifinterface.media.ExifInterface
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.provider.ExifProvider
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

@ExtendWith(InstantTaskExecutorExtension::class)
class FileInfoBottomSheetDialogViewModelTest {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private val fileProvider = mockk<FileProvider>()
    private val contextDataProviderWrapper = mockk<ContextDataProviderWrapper>()
    private val mockExifProvider = mockk<ExifProvider>()
    private lateinit var viewModel: FileInfoBottomSheetDialogViewModel
    private val mockCoroutineDispatcher: CoroutineDispatcherProvider =
        TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @BeforeEach
    fun setUp() {
        viewModel = FileInfoBottomSheetDialogViewModel(
            coroutineDispatcher = mockCoroutineDispatcher,
            fileProvider = fileProvider,
            contextDataProviderWrapper = contextDataProviderWrapper,
            exifProvider = mockExifProvider
        )
    }

    @Test
    fun `test onViewCreated success exists false`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetUpData)
        val file1 = TrueCloudFilesModel.File(
            id = "1",
            name = "namefile1",
            size = "100 mb"
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every { contextDataProviderWrapper.get().getString(any()) } returns "titleName"

        val mockFile = mockk<File>(relaxed = true)

        every {
            fileProvider.getFile(any())
        } returns mockFile
        every {
            fileProvider.getFile(any(), any())
        } returns mockFile
        every { mockFile.exists() } returns false
        every { mockFile.mkdir() } returns true
        every { mockFile.path } returns "pathx/"
        every { mockFile.absolutePath } returns "pathx/absolutePath"

        // act
        viewModel.onViewCreated(file1)

        // assert
        testObserver.assertHasValue()
        testObserver.assertValue { _value ->
            _value[0].second == "namefile1" &&
                _value[1].second == "100 mb"
        }
    }
    @Disabled
    @Test
    fun `test onViewCreated success onSetMaxHeightView more than max`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetMaxHeightView)
        val file1 = TrueCloudFilesModel.File(
            id = "1",
            name = "namefile1",
            size = "100 mb"
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every { contextDataProviderWrapper.get().getString(any()) } returns "titleName"

        val mockFile = mockk<File>()
        val mockExifInterface = mockk<ExifInterface>()

        every {
            fileProvider.getFile(any())
        } returns mockFile
        every {
            fileProvider.getFile(any(), any())
        } returns mockFile
        every { mockFile.exists() } returns true
        every { mockFile.mkdir() } returns true
        every { mockFile.path } returns "pathx/"
        every { mockFile.absolutePath } returns "pathx/absolutePath"
        every { mockExifProvider.getExif(any()) } returns mockExifInterface
        every { mockExifInterface.getAttribute(any()) } returns "getAttribute"
        every { mockExifInterface.getAttribute(ExifInterface.TAG_DATETIME) } returns "DATETIME"

        // act
        viewModel.onViewCreated(file1)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onViewCreated success onSetMaxHeightView less than max`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetMaxHeightView)
        val file1 = TrueCloudFilesModel.File(
            id = "1",
            name = "namefile1",
            size = "100 mb"
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every { contextDataProviderWrapper.get().getString(any()) } returns "titleName"

        val mockFile = mockk<File>()
        val mockExifInterface = mockk<ExifInterface>()

        every {
            fileProvider.getFile(any())
        } returns mockFile
        every {
            fileProvider.getFile(any(), any())
        } returns mockFile
        every { mockFile.exists() } returns true
        every { mockFile.mkdir() } returns true
        every { mockFile.path } returns "pathx/"
        every { mockFile.absolutePath } returns "pathx/absolutePath"
        every { mockExifProvider.getExif(any()) } returns mockExifInterface

        // act
        viewModel.onViewCreated(file1)

        // assert
        testObserver.assertNoValue()
    }

    @Test
    fun `test onViewCreated success exists true`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetUpData)
        val file1 = TrueCloudFilesModel.File(
            id = "1",
            name = "namefile1",
            size = "100 mb"
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every { contextDataProviderWrapper.get().getString(any()) } returns "titleName"

        val mockFile = mockk<File>()
        val mockExifInterface = mockk<ExifInterface>()

        every {
            fileProvider.getFile(any())
        } returns mockFile
        every {
            fileProvider.getFile(any(), any())
        } returns mockFile
        every { mockFile.exists() } returns true
        every { mockFile.mkdir() } returns true
        every { mockFile.path } returns "pathx/"
        every { mockFile.absolutePath } returns "pathx/absolutePath"
        every { mockExifProvider.getExif(any()) } returns mockExifInterface
        every { mockExifInterface.getAttribute(any()) } returns "getAttribute"
        every { mockExifInterface.getAttribute(ExifInterface.TAG_DATETIME) } returns "DATETIME"

        // act
        viewModel.onViewCreated(file1)

        // assert
        testObserver.assertHasValue()
        testObserver.assertValue { _value ->
            _value[0].second == "namefile1" &&
                _value[1].second == "DATETIME" &&
                _value[2].second == "getAttribute" &&
                _value[3].second == "getAttribute" &&
                _value[4].second == "100 mb"
        }
    }
    @Test
    fun `test onViewCreated success exists true and null value`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetUpData)
        val file1 = TrueCloudFilesModel.File(
            id = "1",
            name = "namefile1",
            size = "100 mb"
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every { contextDataProviderWrapper.get().getString(any()) } returns "titleName"

        val mockFile = mockk<File>()
        val mockExifInterface = mockk<ExifInterface>()

        every {
            fileProvider.getFile(any())
        } returns mockFile
        every {
            fileProvider.getFile(any(), any())
        } returns mockFile
        every { mockFile.exists() } returns true
        every { mockFile.mkdir() } returns true
        every { mockFile.path } returns "pathx/"
        every { mockFile.absolutePath } returns "pathx/absolutePath"
        every { mockExifProvider.getExif(any()) } returns mockExifInterface
        every { mockExifInterface.getAttribute(any()) } returns null

        // act
        viewModel.onViewCreated(file1)

        // assert
        testObserver.assertHasValue()
        testObserver.assertValue { _value ->
            _value[0].second == "namefile1" &&
                _value[1].second == "100 mb"
        }
    }
}
