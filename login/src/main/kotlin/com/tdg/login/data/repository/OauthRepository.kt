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
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

interface OauthRepository {
    fun login(request: OauthRequest): Flow<OauthResponse>
}

class OauthRepositoryImpl @Inject constructor(
    private val apiInterface: OauthApiInterface,
    private val userRepository: UserRepository
) : OauthRepository {

    override fun login(request: OauthRequest): Flow<OauthResponse> {
        return flow {
            val clientId = request.clientId.toRequestBody("text/plain".toMediaTypeOrNull())
            val clientSecret =
                request.clientSecret.toRequestBody("text/plain".toMediaTypeOrNull())
            val username = request.username.toRequestBody("text/plain".toMediaTypeOrNull())
            val password = request.password.toRequestBody("text/plain".toMediaTypeOrNull())
            val grantType = request.grantType.toRequestBody("text/plain".toMediaTypeOrNull())
            val scope = request.scope.toRequestBody("text/plain".toMediaTypeOrNull())
            val deviceId = request.deviceId.toRequestBody("text/plain".toMediaTypeOrNull())
            val deviceModel = request.deviceModel.toRequestBody("text/plain".toMediaTypeOrNull())
            val latlong = request.latlong.toRequestBody("text/plain".toMediaTypeOrNull())
            val ipAddress = request.ipAddress.toRequestBody("text/plain".toMediaTypeOrNull())
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
                    userRepository.saveSsoId(getSsoId(body.accessToken.orEmpty()))
                    userRepository.saveAccessToken(body.accessToken.orEmpty())
                    emit(body)
                } else {
                    error(errorBody()?.string().orEmpty())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun getSsoId(accessToken: String): String {
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