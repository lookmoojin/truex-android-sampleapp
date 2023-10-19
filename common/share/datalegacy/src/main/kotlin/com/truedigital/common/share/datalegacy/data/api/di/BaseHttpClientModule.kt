package com.truedigital.common.share.datalegacy.data.api.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.CacheInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.ChuckerLoggerInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.ContentTypeInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.DNSFailOverInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.FeaturePathInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.FeaturePathV2Interceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.HeaderWrapperInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.NoRetryInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.provideOkHttpCache
import com.truedigital.core.BuildConfig
import com.truedigital.core.constant.AppConfig
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.domain.usecase.IsBypassSSLUseCase
import com.truedigital.core.domain.usecase.MapPinnedDomainsUseCase
import dagger.Module
import dagger.Provides
import okhttp3.CertificatePinner
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class BaseHttpClientModule {

    companion object {
        private const val MAX_IDLE_CONNECTIONS = 5
        private const val KEEP_ALIVE_DURATION = 5L
    }

    @Provides
    @Singleton
    @Json
    fun providesContentTypeJson(): ContentTypeInterceptor {
        return ContentTypeInterceptor(ContentTypeInterceptor.CONTENT_TYPE_JSON)
    }

    @Provides
    @Singleton
    @WithNoRetryInterceptor
    fun providesNoRetryInterceptor(
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): Interceptor {
        return NoRetryInterceptor(
            ContentTypeInterceptor.CONTENT_TYPE_JSON, coroutineDispatcherProvider
        )
    }

    @Provides
    @Singleton
    @Multipart
    fun providesContentTypeMultipart(): ContentTypeInterceptor {
        return ContentTypeInterceptor(ContentTypeInterceptor.CONTENT_TYPE_MULTIPART_FORM_DATA)
    }

    @Provides
    @Singleton
    @None
    fun providesContentTypeNone(): ContentTypeInterceptor {
        return ContentTypeInterceptor(ContentTypeInterceptor.CONTENT_TYPE_NON)
    }

    @Provides
    @Singleton
    fun provideCertificatePinner(
        mapPinnedDomainsUseCase: MapPinnedDomainsUseCase
    ): CertificatePinner {
        val mapPinnedDomains = mapPinnedDomainsUseCase.execute()
        val certificatePinnerBuilder = CertificatePinner.Builder()
        for ((pin, hosts) in mapPinnedDomains) {
            for (host in hosts) {
                certificatePinnerBuilder.add(host, pin)
            }
        }
        return certificatePinnerBuilder.build()
    }

    @Provides
    @Singleton
    @JsonFeaturePathV1OkHttp
    fun providesFeatureJsonOkHttp(
        @HeaderOkHttp okHttpClient: OkHttpClient,
        @Json contentTypeInterceptor: ContentTypeInterceptor,
        isBypassSSLUseCase: IsBypassSSLUseCase,
        featurePathInterceptor: FeaturePathInterceptor,
        provideCertificatePinner: CertificatePinner
    ): OkHttpClient {
        return okHttpClient.newBuilder().addInterceptor(contentTypeInterceptor)
            .addInterceptor(featurePathInterceptor)
            .apply {
                if (!isBypassSSLUseCase.execute(BuildConfig.DEBUG)) {
                    this.certificatePinner(provideCertificatePinner)
                }
            }
            .build()
    }

    @Provides
    @Singleton
    @JsonFeaturePathV2OkHttp
    fun providesFeature2JsonOkHttp(
        @HeaderOkHttp okHttpClient: OkHttpClient,
        @Json contentTypeInterceptor: ContentTypeInterceptor,
        featurePathInterceptor: FeaturePathV2Interceptor,
        isBypassSSLUseCase: IsBypassSSLUseCase,
        provideCertificatePinner: CertificatePinner,
    ): OkHttpClient {
        return okHttpClient.newBuilder().addInterceptor(contentTypeInterceptor)
            .addInterceptor(featurePathInterceptor)
            .apply {
                if (!isBypassSSLUseCase.execute(BuildConfig.DEBUG)) {
                    this.certificatePinner(provideCertificatePinner)
                }
            }.build()
    }

    @Provides
    @Singleton
    @JsonWithNoRetryInterceptorOkHttp
    fun providesFeature2JsonWithNoRetryOkHttp(
        @HeaderOkHttp okHttpClient: OkHttpClient,
        @WithNoRetryInterceptor noRetryInterceptor: Interceptor,
        featurePathInterceptor: FeaturePathV2Interceptor,
        isBypassSSLUseCase: IsBypassSSLUseCase,
        provideCertificatePinner: CertificatePinner,
    ): OkHttpClient {
        return okHttpClient.newBuilder().addInterceptor(noRetryInterceptor)
            .addInterceptor(featurePathInterceptor)
            .apply {
                if (!isBypassSSLUseCase.execute(BuildConfig.DEBUG)) {
                    this.certificatePinner(provideCertificatePinner)
                }
            }.build()
    }

    @Provides
    @Singleton
    @MultipartFeaturePathV2OkHttp
    fun providesFeature2MultipartOkHttp(
        @HeaderOkHttp okHttpClient: OkHttpClient,
        @Multipart contentTypeInterceptor: ContentTypeInterceptor,
        featurePathInterceptor: FeaturePathV2Interceptor,
        isBypassSSLUseCase: IsBypassSSLUseCase,
        provideCertificatePinner: CertificatePinner
    ): OkHttpClient {
        return okHttpClient.newBuilder().addInterceptor(contentTypeInterceptor)
            .addInterceptor(featurePathInterceptor)
            .apply {
                if (!isBypassSSLUseCase.execute(BuildConfig.DEBUG)) {
                    this.certificatePinner(provideCertificatePinner)
                }
            }.build()
    }

    @Provides
    @Singleton
    @HeaderOkHttp
    fun providesHeaderOkHttpClient(
        @LoggingOkHttp okHttpClient: OkHttpClient,
        headerWrapperInterceptor: HeaderWrapperInterceptor,
        isBypassSSLUseCase: IsBypassSSLUseCase,
        provideCertificatePinner: CertificatePinner,
    ): OkHttpClient {
        return okHttpClient.newBuilder().addInterceptor(headerWrapperInterceptor)
            .cache(provideOkHttpCache())
            .apply {
                if (!isBypassSSLUseCase.execute(BuildConfig.DEBUG)) {
                    this.certificatePinner(provideCertificatePinner)
                }
            }.build()
    }

    @Provides
    @Singleton
    @JsonWithNoHeaderOkHttp
    fun providesJsonOkHttpClient(
        @LoggingOkHttp okHttpClient: OkHttpClient,
        @Json contentTypeInterceptor: ContentTypeInterceptor,
        isBypassSSLUseCase: IsBypassSSLUseCase,
        provideCertificatePinner: CertificatePinner,
    ): OkHttpClient {
        return okHttpClient.newBuilder().addInterceptor(contentTypeInterceptor)
            .cache(provideOkHttpCache())
            .apply {
                if (!isBypassSSLUseCase.execute(BuildConfig.DEBUG)) {
                    this.certificatePinner(provideCertificatePinner)
                }
            }.build()
    }

    @Provides
    @Singleton
    @LoggingOkHttp
    fun providesLoggingOkHttpClient(
        @DefaultOkHttp okHttpClient: OkHttpClient,
        isBypassSSLUseCase: IsBypassSSLUseCase,
        provideCertificatePinner: CertificatePinner,
    ): OkHttpClient {
        return okHttpClient.newBuilder().apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
            if (!isBypassSSLUseCase.execute(BuildConfig.DEBUG)) {
                certificatePinner(provideCertificatePinner)
            }
        }.build()
    }

    @Singleton
    @Provides
    @DefaultOkHttp
    fun provideDefaultOkHttpClient(
        chuckerLoggerInterceptor: ChuckerLoggerInterceptor,
        isBypassSSLUseCase: IsBypassSSLUseCase,
        provideCertificatePinner: CertificatePinner,
    ): OkHttpClient {
        val connectionPool =
            ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.MINUTES)
        return OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor()).addNetworkInterceptor(CacheInterceptor())
            .addInterceptor(chuckerLoggerInterceptor.chuckerInterceptor)
            .connectionPool(connectionPool).protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
            .addInterceptor(DNSFailOverInterceptor(connectionPool))
            .readTimeout(AppConfig.REST_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(AppConfig.REST_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            .connectTimeout(AppConfig.REST_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            .apply {
                if (!isBypassSSLUseCase.execute(BuildConfig.DEBUG)) {
                    this.certificatePinner(provideCertificatePinner)
                }
            }
            .build()
    }
}
