package com.truedigital.foundation.di

import androidx.work.WorkerFactory
import com.truedigital.foundation.worker.ListenableWorkerFactory
import dagger.Binds
import dagger.Module

@Module
interface WorkerModule {

    @Binds
    fun bindsWorkerFactory(
        workerFactoryImpl: ListenableWorkerFactory
    ): WorkerFactory
}
