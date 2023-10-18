package com.tdg.login.di

import com.tdg.login.api.ApiBuilder
import com.tdg.login.api.OauthApiInterface
import com.tdg.login.api.interceptor.AuthInterceptor
import com.tdg.login.base.addInterceptor
import com.tdg.login.domain.usecase.AuthDomainUseCase
import com.tdg.login.domain.usecase.AuthDomainUseCaseImpl.Companion.PREPROD
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter

@Module
object OauthApiModule {

    @Provides
    fun providesOauthApiInterface(
        @DefaultOkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        authDomainUseCase: AuthDomainUseCase
    ): OauthApiInterface {
        return ApiBuilder.ApiBasicBuilder(
            okHttpClient.addInterceptor(AuthInterceptor(authDomainUseCase)),
            gsonConverterFactory
        ).build(PREPROD)
    }
}
