package com.truedigital.foundation.injections

import android.content.Context
import com.truedigital.foundation.di.FoundationModule
import com.truedigital.foundation.di.ViewModelModule
import com.truedigital.foundation.di.WorkerModule
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        FoundationModule::class,
        ViewModelModule::class,
        WorkerModule::class
    ]
)
interface FoundationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): FoundationComponent
    }
}
