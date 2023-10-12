package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Context
import androidx.navigation.NavController
import com.truedigital.features.truecloudv3.navigation.IntroSharedToSharedViewer
import com.truedigital.features.truecloudv3.navigation.router.IntroTrueCloudRouterUseCase
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class IntroTrueCloudSharedFileViewModelTest {
    private lateinit var viewModel: IntroTrueCloudSharedFileViewModel
    private val router: IntroTrueCloudRouterUseCase = mockk(relaxed = true)
    private val setRouterToNavControllerUseCase: SetRouterToNavControllerUseCase =
        mockk(relaxed = true)
    private val context = mockk<Context>()
    private val getNavigationControllerRepository: GetNavigationControllerRepository =
        mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        viewModel = IntroTrueCloudSharedFileViewModel(
            router = router,
            setRouterToNavControllerUseCase = setRouterToNavControllerUseCase
        )
    }

    @Test
    fun `test setRouterToNavController`() = runTest {
        val navController = NavController(context)
        viewModel.setRouterToNavController(navController)
        assertNotNull(getNavigationControllerRepository.getRouterToNavController())
    }

    @Test
    fun `test navigateToSharedFileViewer`() = runTest {
        every { router.execute(destination = any(), bundle = any()) } just runs
        viewModel.navigateToSharedFileViewer("")
        verify(exactly = 1) {
            router.execute(
                destination = IntroSharedToSharedViewer,
                bundle = any()
            )
        }
    }
}
