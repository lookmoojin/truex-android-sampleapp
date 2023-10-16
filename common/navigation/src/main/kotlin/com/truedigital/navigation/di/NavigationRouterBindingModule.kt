package com.truedigital.navigation.di

import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.data.repository.router.NavigationRouterRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface NavigationRouterBindingModule {

    @Binds
    @Singleton
    fun bindNavigationRouterRepository(
        navigationRouterRepositoryImpl: NavigationRouterRepositoryImpl
    ): NavigationRouterRepository
}
