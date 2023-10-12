package com.truedigital.features.truecloudv3.navigation.router

import androidx.navigation.NavController
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.navigation.EditContactToContactSelectLabelBottomSheet
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class ContactEditRouterTest {

    private val navigationRouterRepository: NavigationRouterRepository = mockk()
    private val getNavigationControllerRepository: GetNavigationControllerRepository = mockk()
    private val navController: NavController = mockk()

    @Test
    fun testRouteMainToContactRouter() {
        val router = ContactEditRouterUseCaseImpl(
            navigationRouterRepository,
            getNavigationControllerRepository
        )

        every { getNavigationControllerRepository.getRouterToNavController() } returns navController
        // act
        every {
            navigationRouterRepository.routeTo(
                destination = any(),
                bundle = anyOrNull()
            )
        } just runs
        // arrange

        // act
        router.execute(destination = EditContactToContactSelectLabelBottomSheet, bundle = any())

        // assert
        verify(exactly = 1) {
            navController.navigateSafe(
                R.id.action_TrueCloudV3EditContactFragment_to_trueCloudV3ContactSelectLabelBottomSheetDialogFragment,
                null
            )
        }
    }
}
