package com.truedigital.navigation.di

import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface NavigationMainBindsModule {

    @Binds
    @Singleton
    fun bindsGetNavigationControllerUseCase(
        getNavigationControllerUseCaseImpl: GetNavigationControllerRepositoryImpl
    ): GetNavigationControllerRepository
}