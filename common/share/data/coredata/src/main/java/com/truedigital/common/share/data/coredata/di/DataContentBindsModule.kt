package com.truedigital.common.share.data.coredata.di

import com.truedigital.common.share.data.coredata.data.repository.CmsContentRepository
import com.truedigital.common.share.data.coredata.data.repository.CmsContentRepositoryImpl
import com.truedigital.common.share.data.coredata.data.repository.PreLoadAdsCacheRepository
import com.truedigital.common.share.data.coredata.data.repository.PreLoadAdsCacheRepositoryImpl
import com.truedigital.common.share.data.coredata.data.repository.ShortPersonalizeRepository
import com.truedigital.common.share.data.coredata.data.repository.ShortPersonalizeRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DataContentBindsModule {

    @Binds
    fun bindsCmsContentRepository(
        cmsContentRepositoryImpl: CmsContentRepositoryImpl
    ): CmsContentRepository

    @Binds
    fun bindsShortPersonalizeRepository(
        similarPersonalizeRepositoryImpl: ShortPersonalizeRepositoryImpl
    ): ShortPersonalizeRepository

    @Binds
    @Singleton
    fun bindsPreLoadAdsCacheRepository(
        preLoadAdsCacheRepositoryImpl: PreLoadAdsCacheRepositoryImpl
    ): PreLoadAdsCacheRepository
}
