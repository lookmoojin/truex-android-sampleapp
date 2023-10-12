package com.truedigital.foundation.di

import androidx.lifecycle.ViewModelProvider
import com.truedigital.foundation.presentations.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
