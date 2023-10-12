package com.truedigital.features.truecloudv3.di

import com.google.gson.GsonBuilder
import com.truedigital.common.share.datalegacy.data.api.ApiGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.di.DefaultOkHttp
import com.truedigital.common.share.datalegacy.data.api.interceptor.ContentTypeInterceptor
import com.truedigital.common.share.datalegacy.domain.endpoint.usecase.GetApiConfigurationUseCase
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.RxErrorHandlingAdapter
import com.truedigital.core.api.di.RxJava2Adapter
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.features.truecloudv3.BuildConfig
import com.truedigital.features.truecloudv3.annotations.TrueCloudV3HttpClient
import com.truedigital.features.truecloudv3.annotations.TrueCloudV3Interceptor
import com.truedigital.features.truecloudv3.common.TrueCloudV3ApiKey.BASE_PUBLIC_URL_KEY
import com.truedigital.features.truecloudv3.common.TrueCloudV3ApiKey.BASE_URL_KEY
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3DownloadInterface
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3GetSharedFileInterface
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3OauthInterceptor
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3SearchFileInterface
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3UploadInterface
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class TrueCloudV3ProvidesModule {

    @Provides
    @Singleton
    @TrueCloudV3Interceptor
    fun providesTrueCloudV3OauthInterceptor(
        deviceRepository: DeviceRepository,
        authManagerWrapper: AuthManagerWrapper
    ): Interceptor {
        return TrueCloudV3OauthInterceptor(deviceRepository, authManagerWrapper)
    }

    @Provides
    @TrueCloudV3HttpClient
    fun providesBaseOkHttpClient(
        @DefaultOkHttp okHttpClient: OkHttpClient,
        @TrueCloudV3Interceptor trueCloudOauthInterceptor: Interceptor,
    ): OkHttpClient {
        return okHttpClient.newBuilder()
            .addInterceptor(ContentTypeInterceptor(ContentTypeInterceptor.CONTENT_TYPE_JSON))
            .addInterceptor(trueCloudOauthInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesTrueCloudV3Interface(
        @TrueCloudV3HttpClient okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory,
        getApiConfigurationUseCase: GetApiConfigurationUseCase
    ): TrueCloudV3Interface {
        val gson = GsonBuilder().serializeNulls().create()
        val converter = GsonConverterFactory.create(gson)
        val baseUrl = getApiConfigurationUseCase.getServiceUrl(true, BASE_URL_KEY)
            .takeIf { it.isNotEmpty() } ?: BuildConfig.BASE_URL_TRUE_CLOUD
        return ApiGsonBuilder(
            okHttpClient = okHttpClient,
            gsonConverterFactory = converter,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(baseUrl)
    }

    @Provides
    @Singleton
    fun providesTrueCloudV3UploadInterface(
        @TrueCloudV3HttpClient okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory,
        getApiConfigurationUseCase: GetApiConfigurationUseCase
    ): TrueCloudV3UploadInterface {
        val baseUrl = getApiConfigurationUseCase.getServiceUrl(true, BASE_URL_KEY)
            .takeIf { it.isNotEmpty() } ?: BuildConfig.BASE_URL_TRUE_CLOUD
        return ApiGsonBuilder(
            okHttpClient = okHttpClient,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(baseUrl)
    }

    @Provides
    @Singleton
    fun providesTrueCloudV3DownloadInterface(
        @TrueCloudV3HttpClient okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory,
        getApiConfigurationUseCase: GetApiConfigurationUseCase
    ): TrueCloudV3DownloadInterface {
        val baseUrl = getApiConfigurationUseCase.getServiceUrl(true, BASE_URL_KEY)
            .takeIf { it.isNotEmpty() } ?: BuildConfig.BASE_URL_TRUE_CLOUD
        return ApiGsonBuilder(
            okHttpClient = okHttpClient,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(baseUrl)
    }

    @Provides
    @Singleton
    fun providesTrueCloudV3GetSharedFileInterface(
        @DefaultOkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory,
        getApiConfigurationUseCase: GetApiConfigurationUseCase
    ): TrueCloudV3GetSharedFileInterface {
        val baseUrl = getApiConfigurationUseCase.getServiceUrl(true, BASE_PUBLIC_URL_KEY)
            .takeIf { it.isNotEmpty() } ?: BuildConfig.BASE_URL_PUBLIC
        return ApiGsonBuilder(
            okHttpClient = okHttpClient.newBuilder()
                .addInterceptor(ContentTypeInterceptor(ContentTypeInterceptor.CONTENT_TYPE_JSON))
                .build(),
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(baseUrl)
    }

    @Provides
    @Singleton
    fun providesTrueCloudV3SearchFileInterface(
        @TrueCloudV3HttpClient okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory,
        getApiConfigurationUseCase: GetApiConfigurationUseCase
    ): TrueCloudV3SearchFileInterface {
        val baseUrl = getApiConfigurationUseCase.getServiceUrl(true, BASE_URL_KEY)
            .takeIf { it.isNotEmpty() } ?: BuildConfig.BASE_URL_TRUE_CLOUD
        return ApiGsonBuilder(
            okHttpClient = okHttpClient,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(baseUrl)
    }
}
