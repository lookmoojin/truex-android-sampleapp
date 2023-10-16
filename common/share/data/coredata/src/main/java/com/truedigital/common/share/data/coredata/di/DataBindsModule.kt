package com.truedigital.common.share.data.coredata.di

import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepositoryImpl
import com.truedigital.common.share.data.coredata.data.repository.RecommendPersonalizeRepository
import com.truedigital.common.share.data.coredata.data.repository.RecommendPersonalizeRepositoryImpl
import com.truedigital.common.share.data.coredata.deeplink.usecase.GenerateDeeplinkFormatUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.GenerateDeeplinkFormatUseCaseImpl
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCaseImpl
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDynamicUrlUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDynamicUrlUseCaseImpl
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsInternalDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsInternalDeeplinkUseCaseImpl
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsPrivilegeUrlUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsPrivilegeUrlUseCaseImpl
import com.truedigital.common.share.data.coredata.deeplink.usecase.ValidateDeeplinkUrlUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.ValidateDeeplinkUrlUseCaseImpl
import com.truedigital.common.share.data.coredata.domain.GetBaseShelfUseCase
import com.truedigital.common.share.data.coredata.domain.GetBaseShelfUseCaseImpl
import com.truedigital.common.share.data.coredata.domain.GetEnableCommunicatorUseCase
import com.truedigital.common.share.data.coredata.domain.GetEnableCommunicatorUseCaseImpl
import com.truedigital.common.share.data.coredata.domain.GetEnablePrivilegeUseCase
import com.truedigital.common.share.data.coredata.domain.GetEnablePrivilegeUseCaseImpl
import com.truedigital.common.share.data.coredata.domain.usecase.CountViewUseCase
import com.truedigital.common.share.data.coredata.domain.usecase.CountViewUseCaseImpl
import com.truedigital.common.share.data.coredata.domain.usecase.GetPreLoadAdsViewUseCase
import com.truedigital.common.share.data.coredata.domain.usecase.GetPreLoadAdsViewUseCaseImpl
import com.truedigital.common.share.data.coredata.domain.usecase.PreLoadAdsViewUseCase
import com.truedigital.common.share.data.coredata.domain.usecase.PreLoadAdsViewUseCaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DataBindsModule {

    @Binds
    fun bindsCmsShelvesRepository(
        cmsShelvesRepositoryImpl: CmsShelvesRepositoryImpl
    ): CmsShelvesRepository

    @Binds
    fun bindsRecommendPersonalizeRepository(
        recommendPersonalizeRepositoryImpl: RecommendPersonalizeRepositoryImpl
    ): RecommendPersonalizeRepository

    @Binds
    fun bindsGetBaseShelfUseCase(
        getBaseShelfUseCaseImpl: GetBaseShelfUseCaseImpl
    ): GetBaseShelfUseCase

    @Binds
    fun bindsGetEnableCommunicatorUseCase(
        getEnableCommunicatorUseCaseImpl: GetEnableCommunicatorUseCaseImpl
    ): GetEnableCommunicatorUseCase

    @Binds
    fun bindsGetEnablePrivilegeUseCase(
        getEnablePrivilegeUseCaseImpl: GetEnablePrivilegeUseCaseImpl
    ): GetEnablePrivilegeUseCase

    @Binds
    fun bindsIsInternalDeeplinkUseCase(
        isInternalDeeplinkUseCaseImpl: IsInternalDeeplinkUseCaseImpl
    ): IsInternalDeeplinkUseCase

    @Binds
    @Singleton
    fun bindsGenerateDeeplinkFormatUseCase(
        generateDeeplinkFormatUseCaseImpl: GenerateDeeplinkFormatUseCaseImpl
    ): GenerateDeeplinkFormatUseCase

    @Binds
    @Singleton
    fun bindsIsDomainDeeplinkUrlUseCase(
        isDomainDeeplinkUrlUseCaseImpl: IsDomainDeeplinkUrlUseCaseImpl
    ): IsDomainDeeplinkUrlUseCase

    @Binds
    @Singleton
    fun bindsIsDynamicUrlUseCase(
        isDynamicUrlUseCaseImpl: IsDynamicUrlUseCaseImpl
    ): IsDynamicUrlUseCase

    @Binds
    @Singleton
    fun bindsIsPrivilegeUrlUseCase(
        IsPrivilegeUrlUseCaseImpl: IsPrivilegeUrlUseCaseImpl
    ): IsPrivilegeUrlUseCase

    @Binds
    fun bindsPreLoadAdsViewUseCase(
        preLoadAdsViewUseCaseImpl: PreLoadAdsViewUseCaseImpl
    ): PreLoadAdsViewUseCase

    @Binds
    fun bindsGetPreLoadAdsViewUseCase(
        getPreLoadAdsViewUseCaseImpl: GetPreLoadAdsViewUseCaseImpl
    ): GetPreLoadAdsViewUseCase

    @Binds
    fun bindsValidateDeeplinkUrlUseCase(
        validateDeeplinkUrlUseCaseImpl: ValidateDeeplinkUrlUseCaseImpl
    ): ValidateDeeplinkUrlUseCase

    @Binds
    fun bindsCountViewUseCaseImpl(
        countViewUseCaseImpl: CountViewUseCaseImpl
    ): CountViewUseCase
}
