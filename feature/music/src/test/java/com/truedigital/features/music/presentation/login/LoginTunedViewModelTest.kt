package com.truedigital.features.music.presentation.login

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.domain.usecase.data.DeleteRoomDataUseCase
import com.truedigital.features.tuned.presentation.main.facade.MusicAuthenticationFacade
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.reactivex.Single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TestCoroutinesExtension::class)
class LoginTunedViewModelTest {

    private lateinit var viewModel: LoginTunedViewModel
    private val deleteRoomDataUseCase: DeleteRoomDataUseCase = mock()
    private val musicAuthenticationFacade: MusicAuthenticationFacade = mock()

    @BeforeEach
    fun setUp() {
        viewModel = LoginTunedViewModel(
            deleteRoomDataUseCase = deleteRoomDataUseCase,
            musicAuthenticationFacade = musicAuthenticationFacade
        )
    }

    @Test
    fun testLoginTunedMusic_isLogOut_matchUserId_verifyLogOutOnceTime() = runTest {
        val mockUserId = 1
        val mockAccessToken = "accessToken"
        whenever(musicAuthenticationFacade.isLogout()).thenReturn(true)
        whenever(musicAuthenticationFacade.getTrueUserId()).thenReturn(mockUserId)
        whenever(musicAuthenticationFacade.loginJwt(any(), any())).thenReturn(Single.just(Any()))

        viewModel.loginTunedMusic(mockUserId, mockAccessToken)

        verify(musicAuthenticationFacade, times(1)).logout()
        verify(deleteRoomDataUseCase, times(1)).execute()
    }

    @Test
    fun testLoginTunedMusic_isLogOut_notMatchUserId_verifyLogOutOnceTime() = runTest {
        val mockUserId = 1
        val mockAccessToken = "accessToken"
        whenever(musicAuthenticationFacade.isLogout()).thenReturn(true)
        whenever(musicAuthenticationFacade.getTrueUserId()).thenReturn(mockUserId)
        whenever(musicAuthenticationFacade.loginJwt(any(), any())).thenReturn(Single.just(Any()))

        viewModel.loginTunedMusic(2, mockAccessToken)

        verify(musicAuthenticationFacade, times(1)).logout()
        verify(deleteRoomDataUseCase, times(1)).execute()
    }

    @Test
    fun testLoginTunedMusic_isNotLogOut_matchUserId_verifyLogOutZeroTime() = runTest {
        val mockUserId = 1
        val mockAccessToken = "accessToken"
        whenever(musicAuthenticationFacade.isLogout()).thenReturn(false)
        whenever(musicAuthenticationFacade.getTrueUserId()).thenReturn(mockUserId)
        whenever(musicAuthenticationFacade.loginJwt(any(), any())).thenReturn(Single.just(Any()))

        viewModel.loginTunedMusic(mockUserId, mockAccessToken)

        verify(musicAuthenticationFacade, times(0)).logout()
        verify(deleteRoomDataUseCase, times(0)).execute()
    }

    @Test
    fun testLoginTunedMusic_isNotLogOut_notMatchUserId_verifyLogOutOnceTime() = runTest {
        val mockUserId = 1
        val mockAccessToken = "accessToken"
        whenever(musicAuthenticationFacade.isLogout()).thenReturn(false)
        whenever(musicAuthenticationFacade.getTrueUserId()).thenReturn(mockUserId)
        whenever(musicAuthenticationFacade.loginJwt(any(), any())).thenReturn(Single.just(Any()))

        viewModel.loginTunedMusic(2, mockAccessToken)

        verify(musicAuthenticationFacade, times(1)).logout()
        verify(deleteRoomDataUseCase, times(1)).execute()
    }

    @Test
    fun testLoginTunedMusic_isNotLogOut_notMatchUserId_verifyLogOutOnceTime_onError() = runTest {
        val mockUserId = 1
        val mockAccessToken = "accessToken"
        whenever(musicAuthenticationFacade.getTrueUserId()).thenReturn(mockUserId)
        whenever(
            musicAuthenticationFacade.loginJwt(
                any(),
                any()
            )
        ).thenReturn(Single.error(Throwable("")))

        viewModel.loginTunedMusic(2, mockAccessToken)

        verify(musicAuthenticationFacade, times(1)).logout()
        verify(deleteRoomDataUseCase, times(1)).execute()
    }
}
