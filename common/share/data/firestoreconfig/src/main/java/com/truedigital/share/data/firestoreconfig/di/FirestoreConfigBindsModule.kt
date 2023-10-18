package com.truedigital.share.data.firestoreconfig.di

import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.data.firestoreconfig.FirestoreUtilImpl
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainCacheRepository
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainCacheRepositoryImpl
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainRepository
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainRepositoryImpl
import com.truedigital.share.data.firestoreconfig.domainconfig.usecase.GetDomainServiceApiDataUseCase
import com.truedigital.share.data.firestoreconfig.domainconfig.usecase.GetDomainServiceApiDataUseCaseImpl
import com.truedigital.share.data.firestoreconfig.featureconfig.repository.FeatureConfigRepository
import com.truedigital.share.data.firestoreconfig.featureconfig.repository.FeatureConfigRepositoryImpl
import com.truedigital.share.data.firestoreconfig.featureconfig.usecase.GetFeatureConfigUsecase
import com.truedigital.share.data.firestoreconfig.featureconfig.usecase.GetFeatureConfigUsecaseImpl
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepositoryImpl
import com.truedigital.share.data.firestoreconfig.initialappconfig.usecase.GetInitialAppConfigUsecase
import com.truedigital.share.data.firestoreconfig.initialappconfig.usecase.GetInitialAppConfigUsecaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface FirestoreConfigBindsModule {

    @Binds
    @Singleton
    fun bindsFirestoreUtil(
        firestoreUtilImpl: FirestoreUtilImpl
    ): FirestoreUtil

    @Binds
    @Singleton
    fun bindsDomainRepository(
        domainRepositoryImpl: DomainRepositoryImpl
    ): DomainRepository

    @Binds
    @Singleton
    fun bindsDomainCacheRepository(
        domainCacheRepositoryImpl: DomainCacheRepositoryImpl
    ): DomainCacheRepository

    @Binds
    @Singleton
    fun bindsGetDomainServiceApiDataUseCase(
        getDomainServiceApiDataUseCaseImpl: GetDomainServiceApiDataUseCaseImpl
    ): GetDomainServiceApiDataUseCase

    @Binds
    @Singleton
    fun bindsInitialAppConfigRepository(
        initialAppConfigRepositoryImpl: InitialAppConfigRepositoryImpl
    ): InitialAppConfigRepository

    @Binds
    @Singleton
    fun bindsFeatureConfigRepository(
        featureConfigRepositoryImpl: FeatureConfigRepositoryImpl
    ): FeatureConfigRepository

    @Binds
    @Singleton
    fun bindsGetFeatureConfigUsecase(
        getFeatureConfigUsecaseImpl: GetFeatureConfigUsecaseImpl
    ): GetFeatureConfigUsecase

    @Binds
    fun bindsGetInitialAppConfigUsecase(
        getInitialAppConfigUsecaseImpl: GetInitialAppConfigUsecaseImpl
    ): GetInitialAppConfigUsecase
}
