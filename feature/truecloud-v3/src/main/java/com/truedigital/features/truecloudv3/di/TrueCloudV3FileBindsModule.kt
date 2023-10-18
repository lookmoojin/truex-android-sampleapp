package com.truedigital.features.truecloudv3.di

import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListWithCategoryUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListWithCategoryUseCaseImpl
import com.truedigital.features.truecloudv3.provider.ExifProvider
import com.truedigital.features.truecloudv3.provider.ExifProviderImpl
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.features.truecloudv3.provider.FileProviderImpl
import dagger.Binds
import dagger.Module

@Module
interface TrueCloudV3FileBindsModule {

    @Binds
    fun bindsGetStorageListUseCase(
        getStorageListUseCaseImpl: GetStorageListUseCaseImpl
    ): GetStorageListUseCase

    @Binds
    fun bindsGetStorageListWithCategoryUseCase(
        getStorageListWithCategoryUseCaseImpl: GetStorageListWithCategoryUseCaseImpl
    ): GetStorageListWithCategoryUseCase

    @Binds
    fun bindsFileProvider(
        fileProviderImpl: FileProviderImpl
    ): FileProvider

    @Binds
    fun bindsExifProvider(
        exifProviderImpl: ExifProviderImpl
    ): ExifProvider
}
