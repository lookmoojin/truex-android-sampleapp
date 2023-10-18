package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class CreateContactCustomLabelDialogViewModelTest {
    private lateinit var viewModel: CreateContactCustomLabelDialogViewModel

    @BeforeEach
    fun setUp() {
        viewModel = CreateContactCustomLabelDialogViewModel()
    }

    @Test
    fun `test CustomLabel setPhoneLabel success`() {
        // arrange
        val customPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 111
        )
        viewModel.customPhoneLabelModel = customPhoneLabelModel
        // act
        viewModel.setPhoneLabel(customPhoneLabelModel)
    }

    @Test
    fun `test onClickLabel success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onLableCustomFinish)

        // act
        viewModel.onClickCreateLabel("label test")

        // assert
        testObserver.assertHasValue()
    }
}
