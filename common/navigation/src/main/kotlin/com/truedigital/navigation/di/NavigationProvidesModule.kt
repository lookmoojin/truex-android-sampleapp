package com.truedigital.navigation.di

import com.truedigital.common.share.datalegacy.data.api.ApiScalarsAndGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV2OkHttp
import com.truedigital.core.BuildConfig
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.RxJava2Adapter
import com.truedigital.core.api.di.ScalarsConverter
import com.truedigital.navigation.data.api.PersonaApiInterface
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import javax.inject.Singleton

@Module
class NavigationProvidesModule {

    @Provides
    @Singleton
    fun providesPersonaApiInterface(
        @JsonFeaturePathV2OkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
    ): PersonaApiInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient,
            gsonConverterFactory,
            scalarsConverterFactory,
            rxJavaAdapterFactory,
            errorHandlingAdapterFactory,
            errorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_CONTENT)
    }
}
