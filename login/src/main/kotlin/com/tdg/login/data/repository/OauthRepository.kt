package com.tdg.login.data.repository

import com.tdg.login.api.OauthApiInterface
import com.tdg.login.data.model.OauthRequest
import com.tdg.login.data.model.OauthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

interface OauthRepository {
    fun login(request: OauthRequest): Flow<OauthResponse>
}

class OauthRepositoryImpl @Inject constructor(
    private val apiInterface: OauthApiInterface
) : OauthRepository {

    companion object {
        const val LOGIN_FAILED = "login failed"
    }

    override fun login(request: OauthRequest): Flow<OauthResponse> {
        return flow {
            val clientId = RequestBody.create(MediaType.parse("text/plain"), request.clientId)
            val clientSecret =
                RequestBody.create(MediaType.parse("text/plain"), request.clientSecret)
            val username = RequestBody.create(MediaType.parse("text/plain"), request.username)
            val password = RequestBody.create(MediaType.parse("text/plain"), request.password)
            val grantType = RequestBody.create(MediaType.parse("text/plain"), request.grantType)
            val scope = RequestBody.create(MediaType.parse("text/plain"), request.scope)
            val deviceId = RequestBody.create(MediaType.parse("text/plain"), request.deviceId)
            val deviceModel = RequestBody.create(MediaType.parse("text/plain"), request.deviceModel)
            val latlong = RequestBody.create(MediaType.parse("text/plain"), request.latlong)
            val ipAddress = RequestBody.create(MediaType.parse("text/plain"), request.ipAddress)
            apiInterface.login(
                clientId,
                clientSecret,
                username,
                password,
                grantType,
                scope,
                deviceId,
                deviceModel,
                latlong,
                ipAddress
            ).run {
                val body = body()
                if (isSuccessful && body != null) {
                    emit(body)
                } else {
                    error(LOGIN_FAILED)
                }
            }
        }
    }
}