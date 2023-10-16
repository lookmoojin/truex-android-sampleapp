package com.truedigital.component.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truedigital.component.presentation.MainContainerViewModel
import com.truedigital.component.widget.livecommerce.presentation.viewmodel.LiveCommerceWidgetViewModel
import com.truedigital.foundation.di.scopes.ViewModelKey
import com.truedigital.foundation.presentations.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ComponentViewModelsModule {

    @Binds
    fun bindsViewModelFactory(
        viewModelFactory: ViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainContainerViewModel::class)
    fun bindsMainContainerViewModel(
        mainContainerViewModel: MainContainerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LiveCommerceWidgetViewModel::class)
    fun bindsLiveCommerceWidgetViewModel(
        liveCommerceWidgetViewModelImpl: LiveCommerceWidgetViewModel
    ): ViewModel
}
