package com.truedigital.core.utils.networkconnection.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.truedigital.core.utils.networkconnection.NetworkConnectivityListener

internal class ActivityLifecycleCallbacksImp :
    Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity) {
        // DO NOTHING
    }

    override fun onActivityStarted(activity: Activity) {
        // DO NOTHING
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
        // DO NOTHING
    }

    override fun onActivityStopped(activity: Activity) {
        // DO NOTHING
    }

    override fun onActivityDestroyed(activity: Activity) {
        // DO NOTHING
    }
    override fun onActivityCreated(activity: Activity, p1: Bundle?) = safeRun(TAG) {
        if (activity !is LifecycleOwner) return

        if (activity is FragmentActivity)
            addLifecycleCallbackToFragments(activity)

        if (activity !is NetworkConnectivityListener || !activity.shouldBeCalled) return

        activity.onListenerCreated()
    }

    override fun onActivityResumed(activity: Activity) = safeRun {
        if (activity !is LifecycleOwner) return
        if (activity !is NetworkConnectivityListener) return

        activity.onListenerResume()
    }

    private fun addLifecycleCallbackToFragments(activity: FragmentActivity) = safeRun(
        TAG
    ) {

        val callback = object : FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentCreated(
                fm: FragmentManager,
                fragment: Fragment,
                savedInstanceState: Bundle?
            ) {
                if (fragment !is NetworkConnectivityListener || !fragment.shouldBeCalled) return
                fragment.onListenerCreated()
            }

            override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
                if (fragment is NetworkConnectivityListener)
                    fragment.onListenerResume()
            }
        }

        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(callback, true)
    }

    companion object {
        const val TAG = "ActivityCallbacks"
    }
}
