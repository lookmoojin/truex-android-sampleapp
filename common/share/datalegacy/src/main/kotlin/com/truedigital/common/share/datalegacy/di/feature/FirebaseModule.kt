package com.truedigital.common.share.datalegacy.di.feature

import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.truedigital.common.share.datalegacy.R
import com.truedigital.common.share.datalegacy.utils.FirebaseUtil
import com.truedigital.common.share.datalegacy.utils.FirebaseUtilInterface
import com.truedigital.core.BuildConfig.FIREBASE_REMOTE_CONFIG_MINIMUM_FETCH
import com.truedigital.share.data.firestoreconfig.BuildConfig
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseUtilInterface(): FirebaseUtilInterface = FirebaseUtil.instance

    @Provides
    @Singleton
    fun provideTIDFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig(
            FirebaseApp.getInstance(
                BuildConfig.FIRE_BASE_SECURE_NAME
            )
        )

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = FIREBASE_REMOTE_CONFIG_MINIMUM_FETCH
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate().addOnFailureListener {
            Timber.e("[Firebase] RemoteConfig: ${it.message}", it.printStackTrace())
        }

        return remoteConfig
    }
}
