package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class TrueCloudV3PhotoEditorTextViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private lateinit var viewModel: TrueCloudV3PhotoEditorTextViewModel

    @BeforeEach
    fun setUp() {
        viewModel = TrueCloudV3PhotoEditorTextViewModel()
    }

    @Test
    fun `test onConfirmClick Main`() = runTest {
        // arrange
        val onSaveSettingTestObserver = TestObserver.test(viewModel.onSaveSetting)
        val onResetMenuTestObserver = TestObserver.test(viewModel.onResetMenu)

        // act
        viewModel.onConfirmClick(0)

        // assert
        onSaveSettingTestObserver.assertHasValue()
        onResetMenuTestObserver.assertNoValue()
    }

    @Test
    fun `test onConfirmClick Not Main`() = runTest {
        // arrange
        val onSaveSettingTestObserver = TestObserver.test(viewModel.onSaveSetting)
        val onResetMenuTestObserver = TestObserver.test(viewModel.onResetMenu)

        // act
        viewModel.onConfirmClick(1)

        // assert
        onSaveSettingTestObserver.assertNoValue()
        onResetMenuTestObserver.assertHasValue()
    }

    @Test
    fun `test onBackClick Main`() = runTest {
        // arrange
        val onShowConfirmTestObserver = TestObserver.test(viewModel.onShowConfirm)
        val onResetMenuTestObserver = TestObserver.test(viewModel.onResetMenu)

        // act
        viewModel.onBackClick(0)

        // assert
        onShowConfirmTestObserver.assertHasValue()
        onResetMenuTestObserver.assertNoValue()
    }

    @Test
    fun `test onBackClick Not Main`() = runTest {
        // arrange
        val onShowConfirmTestObserver = TestObserver.test(viewModel.onShowConfirm)
        val onResetMenuTestObserver = TestObserver.test(viewModel.onResetMenu)

        // act
        viewModel.onBackClick(1)

        // assert
        onShowConfirmTestObserver.assertNoValue()
        onResetMenuTestObserver.assertHasValue()
    }
}
