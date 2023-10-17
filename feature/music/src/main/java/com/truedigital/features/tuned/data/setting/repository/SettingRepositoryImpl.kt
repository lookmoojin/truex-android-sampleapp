package com.truedigital.features.tuned.data.setting.repository

import android.support.v4.media.session.PlaybackStateCompat
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import com.truedigital.features.tuned.injection.module.SharePreferenceModule
import javax.inject.Inject
import javax.inject.Named

class SettingRepositoryImpl @Inject constructor(
    private val configuration: Configuration,
    @Named(SharePreferenceModule.KEY_SETTING_PREFERENCES) private val sharedPreferences: ObfuscatedKeyValueStoreInterface
) : SettingRepository {

    companion object {
        const val ALLOW_MOBILE_DATA_STREAMING_KEY = "allow_mobile_data_streaming"
        const val SHUFFLE_PLAY_KEY = "shuffle_play"
        const val REPEAT_PLAY_KEY = "repeat_play"
        const val AD_COUNTER_KEY = "ad_counter"
        const val SHARED_PREFERENCE_VERSION_KEY = "shared_preference_version"
    }

    init {
        migrateSharedPreferences()
    }

    private fun migrateSharedPreferences() {
        var currentVersion = sharedPreferences.get(SHARED_PREFERENCE_VERSION_KEY, 1)
        if (currentVersion == 1) {
            sharedPreferences.get<Boolean>(REPEAT_PLAY_KEY)?.let {
                val newRepeatMode =
                    if (it) PlaybackStateCompat.REPEAT_MODE_ALL else PlaybackStateCompat.REPEAT_MODE_NONE
                sharedPreferences.put(REPEAT_PLAY_KEY, newRepeatMode)
            }
            currentVersion++
        }
        sharedPreferences.put(SHARED_PREFERENCE_VERSION_KEY, currentVersion)
    }

    override fun setAllowMobileDataStreaming(isAllowed: Boolean) {
        sharedPreferences.put(ALLOW_MOBILE_DATA_STREAMING_KEY, isAllowed)
    }

    override fun allowMobileDataStreaming(): Boolean =
        sharedPreferences.get(
            ALLOW_MOBILE_DATA_STREAMING_KEY,
            configuration.enableMobileDataByDefault
        )

    override fun setShufflePlay(enable: Boolean) {
        sharedPreferences.put(SHUFFLE_PLAY_KEY, enable)
    }

    override fun isShufflePlayEnabled(): Boolean =
        sharedPreferences.get(SHUFFLE_PLAY_KEY, true)

    override fun setRepeatMode(mode: Int) {
        sharedPreferences.put(REPEAT_PLAY_KEY, mode)
    }

    override fun getRepeatMode(): Int =
        sharedPreferences.get(REPEAT_PLAY_KEY, PlaybackStateCompat.REPEAT_MODE_NONE)

    override fun addAdCounter() {
        val count = sharedPreferences.get(AD_COUNTER_KEY, 0) + 1
        sharedPreferences.put(AD_COUNTER_KEY, count)
    }

    override fun resetAdCounter() {
        sharedPreferences.put(AD_COUNTER_KEY, 0)
    }

    override fun getAdCounter(): Int =
        sharedPreferences.get(AD_COUNTER_KEY, 0)
}
