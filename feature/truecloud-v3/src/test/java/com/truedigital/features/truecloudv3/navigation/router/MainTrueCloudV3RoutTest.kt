package com.truedigital.features.truecloudv3.navigation.router

import androidx.navigation.NavController
import com.nhaarman.mockitokotlin2.anyOrNull
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.navigation.MainToIntroMigrate
import com.truedigital.features.truecloudv3.navigation.MainToMigrate
import com.truedigital.features.truecloudv3.navigation.MainToSetting
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MainTrueCloudV3RoutTest {
    private val navController = mockk<NavController>()
    private val navigationRouterRepository: NavigationRouterRepository = mockk()
    private val getNavigationControllerRepository: GetNavigationControllerRepository = mockk()
    private lateinit var router: MainTrueCloudV3RouterUseCase

    @BeforeEach
    fun setup() {
        router = MainTrueCloudV3RouterUseCaseImpl(
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
    }

    @Test
    fun testRouteMainToIntroMigrate() {
        // act
        router.execute(destination = MainToIntroMigrate, bundle = anyOrNull())
        // assert
        verify(exactly = 1) {
            navController.navigateSafe(
                R.id.action_mainTrueCloudFragment_to_introMigrateDataFragment,
                null
            )
        }
    }

    @Test
    fun testRouteMainToSetting() {
        // act
        router.execute(MainToSetting)

        // assert
        verify(exactly = 1) {
            navController.navigateSafe(
                R.id.action_mainTrueCloudFragment_to_settingTrueCloudV3Fragment,
                null
            )
        }
    }

    @Test
    fun testRouteMainToMigrate() {
        // act
        router.execute(MainToMigrate)

        // assert
        verify(exactly = 1) {
            navController.navigateSafe(
                R.id.action_mainTrueCloudFragment_to_MigrateDataFragment,
                null
            )
        }
    }
}
