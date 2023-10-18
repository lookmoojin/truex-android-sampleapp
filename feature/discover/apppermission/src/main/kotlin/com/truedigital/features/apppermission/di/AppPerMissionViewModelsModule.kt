package com.truedigital.features.apppermission.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truedigital.features.apppermission.presenation.AppPermissionGuideViewModel
import com.truedigital.foundation.di.scopes.ViewModelKey
import com.truedigital.foundation.presentations.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AppPerMissionViewModelsModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AppPermissionGuideViewModel::class)
    fun provideAppPermissionGuideViewModel(viewModel: AppPermissionGuideViewModel): ViewModel
}
