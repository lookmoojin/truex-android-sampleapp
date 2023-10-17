package com.truedigital.features.tuned.data.setting.repository

import android.support.v4.media.session.PlaybackStateCompat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import com.truedigital.features.tuned.data.setting.repository.SettingRepositoryImpl.Companion.AD_COUNTER_KEY
import com.truedigital.features.tuned.data.setting.repository.SettingRepositoryImpl.Companion.ALLOW_MOBILE_DATA_STREAMING_KEY
import com.truedigital.features.tuned.data.setting.repository.SettingRepositoryImpl.Companion.REPEAT_PLAY_KEY
import com.truedigital.features.tuned.data.setting.repository.SettingRepositoryImpl.Companion.SHARED_PREFERENCE_VERSION_KEY
import com.truedigital.features.tuned.data.setting.repository.SettingRepositoryImpl.Companion.SHUFFLE_PLAY_KEY
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SettingRepositoryTest {
    private val configuration: Configuration = mock()
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()
    private lateinit var settingRepository: SettingRepository

    @BeforeEach
    fun setUp() {
        whenever(sharedPreferences.get(SHARED_PREFERENCE_VERSION_KEY, 1)).thenReturn(0)
        settingRepository = SettingRepositoryImpl(configuration, sharedPreferences)
    }

    @Test
    fun init_currentVersionIsOne_repeatPlayNull_notVerifyPutRepeatModeAll() {
        whenever(sharedPreferences.get(SHARED_PREFERENCE_VERSION_KEY, 1)).thenReturn(1)
        whenever(sharedPreferences.get<Boolean>(REPEAT_PLAY_KEY)).thenReturn(null)

        settingRepository = SettingRepositoryImpl(configuration, sharedPreferences)

        verify(sharedPreferences, times(0)).put(
            REPEAT_PLAY_KEY,
            PlaybackStateCompat.REPEAT_MODE_ALL
        )
        verify(sharedPreferences, times(0)).put(
            REPEAT_PLAY_KEY,
            PlaybackStateCompat.REPEAT_MODE_NONE
        )
        verify(sharedPreferences, times(1)).put(
            SHARED_PREFERENCE_VERSION_KEY,
            2
        )
    }

    @Test
    fun init_currentVersionIsOne_repeatPlayTrue_verifyPutRepeatModeAll() {
        whenever(sharedPreferences.get(SHARED_PREFERENCE_VERSION_KEY, 1)).thenReturn(1)
        whenever(sharedPreferences.get<Boolean>(REPEAT_PLAY_KEY)).thenReturn(true)

        settingRepository = SettingRepositoryImpl(configuration, sharedPreferences)

        verify(sharedPreferences, times(1)).put(
            REPEAT_PLAY_KEY,
            PlaybackStateCompat.REPEAT_MODE_ALL
        )
        verify(sharedPreferences, times(1)).put(
            SHARED_PREFERENCE_VERSION_KEY,
            2
        )
    }

    @Test
    fun init_currentVersionIsOne_repeatPlayFalse_verifyPutRepeatModeNone() {
        whenever(sharedPreferences.get(SHARED_PREFERENCE_VERSION_KEY, 1)).thenReturn(1)
        whenever(sharedPreferences.get<Boolean>(REPEAT_PLAY_KEY)).thenReturn(false)

        settingRepository = SettingRepositoryImpl(configuration, sharedPreferences)

        verify(sharedPreferences, times(1)).put(
            REPEAT_PLAY_KEY,
            PlaybackStateCompat.REPEAT_MODE_NONE
        )
        verify(sharedPreferences, times(1)).put(
            SHARED_PREFERENCE_VERSION_KEY,
            2
        )
    }

    @Test
    fun setAllowMobileDataStreaming_verifyGetAllowMobileDataStreaming() {
        settingRepository.setAllowMobileDataStreaming(false)

        verify(sharedPreferences, times(1)).put(
            ALLOW_MOBILE_DATA_STREAMING_KEY,
            false
        )
    }

    @Test
    fun allowMobileDataStreaming_verifyGetAllowMobileDataStreaming() {
        whenever(configuration.enableMobileDataByDefault).thenReturn(true)

        settingRepository.allowMobileDataStreaming()

        verify(sharedPreferences, times(1)).get(
            ALLOW_MOBILE_DATA_STREAMING_KEY,
            true
        )
    }

    @Test
    fun setShufflePlay_verifyPutShufflePlay() {
        settingRepository.setShufflePlay(true)

        verify(sharedPreferences, times(1)).put(SHUFFLE_PLAY_KEY, true)
    }

    @Test
    fun isShufflePlayEnabled_verifyGetShufflePlay() {
        assertEquals(true, settingRepository.isShufflePlayEnabled())
        verify(sharedPreferences, times(1)).get(SHUFFLE_PLAY_KEY, true)
    }

    @Test
    fun setRepeatMode_verifyPutRepeatPlay() {
        settingRepository.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)

        verify(sharedPreferences, times(1)).put(
            REPEAT_PLAY_KEY,
            PlaybackStateCompat.REPEAT_MODE_ALL
        )
    }

    @Test
    fun getRepeatMode_verifyGetRepeatPlay() {
        assertEquals(PlaybackStateCompat.REPEAT_MODE_NONE, settingRepository.getRepeatMode())
        verify(sharedPreferences, times(1)).get(
            REPEAT_PLAY_KEY,
            PlaybackStateCompat.REPEAT_MODE_NONE
        )
    }

    @Test
    fun addAdCounter_verifyPutAdCounter() {
        whenever(sharedPreferences.get(AD_COUNTER_KEY, 0)).thenReturn(1)

        settingRepository.addAdCounter()

        verify(sharedPreferences, times(1)).put(AD_COUNTER_KEY, 2)
    }

    @Test
    fun resetAdCounter_verifyPutAdCounter() {
        settingRepository.resetAdCounter()

        verify(sharedPreferences, times(1)).put(AD_COUNTER_KEY, 0)
    }

    @Test
    fun getAdCounter_verifyGetAdCounter() {
        assertEquals(0, settingRepository.getAdCounter())

        verify(sharedPreferences, times(1)).get(AD_COUNTER_KEY, 0)
    }
}
