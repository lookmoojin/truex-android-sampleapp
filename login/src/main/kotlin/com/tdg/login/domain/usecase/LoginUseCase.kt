package com.tdg.login.domain.usecase

import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.tdg.login.data.model.OauthRequest
import com.tdg.login.data.repository.OauthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LoginUseCase {
    fun execute(username: String, password: String, isPreProd: Boolean): Flow<String>
}

class LoginUseCaseImpl @Inject constructor(
    private val authDomainUseCase: AuthDomainUseCase,
    private val oauthRepository: OauthRepository
) : LoginUseCase {
    @RequiresApi(Build.VERSION_CODES.FROYO)
    override fun execute(
        username: String,
        password: String,
        isPreProd: Boolean
    ): Flow<String> {
        authDomainUseCase.save(isPreProd)
        return oauthRepository.login(
            OauthRequest(
                username = username,
                password = Base64.encodeToString(password.toByteArray(), Base64.DEFAULT)
            )
        ).map {
            it.accessToken.orEmpty()
        }
    }
}
