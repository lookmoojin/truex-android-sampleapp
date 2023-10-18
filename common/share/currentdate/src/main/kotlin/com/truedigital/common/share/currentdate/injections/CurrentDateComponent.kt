package com.truedigital.common.share.currentdate.injections

import com.truedigital.common.share.currentdate.DateTimeInterface
import com.truedigital.common.share.currentdate.di.DateTimeBindsModule
import com.truedigital.common.share.currentdate.di.DateTimeModule
import com.truedigital.common.share.currentdate.repository.DateTimeRepository
import com.truedigital.common.share.currentdate.usecase.ConvertTimeToMillisecondsUseCase
import com.truedigital.common.share.currentdate.usecase.ConvertToDateFormatUseCase
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.core.injections.CoreSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DateTimeModule::class,
        DateTimeBindsModule::class
    ],
    dependencies = [
        CoreSubComponent::class,
        DataLegacySubComponent::class
    ]
)
interface CurrentDateComponent {

    companion object {

        private lateinit var currentDateComponent: CurrentDateComponent

        fun initialize(currentDateComponent: CurrentDateComponent) {
            this.currentDateComponent = currentDateComponent
        }

        fun getInstance(): CurrentDateComponent {
            if (!(::currentDateComponent.isInitialized)) {
                error("CurrentDateComponent not initialize")
            }
            return currentDateComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            coreSubComponent: CoreSubComponent,
            dataLegacySubComponent: DataLegacySubComponent
        ): CurrentDateComponent
    }

    // Subcomponent
    fun getCurrentDateSubComponent(): CurrentDateSubComponent
}

@Subcomponent
interface CurrentDateSubComponent {
    // APIs
    fun getDateTimeInterface(): DateTimeInterface

    // Repositories
    fun getDateTimeRepository(): DateTimeRepository

    // Use cases
    fun getConvertTimeToMillisecondsUseCase(): ConvertTimeToMillisecondsUseCase
    fun getGetCurrentDateTimeUseCase(): GetCurrentDateTimeUseCase
    fun getConvertToDateFormatUseCase(): ConvertToDateFormatUseCase
}
