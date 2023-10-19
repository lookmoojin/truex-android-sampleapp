package com.truedigital.core

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.startup.Initializer
import com.livefront.bridge.Bridge
import com.livefront.bridge.SavedStateHandler
import com.livefront.bridge.ViewSavedStateHandler
import com.truedigital.core.di.CommonCoreModule
import com.truedigital.core.extensions.NotLoggingTree
import com.truedigital.core.injections.CoreComponent
import com.truedigital.core.injections.DaggerCoreComponent
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder.registerConnectivityBroadcaster
import com.truedigital.foundation.FoundationApplication
import icepick.Icepick
import timber.log.Timber

class CoreInitializer : Initializer<CoreComponent> {

    override fun create(context: Context): CoreComponent {
        val app = (context as Application)
        app.registerConnectivityBroadcaster()

        initBridge(app)
        initTimberLog()

        return DaggerCoreComponent.builder()
            .foundationComponent(FoundationApplication.foundationComponent(context))
            .commonCoreModule(CommonCoreModule(app))
            .build()
            .apply {
                CoreComponent.initialize(this)
            }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    private fun initTimberLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(NotLoggingTree())
        }
    }

    private fun initBridge(application: Application) {
        Bridge.initialize(
            application,
            object : SavedStateHandler {
                override fun saveInstanceState(@NonNull target: Any, @NonNull state: Bundle) {
                    Icepick.saveInstanceState(target, state)
                }

                override fun restoreInstanceState(@NonNull target: Any, @Nullable state: Bundle?) {
                    Icepick.restoreInstanceState(target, state)
                }
            },
            object : ViewSavedStateHandler {
                override fun <T : View?> saveInstanceState(
                    target: T & Any,
                    parentState: Parcelable?
                ): Parcelable {
                    return Icepick.saveInstanceState(target, parentState)
                }

                override fun <T : View?> restoreInstanceState(
                    target: T & Any,
                    state: Parcelable?
                ): Parcelable? {
                    return Icepick.restoreInstanceState(target, state)
                }
            }
        )
    }
}
