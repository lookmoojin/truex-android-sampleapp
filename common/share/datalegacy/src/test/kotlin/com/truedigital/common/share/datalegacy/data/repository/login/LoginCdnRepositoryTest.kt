package com.truedigital.common.share.datalegacy.data.repository.login

import com.truedigital.common.share.datalegacy.data.login.api.LoginCdnApiInterface
import com.truedigital.common.share.datalegacy.data.login.model.DefaultSignInUrl
import com.truedigital.common.share.datalegacy.data.login.model.DefaultUrlConfigResponse
import com.truedigital.core.extensions.collectSafe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

interface LoginCdnRepositoryTestTestCase {
    fun `Given call api success When getCdnLoginUrl Then return data`()
    fun `Given call api fail When getCdnLoginUrl Then throw error`()
    fun `Given have data cache When getCdnLoginUrl Then return data and getCdnUrlConfig call 1 time`()
    fun `Given call api body null When getCdnLoginUrl Then throw error`()
    fun `Given call api data null when getCdnLoginUrl Then Throw error`()
    fun `test clearCache`()
}

class LoginCdnRepositoryTestTest : LoginCdnRepositoryTestTestCase {

    private val api = mockk<LoginCdnApiInterface>()
    private lateinit var repository: LoginCdnRepository

    @BeforeEach
    fun setup() {
        repository = spyk(
            LoginCdnRepositoryImpl(
                api = api
            )
        )
    }

    @Test
    override fun `Given call api success When getCdnLoginUrl Then return data`() = runTest {
        val mockData = DefaultUrlConfigResponse(
            alpha = DefaultSignInUrl("alpha"),
            preprod = DefaultSignInUrl("preprod"),
            production = DefaultSignInUrl("production")
        )
        val response = Response.success(mockData)
        coEvery { api.getCdnUrlConfig() } returns response

        repository.getCdnLoginUrl()
            .collectSafe { data ->
                assertNotNull(data)

                data.onSuccess {
                    assertEquals("alpha", it?.alpha?.signInUrl)
                    assertEquals("preprod", it?.preprod?.signInUrl)
                    assertEquals("production", it?.production?.signInUrl)
                }
            }
    }

    @Test
    override fun `Given call api fail When getCdnLoginUrl Then throw error`() = runTest {
        val errorBody = """{"code": 404}""".trimIndent().toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<DefaultUrlConfigResponse>(404, errorBody)
        coEvery { api.getCdnUrlConfig() } returns response

        try {
            repository.getCdnLoginUrl().first()
        } catch (e: NoSuchElementException) {
            assertNotNull(e)
        }
    }

    @Test
    override fun `Given have data cache When getCdnLoginUrl Then return data and getCdnUrlConfig call 1 time`() = runTest {
        val mockData = DefaultUrlConfigResponse(
            alpha = null,
            preprod = null,
            production = null
        )
        val response = Response.success(mockData)
        coEvery { api.getCdnUrlConfig() } returns response

        repository.getCdnLoginUrl()
            .collectSafe { data ->
                assertNotNull(data)
            }
        repository.getCdnLoginUrl()
            .collectSafe { data ->
                assertNotNull(data)
            }

        coVerify(exactly = 1) { api.getCdnUrlConfig() }
    }

    @Test
    override fun `Given call api body null When getCdnLoginUrl Then throw error`() = runTest {
        val response = Response.success<DefaultUrlConfigResponse>(null)
        coEvery { api.getCdnUrlConfig() } returns response

        try {
            repository.getCdnLoginUrl().first()
        } catch (e: NoSuchElementException) {
            assertNotNull(e)
        }
    }

    @Test
    override fun `Given call api data null when getCdnLoginUrl Then Throw error`() = runTest {
        val mockData = DefaultUrlConfigResponse()
        val response = Response.success(mockData)
        coEvery { api.getCdnUrlConfig() } returns response

        try {
            repository.getCdnLoginUrl().first()
        } catch (e: NoSuchElementException) {
            assertNotNull(e)
        }
    }

    @Test
    override fun `test clearCache`() = runTest {
        repository.clearCache()

        coVerify { repository.clearCache() }
    }
}
