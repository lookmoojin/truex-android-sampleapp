package com.truedigital.navigation.di

import com.truedigital.navigation.data.repository.CountryRepository
import com.truedigital.navigation.data.repository.CountryRepositoryImpl
import com.truedigital.navigation.data.repository.GetCacheInterContentRepository
import com.truedigital.navigation.data.repository.GetCacheInterContentRepositoryImpl
import com.truedigital.navigation.data.repository.GetInterContentRepository
import com.truedigital.navigation.data.repository.GetInterContentRepositoryImpl
import com.truedigital.navigation.domain.usecase.GetPersonaConfigUseCase
import com.truedigital.navigation.domain.usecase.GetPersonaConfigUseCaseImpl
import com.truedigital.navigation.domain.usecase.GetTodayPersonaSegmentEnableUseCase
import com.truedigital.navigation.domain.usecase.GetTodayPersonaSegmentEnableUseCaseImpl
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface NavigationMainBindsModule {

    @Binds
    @Singleton
    fun bindsCountryRepository(
        countryRepositoryImpl: CountryRepositoryImpl
    ): CountryRepository

    @Binds
    @Singleton
    fun bindsGetInterContentRepository(
        getInterContentRepositoryImpl: GetInterContentRepositoryImpl
    ): GetInterContentRepository

    @Binds
    @Singleton
    fun bindsGetCacheInterContentRepository(
        getCacheInterContentRepositoryImpl: GetCacheInterContentRepositoryImpl
    ): GetCacheInterContentRepository

    @Binds
    @Singleton
    fun bindsGetNavigationControllerUseCase(
        getNavigationControllerUseCaseImpl: GetNavigationControllerRepositoryImpl
    ): GetNavigationControllerRepository

    @Binds
    fun bindsGetTodayPersonaSegmentEnableUseCase(
        getTodayPersonaSegmentEnableUseCaseImpl: GetTodayPersonaSegmentEnableUseCaseImpl
    ): GetTodayPersonaSegmentEnableUseCase

    @Binds
    fun bindsGetPersonaConfigUseCase(
        getPersonaConfigUseCaseImpl: GetPersonaConfigUseCaseImpl
    ): GetPersonaConfigUseCase
}
