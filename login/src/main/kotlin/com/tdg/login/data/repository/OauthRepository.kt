package com.tdg.login.data.repository

import com.tdg.login.api.OauthApiInterface
import com.tdg.login.data.model.OauthRequest
import com.tdg.login.data.model.OauthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
            apiInterface.login(request).run {
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