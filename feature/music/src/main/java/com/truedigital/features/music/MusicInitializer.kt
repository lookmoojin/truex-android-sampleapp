package com.truedigital.features.music

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.startup.Initializer
import com.newrelic.agent.android.NewRelic
import com.truedigital.common.share.analytics.AnalyticsInitializer
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.common.share.data.coredata.CoreDataInitializer
import com.truedigital.common.share.data.coredata.injections.CoreDataComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.common.share.nativeshare.LinkGeneratorInitializer
import com.truedigital.common.share.nativeshare.injections.LinkGeneratorComponent
import com.truedigital.common.share.webview.externalbrowser.ExternalBrowserInitializer
import com.truedigital.common.share.webview.externalbrowser.injections.ExternalBrowserComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.extensions.runOnUiThread
import com.truedigital.core.injections.CoreComponent
import com.truedigital.features.listens.share.ListenShareInitializer
import com.truedigital.features.listens.share.injection.ListenShareComponent
import com.truedigital.features.music.injections.DaggerMusicComponent
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.service.appstatus.ApplicationVisibilityReciever
import com.truedigital.foundation.NotificationChannelInfo
import com.truedigital.navigation.NavigationInitializer
import com.truedigital.navigation.injections.NavigationComponent
import com.truedigital.share.data.firestoreconfig.initializer.FirestoreConfigInitializer
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent
import com.truedigital.share.data.geoinformation.initializer.GeoInformationInitializer
import com.truedigital.share.data.geoinformation.injections.GeoInformationComponent
import com.truedigital.share.data.prasarn.PrasarnInitializer
import com.truedigital.share.data.prasarn.injections.PrasarnComponent

class MusicInitializer : Initializer<MusicComponent>, LifecycleObserver {

    companion object {
        lateinit var applicationContext: Application
        var appIsInForeground = false
    }

    override fun create(context: Context): MusicComponent {
        return DaggerMusicComponent.factory().create(
            AnalyticsComponent.getInstance().getAnalyticsSubComponent(),
            CoreComponent.getInstance().getCoreSubComponent(),
            CoreDataComponent.getInstance().getCoreDataSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent(),
            ExternalBrowserComponent.getInstance().getExternalBrowserSubComponent(),
            FirestoreConfigComponent.getInstance().getFirestoreConfigSubComponent(),
            GeoInformationComponent.getInstance().getGeoInformationSubComponent(),
            NavigationComponent.getInstance().getNavigationSubComponent(),
            LinkGeneratorComponent.getInstance().getLinkGeneratorSubComponent(),
            PrasarnComponent.getInstance().getPrasarnSubComponent(),
            ListenShareComponent.getInstance().getListenShareSubComponent()
        ).apply {
            MusicComponent.initialize(this)
        }.also {
            applicationContext = (context as Application)

            context.runOnUiThread {
                ProcessLifecycleOwner.get().lifecycle.addObserver(this@MusicInitializer)
            }

            applicationContext.onCreate().apply {
                try {
                    initNotificationChannel(applicationContext)
                } catch (exception: Exception) {
                    val handlingExceptionMap = mapOf(
                        "Key" to "MusicInitializer",
                        "Value" to "initNotificationChannel"
                    )
                    NewRelic.recordHandledException(
                        Exception(exception), handlingExceptionMap
                    )
                }
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AnalyticsInitializer::class.java,
        CoreInitializer::class.java,
        CoreDataInitializer::class.java,
        DataLegacyInitializer::class.java,
        ExternalBrowserInitializer::class.java,
        FirestoreConfigInitializer::class.java,
        GeoInformationInitializer::class.java,
        NavigationInitializer::class.java,
        LinkGeneratorInitializer::class.java,
        PrasarnInitializer::class.java,
        ListenShareInitializer::class.java
    )

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        appIsInForeground = true
        val intent = Intent(applicationContext, ApplicationVisibilityReciever::class.java)
        intent.action = ApplicationVisibilityReciever.APPLICATION_VISIBILITY_CHANGE_ACTION
        intent.putExtra(ApplicationVisibilityReciever.MOVE_TO_FOREGROUND_KEY, true)
        applicationContext.sendBroadcast(intent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        appIsInForeground = false
        val intent = Intent(applicationContext, ApplicationVisibilityReciever::class.java)
        intent.action = ApplicationVisibilityReciever.APPLICATION_VISIBILITY_CHANGE_ACTION
        intent.putExtra(ApplicationVisibilityReciever.MOVE_TO_FOREGROUND_KEY, false)
        applicationContext.sendBroadcast(intent)
    }

    private fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NotificationChannelInfo.MUSIC_CHANNEL_ID,
                NotificationChannelInfo.MUSIC_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
