package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class TrueCloudV3PhotoEditorAdjustViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private lateinit var viewModel: TrueCloudV3PhotoEditorAdjustViewModel

    @BeforeEach
    fun setUp() {
        viewModel = TrueCloudV3PhotoEditorAdjustViewModel()
    }

    @Test
    fun `test setEffect`() = runTest {
        // arrange
        val testResults =
            mutableListOf<Pair<TrueCloudV3PhotoEditorAdjustViewModel.UiEffect?, Double>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentEffect.toList(testResults)
        }

        // act
        viewModel.setEffect(TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.BRIGHTNESS)
        viewModel.setScale(50.0)
        advanceTimeBy(1000L)
        runCurrent()
        viewModel.setEffect(TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.CONTRAST)
        advanceTimeBy(500L)
        runCurrent()
        viewModel.setEffect(TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.SATURATION)
        advanceTimeBy(1000L)
        runCurrent()
        viewModel.setScale(10.0)
        runCurrent()
        viewModel.setScale(20.0)
        advanceTimeBy(1000L)
        runCurrent()

        // assert
        assertThat(
            testResults,
            equalTo(
                listOf(
                    TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.BRIGHTNESS to 50.0,
                    TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.SATURATION to 50.0,
                    TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.SATURATION to 10.0,
                    TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.SATURATION to 20.0,
                )
            ),
        )
    }
}
