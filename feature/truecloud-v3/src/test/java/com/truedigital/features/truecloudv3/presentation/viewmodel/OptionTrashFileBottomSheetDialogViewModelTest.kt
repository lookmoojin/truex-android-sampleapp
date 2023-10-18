package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class OptionTrashFileBottomSheetDialogViewModelTest {

    private lateinit var viewModel: OptionTrashFileBottomSheetDialogViewModel
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        viewModel = OptionTrashFileBottomSheetDialogViewModel(
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
        // act
        viewModel.init(fileList)
    }

    @Test
    fun `test onClickRestore success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onRestore)
        // act
        viewModel.onClickRestore()
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickDelete success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onConfirmDelete)
        // act
        viewModel.onClickDelete()
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onDeleteSelectItem success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.clickDelete)
        // act
        viewModel.deleteSelectedItem()
        // assert
        testObserver.assertHasValue()
    }
}
