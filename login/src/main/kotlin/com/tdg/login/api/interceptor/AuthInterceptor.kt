package com.tdg.login.api.interceptor

import com.tdg.login.domain.usecase.AuthDomainUseCase
import com.tdg.login.domain.usecase.AuthDomainUseCaseImpl.Companion.PREPROD
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authDomainUseCase: AuthDomainUseCase
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.toString()
        val domain = authDomainUseCase.execute()
        val customRequest = chain.request().newBuilder()
            .url(url.replace(PREPROD, domain))
            .build()
        return chain.proceed(customRequest)
    }
}
