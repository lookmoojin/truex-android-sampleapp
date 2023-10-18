package com.truedigital.foundation.di

import androidx.multidex.MultiDexApplication
import com.truedigital.foundation.FoundationApplication
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface FoundationModule {

    @Binds
    @Singleton
    fun bindApplication(application: FoundationApplication): MultiDexApplication
}
