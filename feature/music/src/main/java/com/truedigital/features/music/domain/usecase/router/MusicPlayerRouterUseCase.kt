package com.truedigital.features.music.domain.usecase.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavOptions
import com.truedigital.features.music.navigation.router.MusicPlayerToPlayerQueue
import com.truedigital.features.tuned.presentation.player.view.PlayerQueueActivity
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.router.Destination
import javax.inject.Inject

interface MusicPlayerRouterUseCase {
    fun execute(destination: Destination, bundle: Bundle? = null, activity: Activity)
    fun execute(stringUrl: String, navOptions: NavOptions? = null)
    fun getLastDestination(): Destination?
}

class MusicPlayerRouterUseCaseImpl @Inject constructor(
    private val navigationRouterRepository: NavigationRouterRepository,
) : MusicPlayerRouterUseCase {
    override fun execute(destination: Destination, bundle: Bundle?, activity: Activity) {
        navigationRouterRepository.routeTo(destination, bundle)

        when (destination) {
            MusicPlayerToPlayerQueue -> {
                activity.startActivity(
                    Intent(activity, PlayerQueueActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    ),
                    bundle
                )
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun execute(stringUrl: String, navOptions: NavOptions?) {
        navigationRouterRepository.routeWithDeeplink(stringUrl, navOptions)
    }

    override fun getLastDestination(): Destination? {
        return navigationRouterRepository.getLastDestination()
    }
}
