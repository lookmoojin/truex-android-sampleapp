package com.truedigital.features.truecloudv3.navigation.router

import androidx.navigation.NavController
import com.nhaarman.mockitokotlin2.anyOrNull
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.navigation.MainToContact
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class ContactListRouterTest {

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
        every { getNavigationControllerRepository.getRouterToNavController() } returns navController
        // act
        every {
            navigationRouterRepository.routeTo(
                destination = any(),
                bundle = anyOrNull()
            )
        } just runs
        // act
        router.execute(destination = MainToContact, bundle = anyOrNull())

        // assert
        verify(exactly = 1) {
            navController.navigate(
                R.id.action_mainTrueCloudFragment_to_contactFragment,
                null
            )
        }
    }
}
