package com.truedigital.features.truecloudv3.provider

import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.GetSecureTokenServiceResponse
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.util.Calendar
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SecureTokenServiceProviderTest {
    private lateinit var sTSProvider: SecureTokenServiceProvider
    private lateinit var sTSProviderImpl: SecureTokenServiceProviderImpl
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()
    private val userRepository: UserRepository = mockk()
    private val dataStoreUtil: DataStoreUtil = mockk()
    private val gsonProvider: GsonProvider = mockk()
    @BeforeEach
    fun setup() {
        sTSProvider = SecureTokenServiceProviderImpl(
            trueCloudV3Interface = trueCloudV3Interface,
            userRepository = userRepository,
            dataStoreInterface = dataStoreUtil,
            gsonProvider = gsonProvider
        )
        sTSProviderImpl = SecureTokenServiceProviderImpl(
            trueCloudV3Interface = trueCloudV3Interface,
            userRepository = userRepository,
            dataStoreInterface = dataStoreUtil,
            gsonProvider = gsonProvider
        )
        coEvery { userRepository.getSsoId() }.returns("ssoid")
    }

    @Test
    fun `test STSProvider getSTS null`() = runTest {
        // arrange
        val dataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            endpoint = "endpoint",
        )
        val response = GetSecureTokenServiceResponse(
            data = dataResponse,
            error = null
        )
        val secureTokenServiceDataResponse = mockk<SecureTokenServiceDataResponse>()
        coEvery { gsonProvider.getDataClass(any(), SecureTokenServiceDataResponse::class.java) } returns dataResponse
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns Gson().toJson(secureTokenServiceDataResponse)
        coEvery {
            dataStoreUtil.putPreference(
                key = any<Preferences.Key<String>>(),
                value = any()
            )
        } returns Unit

        coEvery {
            trueCloudV3Interface.getSecureTokenService(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.success(response)

        // act
        val flow = sTSProvider.getSTS()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(dataResponse, response)
        }
    }

    @Test
    fun `test STSProvider getSTS expired`() = runTest {
        // arrange
        val dataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            endpoint = "endpoint",
            expiresAt = "2022-04-07T08:19:27.019Z",
        )
        val response = GetSecureTokenServiceResponse(
            data = dataResponse,
            error = null
        )

        coEvery { gsonProvider.getDataClass(any(), SecureTokenServiceDataResponse::class.java) } returns dataResponse
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns Gson().toJson(dataResponse)
        coEvery {
            dataStoreUtil.putPreference(
                key = any<Preferences.Key<String>>(),
                value = any()
            )
        } returns Unit

        val calendar = mockk<Calendar>()
        val date = mockk<Date>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns calendar
        every { calendar.time } returns date
        coEvery {
            trueCloudV3Interface.getSecureTokenService(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.success(response)

        // act
        val flow = sTSProvider.getSTS()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(dataResponse, response)
        }
    }

    @Test
    fun `test STSProvider getSTS null and empty data`() = runTest {
        // arrange
        val dataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            endpoint = "endpoint",
            expiresAt = "2022-04-07T08:19:27.019Z",
        )
        val response = GetSecureTokenServiceResponse(
            data = dataResponse,
            error = null
        )
        coEvery { gsonProvider.getDataClass(any(), SecureTokenServiceDataResponse::class.java) } returns dataResponse
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns ""
        coEvery {
            dataStoreUtil.putPreference(
                key = any<Preferences.Key<String>>(),
                value = any()
            )
        } returns Unit

        coEvery {
            trueCloudV3Interface.getSecureTokenService(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.success(response)

        // act
        val flow = sTSProvider.getSTS()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(dataResponse, response)
        }
    }

    @Test
    fun `test getSTSNetworkFlow isloading`() = runTest {
        // arrange
        val dataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            endpoint = "endpoint",
            expiresAt = "2023-04-07T08:19:27Z",
        )
        coEvery { gsonProvider.getDataClass(any(), SecureTokenServiceDataResponse::class.java) } returns dataResponse

        val calendar = mockk<Calendar>()
        val date = mockk<Date>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns calendar
        every { calendar.time } returns date
        every { date.time } returns 0L
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns Gson().toJson(dataResponse)

        sTSProviderImpl.loading = true
        // act
        val flow = sTSProviderImpl.getSTSNetworkFlow()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(dataResponse, response)
        }
    }
    @Test
    fun `test getRawData is null`() = runTest {
        // arrange
        val dataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            endpoint = "endpoint",
            expiresAt = "2023-04-07T08:19:27Z",
        )
        coEvery { gsonProvider.getDataClass(any(), SecureTokenServiceDataResponse::class.java) } returns dataResponse

        val calendar = mockk<Calendar>()
        val date = mockk<Date>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns calendar
        every { calendar.time } returns date
        every { date.time } returns 0L
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns "" andThen Gson().toJson(dataResponse)

        sTSProviderImpl.loading = true
        // act
        val flow = sTSProviderImpl.getSTSNetworkFlow()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(dataResponse, response)
        }
    }

    @Test
    fun `test getSTSNetworkFlow isloading success`() = runTest {
        // arrange
        val dataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            endpoint = "endpoint",
            expiresAt = "2023-04-07T08:19:27Z",
        )
        val response = GetSecureTokenServiceResponse(
            data = dataResponse,
            error = null
        )

        coEvery { gsonProvider.getDataClass(any(), SecureTokenServiceDataResponse::class.java) } returns dataResponse
        coEvery {
            trueCloudV3Interface.getSecureTokenService(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.success(response)

        coEvery {
            dataStoreUtil.putPreference(
                key = any<Preferences.Key<String>>(),
                value = any()
            )
        } returns Unit

        // act
        val flow = sTSProviderImpl.getSTSNetworkFlow()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(dataResponse, response)
        }
    }
    @Test
    fun `test getSTSNetworkFlow isloading true`() = runTest {
        // arrange
        val dataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            endpoint = "endpoint",
            expiresAt = "2023-04-07T08:19:27Z",
        )

        coEvery { gsonProvider.getDataClass(any(), SecureTokenServiceDataResponse::class.java) } returns dataResponse
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns "" andThen Gson().toJson(dataResponse)
        sTSProviderImpl.loading = true
        // act
        val flow = sTSProviderImpl.getSTSNetworkFlow()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(dataResponse, response)
        }
    }

    @Test
    fun `test getSTSNetworkFlow error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.getSecureTokenService(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.error(
            404,
            responseBody
        )

        // act
        val flow = sTSProviderImpl.getSTSNetworkFlow()

        // assert
        flow.catch { response ->
            assertEquals("get sts error", response.message)
        }.collect()
    }
}
