package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
class SaveSharedFileBottomSheetDialogViewModelTest {
    private lateinit var viewModel: SaveSharedFileBottomSheetDialogViewModel
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        viewModel = SaveSharedFileBottomSheetDialogViewModel(
            analyticManagerInterface = analyticManagerInterface
        )
    }

    @Test
    fun `test setSharedId`() = runTest {
        viewModel.setId("id")
        assertEquals("id", viewModel.sharedId)
    }

    @Test
    fun `test setShareAccessToken`() = runTest {
        viewModel.setToken("token")
        assertEquals("token", viewModel.sharedToken)
    }

    @Test
    fun `test onSaveToDeviceClick`() = runTest {
        val testObserver = TestObserver.test(viewModel.onSaveToDevice)
        val expectedValue: Pair<String?, String?> = Pair("id", "token")
        viewModel.setId("id")
        viewModel.setToken("token")
        viewModel.onSaveToDeviceClick()
        testObserver.assertValue(expectedValue)
    }

    @Test
    fun `test onSaveToCloudClick`() = runTest {
        val testObserver = TestObserver.test(viewModel.onSaveToCloud)
        val expectedValue: Pair<String?, String?> = Pair("id", "token")
        viewModel.setId("id")
        viewModel.setToken("token")
        viewModel.onSaveToCloudClick()
        testObserver.assertValue(expectedValue)
    }
}
