package com.tdg.login.data.repository

import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.tdg.login.api.OauthApiInterface
import com.tdg.login.base.fromJson
import com.tdg.login.data.model.OauthRequest
import com.tdg.login.data.model.OauthResponse
import com.tdg.login.data.model.PayloadResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

interface OauthRepository {
    fun login(request: OauthRequest): Flow<OauthResponse>
    fun getSsoId(): String
}

class OauthRepositoryImpl @Inject constructor(
    private val apiInterface: OauthApiInterface
) : OauthRepository {

    companion object {
        private var accessToken: String = ""
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
                    accessToken = body.accessToken.orEmpty()
                    emit(body)
                } else {
                    error(errorBody()?.string().orEmpty())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    override fun getSsoId(): String {
        val payloadJson = decodeJwtPayload(accessToken)
        val payload = Gson().fromJson<PayloadResponse>(payloadJson)
        return payload.sub.orEmpty()
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    private fun decodeJwtPayload(jwt: String): String {
        val jwtParts = jwt.split(".")
        if (jwtParts.size != 3) {
            throw IllegalArgumentException("Invalid JWT format")
        }
        val base64EncodedPayload = jwtParts[1]
        val decodedPayload = Base64.decode(base64EncodedPayload, Base64.URL_SAFE)
        return String(decodedPayload, Charsets.UTF_8)
    }
}