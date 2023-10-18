package com.truedigital.features.truecloudv3.navigation.router

import androidx.navigation.NavController
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.navigation.ContactSelectLabelBottomSheetToCustomLabelDialog
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class ContactSelectLabelRouterTest {

    private val navigationRouterRepository: NavigationRouterRepository = mockk()
    private val getNavigationControllerRepository: GetNavigationControllerRepository = mockk()
    private val navController: NavController = mockk()

    @Test
    fun testRouteMainToContactRouter() {
        // arrange

        val router = ContactSelectLabelRouterUseCaseImpl(
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
        router.execute(
            destination = ContactSelectLabelBottomSheetToCustomLabelDialog,
            bundle = any()
        )
        // assert
        verify(exactly = 1) {
            navController.navigateSafe(
                R.id.action_TrueCloudV3ContactSelectLabelBottomSheetDialogFragment_to_trueCloudV3CustomLabelDialogFragment,
                null
            )
        }
    }
}
