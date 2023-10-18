package com.truedigital.features.apppermission.di

import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepository
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepositoryImpl
import com.truedigital.features.apppermission.usecase.GetPermissionAgreeButtonUseCase
import com.truedigital.features.apppermission.usecase.GetPermissionAgreeButtonUseCaseImpl
import com.truedigital.features.apppermission.usecase.GetPermissionDataUseCase
import com.truedigital.features.apppermission.usecase.GetPermissionDataUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface AppPerMissionModule {

    @Binds
    fun bindsAppPermissionGetConfigRepository(
        appPermissionGetConfigRepositoryImpl: AppPermissionGetConfigRepositoryImpl
    ): AppPermissionGetConfigRepository

    @Binds
    fun bindsGetPermissionAgreeButtonUseCase(
        getPermissionAgreeButtonUseCaseImpl: GetPermissionAgreeButtonUseCaseImpl
    ): GetPermissionAgreeButtonUseCase

    @Binds
    fun bindsGetPermissionDataUseCase(
        getPermissionDataUseCaseImpl: GetPermissionDataUseCaseImpl
    ): GetPermissionDataUseCase
}
