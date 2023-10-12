package com.tdg.login.domain.usecase

import com.tdg.login.data.model.OauthRequest
import com.tdg.login.data.repository.OauthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LoginUseCase {
    fun login(username: String, password: String): Flow<String>
}

class LoginUseCaseImpl @Inject constructor(
    private val oauthRepository: OauthRepository
) : LoginUseCase {
    override fun login(username: String, password: String): Flow<String> {
        return oauthRepository.login(
            OauthRequest(
                username = "0620922456",
                password = "VGVzdFBhc3MwMQ==" //encode base64
            )
        ).map {
            it.toString()
        }
    }
}
