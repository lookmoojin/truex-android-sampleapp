package com.truedigital.share.data.firestoreconfig.di

import com.truedigital.share.data.firestoreconfig.domainconfig.repository.secure.FireBaseSecureRepository
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.secure.FireBaseSecureRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface FireStoreSecureRepositoryModule {

    @Binds
    @Singleton
    fun bindsFireBaseSecureRepository(
        fireBaseSecureRepositoryImpl: FireBaseSecureRepositoryImpl
    ): FireBaseSecureRepository
}
