package com.truedigital.features.truecloudv3.data.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.taskexecutor.SerialExecutor
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3DownloadInterface
import com.truedigital.features.truecloudv3.data.model.InitialDownloadResponse
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals

class TrueCloudV3DownloadWorkerTest {

    val mockContext = mockk<Context>()
    val mockWorkerParams = mockk<WorkerParameters>()
    val mockTrueCloudV3DownloadInterface = mockk<TrueCloudV3DownloadInterface>()
    val mockUserRepository = mockk<UserRepository>()
    val mockSecureTokenServiceProvider = mockk<SecureTokenServiceProvider>()
    val mockContextDataProvider = mockk<ContextDataProvider>()
    val taskExecutor = mockk<TaskExecutor>()
    val serialExecutor = mockk<SerialExecutor>()
    lateinit var worker: TrueCloudV3DownloadWorker

    @BeforeEach
    fun setup() {
        val inputData = Data.Builder()
            .putString("key", "key")
            .putString("file_path", "file_path")
            .build()

        every { mockWorkerParams.inputData } returns inputData
        every { mockWorkerParams.taskExecutor } returns taskExecutor
        every { taskExecutor.mainThreadExecutor } returns serialExecutor
        worker = TrueCloudV3DownloadWorker(
            mockContext,
            mockWorkerParams
        )
    }

    @Test
    @Disabled
    fun testDoWork() {
        val mockResponse = mockk<Response<InitialDownloadResponse>>()
        val mockContext = mockk<Context>()
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )

        coEvery {
            mockTrueCloudV3DownloadInterface.initialDownload(
                any(),
                any()
            )
        } returns mockResponse
        every { mockResponse.isSuccessful } returns true
        every { mockSecureTokenServiceProvider.getSTS() } returns flowOf(stsData)
        every { mockUserRepository.getSsoId() } returns "ssoid"
        every { mockContextDataProvider.getDataContext() } returns mockContext
        every { mockContext.startService(any()) } returns mockk()

        runTest {
            worker.doWork().also { result ->
                // Assert that the result is success
                assertEquals(ListenableWorker.Result.success(), result)
            }
        }
    }

    @Test
    @Disabled
    fun testDoWorkError() {
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        val mockContext = mockk<Context>()
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )

        coEvery {
            mockTrueCloudV3DownloadInterface.initialDownload(
                any(),
                any()
            )
        } returns Response.error(400, responseBody)
        every { mockSecureTokenServiceProvider.getSTS() } returns flowOf(stsData)
        every { mockUserRepository.getSsoId() } returns "ssoid"
        every { mockContextDataProvider.getDataContext() } returns mockContext
        every { mockContext.startService(any()) } returns mockk()

        runTest {
            worker.doWork().also { result ->
                // Assert that the result is success
                assertEquals(ListenableWorker.Result.success(), result)
            }
        }
    }
}
