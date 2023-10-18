package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3ObjectInfoModel
import com.truedigital.features.truecloudv3.domain.usecase.GetObjectInfoUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class TrueCloudV3FileViewerViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val getObjectInfoUseCase: GetObjectInfoUseCase = mockk()
    private lateinit var viewModel: TrueCloudV3FileViewerViewModel

    @BeforeEach
    fun setUp() {
        viewModel = TrueCloudV3FileViewerViewModel(
            coroutineDispatcher = coroutineDispatcher,
            getObjectInfoUseCase = getObjectInfoUseCase,
        )
    }

    @Test
    fun `test setObjectFile success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowPreview)
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val objectInfoModel = TrueCloudV3ObjectInfoModel(
            id = "id",
            parentObjectId = "parentObjectId",
            fileUrl = "fileUrl",
        )
        every {
            getObjectInfoUseCase.execute(any())
        } returns flowOf(objectInfoModel)

        // act
        viewModel.setObjectFile(file)

        // assert
        coVerify(exactly = 1) { getObjectInfoUseCase.execute(any()) }
        testObserver.assertHasValue()
    }

    @Test
    fun `test setObjectFile onError`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowPreview)
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        every {
            getObjectInfoUseCase.execute(any())
        } coAnswers { flow { error("mock error") } }

        // act
        viewModel.setObjectFile(file)

        // assert
        coVerify(exactly = 1) { getObjectInfoUseCase.execute(any()) }
        testObserver.assertNoValue()
    }
}
