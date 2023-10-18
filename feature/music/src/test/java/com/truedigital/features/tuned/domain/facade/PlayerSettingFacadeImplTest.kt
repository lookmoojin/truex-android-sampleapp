package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerSettingFacadeImplTest {

    private lateinit var playerSettingFacade: PlayerSettingFacadeImpl
    private val musicUserRepository: MusicUserRepository = mock()
    private val settingRepository: SettingRepository = mock()

    @BeforeEach
    fun setup() {
        playerSettingFacade = PlayerSettingFacadeImpl(musicUserRepository, settingRepository)
    }

    @Test
    fun loadMobileDataStreamingState_check_1_time_call() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(true)

        playerSettingFacade.loadMobileDataStreamingState()

        verify(settingRepository, times(1)).allowMobileDataStreaming()
    }

    @Test
    fun loadToggleMobileDataStreamingState_isAllowedTrue_verify() {
        doNothing().whenever(settingRepository).setAllowMobileDataStreaming(any())

        playerSettingFacade.toggleMobileDataStreamingState(true)

        verify(settingRepository, times(1)).setAllowMobileDataStreaming(true)
    }

    @Test
    fun loadHighQualityAudioState_check_audioQuality_is_high() {
        whenever(musicUserRepository.get()).thenReturn(Single.just(getUser("high")))

        playerSettingFacade.loadHighQualityAudioState()
            .test()
            .assertValue { isHigh ->
                isHigh
            }

        verify(musicUserRepository, times(1)).get()
    }

    @Test
    fun loadHighQualityAudioState_check_audioQuality_is_not_high() {
        whenever(musicUserRepository.get()).thenReturn(Single.just(getUser("low")))

        playerSettingFacade.loadHighQualityAudioState()
            .test()
            .assertValue { isHigh ->
                !isHigh
            }

        verify(musicUserRepository, times(1)).get()
    }

    @Test
    fun toggleHighQualityAudioState_check_isAllowed_is_true() {
        whenever(musicUserRepository.update(audioQuality = "high")).thenReturn(Single.just(getUser("high")))

        playerSettingFacade.toggleHighQualityAudioState(true)
            .test()
            .assertValue { user ->
                user.audioQuality == "high"
            }

        verify(musicUserRepository, times(1)).update(audioQuality = "high")
    }

    @Test
    fun toggleHighQualityAudioState_check_isAllowed_is_false() {
        whenever(musicUserRepository.update(audioQuality = "low")).thenReturn(Single.just(getUser("low")))

        playerSettingFacade.toggleHighQualityAudioState(false)
            .test()
            .assertValue { user ->
                user.audioQuality == "low"
            }

        verify(musicUserRepository, times(1)).update(audioQuality = "low")
    }

    private fun getUser(audioQuality: String): User {
        return User(
            userId = 1,
            displayName = "Test",
            firstName = "Test",
            lastName = "Test",
            primaryEmail = "Email",
            isPrimaryEmailValidated = true,
            image = "image",
            backgroundImage = "image",
            followers = listOf(),
            following = listOf(),
            isPublic = true,
            optedIn = true,
            blocked = listOf(),
            language = "th",
            subscriptions = listOf(),
            devices = listOf(),
            isFacebookUser = true,
            circle = "",
            birthYear = 1,
            gender = "",
            logins = listOf(),
            action = "action",
            audioQuality = audioQuality,
            contentLanguages = listOf(),
            country = "",
            isVerified = false,
            isTwitterUser = false
        )
    }
}
