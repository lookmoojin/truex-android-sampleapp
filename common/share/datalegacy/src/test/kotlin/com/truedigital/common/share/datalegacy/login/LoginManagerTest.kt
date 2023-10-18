package com.truedigital.common.share.datalegacy.login

import com.truedigital.authentication.presentation.AccessTokenListener
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LoginManagerTest {

    private lateinit var authManagerWrapper: AuthManagerWrapper
    private lateinit var loginManager: LoginManagerImpl

    @BeforeEach
    fun setUp() {
        authManagerWrapper = mockk(relaxed = true)
        loginManager = LoginManagerImpl(authManagerWrapper)
    }

    @Test
    fun `test isLoggedIn`() {
        // Given
        every { authManagerWrapper.isLoggedIn() } returns true

        // When
        val isLoggedIn = loginManager.isLoggedIn()

        // Then
        assertTrue(isLoggedIn)
        verify { authManagerWrapper.isLoggedIn() }
    }

    @Test
    fun `test getAccessToken`() {
        // Given
        every { authManagerWrapper.getAccessToken() } returns "mockAccessToken"

        // When
        val accessToken = loginManager.getAccessToken()

        // Then
        assertEquals("mockAccessToken", accessToken)
        verify { authManagerWrapper.getAccessToken() }
    }

    @Test
    fun `test getAccessToken with listener`() {
        // Given
        val accessTokenListener = mockk<AccessTokenListener>()

        // When
        loginManager.getAccessToken(accessTokenListener)

        // Then
        verify { authManagerWrapper.getAccessToken(accessTokenListener) }
    }

    @Test
    fun `test getProfile`() {
        // Given
        every { authManagerWrapper.onGetProfile() } returns Observable.just(mockk())

        // When
        val profileObservable = loginManager.getProfile()

        // Then
        assertNotNull(profileObservable)
        verify { authManagerWrapper.onGetProfile() }
    }
}
