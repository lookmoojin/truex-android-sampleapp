package com.truedigital.common.share.datalegacy.data.api.di

import com.truedigital.common.share.datalegacy.data.api.ApiScalarsAndGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.ice.IceApiInterface
import com.truedigital.core.BuildConfig
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.RxErrorHandlingAdapter
import com.truedigital.core.api.di.RxJava2Adapter
import com.truedigital.core.api.di.ScalarsConverter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import javax.inject.Singleton

@Module
object IceApiModule {

    @Provides
    @Singleton
    fun providesIceApiInterface(
        @JsonFeaturePathV1OkHttp okHttpClient: OkHttpClient,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactor: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): IceApiInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient,
            scalarsConverterFactory,
            gsonConverterFactory,
            rxJavaAdapterFactor,
            errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_DMP2)
    }
}
