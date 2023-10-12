package com.truedigital.features.truecloudv3.navigation.router

import androidx.navigation.NavController
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.navigation.FileViewerToOptionFileBottomSheet
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class TrueCloudV3FileViewerRouterTest {
    private val navController = mockk<NavController>()
    private val navigationRouterRepository: NavigationRouterRepository = mockk()
    private val getNavigationControllerRepository: GetNavigationControllerRepository = mockk()

    @Test
    fun testRouteMainToContactRouter() {
        // arrange
        val router: TrueCloudV3FileViewerRouterUseCase = TrueCloudV3FileViewerRouterUseCaseImpl(
            navigationRouterRepository,
            getNavigationControllerRepository
        )

        // act
        every {
            navigationRouterRepository.routeTo(
                destination = any(),
                bundle = anyOrNull()
            )
        } just runs
        every { getNavigationControllerRepository.getRouterToNavController() } returns navController
        router.execute(destination = FileViewerToOptionFileBottomSheet, bundle = any())

        verify(exactly = 1) {
            navController.navigateSafe(
                R.id.action_FileViewerFragment_to_optionFileBottomSheetDialogFragment,
                any()
            )
        }
    }
}
