package com.truedigital.features.truecloudv3.provider

import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import javax.inject.Inject

interface AmazonS3ClientProvider {
    fun getS3ClientProvider(
        endPoint: String,
        accessToken: String,
        secretKey: String,
        sessionKey: String
    ): AmazonS3Client
}

class AmazonS3ClientProviderImpl @Inject constructor() : AmazonS3ClientProvider {

    companion object {
        private const val timeout = 600000
        private const val retry = 3
        private const val maxConnection = 10
    }
    override fun getS3ClientProvider(
        endpoint: String,
        accessToken: String,
        secretKey: String,
        sessionKey: String
    ): AmazonS3Client {
        val credentials = BasicSessionCredentials(accessToken, secretKey, sessionKey)
        val region: Region = Region.getRegion(Regions.AP_SOUTHEAST_1)
        val s3Client = AmazonS3Client(
            credentials,
            region,
            getClientConfiguration()
        )
        s3Client.endpoint = endpoint
        return s3Client
    }

    private fun getClientConfiguration(): ClientConfiguration {
        return ClientConfiguration()
            .withMaxErrorRetry(retry)
            .withConnectionTimeout(timeout)
            .withSocketTimeout(timeout)
            .withProtocol(Protocol.HTTPS)
            .withMaxConnections(maxConnection)
    }
}
