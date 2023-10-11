package com.truedigital.features.truecloudv3.di

import com.truedigital.features.truecloudv3.data.repository.MigrateDataRepository
import com.truedigital.features.truecloudv3.data.repository.MigrateDataRepositoryImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetMigrateFailDisplayTimeUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetMigrateFailDisplayTimeUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.MigrateDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.MigrateDataUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.PatchMigrateStatusUseCase
import com.truedigital.features.truecloudv3.domain.usecase.PatchMigrateStatusUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.SetMigrateFailDisplayTimeUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SetMigrateFailDisplayTimeUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface TrueCloudV3IntroMigrateBindsModule {

    @Binds
    fun bindsMigrateDataRepository(
        migrateDataRepositoryImpl: MigrateDataRepositoryImpl
    ): MigrateDataRepository

    @Binds
    fun bindsPatchMigrateStatusUseCase(
        patchMigrateStatusUseCaseImpl: PatchMigrateStatusUseCaseImpl
    ): PatchMigrateStatusUseCase

    @Binds
    fun bindsGetMigrateFailDisplayTimeUseCase(
        getMigrateFailDisplayTimeUseCaseImpl: GetMigrateFailDisplayTimeUseCaseImpl
    ): GetMigrateFailDisplayTimeUseCase

    @Binds
    fun bindsSetMigrateFailDisplayTimeUseCase(
        setMigrateFailDisplayTimeUseCaseImpl: SetMigrateFailDisplayTimeUseCaseImpl
    ): SetMigrateFailDisplayTimeUseCase

    @Binds
    fun bindsMigrateDataUseCase(
        migrateDataUseCaseImpl: MigrateDataUseCaseImpl
    ): MigrateDataUseCase
}
