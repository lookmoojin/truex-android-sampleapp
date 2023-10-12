package com.truedigital.features.truecloudv3.di

import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker
import dagger.Binds
import dagger.Module

@Module
interface TrueCloudV3MainProvidesModule {

    @Binds
    fun bindTrueCloudV3DownloadWorker(
        trueCloudV3DownloadWorker: TrueCloudV3DownloadWorker
    ): TrueCloudV3DownloadWorker
}
