package com.truedigital.core.injections

import android.content.Context
import androidx.work.WorkManager
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.NetworkModule
import com.truedigital.core.api.di.RxErrorHandlingAdapter
import com.truedigital.core.api.di.RxJava2Adapter
import com.truedigital.core.api.di.ScalarsConverter
import com.truedigital.core.api.di.XmlConverter
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.repository.BuildVariantRepository
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.core.di.CommonCoreBindsModule
import com.truedigital.core.di.CommonCoreModule
import com.truedigital.core.di.LocalizationModule
import com.truedigital.core.domain.usecase.GetAnimalUseCase
import com.truedigital.core.domain.usecase.GetCountViewFormatUseCase
import com.truedigital.core.domain.usecase.GetLocalizationUseCase
import com.truedigital.core.domain.usecase.GetPinnedDomainsUseCase
import com.truedigital.core.domain.usecase.GetRocketUseCase
import com.truedigital.core.domain.usecase.IsBypassSSLUseCase
import com.truedigital.core.domain.usecase.MapPinnedDomainsUseCase
import com.truedigital.core.domain.usecase.SetLocalizationUseCase
import com.truedigital.core.manager.ApplicationPackageManager
import com.truedigital.core.manager.location.LocationManager
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.utils.DataStoreInterface
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.core.utils.EncryptUtil
import com.truedigital.core.utils.GsonUtil
import com.truedigital.core.utils.SharedPrefsInterface
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.foundation.injections.FoundationComponent
import dagger.Component
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.CallAdapter
import retrofit2.Converter
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CommonCoreModule::class,
        CommonCoreBindsModule::class,
        LocalizationModule::class,
        NetworkModule::class
    ],
    dependencies = [
        FoundationComponent::class,
    ]
)
interface CoreComponent {

    companion object {
        private lateinit var coreComponent: CoreComponent

        fun initialize(coreComponent: CoreComponent) {
            this.coreComponent = coreComponent
        }

        fun getInstance(): CoreComponent {
            if (!(::coreComponent.isInitialized)) {
                error("CoreComponent not initialize")
            }
            return coreComponent
        }
    }

    // Sub component
    fun getCoreSubComponent(): CoreSubComponent
}

@Subcomponent
interface CoreSubComponent {
    @RxJava2Adapter
    fun getRxJava2CallAdapterFactory(): CallAdapter.Factory

    @ErrorHandlingAdapter
    fun getErrorHandlingCallAdapterFactory(): CallAdapter.Factory

    @RxErrorHandlingAdapter
    fun getRxErrorHandlingCallAdapterFactory(): CallAdapter.Factory

    @ScalarsConverter
    fun getScalarsConverterFactory(): Converter.Factory

    @GsonConverter
    fun getGsonConverterFactory(): Converter.Factory

    @XmlConverter
    fun getSimpleXmlConverterFactory(): Converter.Factory

    // Utilities
    fun getContext(): Context
    fun getWorkManager(): WorkManager
    fun getDispatchersIO(): CoroutineDispatcher
    fun getSharedPrefsUtils(): SharedPrefsUtils
    fun getDataStoreUtil(): DataStoreUtil
    fun getSharedPrefsInterface(): SharedPrefsInterface
    fun getApplicationPackageManager(): ApplicationPackageManager
    fun getLocationManager(): LocationManager
    fun getEncryptUtil(): EncryptUtil
    fun getCoroutineDispatcherProvider(): CoroutineDispatcherProvider
    fun getContextDataProvider(): ContextDataProvider
    fun getDataStoreInterface(): DataStoreInterface
    fun getGsonUtil(): GsonUtil

    // Repositories
    fun getDeviceRepository(): DeviceRepository
    fun getBuildVariantRepository(): BuildVariantRepository
    fun getLocalizationRepository(): LocalizationRepository

    // Usecases
    fun getGetLocalizationUseCase(): GetLocalizationUseCase
    fun getSetLocalizationUseCase(): SetLocalizationUseCase
    fun getGetCountViewFormatUseCase(): GetCountViewFormatUseCase
    fun getAnimalUseCase(): GetAnimalUseCase
    fun getRocketUseCase(): GetRocketUseCase
    fun isBypassSSLUseCase(): IsBypassSSLUseCase
    fun getPinnedDomainsUseCase(): GetPinnedDomainsUseCase
    fun mapPinnedDomainsUseCase(): MapPinnedDomainsUseCase
}
