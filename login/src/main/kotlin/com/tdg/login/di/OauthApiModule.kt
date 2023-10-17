package com.tdg.login.di

import com.tdg.login.api.ApiBuilder
import com.tdg.login.api.OauthApiInterface
import com.tdg.login.domain.usecase.LoginDomainUseCase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter
import javax.inject.Inject
import javax.inject.Singleton

@Module
object OauthApiModule {

    @Provides
    fun providesOauthApiInterface(
        @DefaultOkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        loginDomainUseCase : LoginDomainUseCase
    ): OauthApiInterface {
        return ApiBuilder.ApiScalarsAndGsonBuilder(
            okHttpClient,
            gsonConverterFactory
        ).build(loginDomainUseCase.execute())
    }
}
