package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.user.model.AssociatedDevice
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.domain.facade.authentication.AuthenticationThirdPartyJWTUseCase
import com.truedigital.features.tuned.domain.facade.authentication.GetAuthenticatedUserUseCase
import com.truedigital.features.tuned.domain.facade.authentication.MusicAuthenticationFacadeImpl
import com.truedigital.features.tuned.presentation.main.facade.MusicAuthenticationFacade
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MusicAuthenticationFacadeImplTest {
    private lateinit var musicAuthenticationFacade: MusicAuthenticationFacade
    private val authenticationThirdPartyJWTUseCase: AuthenticationThirdPartyJWTUseCase = mock()
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mock()
    private val musicUserRepository: MusicUserRepository = mock()
    private val deviceRepository: DeviceRepository = mock()
    private val authenticationTokenRepository: AuthenticationTokenRepository = mock()

    @BeforeEach
    fun setup() {
        musicAuthenticationFacade = MusicAuthenticationFacadeImpl(
            authenticationThirdPartyJWTUseCase,
            getAuthenticatedUserUseCase,
            musicUserRepository,
            deviceRepository,
            authenticationTokenRepository
        )
    }

    @Test
    fun getTrueUserId_returnId() {
        val mockId = 1234
        whenever(musicUserRepository.getTrueUserId()).thenReturn(mockId)

        val result = musicAuthenticationFacade.getTrueUserId()

        assertEquals(mockId, result)
        verify(musicUserRepository, times(1)).getTrueUserId()
    }

    @Test
    fun loginJwt_hasUserTrue_existDeviceUniqueId_verifyGetAuthenticatedUser() {
        val mockUniqueId = "1"
        val mockUser = MockDataModel.mockUserTuned.copy(
            devices = listOf(
                AssociatedDevice(
                    deviceId = 1,
                    uniqueId = mockUniqueId,
                    displayName = "name",
                    lastSeen = "lastSeen"
                )
            )
        )
        whenever(musicUserRepository.exist()).thenReturn(true)
        whenever(deviceRepository.get()).thenReturn(MockDataModel.mockDevice.copy(uniqueId = mockUniqueId))
        whenever(getAuthenticatedUserUseCase.execute(any())).thenReturn(Single.just(mockUser))

        musicAuthenticationFacade.loginJwt(1, "jwt")
            .test()
            .assertNoErrors()

        verify(getAuthenticatedUserUseCase, times(1)).execute(any())
    }

    @Test
    fun loginJwt_hasUserFalse_notExistDeviceUniqueId_verifyAuthenticationThirdPartyJWT() {
        val mockUniqueId = "1"
        val mockUser = MockDataModel.mockUserTuned.copy(
            devices = listOf(
                AssociatedDevice(
                    deviceId = 1,
                    uniqueId = "2",
                    displayName = "name",
                    lastSeen = "lastSeen"
                )
            )
        )
        whenever(musicUserRepository.exist()).thenReturn(false)
        whenever(deviceRepository.get()).thenReturn(MockDataModel.mockDevice.copy(uniqueId = mockUniqueId))
        whenever(authenticationThirdPartyJWTUseCase.execute(any(), any())).thenReturn(
            Single.just(
                mockUser
            )
        )
        whenever(musicUserRepository.addDevice(any())).thenReturn(Single.just(true))
        whenever(musicUserRepository.get(true)).thenReturn(Single.just(mockUser))

        musicAuthenticationFacade.loginJwt(1, "jwt")
            .test()
            .assertNoErrors()

        verify(authenticationThirdPartyJWTUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun loginJwt_hasUserTrue_getAuthenticatedUserError_verifyAuthenticationThirdPartyJWT() {
        val mockUniqueId = "1"
        val mockUser = MockDataModel.mockUserTuned.copy(
            devices = listOf(
                AssociatedDevice(
                    deviceId = 1,
                    uniqueId = mockUniqueId,
                    displayName = "name",
                    lastSeen = "lastSeen"
                )
            )
        )
        whenever(musicUserRepository.exist()).thenReturn(true)
        whenever(deviceRepository.get()).thenReturn(MockDataModel.mockDevice.copy(uniqueId = mockUniqueId))
        whenever(getAuthenticatedUserUseCase.execute(any())).thenReturn(Single.error(Throwable()))
        whenever(authenticationThirdPartyJWTUseCase.execute(any(), any())).thenReturn(
            Single.just(mockUser)
        )

        musicAuthenticationFacade.loginJwt(1, "jwt")
            .test()
            .assertNoErrors()

        verify(authenticationThirdPartyJWTUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun isLogout_isLogout_is_true_case_userId_is_null() {
        whenever(musicUserRepository.getUserId()).thenReturn(null)
        whenever(authenticationTokenRepository.getUserIdFromToken()).thenReturn(123)

        val isLogout = musicAuthenticationFacade.isLogout()

        assert(isLogout)

        verify(musicUserRepository, times(1)).getUserId()
        verify(authenticationTokenRepository, times(1)).getUserIdFromToken()
    }

    @Test
    fun isLogout_isLogout_is_true_case_userIdFromToken_is_null() {
        whenever(musicUserRepository.getUserId()).thenReturn(1234)
        whenever(authenticationTokenRepository.getUserIdFromToken()).thenReturn(null)

        val isLogout = musicAuthenticationFacade.isLogout()

        assert(isLogout)

        verify(musicUserRepository, times(1)).getUserId()
        verify(authenticationTokenRepository, times(1)).getUserIdFromToken()
    }

    @Test
    fun isLogout_isLogout_is_true_case_userIdFromToken_Not_equal_to_userId() {
        whenever(musicUserRepository.getUserId()).thenReturn(1234)
        whenever(authenticationTokenRepository.getUserIdFromToken()).thenReturn(4455)

        val isLogout = musicAuthenticationFacade.isLogout()

        assert(isLogout)

        verify(musicUserRepository, times(1)).getUserId()
        verify(authenticationTokenRepository, times(1)).getUserIdFromToken()
    }

    @Test
    fun isLogout_userIdNull_userIdFromTokenNull_returnTrue() {
        whenever(musicUserRepository.getUserId()).thenReturn(null)
        whenever(authenticationTokenRepository.getUserIdFromToken()).thenReturn(null)

        val isLogout = musicAuthenticationFacade.isLogout()

        assert(isLogout)

        verify(musicUserRepository, times(1)).getUserId()
        verify(authenticationTokenRepository, times(1)).getUserIdFromToken()
    }

    @Test
    fun isLogout_userIdEqualUserIdFromToken_returnFalse() {
        whenever(musicUserRepository.getUserId()).thenReturn(123)
        whenever(authenticationTokenRepository.getUserIdFromToken()).thenReturn(123)

        val isLogout = musicAuthenticationFacade.isLogout()

        assert(!isLogout)

        verify(musicUserRepository, times(1)).getUserId()
        verify(authenticationTokenRepository, times(1)).getUserIdFromToken()
    }

    @Test
    fun logout_verifyLogoutAndClearToken() {
        doNothing().`when`(authenticationTokenRepository).clearToken()
        doNothing().`when`(musicUserRepository).logout()

        musicAuthenticationFacade.logout()

        verify(authenticationTokenRepository, times(1)).clearToken()
        verify(musicUserRepository, times(1)).logout()
    }
}
