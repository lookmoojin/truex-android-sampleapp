package com.tdg.truex_android_sampleapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tdg.truex_android_sampleapp.MainViewModel
import com.tdg.truex_android_sampleapp.ViewModelFactory
import com.tdg.truex_android_sampleapp.di.scops.ViewModelKey
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
