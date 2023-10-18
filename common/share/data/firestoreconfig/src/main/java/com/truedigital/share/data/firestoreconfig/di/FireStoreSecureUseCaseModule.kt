package com.truedigital.share.data.firestoreconfig.di

import com.truedigital.share.data.firestoreconfig.domainconfig.usecase.GetFireBaseSecureUseCase
import com.truedigital.share.data.firestoreconfig.domainconfig.usecase.GetFireBaseSecureUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface FireStoreSecureUseCaseModule {

    @Binds
    fun bindsGetFireBaseSecureUseCase(
        getFireBaseSecureUseCaseImpl: GetFireBaseSecureUseCaseImpl
    ): GetFireBaseSecureUseCase
}
