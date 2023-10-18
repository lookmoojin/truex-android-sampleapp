package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.navigation.OptionFileSelectedToFileLocatorFragment
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
class OptionFileSelectedBottomSheetDialogViewModelTest {

    private val router: FileTrueCloudRouterUseCase = mockk()
    private lateinit var viewModel: OptionFileSelectedBottomSheetDialogViewModel
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)
    @BeforeEach
    fun setUp() {
        viewModel = OptionFileSelectedBottomSheetDialogViewModel(
            fileTrueCloudRouterUseCase = router,
            analyticManagerInterface = analyticManagerInterface
        )
        val fileList = arrayListOf(
            TrueCloudFilesModel.File(
                id = "id1"
            ),
            TrueCloudFilesModel.File(
                id = "id2"
            )
        )
        val folderId = "id"
        val category = "UNSUPPORTED_FORMAT"
        // act
        viewModel.init(fileList, folderId, category)
    }
    @Test
    fun `test onClickMove success`() = runTest {
        // arrange
        every { router.execute(any(), any()) } just runs
        // act
        viewModel.onClickMoveTo()

        // assert
        verify(exactly = 1) { router.execute(OptionFileSelectedToFileLocatorFragment, any()) }
    }

    @Test
    fun `test File onClickCopy success`() = runTest {
        // arrange
        every { router.execute(any(), any()) } just runs
        // act
        viewModel.onClickCopy()
        // assert
        verify(exactly = 1) { router.execute(OptionFileSelectedToFileLocatorFragment, any()) }
    }

    @Test
    fun `test File onOpenPageMove success`() = runTest {
        // arrange
        every { router.execute(any(), any()) } just runs
        // act
        viewModel.openPage("move")
        // assert
        verify(exactly = 1) { router.execute(OptionFileSelectedToFileLocatorFragment, any()) }
    }

    @Test
    fun `test File onOpenPageCopy success`() = runTest {
        // arrange
        every { router.execute(any(), any()) } just runs
        // act
        viewModel.openPage("copy")
        // assert
        verify(exactly = 1) { router.execute(OptionFileSelectedToFileLocatorFragment, any()) }
    }

    @Test
    fun `test category not null`() = runTest {
        // arrange
        // act
        viewModel.categoryType
        // assert
        assertEquals(FileCategoryType.UNSUPPORTED_FORMAT, viewModel.categoryType)
    }

    @Test
    fun `test onSelectMode true success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onDisableSelectFileMode)
        // act
        viewModel.onSetDisableSelectFileMode(true)
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onDeleteSelectedItem success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.clickDelete)
        val fileList = arrayListOf(
            TrueCloudFilesModel.File(
                id = "id1"
            ),
            TrueCloudFilesModel.File(
                id = "id2"
            )
        )
        // act
        viewModel.deleteSelectedItem()
        // assert
        testObserver.assertValue(fileList)
    }
}
