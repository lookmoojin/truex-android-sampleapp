package com.truedigital.common.share.datalegacy.login

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.nhaarman.mockitokotlin2.spy
import com.truedigital.authentication.social.SocialLoginCallbacks
import com.truedigital.authentication.social.SocialLoginService
import com.truedigital.authentication.social.model.SocialAuthParams
import com.truedigital.core.provider.ContextDataProvider
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SocialLoginServiceImplTest {

    private val mockSocialAuthParams: SocialAuthParams = mockk()
    private val mockSocialLoginCallbacks: SocialLoginCallbacks = mockk()
    private val mockGoogleSignInClient: GoogleSignInClient = mockk()
    private val contextDataProvider: ContextDataProvider = mockk()
    private val mockIntent: Intent = mockk()
    private lateinit var socialLoginService: SocialLoginService

    @BeforeEach
    fun setUp() {
        socialLoginService = SocialLoginServiceImpl(contextDataProvider)
    }

    @Test
    fun `when callback on socialAuth with google provider, should start Google SignIn screen`() = runTest {
        val activity: Activity = spy()
        mockkObject(activity)

        mockkStatic(GoogleSignIn::class)

        every { activity.getString(any()) }.returns("server_client_id")
        every { mockSocialAuthParams.provider } returns ("google")
        every { GoogleSignIn.getClient(any(), any()) } returns mockGoogleSignInClient
        every { mockGoogleSignInClient.signInIntent } returns mockIntent

        socialLoginService.socialAuth(activity, mockSocialAuthParams, mockSocialLoginCallbacks)

        coVerify { activity.startActivityForResult(mockIntent, 999) }
    }
}
