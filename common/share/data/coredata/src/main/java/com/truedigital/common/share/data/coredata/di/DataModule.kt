package com.truedigital.common.share.data.coredata.di

import com.truedigital.common.share.data.coredata.data.api.CmsFnCounterApiInterface
import com.truedigital.common.share.data.coredata.data.api.CmsShelvesApiInterface
import com.truedigital.common.share.data.coredata.data.api.PersonalizeApiInterface
import com.truedigital.common.share.data.coredata.data.api.SimilarApiInterface
import com.truedigital.common.share.datalegacy.data.api.ApiGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.ApiScalarsAndGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.di.BASE_URL_CMS_FN
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV1OkHttp
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV2OkHttp
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
object DataModule {

    @Singleton
    @Provides
    fun provideCmsShelvesApiInterface(
        @JsonFeaturePathV2OkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): CmsShelvesApiInterface {
        return ApiGsonBuilder(
            okHttpClient = okHttpClient,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_CONTENT)
    }

    @Singleton
    @Provides
    fun providePersonalizeApiInterface(
        @JsonFeaturePathV1OkHttp okHttpClient: OkHttpClient,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): PersonalizeApiInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient = okHttpClient,
            scalarsConverterFactory = scalarsConverterFactory,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_DMP2)
    }

    @Singleton
    @Provides
    fun provideSimilarApiInterface(
        @JsonFeaturePathV2OkHttp okHttpClient: OkHttpClient,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): SimilarApiInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient = okHttpClient,
            scalarsConverterFactory = scalarsConverterFactory,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_CONTENT)
    }

    @Singleton
    @Provides
    fun provideCmsFnCounterApiInterface(
        @JsonFeaturePathV1OkHttp okHttpClient: OkHttpClient,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactor: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): CmsFnCounterApiInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient = okHttpClient,
            scalarsConverterFactory = scalarsConverterFactory,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactor,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BASE_URL_CMS_FN)
    }
}
