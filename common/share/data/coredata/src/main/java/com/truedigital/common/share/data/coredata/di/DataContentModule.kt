package com.truedigital.common.share.data.coredata.di

import com.appsflyer.AppsFlyerLib
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.truedigital.common.share.data.coredata.data.api.CmsContentApiInterface
import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeShortLinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeShortLinkUseCaseImpl
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDynamicUrlUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsPrivilegeUrlUseCase
import com.truedigital.common.share.datalegacy.data.api.ApiScalarsAndGsonBuilder
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
object DataContentModule {

    @Provides
    @Singleton
    fun provideCmsContentApiInterface(
        @JsonFeaturePathV2OkHttp okHttpClient: OkHttpClient,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): CmsContentApiInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient = okHttpClient,
            scalarsConverterFactory = scalarsConverterFactory,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_CONTENT)
    }

    @Provides
    @Singleton
    fun provideDecodeShortLinkUseCase(
        isDynamicUrlUseCase: IsDynamicUrlUseCase,
        isPrivilegeUrlUseCase: IsPrivilegeUrlUseCase
    ): DecodeShortLinkUseCase {
        return DecodeShortLinkUseCaseImpl(
            FirebaseDynamicLinks.getInstance(),
            AppsFlyerLib.getInstance(),
            isDynamicUrlUseCase,
            isPrivilegeUrlUseCase
        )
    }
}
