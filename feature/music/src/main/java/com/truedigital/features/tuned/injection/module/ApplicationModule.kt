package com.truedigital.features.tuned.injection.module

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.session.MediaButtonReceiver
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.Crypto
import com.facebook.crypto.CryptoConfig
import com.truedigital.features.music.MusicInitializer
import com.truedigital.features.tuned.common.extensions.startServiceDefault
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideMediaSession(context: Context): MediaSessionCompat {
        val mediaButtonReceiver = ComponentName(context, MediaButtonReceiver::class.java)
        val flags = 0 or FLAG_IMMUTABLE
        val mediaButtonPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(Intent.ACTION_MEDIA_BUTTON).setClass(context, MediaButtonReceiver::class.java),
            flags
        )

        val mediaSessionCallback = object : MediaSessionCompat.Callback() {
            override fun onPlay() = context.startServiceDefault {
                action = MusicPlayerController.ACTION_PLAY
            }

            override fun onPause() = context.startServiceDefault {
                action = MusicPlayerController.ACTION_PAUSE
            }

            override fun onSkipToNext() =
                context.startServiceDefault {
                    action = MusicPlayerController.ACTION_SKIP_NEXT
                }

            override fun onSkipToPrevious() =
                context.startServiceDefault {
                    action = MusicPlayerController.ACTION_SKIP_PREVIOUS
                }

            override fun onStop() = context.startServiceDefault {
                action = MusicPlayerController.ACTION_STOP
            }

            override fun onSeekTo(pos: Long) =
                context.startServiceDefault {
                    action = MusicPlayerController.ACTION_SEEK
                    putExtra(MusicPlayerController.ACTION_SEEK, pos)
                }
        }

        val mediaSession = MediaSessionCompat(
            context,
            MusicInitializer::class.java.simpleName,
            mediaButtonReceiver,
            mediaButtonPendingIntent
        )
        mediaSession.setCallback(mediaSessionCallback)
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        mediaSession.isActive = true

        return mediaSession
    }

    @Provides
    @Singleton
    fun provideMainHandler(): Handler = Handler(Looper.getMainLooper())

    @Provides
    @Singleton
    fun provideCrypto(context: Context): Crypto =
        AndroidConceal.get()
            .createDefaultCrypto(SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256))
}
