package com.truedigital.navigation.usecase

import android.app.Activity
import androidx.navigation.NavController
import com.truedigital.component.base.core.CoreActivity
import javax.inject.Inject

interface GetNavigationControllerRepository {

    var activity: Activity?
    var cacheFirstNavController: NavController?
    var cacheSecondaryNavController: NavController?

    fun setRouterToNavController(navController: NavController)
    fun setRouterSecondaryToNavController(navController: NavController)
    fun setCrossRouterNavController(activity: Activity)
    fun getRouterToNavController(): NavController?
    fun getRouterSecondaryToNavController(): NavController?
    fun getCrossRouterNavController(): NavController?
}

class GetNavigationControllerRepositoryImpl @Inject constructor() :
    GetNavigationControllerRepository {

    override var activity: Activity? = null
    override var cacheFirstNavController: NavController? = null
    override var cacheSecondaryNavController: NavController? = null

    override fun setRouterToNavController(navController: NavController) {
        this.cacheFirstNavController = navController
    }

    override fun setRouterSecondaryToNavController(navController: NavController) {
        this.cacheSecondaryNavController = navController
    }

    override fun setCrossRouterNavController(activity: Activity) {
        this.activity = activity
    }

    override fun getRouterToNavController(): NavController? {
        return cacheFirstNavController
    }

    override fun getRouterSecondaryToNavController(): NavController? {
        return cacheSecondaryNavController
    }

    override fun getCrossRouterNavController(): NavController? {
        var navController: NavController? = null
        runCatching {
            (activity as? CoreActivity)?.getNavController()
        }.onSuccess {
            navController = it
        }
        return navController
    }
}
