package com.truedigital.common.share.data.coredata.di

import com.truedigital.common.share.data.coredata.data.repository.CmsContentRepository
import com.truedigital.common.share.data.coredata.data.repository.CmsContentRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface DataContentBindsModule {

    @Binds
    fun bindsCmsContentRepository(
        cmsContentRepositoryImpl: CmsContentRepositoryImpl
    ): CmsContentRepository
}
