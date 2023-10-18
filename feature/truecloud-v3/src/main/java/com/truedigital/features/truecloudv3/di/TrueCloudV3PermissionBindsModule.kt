package com.truedigital.features.truecloudv3.di

import com.truedigital.features.truecloudv3.data.repository.PermissionDisableRepository
import com.truedigital.features.truecloudv3.data.repository.PermissionDisableRepositoryImpl
import com.truedigital.features.truecloudv3.domain.usecase.TrueCloudV3ConfigPermissionImageUseCase
import com.truedigital.features.truecloudv3.domain.usecase.TrueCloudV3ConfigPermissionImageUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface TrueCloudV3PermissionBindsModule {

    @Binds
    fun bindsPermissionDisableRepository(
        permissionDisableRepositoryImpl: PermissionDisableRepositoryImpl
    ): PermissionDisableRepository

    @Binds
    fun bindsTrueCloudV3ConfigPermissionImageUseCase(
        trueCloudV3ConfigPermissionImageUseCaseImpl: TrueCloudV3ConfigPermissionImageUseCaseImpl
    ): TrueCloudV3ConfigPermissionImageUseCase
}
