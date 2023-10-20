package com.tdcm.trueidapp.startuptrace

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import java.util.concurrent.TimeUnit

object StartupTrace : Application.ActivityLifecycleCallbacks, LifecycleObserver,
    LifecycleEventObserver {

    private val MAX_LATENCY_BEFORE_UI_INIT = TimeUnit.MINUTES.toMillis(1)

    var appStartTime: Long? = null
    private var onCreateTime: Long? = null
    var isStartedFromBackground = false
    var atLeastOnTimeOnBackground = false

    private var isRegisteredForLifecycleCallbacks = false
    private var appContext: Context? = null

//    private var trace: Trace? = null

    /**
     * If the time difference between app starts and creation of any Activity is larger than
     * MAX_LATENCY_BEFORE_UI_INIT, set isTooLateToInitUI to true and we don't send AppStart Trace.
     */
    var isTooLateToInitUI = false

    /**
     * We use StartFromBackgroundRunnable to detect if app is started from background or foreground.
     * If app is started from background, we do not generate AppStart trace. This runnable is posted
     * to main UI thread from StartupTimeProvider. If app is started from background, this runnable
     * will be executed before any activity's onCreate() method. If app is started from foreground,
     * activity's onCreate() method is executed before this runnable.
     */
    object StartFromBackgroundRunnable : Runnable {
        override fun run() {
            // if no activity has ever been created.
            if (onCreateTime == null) {
                isStartedFromBackground = true
            }
        }
    }

    fun onColdStartInitiated(context: Context) {
        appStartTime = System.currentTimeMillis()
//        trace = Firebase.performance.newTrace("cold_startup_time")
//        trace!!.start()

        val appContext = context.applicationContext
        if (appContext is Application) {
            appContext.registerActivityLifecycleCallbacks(this)
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
            isRegisteredForLifecycleCallbacks = true
            this.appContext = appContext
        }
    }

    private fun unregisterActivityLifecycleCallbacks() {
        if (!isRegisteredForLifecycleCallbacks) {
            return
        }
        (appContext as Application).unregisterActivityLifecycleCallbacks(this)
        isRegisteredForLifecycleCallbacks = false
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        if (isStartedFromBackground || onCreateTime != null) {
            return
        }
        onCreateTime = System.currentTimeMillis()

        if ((onCreateTime!! - appStartTime!!) > MAX_LATENCY_BEFORE_UI_INIT) {
            isTooLateToInitUI = true
        }
    }

    override fun onActivityStarted(p0: Activity) {
        // Do nothing
    }

    override fun onActivityResumed(activity: Activity) {
        if (isStartedFromBackground || isTooLateToInitUI || atLeastOnTimeOnBackground) {
            unregisterActivityLifecycleCallbacks()
            return
        }

//        if (activity !is SplashActivity) {
//            Timber.i("FirebaseApp Cold start finished after ${System.currentTimeMillis() - appStartTime!!} ms")
//            trace?.stop()
//            trace = null
//
//            if (isRegisteredForLifecycleCallbacks) {
//                unregisterActivityLifecycleCallbacks()
//            }
//        }
    }

    override fun onActivityPaused(p0: Activity) {
        // Do nothing
    }

    override fun onActivityStopped(p0: Activity) {
        // Do nothing
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        // Do nothing
    }

    override fun onActivityDestroyed(p0: Activity) {
        // Do nothing
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_STOP) {
            atLeastOnTimeOnBackground = true
            ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        }
    }
}
