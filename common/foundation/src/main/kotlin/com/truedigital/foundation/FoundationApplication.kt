package com.truedigital.foundation

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.newrelic.agent.android.NewRelic
import com.truedigital.foundation.injections.DaggerFoundationComponent
import com.truedigital.foundation.injections.FoundationComponent
import tech.okcredit.startup_instrumentation.AppStartUpTracer
import timber.log.Timber

open class FoundationApplication : MultiDexApplication(), Configuration.Provider {

    private val mainHandler = Handler(Looper.getMainLooper())

    private val foundationComponent: FoundationComponent by lazy {
        DaggerFoundationComponent.factory().create(this)
    }

    companion object {
        lateinit var appContext: Context
        var isAppInForeground = false
        var isInitDone = false

        @JvmStatic
        fun foundationComponent(context: Context) =
            (context.applicationContext as FoundationApplication).foundationComponent
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)

        System.setProperty(
            "kotlinx.coroutines.debug",
            if (BuildConfig.DEBUG) "on" else "off"
        )
        appContext = context

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(
                LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> isAppInForeground = true
                        Lifecycle.Event.ON_STOP -> isAppInForeground = false
                        else -> {}
                    }
                }
            )
    }

    override fun onCreate() {
        AppStartUpTracer.start()
        super.onCreate()
        try {
            createNotificationChannels()
        } catch (exception: Exception) {
            val handlingExceptionMap = mapOf(
                "Key" to "FoundationApplication",
                "Value" to "createNotificationChannels"
            )
            NewRelic.recordHandledException(
                Exception(exception), handlingExceptionMap
            )
        }

        AppStartUpTracer.stop(this) { appStartUpMetrics ->
            Timber.d("Total Time = ${appStartUpMetrics.totalTime}") // Time Taken For Cold StartUp
            Timber.d("Process Fork To CP = ${appStartUpMetrics.processForkToContentProvider}") // Time Taken From Process start to initialising content provider
            Timber.d("Content provider = ${appStartUpMetrics.contentProviderToAppStart}") // Time Taken for initialising content providers
            Timber.d("Application Create = ${appStartUpMetrics.applicationOnCreateTime}") // Time Taken for running Application onCreate
            Timber.d("First Draw = ${appStartUpMetrics.appOnCreateEndToFirstDraw}") // Time Taken from end of Application onCreate to drawing first frame

            val startupTracer = mapOf(
                "Total Time" to "${appStartUpMetrics.totalTime}",
                "Process Fork To CP" to "${appStartUpMetrics.processForkToContentProvider}",
                "Content provider" to "${appStartUpMetrics.contentProviderToAppStart}",
                "Application Create" to "${appStartUpMetrics.applicationOnCreateTime}",
                "First Draw" to "${appStartUpMetrics.appOnCreateEndToFirstDraw}"
            )

            NewRelic.recordCustomEvent("AppStartUpTracer", startupTracer)
        }

        isInitDone = true
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val logging = if (BuildConfig.DEBUG) {
            android.util.Log.DEBUG
        } else {
            android.util.Log.ERROR
        }
        return Configuration.Builder()
            .setMinimumLoggingLevel(logging)
            .build()
    }

    override fun registerReceiver(
        receiver: BroadcastReceiver?,
        filter: IntentFilter?
    ): Intent? {
        if (isInitDone) {
            return super.registerReceiver(receiver, filter)
        }
        mainHandler.post { super.registerReceiver(receiver, filter) }
        return null
    }

    private fun createNotificationChannels() {
        createNotificationChannel(NotificationChannelInfo.CallFeature)
        createNotificationChannel(NotificationChannelInfo.ChatFeature)
        createNotificationChannel(NotificationChannelInfo.DefaultFeature)
        createNotificationChannel(NotificationChannelInfo.CommunityFeature)
        createNotificationChannel(NotificationChannelInfo.TrueCloudDownloadNotificationFeature)
        createNotificationChannel(NotificationChannelInfo.MusicNotificationFeature)
    }

    private fun createNotificationChannel(feature: NotificationChannelInfo) {
        val channel =
            NotificationChannel(feature.channelId, feature.channelName, feature.importance)
        getNotificationManager().createNotificationChannel(channel)
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}

sealed class NotificationChannelInfo(
    val channelId: String,
    val channelName: String,
    val importance: Int
) {
    companion object {
        const val TRUE_ID_CHANNEL_ID = "default_channel"
        const val TRUE_ID_CHANNEL_NAME = "Default Channel"
        const val CALL_CHANNEL_ID = "call_channel"
        const val CALL_CHANNEL_NAME = "Call Channel"
        const val CHAT_CHANNEL_ID = "chat_channel"
        const val CHAT_CHANNEL_NAME = "Chat Channel"
        const val COMMUNITY_CHANNEL_ID = "community_channel"
        const val COMMUNITY_CHANNEL_NAME = "Community Channel"
        const val TRUE_CLOUD_DOWNLOAD_CHANNEL_ID = "true_cloud_download_notification"
        const val TRUE_CLOUD_DOWNLOAD_CHANNEL_NAME = "True Cloud Download"
        const val MUSIC_CHANNEL_ID = "media_session_service_notification"
        const val MUSIC_CHANNEL_NAME = "Music Background"
    }

    object ChatFeature : NotificationChannelInfo(
        CHAT_CHANNEL_ID,
        CHAT_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    )

    object CallFeature : NotificationChannelInfo(
        CALL_CHANNEL_ID,
        CALL_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    )

    object CommunityFeature : NotificationChannelInfo(
        COMMUNITY_CHANNEL_ID,
        COMMUNITY_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
    )

    object DefaultFeature : NotificationChannelInfo(
        TRUE_ID_CHANNEL_ID,
        TRUE_ID_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
    )

    object TrueCloudDownloadNotificationFeature : NotificationChannelInfo(
        TRUE_CLOUD_DOWNLOAD_CHANNEL_ID,
        TRUE_CLOUD_DOWNLOAD_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    )

    object MusicNotificationFeature : NotificationChannelInfo(
        MUSIC_CHANNEL_ID,
        MUSIC_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW
    )
}

fun Activity.foundationComponent() = FoundationApplication.foundationComponent(this)
fun Fragment.foundationComponent() = FoundationApplication.foundationComponent(requireContext())
