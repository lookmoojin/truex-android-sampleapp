package com.truedigital.share.data.firestoreconfig.injections

import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.data.firestoreconfig.di.FireStoreSecureRepositoryModule
import com.truedigital.share.data.firestoreconfig.di.FireStoreSecureUseCaseModule
import com.truedigital.share.data.firestoreconfig.di.FirestoreConfigBindsModule
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainCacheRepository
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainRepository
import com.truedigital.share.data.firestoreconfig.domainconfig.usecase.GetDomainServiceApiDataUseCase
import com.truedigital.share.data.firestoreconfig.domainconfig.usecase.GetFireBaseSecureUseCase
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.usecase.GetInitialAppConfigUsecase
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        FirestoreConfigBindsModule::class,
        FireStoreSecureRepositoryModule::class,
        FireStoreSecureUseCaseModule::class
    ],
    dependencies = [
        CoreSubComponent::class
    ]
)
interface FirestoreConfigComponent {

    companion object {
        private lateinit var firestoreConfigComponent: FirestoreConfigComponent

        fun initialize(firestoreConfigComponent: FirestoreConfigComponent) {
            this.firestoreConfigComponent = firestoreConfigComponent
        }

        fun getInstance(): FirestoreConfigComponent {
            if (!(::firestoreConfigComponent.isInitialized)) {
                error("FirestoreConfigComponent not initialize")
            }
            return firestoreConfigComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            coreSubComponent: CoreSubComponent
        ): FirestoreConfigComponent
    }

    fun getFirestoreConfigSubComponent(): FirestoreConfigSubComponent
}

@Subcomponent
interface FirestoreConfigSubComponent {
    fun getFirestoreUtil(): FirestoreUtil
    fun getInitialAppConfigRepository(): InitialAppConfigRepository
    fun getDomainRepository(): DomainRepository
    fun getDomainCacheRepository(): DomainCacheRepository

    fun getGetDomainServiceApiDataUseCase(): GetDomainServiceApiDataUseCase
    fun getGetInitialAppConfigUsecase(): GetInitialAppConfigUsecase
    fun getGetFireBaseSecureUseCase(): GetFireBaseSecureUseCase
}
