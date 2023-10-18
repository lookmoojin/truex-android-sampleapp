package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.navigation.OptingMainToCreateNewFolder
import com.truedigital.features.truecloudv3.navigation.OptingMainToSortByBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class OptionMainFileBottomSheetDialogViewModelTest {
    private val router: FileTrueCloudRouterUseCase = mockk()
    private lateinit var viewModel: OptionMainFileBottomSheetDialogViewModel
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        viewModel = OptionMainFileBottomSheetDialogViewModel(
            router = router,
            analyticManagerInterface = analyticManagerInterface
        )
    }

    @Test
    fun `test onClickSortBy success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickSortBy()

        // assert
        verify(exactly = 1) { router.execute(OptingMainToSortByBottomSheet, any()) }
    }

    @Test
    fun `test onClickCreateNewFolder success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickCreateNewFolder()

        // assert
        verify(exactly = 1) { router.execute(OptingMainToCreateNewFolder, any()) }
    }

    @Test
    fun `test onReceiveSortBy success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSortBy)

        // act
        viewModel.setSortType(SortType.SORT_DATE_DESC)
        viewModel.onReceiveSortBy(SortType.SORT_DATE_DESC)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onReceiveCreateFolder success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onCreateNewFolder)

        // act
        viewModel.onReceiveCreateFolder("folder name test")

        // assert
        testObserver.assertHasValue()
    }
}
