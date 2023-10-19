package com.truedigital.common.share.data.coredata.di

import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepositoryImpl
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
import com.truedigital.common.share.data.coredata.domain.usecase.CountViewUseCase
import com.truedigital.common.share.data.coredata.domain.usecase.CountViewUseCaseImpl
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
    fun bindsGetBaseShelfUseCase(
        getBaseShelfUseCaseImpl: GetBaseShelfUseCaseImpl
    ): GetBaseShelfUseCase

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
    fun bindsValidateDeeplinkUrlUseCase(
        validateDeeplinkUrlUseCaseImpl: ValidateDeeplinkUrlUseCaseImpl
    ): ValidateDeeplinkUrlUseCase

    @Binds
    fun bindsCountViewUseCaseImpl(
        countViewUseCaseImpl: CountViewUseCaseImpl
    ): CountViewUseCase
}
