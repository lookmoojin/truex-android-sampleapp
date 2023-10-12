package com.truedigital.core.di

import com.truedigital.core.data.repository.BuildVariantRepository
import com.truedigital.core.data.repository.BuildVariantRepositoryImpl
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.core.data.repository.DeviceRepositoryImpl
import com.truedigital.core.domain.usecase.GetAnimalUseCase
import com.truedigital.core.domain.usecase.GetAnimalUseCaseImpl
import com.truedigital.core.domain.usecase.GetCountViewFormatUseCase
import com.truedigital.core.domain.usecase.GetCountViewFormatUseCaseImpl
import com.truedigital.core.domain.usecase.GetPinnedDomainsUseCase
import com.truedigital.core.domain.usecase.GetPinnedDomainsUseCaseImpl
import com.truedigital.core.domain.usecase.GetRocketUseCase
import com.truedigital.core.domain.usecase.GetRocketUseCaseImpl
import com.truedigital.core.domain.usecase.IsBypassSSLUseCase
import com.truedigital.core.domain.usecase.IsBypassSSLUseCaseImpl
import com.truedigital.core.domain.usecase.MapPinnedDomainsUseCase
import com.truedigital.core.domain.usecase.MapPinnedDomainsUseCaseImpl
import com.truedigital.core.manager.ApplicationPackageManager
import com.truedigital.core.manager.ApplicationPackageManagerImpl
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.provider.ContextDataProviderImp
import com.truedigital.core.utils.DataStoreInterface
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.core.utils.EncryptUtil
import com.truedigital.core.utils.EncryptUtilImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface CommonCoreBindsModule {

    @Binds
    fun bindsApplicationPackageManager(
        applicationPackageManagerImpl: ApplicationPackageManagerImpl
    ): ApplicationPackageManager

    @Binds
    fun bindsDeviceRepository(
        deviceRepositoryImpl: DeviceRepositoryImpl
    ): DeviceRepository

    @Binds
    fun bindsBuildVariantRepository(
        buildVariantRepositoryImpl: BuildVariantRepositoryImpl
    ): BuildVariantRepository

    @Binds
    fun bindsGetCountViewFormatUseCase(
        getCountViewFormatUseCaseImpl: GetCountViewFormatUseCaseImpl
    ): GetCountViewFormatUseCase

    @Binds
    fun bindsEncryptUtil(
        encryptUtilImpl: EncryptUtilImpl
    ): EncryptUtil

    @Binds
    @Singleton
    fun bindsContextDataProvider(
        contextDataProviderImpl: ContextDataProviderImp
    ): ContextDataProvider

    @Binds
    @Singleton
    fun bindsDataStoreUtil(
        dataStoreUtil: DataStoreUtil
    ): DataStoreInterface

    @Binds
    @Singleton
    fun bindsGetAnimalUseCase(
        getAnimalUseCaseImpl: GetAnimalUseCaseImpl
    ): GetAnimalUseCase

    @Binds
    @Singleton
    fun bindsGetRocketUseCase(
        getRocketUseCaseImpl: GetRocketUseCaseImpl
    ): GetRocketUseCase

    @Binds
    @Singleton
    fun bindIsBapassSSLUseCase(
        isBypassSSLUseCaseImp: IsBypassSSLUseCaseImpl
    ): IsBypassSSLUseCase

    @Binds
    @Singleton
    fun bindsGetPinnedDomainsUseCase(
        getPinnedDomainsUseCaseImpl: GetPinnedDomainsUseCaseImpl
    ): GetPinnedDomainsUseCase

    @Binds
    @Singleton
    fun bindsMapPinnedDomainsUseCase(
        mapPinnedDomainsUseCaseImpl: MapPinnedDomainsUseCaseImpl
    ): MapPinnedDomainsUseCase
}
