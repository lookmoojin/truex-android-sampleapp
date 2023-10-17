package com.truedigital.features.apppermission

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.analytics.AnalyticsInitializer
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent
import com.truedigital.features.apppermission.injections.AppPerMissionComponent
import com.truedigital.features.apppermission.injections.DaggerAppPerMissionComponent
import com.truedigital.features.onboarding.OnBoardingInitializer
import com.truedigital.features.onboarding.injections.OnBoardingComponent
import com.truedigital.share.data.firestoreconfig.initializer.FirestoreConfigInitializer
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent

class AppPermissionInitializer : Initializer<AppPerMissionComponent> {

    override fun create(context: Context): AppPerMissionComponent {
        return DaggerAppPerMissionComponent.factory().create(
            AnalyticsComponent.getInstance().getAnalyticsSubComponent(),
            CoreComponent.getInstance().getCoreSubComponent(),
            FirestoreConfigComponent.getInstance().getFirestoreConfigSubComponent(),
            OnBoardingComponent.getInstance().getOnBoardingSubComponent()
        ).apply {
            AppPerMissionComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AnalyticsInitializer::class.java,
        CoreInitializer::class.java,
        FirestoreConfigInitializer::class.java,
        OnBoardingInitializer::class.java
    )
}
