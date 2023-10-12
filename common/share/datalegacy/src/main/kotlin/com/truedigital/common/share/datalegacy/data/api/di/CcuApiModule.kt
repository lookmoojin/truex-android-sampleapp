package com.truedigital.common.share.datalegacy.data.api.di

import com.truedigital.common.share.datalegacy.data.api.ApiGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.ccu.CcuApiInterface
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.RxErrorHandlingAdapter
import com.truedigital.core.api.di.RxJava2Adapter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import javax.inject.Singleton

const val BASE_URL = "https://hccu.stm.trueid.net"

@Module
object CcuApiModule {

    @Singleton
    @Provides
    fun provideCcuApiInterface(
        @JsonFeaturePathV2OkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverter: Converter.Factory,
        @RxJava2Adapter rxJavaAdapter: CallAdapter.Factory,
        @ErrorHandlingAdapter errorAdapter: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorAdapter: CallAdapter.Factory
    ): CcuApiInterface {
        return ApiGsonBuilder(
            okHttpClient = okHttpClient,
            gsonConverterFactory = gsonConverter,
            rxJavaAdapterFactory = rxJavaAdapter,
            errorHandlingAdapterFactory = errorAdapter,
            rxErrorHandlingAdapterFactory = rxErrorAdapter
        ).build(BASE_URL)
    }
}
