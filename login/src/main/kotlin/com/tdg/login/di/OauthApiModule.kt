package com.tdg.login.di

import com.tdg.login.api.ApiBuilder
import com.tdg.login.api.OauthApiInterface
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter
import javax.inject.Singleton

@Module
object OauthApiModule {

    @Provides
    @Singleton
    fun providesOauthApiInterface(
        @DefaultOkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
    ): OauthApiInterface {
        return ApiBuilder.ApiScalarsAndGsonBuilder(
            okHttpClient,
            gsonConverterFactory
        ).build("https://iam.trueid-preprod.net/")
    }
}
