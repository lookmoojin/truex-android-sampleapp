package com.truedigital.features.music.di

import com.truedigital.common.share.datalegacy.data.api.ApiGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.di.JsonWithNoHeaderOkHttp
import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import com.truedigital.common.share.datalegacy.extension.addInterceptor
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.RxErrorHandlingAdapter
import com.truedigital.core.api.di.RxJava2Adapter
import com.truedigital.features.music.api.SearchMusicApi
import com.truedigital.features.music.api.interceptor.MusicServicesInterceptor
import com.truedigital.features.tuned.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import javax.inject.Singleton

@Module
object MusicSearchModule {

    @Provides
    @Singleton
    fun providesSearchMusicApi(
        @JsonWithNoHeaderOkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory,
        apiConfigurationManager: ApiConfigurationManager,
    ): SearchMusicApi {
        return ApiGsonBuilder(
            okHttpClient.addInterceptor(MusicServicesInterceptor(apiConfigurationManager)),
            gsonConverterFactory,
            rxJavaAdapterFactory,
            errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory,
        ).build(BuildConfig.API_URL_TUNEDGLOBAL)
    }
}
