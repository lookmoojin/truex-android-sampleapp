package com.truedigital.features.truecloudv3.provider

import android.app.Application
import android.content.Context
import android.net.Uri
import com.amazonaws.services.s3.AmazonS3Client
import com.nhaarman.mockitokotlin2.any
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

internal class TrueCloudV3TransferUtilityProviderTest {
    lateinit var trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider
    private val context = mockk<Context>()
    private val application = mockk<Application>()
    private val s3ClientProvider = mockk<AmazonS3ClientProvider>()
    private val uri: Uri = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        trueCloudV3TransferUtilityProvider = TrueCloudV3TransferUtilityProviderImpl(
            s3ClientProvider = s3ClientProvider
        )
    }

    @Test
    fun testGetTransferUtility() = runTest {
        // arrange
        val secureTokenServiceDataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val amazonS3Client = mockk<AmazonS3Client>()
        every {
            s3ClientProvider.getS3ClientProvider(
                any(),
                any(),
                any(),
                any()
            )
        } returns amazonS3Client
        every { application.applicationContext } returns application
        every { application.packageName } returns ""
        every { application.getSystemService(any()) } returns any()

        // act
        val response = trueCloudV3TransferUtilityProvider.getTransferUtility(
            secureTokenServiceDataResponse,
            application
        )

        // assert
        assertNotNull(response)
    }

    @Test
    fun testGetDefaultTransferUtility() = runTest {
        // arrange
        val s3Client: AmazonS3Client = mockk(relaxed = true)
        every { application.applicationContext } returns application
        every { application.packageName } returns ""
        every { application.getSystemService(any()) } returns any()
        every { s3ClientProvider.getS3ClientProvider(any(), any(), any(), any()) } returns s3Client

        // act
        val response = trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(application)

        // assert
        assertNotNull(response)
    }
}
