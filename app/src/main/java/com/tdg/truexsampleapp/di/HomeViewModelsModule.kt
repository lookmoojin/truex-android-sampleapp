package com.tdg.truexsampleapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tdg.truexsampleapp.MainViewModel
import com.tdg.truexsampleapp.ViewModelFactory
import com.tdg.truexsampleapp.di.scops.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface HomeViewModelsModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindsMainViewModel(
        mainViewModel: MainViewModel,
    ): ViewModel

}
