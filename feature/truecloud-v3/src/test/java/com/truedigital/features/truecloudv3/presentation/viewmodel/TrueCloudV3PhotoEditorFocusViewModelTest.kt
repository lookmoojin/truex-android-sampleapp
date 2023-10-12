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
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class TrueCloudV3PhotoEditorFocusViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private lateinit var viewModel: TrueCloudV3PhotoEditorFocusViewModel

    @BeforeEach
    fun setUp() {
        viewModel = TrueCloudV3PhotoEditorFocusViewModel()
    }

    @Test
    fun `test setBrush`() = runTest {
        // arrange
        val testResults = mutableListOf<Pair<Int?, Double>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentBrush.toList(testResults)
        }

        // act
        viewModel.setBrushColor(1)
        viewModel.setBrushSize(50.0)
        advanceTimeBy(1000L)
        runCurrent()
        viewModel.setBrushColor(2)
        advanceTimeBy(500L)
        runCurrent()
        viewModel.setBrushColor(3)
        advanceTimeBy(1000L)
        runCurrent()
        viewModel.setBrushSize(10.0)
        runCurrent()
        viewModel.setBrushSize(20.0)
        advanceTimeBy(1000L)
        runCurrent()

        // assert
        MatcherAssert.assertThat(
            testResults,
            CoreMatchers.equalTo(
                listOf(
                    1 to 50.0,
                    2 to 50.0,
                    3 to 50.0,
                    3 to 20.0,
                )
            ),
        )
    }
}
