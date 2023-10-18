package com.truedigital.features.tuned.injection.module

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.debugOrNonProdInvoke
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.api.AuthenticatedRequestInterceptor
import com.truedigital.features.tuned.data.api.CancelledRequestInterceptor
import com.truedigital.features.tuned.data.api.ConnectivityInterceptor
import com.truedigital.features.tuned.data.api.ContentLanguageInterceptor
import com.truedigital.features.tuned.data.api.CountryInterceptor
import com.truedigital.features.tuned.data.api.StoreIdInterceptor
import com.truedigital.features.tuned.data.api.UserAgentInterceptor
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.download.ImageManager
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {
    companion object {
        const val DEFAULT_OKHTTP_BUILDER = "default_okhttp_builder"
        const val DEFAULT_OKHTTP = "default_okhttp"
        const val DEFAULT_RETROFIT = "default_retrofit"

        const val TUNED_OKHTTP_BUILDER = "tuned_okhttp_builder"
        const val TUNED_OKHTTP = "tuned_okhttp"
        const val AUTHENTICATED_OKHTTP = "authenticated_okhttp"

        const val METADATA_RETROFIT = "metadata_retrofit"
        const val BASIC_SERVICES_RETROFIT = "basic_services_retrofit"
        const val AUTHENTICATED_SERVICES_RETROFIT = "authenticated_services_retrofit"

        const val HTTP_CODE_UNAUTHORISED = 401
        const val HTTP_CODE_FORBIDDEN = 403
        const val HTTP_CODE_RESOURCE_NOT_FOUND = 404
        const val HTTP_CODE_ENHANCE_YOUR_CALM = 420
        const val HTTP_CODE_UPGRADE_REQUIRED = 426

        private const val DEFAULT_TIMEOUT_SECONDS = 30L
        private const val KEEP_ALIVE_DURATION = 5L
    }

    @Provides
    @Singleton
    @Named(DEFAULT_RETROFIT)
    fun provideDefaultRetrofit(
        @Named(DEFAULT_OKHTTP) httpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(httpClient)
            .baseUrl("http://somebaseurlthatwillgetreplaced/")
            .build()

    @Provides
    @Singleton
    @Named(BASIC_SERVICES_RETROFIT)
    fun provideBasicServicesRetrofit(
        @Named(TUNED_OKHTTP) httpClient: OkHttpClient,
        config: Configuration,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(httpClient)
            .baseUrl(config.servicesUrl)
            .build()

    @Provides
    @Singleton
    @Named(AUTHENTICATED_SERVICES_RETROFIT)
    fun provideAuthenticatedServicesRetrofit(
        @Named(AUTHENTICATED_OKHTTP) httpClient: OkHttpClient,
        config: Configuration,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(httpClient)
            .baseUrl(config.servicesUrl)
            .build()

    @Provides
    @Singleton
    @Named(METADATA_RETROFIT)
    fun provideMetadataRetrofit(
        @Named(TUNED_OKHTTP) httpClient: OkHttpClient,
        config: Configuration,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(httpClient)
            .baseUrl(config.metadataUrl)
            .build()

    @Provides
    @Singleton
    @Named(DEFAULT_OKHTTP)
    fun provideBasicOkHttp(
        @Named(DEFAULT_OKHTTP_BUILDER) builder: OkHttpClient.Builder
    ): OkHttpClient =
        builder.build()

    @Provides
    @Singleton
    @Named(TUNED_OKHTTP)
    fun provideTunedOkHttp(
        @Named(TUNED_OKHTTP_BUILDER) builder: OkHttpClient.Builder
    ): OkHttpClient =
        builder.build()

    @Provides
    @Singleton
    @Named(AUTHENTICATED_OKHTTP)
    fun provideAuthenticatedOkHttp(
        @Named(TUNED_OKHTTP_BUILDER) builder: OkHttpClient.Builder,
        @Named(SharePreferenceModule.KVS_USER) userKeyValueStore: ObfuscatedKeyValueStoreInterface,
        context: Context,
        authTokenRepository: AuthenticationTokenRepository,
        deviceRepository: DeviceRepository,
    ): OkHttpClient =
        builder.addInterceptor(
            AuthenticatedRequestInterceptor(
                context,
                userKeyValueStore,
                authTokenRepository,
                deviceRepository
            )
        ).build()

    @Provides
    @Named(TUNED_OKHTTP_BUILDER)
    fun provideTunedOkHttpBuilder(
        context: Context,
        deviceRepository: DeviceRepository,
        config: Configuration,
        @Named(SharePreferenceModule.KVS_USER) userKeyValueStore: ObfuscatedKeyValueStoreInterface
    ): OkHttpClient.Builder {
        val builder = OkHttpClient().newBuilder()
            .addInterceptor(StoreIdInterceptor(config))
            .addInterceptor(CancelledRequestInterceptor())
            .addInterceptor(ConnectivityInterceptor(context, userKeyValueStore, deviceRepository))
            .addInterceptor(ContentLanguageInterceptor(userKeyValueStore))
            .addInterceptor(CountryInterceptor(userKeyValueStore))
            .addInterceptor(UserAgentInterceptor(context))
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(0, KEEP_ALIVE_DURATION, TimeUnit.MINUTES))
            .protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))

        debugOrNonProdInvoke {
            val loggingInterceptor = HttpLoggingInterceptor { message -> Timber.i(message) }
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
            builder.addNetworkInterceptor(StethoInterceptor())
        }

        return builder
    }

    @Provides
    @Named(DEFAULT_OKHTTP_BUILDER)
    fun provideBasicOkHttpBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient().newBuilder()
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(0, KEEP_ALIVE_DURATION, TimeUnit.MINUTES))
            .protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))

        debugOrNonProdInvoke {
            val loggingInterceptor = HttpLoggingInterceptor { message -> Timber.i(message) }
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
            builder.addNetworkInterceptor(StethoInterceptor())
        }

        return builder
    }

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson) = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideImageDownloadManager(
        context: Context,
        @Named(DEFAULT_OKHTTP) httpClient: OkHttpClient,
        config: Configuration,
        @Named(SharePreferenceModule.KVS_IMAGE_MANAGER) imageKeyValueStore: ObfuscatedKeyValueStoreInterface
    ): ImageManager =
        ImageManager(context, httpClient, config, imageKeyValueStore)
}
