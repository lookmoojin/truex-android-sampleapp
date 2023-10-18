package com.truedigital.features.truecloudv3.navigation.router

import androidx.navigation.NavController
import com.nhaarman.mockitokotlin2.any
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.navigation.ContactDetailToEditContactFragment
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class ContactDetailRouterTest {

    private val navigationRouterRepository: NavigationRouterRepository = mockk()
    private val getNavigationControllerRepository: GetNavigationControllerRepository = mockk()
    private val navController: NavController = mockk()

    @Test
    fun testRouteMainToContactRouter() {
        // arrange

        val router = ContactDetailRouterUseCaseImpl(
            navigationRouterRepository,
            getNavigationControllerRepository
        )

        every { getNavigationControllerRepository.getRouterToNavController() } returns navController
        every { navigationRouterRepository.routeTo(any()) } just runs
        // act
        router.execute(destination = ContactDetailToEditContactFragment, bundle = any())

        // assert
        verify(exactly = 1) {
            navController.navigateSafe(
                R.id.action_TrueCloudV3DetailBottomSheetDialogFragment_to_trueCloudV3EditContactFragment,
                null
            )
        }
    }
}
