package com.truedigital.common.share.datalegacy.injections

import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.truedigital.common.share.datalegacy.data.TvsNowCacheSourceRepository
import com.truedigital.common.share.datalegacy.data.api.di.BaseHttpClientModule
import com.truedigital.common.share.datalegacy.data.api.di.CcuApiModule
import com.truedigital.common.share.datalegacy.data.api.di.CmsFnApiModule
import com.truedigital.common.share.datalegacy.data.api.di.DefaultOkHttp
import com.truedigital.common.share.datalegacy.data.api.di.GraphApiModule
import com.truedigital.common.share.datalegacy.data.api.di.IceApiModule
import com.truedigital.common.share.datalegacy.data.api.di.InterceptorModule
import com.truedigital.common.share.datalegacy.data.api.di.Json
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV1OkHttp
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV2OkHttp
import com.truedigital.common.share.datalegacy.data.api.di.JsonWithNoHeaderOkHttp
import com.truedigital.common.share.datalegacy.data.api.di.JsonWithNoRetryInterceptorOkHttp
import com.truedigital.common.share.datalegacy.data.api.di.MultipartFeaturePathV1OkHttp
import com.truedigital.common.share.datalegacy.data.api.di.TokenInterceptor
import com.truedigital.common.share.datalegacy.data.api.di.WithNoRetryInterceptor
import com.truedigital.common.share.datalegacy.data.api.graph.GraphApiInterface
import com.truedigital.common.share.datalegacy.data.api.interceptor.ChuckerLoggerInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.ContentTypeInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.FeaturePathInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.FeaturePathV2Interceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.HeaderInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.HeaderWrapperInterceptor
import com.truedigital.common.share.datalegacy.data.api.interceptor.SevenTokenInterceptor
import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarRepository
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.repository.CmsShelfRepository
import com.truedigital.common.share.datalegacy.data.repository.login.StateUserLoginRepository
import com.truedigital.common.share.datalegacy.data.repository.multimedia.ConcurrentUserRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.di.feature.AdsShareModule
import com.truedigital.common.share.datalegacy.di.feature.ArticleShareModule
import com.truedigital.common.share.datalegacy.di.feature.AuthWrapperShareModule
import com.truedigital.common.share.datalegacy.di.feature.AvatarModule
import com.truedigital.common.share.datalegacy.di.feature.ConcurrentUserModule
import com.truedigital.common.share.datalegacy.di.feature.ContextModule
import com.truedigital.common.share.datalegacy.di.feature.DataCommonModule
import com.truedigital.common.share.datalegacy.di.feature.DeviceEntitlementModule
import com.truedigital.common.share.datalegacy.di.feature.DeviceModule
import com.truedigital.common.share.datalegacy.di.feature.FirebaseModule
import com.truedigital.common.share.datalegacy.di.feature.LocationModule
import com.truedigital.common.share.datalegacy.di.feature.LoginBindsModule
import com.truedigital.common.share.datalegacy.di.feature.LoginModule
import com.truedigital.common.share.datalegacy.di.feature.OtherModule
import com.truedigital.common.share.datalegacy.di.feature.ProfileSettingModule
import com.truedigital.common.share.datalegacy.di.feature.ProfileShareModule
import com.truedigital.common.share.datalegacy.di.feature.VerifyDeviceModule
import com.truedigital.common.share.datalegacy.domain.GetCurrentSubProfileIdUseCase
import com.truedigital.common.share.datalegacy.domain.ads.usecase.AdvertisingIdUseCase
import com.truedigital.common.share.datalegacy.domain.avatar.usecase.GetAvatarUrlUseCase
import com.truedigital.common.share.datalegacy.domain.avatar.usecase.GetAvatarUrlUserLastedUseCase
import com.truedigital.common.share.datalegacy.domain.config.usecase.GetAppConfigUseCase
import com.truedigital.common.share.datalegacy.domain.endpoint.usecase.GetApiConfigurationUseCase
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.AddDeviceEntitlementUseCase
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.ContainDeviceIdInDeviceListUseCase
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.GetActiveDeviceEntitlementUseCase
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.RemoveActiveDeviceEntitlementUseCase
import com.truedigital.common.share.datalegacy.domain.login.usecase.GetLoginUrlUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.AddKeySharedPrefsUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.EncryptLocationUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoWrapperUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.RemoveKeySharedPrefsUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.WifiInfoUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.ClearProfileCacheUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetCurrentSubProfileUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetNonCachedProfileUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetOtherProfileUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetProfileSettingsUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetProfileUseCase
import com.truedigital.common.share.datalegacy.domain.verifydevice.usecase.VerifyTrustDeviceOwnerUseCase
import com.truedigital.common.share.datalegacy.domain.webview.usecase.GetSystemWebViewMinimumVersionUseCase
import com.truedigital.common.share.datalegacy.helpers.FirebaseAnalyticsHelper
import com.truedigital.common.share.datalegacy.js.TrueIDJavaScriptHandler
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.utils.ArticleDetailStateUtil
import com.truedigital.common.share.datalegacy.utils.FirebaseUtilInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.domain.usecase.IsBypassSSLUseCase
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import dagger.Component
import dagger.Subcomponent
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AuthWrapperShareModule::class,
        BaseHttpClientModule::class,
        CcuApiModule::class,
        CmsFnApiModule::class,
        GraphApiModule::class,
        IceApiModule::class,
        AdsShareModule::class,
        ArticleShareModule::class,
        AvatarModule::class,
        ConcurrentUserModule::class,
        ContextModule::class,
        DataCommonModule::class,
        DeviceEntitlementModule::class,
        DeviceModule::class,
        FirebaseModule::class,
        InterceptorModule::class,
        LocationModule::class,
        LoginModule::class,
        LoginBindsModule::class,
        ProfileShareModule::class,
        ProfileSettingModule::class,
        VerifyDeviceModule::class,
        OtherModule::class
    ],
    dependencies = [
        CoreSubComponent::class,
        FirestoreConfigSubComponent::class
    ]
)
interface DataLegacyComponent {

    companion object {
        private lateinit var dataLegacyComponent: DataLegacyComponent

        fun initialize(dataLegacyComponent: DataLegacyComponent) {
            this.dataLegacyComponent = dataLegacyComponent
        }

        fun getInstance(): DataLegacyComponent {
            if (!(::dataLegacyComponent.isInitialized)) {
                error("DataLegacyComponent not initialize")
            }
            return dataLegacyComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            coreSubComponent: CoreSubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent
        ): DataLegacyComponent
    }

    fun getDataLegacySubComponent(): DataLegacySubComponent
    fun inject(trueIDJavaScriptHandler: TrueIDJavaScriptHandler)
}

@Subcomponent
interface DataLegacySubComponent {

    @JsonFeaturePathV1OkHttp
    fun getJsonFeatureOkHttp(): OkHttpClient

    @JsonFeaturePathV2OkHttp
    fun getJsonFeature2OkHttp(): OkHttpClient

    @JsonWithNoRetryInterceptorOkHttp
    fun getJsonFeature2WithNoRetryOkHttp(): OkHttpClient

    @MultipartFeaturePathV1OkHttp
    fun getMultipartFeatureOkHttpClient(): OkHttpClient

    @JsonWithNoHeaderOkHttp
    fun getJsonOKHttp(): OkHttpClient

    @DefaultOkHttp
    fun getDefaultOkHttp(): OkHttpClient

    // Content type
    @Json
    fun getJsonContentTypeInterceptor(): ContentTypeInterceptor

    // Interceptors
    fun getChuckerLoggerInterceptor(): ChuckerLoggerInterceptor
    fun getSevenTokenInterceptor(): SevenTokenInterceptor
    fun getHeaderWrapperInterceptor(): HeaderWrapperInterceptor
    fun getFeaturePathV2Interceptor(): FeaturePathV2Interceptor
    fun getFeaturePathInterceptor(): FeaturePathInterceptor
    fun getHeaderInterceptor(): HeaderInterceptor

    @TokenInterceptor
    fun getRequestTokenInterceptor(): Interceptor

    @WithNoRetryInterceptor
    fun getNoRetryInterceptor(): Interceptor

    // Firebase
    fun getFirebaseUtilInterface(): FirebaseUtilInterface
    fun getFirebaseRemoteConfig(): FirebaseRemoteConfig

    // fun getIceApiInterface(): IceApiInterface
    fun getGraphApiInterface(): GraphApiInterface
    fun getLoginManagerInterface(): LoginManagerInterface

    // Utilities
    fun getAuthManagerWrapper(): AuthManagerWrapper
    fun getArticleDetailStateUtil(): ArticleDetailStateUtil
    fun getContextDataProviderWrapper(): ContextDataProviderWrapper
    fun getTelephonyManager(): TelephonyManager
    fun getWifiManager(): WifiManager
    fun getFirebaseAnalyticsHelper(): FirebaseAnalyticsHelper
    fun getApiConfigurationManager(): ApiConfigurationManager

    // Use cases
    fun getAdvertisingIdUseCase(): AdvertisingIdUseCase
    fun getGetAvatarUrlUseCase(): GetAvatarUrlUseCase
    fun getGetAvatarUrlUserLastedUseCase(): GetAvatarUrlUserLastedUseCase
    fun getGetAppConfigUseCase(): GetAppConfigUseCase
    fun getContainDeviceIdInDeviceListUseCase(): ContainDeviceIdInDeviceListUseCase
    fun getNetworkInfoUseCase(): NetworkInfoUseCase
    fun getNetworkInfoWrapperUseCase(): NetworkInfoWrapperUseCase
    fun getWifiInfoUseCase(): WifiInfoUseCase
    fun getEncryptLocationUseCase(): EncryptLocationUseCase
    fun getClearProfileCacheUseCase(): ClearProfileCacheUseCase
    fun getGetProfileUseCase(): GetProfileUseCase
    fun getGetNonCachedProfileUseCase(): GetNonCachedProfileUseCase
    fun getGetOtherProfileUseCase(): GetOtherProfileUseCase
    fun getGetProfileSettingsUseCase(): GetProfileSettingsUseCase
    fun getVerifyTrustDeviceOwnerUseCase(): VerifyTrustDeviceOwnerUseCase
    fun getGetSystemWebViewMinimumVersionUseCase(): GetSystemWebViewMinimumVersionUseCase
    fun getGetApiConfigurationUseCase(): GetApiConfigurationUseCase
    fun getAddKeySharedPrefsUseCase(): AddKeySharedPrefsUseCase
    fun getRemoveKeySharedPrefsUseCase(): RemoveKeySharedPrefsUseCase
    fun getGetLoginUrlUseCase(): GetLoginUrlUseCase
    fun getGetCurrentSubProfileUseCase(): GetCurrentSubProfileUseCase
    fun getGetCurrentSubProfileIdUseCase(): GetCurrentSubProfileIdUseCase
    fun getGetActiveDeviceEntitlementUseCase(): GetActiveDeviceEntitlementUseCase
    fun getRemoveActiveDeviceEntitlementUseCase(): RemoveActiveDeviceEntitlementUseCase
    fun getAddDeviceEntitlementUseCase(): AddDeviceEntitlementUseCase

    // Repositories
    fun getAvatarRepository(): AvatarRepository
    fun getConcurrentUserRepository(): ConcurrentUserRepository
    fun getCmsShelfRepository(): CmsShelfRepository
    fun getUserRepository(): UserRepository
    fun getProfileRepository(): ProfileRepository
    fun getTvsNowCacheSourceRepository(): TvsNowCacheSourceRepository
    fun getStateUserLoginRepository(): StateUserLoginRepository
    fun isBypassSSLUseCase(): IsBypassSSLUseCase
    fun provideCertificatePinner(): CertificatePinner
}
