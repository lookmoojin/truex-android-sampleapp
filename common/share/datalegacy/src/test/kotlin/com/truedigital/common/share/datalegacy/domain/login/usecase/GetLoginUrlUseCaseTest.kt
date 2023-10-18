package com.truedigital.common.share.datalegacy.domain.login.usecase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.truedigital.common.share.datalegacy.data.login.model.DefaultSignInUrl
import com.truedigital.common.share.datalegacy.data.login.model.DefaultUrlConfigResponse
import com.truedigital.common.share.datalegacy.data.repository.login.LoginCdnRepository
import com.truedigital.core.BuildConfig.REMOTE_CONFIG_DEFAULT_AAA_SIGNIN_URL
import com.truedigital.core.extensions.collectSafe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

interface GetLoginUrlUseCaseTestCase {
    fun `Given remote config has data When execute Then return remote config URL`()
    fun `Given remote config failed When execute Then return CDN URL`()
    fun `Given remote config and CDN failed When execute Then return default URL`()
}

class GetLoginUrlUseCaseTest : GetLoginUrlUseCaseTestCase {

    private val loginCdnRepository = mockk<LoginCdnRepository>()
    private val firebaseRemoteConfig = mockk<FirebaseRemoteConfig>()
    private lateinit var useCase: GetLoginUrlUseCase

    @BeforeEach
    fun setup() {
        useCase = GetLoginUrlUseCaseImpl(
            firebaseRemoteConfig = firebaseRemoteConfig,
            loginCdnRepository = loginCdnRepository
        )
    }

    @Test
    override fun `Given remote config has data When execute Then return remote config URL`() = runTest {
        every { firebaseRemoteConfig.getString("aaa_signin_flow") } returns "remoteConfigUrl"

        useCase.execute()
            .collectSafe { url ->
                assertEquals("remoteConfigUrl", url)
            }
    }

    @Test
    override fun `Given remote config failed When execute Then return CDN URL`() = runTest {
        val mockData = DefaultUrlConfigResponse(
            alpha = DefaultSignInUrl("alpha"),
            preprod = DefaultSignInUrl("preprod"),
            production = DefaultSignInUrl("production")
        )
        val response = Result.success(mockData)
        every { firebaseRemoteConfig.getString("aaa_signin_flow") } returns ""
        every { loginCdnRepository.getCdnLoginUrl() } returns flowOf(response)

        useCase.execute()
            .collectSafe { url ->
                assertEquals("preprod", url)
            }
    }

    @Test
    override fun `Given remote config and CDN failed When execute Then return default URL`() = runTest {
        val response = Result.success(DefaultUrlConfigResponse())
        every { firebaseRemoteConfig.getString("aaa_signin_flow") } returns ""
        every { loginCdnRepository.getCdnLoginUrl() } returns flowOf(response)

        useCase.execute()
            .collectSafe { url ->
                assertEquals(REMOTE_CONFIG_DEFAULT_AAA_SIGNIN_URL, url)
            }
    }
}
