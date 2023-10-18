package com.truedigital.navigation.data.repository.router

import android.os.Bundle
import androidx.navigation.NavOptions
import com.truedigital.navigation.router.CrossRouter
import com.truedigital.navigation.router.Destination
import javax.inject.Inject

interface NavigationRouterRepository {

    fun routeTo(destination: Destination, bundle: Bundle? = null)

    fun routeWithDeeplink(stringUrl: String, navOptions: NavOptions? = null)

    fun getLastDestination(): Destination?
}

class NavigationRouterRepositoryImpl @Inject constructor(
    private val crossRouter: CrossRouter
) :
    NavigationRouterRepository {

    var latestRoute: Destination? = null

    override fun routeTo(destination: Destination, bundle: Bundle?) {
        latestRoute = destination
    }

    override fun routeWithDeeplink(stringUrl: String, navOptions: NavOptions?) {
        crossRouter.routeWithDeeplink(stringUrl, navOptions)
    }

    override fun getLastDestination(): Destination? {
        return latestRoute
    }
}
