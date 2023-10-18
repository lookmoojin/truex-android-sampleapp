package com.truedigital.features.truecloudv3.provider

import android.content.Context
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtilityOptions
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import javax.inject.Inject

interface TrueCloudV3TransferUtilityProvider {
    fun getTransferUtility(
        data: SecureTokenServiceDataResponse?,
        context: Context
    ): TransferUtility

    fun getDefaultTransferUtility(context: Context): TransferUtility
    fun getTrueCloudV3TransferObserverById(context: Context, id: Int): TrueCloudV3TransferObserver?
    fun resumeTransferById(context: Context, id: Int): TrueCloudV3TransferObserver?
    fun cancelTransferById(context: Context, id: Int): Boolean
    fun pauseTransferById(context: Context, id: Int): Boolean
}

class TrueCloudV3TransferUtilityProviderImpl @Inject constructor(
    private val s3ClientProvider: AmazonS3ClientProvider
) : TrueCloudV3TransferUtilityProvider {
    companion object {
        private const val maxThreadPool = 10
        private const val DEFAULT_BUCKET_NAME = "data"
        private const val EMPTY_STRING = ""
        private const val DEFAULT_ENDPOINT = "https://trueidcloud.trueid.net"
    }

    var endPoint = DEFAULT_ENDPOINT
    var accessToken = EMPTY_STRING
    var secretKey = EMPTY_STRING
    var sessionKey = EMPTY_STRING
    private var transferUtilityInstant: TransferUtility? = null
    override fun getTransferUtility(
        data: SecureTokenServiceDataResponse?,
        context: Context
    ): TransferUtility {
        endPoint = data?.endpoint ?: DEFAULT_ENDPOINT
        accessToken = data?.accessToken.orEmpty()
        secretKey = data?.secretKey.orEmpty()
        sessionKey = data?.sessionKey.orEmpty()
        val s3Client = s3ClientProvider.getS3ClientProvider(
            endPoint = endPoint,
            accessToken = accessToken,
            secretKey = secretKey,
            sessionKey = sessionKey
        )

        val options = TransferUtilityOptions()
        options.transferThreadPoolSize = maxThreadPool

        val transferUtility = TransferUtility.builder()
            .transferUtilityOptions(options)
            .s3Client(s3Client)
            .defaultBucket(DEFAULT_BUCKET_NAME)
            .context(context)
            .build()
        transferUtilityInstant = transferUtility
        return transferUtility
    }

    override fun getDefaultTransferUtility(context: Context): TransferUtility {
        val options = TransferUtilityOptions()
        options.transferThreadPoolSize = maxThreadPool
        return transferUtilityInstant ?: TransferUtility.builder()
            .context(context)
            .transferUtilityOptions(options)
            .defaultBucket(DEFAULT_BUCKET_NAME)
            .s3Client(
                s3ClientProvider.getS3ClientProvider(
                    endPoint = endPoint,
                    accessToken = accessToken,
                    secretKey = secretKey,
                    sessionKey = sessionKey
                )
            )
            .build().also {
                transferUtilityInstant = it
            }
    }

    override fun getTrueCloudV3TransferObserverById(
        context: Context,
        id: Int
    ): TrueCloudV3TransferObserver? {
        return getDefaultTransferUtility(context).getTransferById(id)?.let {
            TrueCloudV3TransferObserver(it)
        }
    }

    override fun resumeTransferById(context: Context, id: Int): TrueCloudV3TransferObserver? {
        return getDefaultTransferUtility(context).resume(id)?.let {
            TrueCloudV3TransferObserver(it)
        }
    }

    override fun cancelTransferById(context: Context, id: Int): Boolean {
        return getDefaultTransferUtility(context).cancel(id)
    }

    override fun pauseTransferById(context: Context, id: Int): Boolean {
        return getDefaultTransferUtility(context).pause(id)
    }
}
