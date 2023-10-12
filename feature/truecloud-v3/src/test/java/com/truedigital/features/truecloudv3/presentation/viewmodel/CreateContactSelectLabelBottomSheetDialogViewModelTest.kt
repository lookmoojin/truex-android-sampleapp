package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.features.truecloudv3.navigation.ContactSelectLabelBottomSheetToCustomLabelDialog
import com.truedigital.features.truecloudv3.navigation.router.ContactSelectLabelRouterUseCase
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
class CreateContactSelectLabelBottomSheetDialogViewModelTest {
    private val router: ContactSelectLabelRouterUseCase = mockk()
    private lateinit var viewModel: CreateContactSelectLabelBottomSheetDialogViewModel
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        viewModel = CreateContactSelectLabelBottomSheetDialogViewModel(
            router = router,
            analyticManagerInterface = analyticManagerInterface
        )
    }

    @Test
    fun `test Setting callMigrate success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickCustom()

        // assert
        verify(exactly = 1) { router.execute(ContactSelectLabelBottomSheetToCustomLabelDialog, any()) }
    }

    @Test
    fun `test setPhoneLabel success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetupView)
        val customPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 111
        )
        // act
        viewModel.setPhoneLabel(customPhoneLabelModel)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickLabel success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSelected)

        // act
        viewModel.onClickLabel("label test")

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onCompleteCustomLabel success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSelected)
        val customPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 111
        )
        // act
        viewModel.onCompleteCustomLabel(customPhoneLabelModel)

        // assert
        testObserver.assertHasValue()
    }
}
