package com.truedigital.features.truecloudv3.navigation.router

import androidx.navigation.NavController
import com.nhaarman.mockitokotlin2.anyOrNull
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.navigation.SettingToMigrating
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class SettingTrueCloudV3RouterTest {

    private val navigationRouterRepository: NavigationRouterRepository = mockk()
    private val getNavigationControllerRepository: GetNavigationControllerRepository = mockk()
    private val navController: NavController = mockk()

    @Test
    fun testRouteSettingToMigrating() {
        // arrange

        val router = SettingTrueCloudV3RouterUseCaseImpl(
            navigationRouterRepository,
            getNavigationControllerRepository,
        )
        every {
            navigationRouterRepository.routeTo(
                destination = any(),
                bundle = anyOrNull(),
            )
        } just runs
        every { getNavigationControllerRepository.getRouterToNavController() } returns navController
        // act
        router.execute(destination = SettingToMigrating, bundle = anyOrNull())

        // assert
        verify(exactly = 1) {
            navController.navigateSafe(
                R.id.action_SettingTrueCloudv3Fragment_to_MigrateDataFragment,
                null,
            )
        }
    }
}
