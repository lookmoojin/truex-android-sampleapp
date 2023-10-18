package com.truedigital.features.truecloudv3.navigation.router

import androidx.navigation.NavController
import com.nhaarman.mockitokotlin2.anyOrNull
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.navigation.IntroMigrateToMigrating
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class IntroMigrateDataRouterTest {

    private val navigationRouterRepository: NavigationRouterRepository = mockk()
    private val getNavigationControllerRepository: GetNavigationControllerRepository = mockk()
    private val navController: NavController = mockk()

    @Test
    fun testRouteIntroMigrateToMigrating() {
        // arrange
        val router = IntroMigrateDataRouterUseCaseImpl(
            navigationRouterRepository,
            getNavigationControllerRepository
        )
        every {
            navigationRouterRepository.routeTo(
                destination = any(),
                bundle = anyOrNull()
            )
        } just runs
        every { getNavigationControllerRepository.getRouterToNavController() } returns navController
        // act
        router.execute(destination = IntroMigrateToMigrating, bundle = anyOrNull())

        // assert
        verify(exactly = 1) {
            navController.navigate(
                R.id.action_IntroMigrateDataFragment_to_MigrateDataFragment,
                null
            )
        }
    }
}
