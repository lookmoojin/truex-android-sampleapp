package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.data.repository.multimedia.ConcurrentUserRepository
import com.truedigital.common.share.datalegacy.data.repository.multimedia.ConcurrentUserRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface ConcurrentUserModule {

    @Binds
    fun bindsConcurrentUserRepository(
        concurrentUserRepositoryImpl: ConcurrentUserRepositoryImpl
    ): ConcurrentUserRepository
}
