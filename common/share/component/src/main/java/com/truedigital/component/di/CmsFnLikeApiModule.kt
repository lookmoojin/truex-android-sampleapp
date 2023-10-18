package com.truedigital.component.di

import com.truedigital.common.share.datalegacy.data.api.ApiScalarsAndGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV1OkHttp
import com.truedigital.component.widget.like.data.api.CmsFnLikeApiInterface
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
object CmsFnLikeApiModule {

    @Provides
    @Singleton
    fun provideCmsFnLikeApiInterface(
        @JsonFeaturePathV1OkHttp okHttpClient: OkHttpClient,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): CmsFnLikeApiInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient = okHttpClient,
            scalarsConverterFactory = scalarsConverterFactory,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_CMS_API_FN)
    }
}
