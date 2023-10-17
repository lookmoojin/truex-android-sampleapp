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
    fun execute(username: String, password: String, isPreProd: Boolean): Flow<Pair<String, String>>
}

class LoginUseCaseImpl @Inject constructor(
    private val loginDomainUseCase: LoginDomainUseCase,
    private val oauthRepository: OauthRepository
) : LoginUseCase {
    @RequiresApi(Build.VERSION_CODES.FROYO)
    override fun execute(
        username: String,
        password: String,
        isPreProd: Boolean
    ): Flow<Pair<String, String>> {
        loginDomainUseCase.save(isPreProd)
        return oauthRepository.login(
            OauthRequest(
                username = username,
                password = Base64.encodeToString(password.toByteArray(), Base64.DEFAULT)
            ),
            isPreProd
        ).map {
            Pair(it.accessToken.orEmpty(), it.refreshToken.orEmpty())
        }
    }
}
