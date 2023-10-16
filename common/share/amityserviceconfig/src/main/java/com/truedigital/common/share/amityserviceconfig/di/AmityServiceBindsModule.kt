package com.truedigital.common.share.amityserviceconfig.di

import com.truedigital.common.share.amityserviceconfig.data.repository.AmityConfigRepository
import com.truedigital.common.share.amityserviceconfig.data.repository.AmityConfigRepositoryImpl
import com.truedigital.common.share.amityserviceconfig.data.repository.AmityEnableSecureModeRepositoryImpl
import com.truedigital.common.share.amityserviceconfig.data.repository.AmitySetupAppLocaleRepositoryImpl
import com.truedigital.common.share.amityserviceconfig.data.repository.AmitySetupRepositoryImpl
import com.truedigital.common.share.amityserviceconfig.data.repository.CommunityGetRegexRepositoryImpl
import com.truedigital.common.share.amityserviceconfig.data.repository.MediaConfigRepositoryImpl
import com.truedigital.common.share.amityserviceconfig.data.repository.PopularFeedConfigRepositoryImpl
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmityEnableSecureModeRepository
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupAppLocaleRepository
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupRepository
import com.truedigital.common.share.amityserviceconfig.domain.repository.CommunityGetRegexRepository
import com.truedigital.common.share.amityserviceconfig.domain.repository.MediaConfigRepository
import com.truedigital.common.share.amityserviceconfig.domain.repository.PopularFeedConfigRepository
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetMediaConfigUseCase
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetMediaConfigUseCaseImpl
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetRegexUseCase
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetRegexUseCaseImpl
import com.truedigital.common.share.amityserviceconfig.domain.usecase.PopularFeedConfigUseCase
import com.truedigital.common.share.amityserviceconfig.domain.usecase.PopularFeedConfigUseCaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface AmityServiceBindsModule {

    @Binds
    @Singleton
    fun bindsAmityConfigRepository(
        amityConfigRepositoryImpl: AmityConfigRepositoryImpl
    ): AmityConfigRepository

    @Binds
    @Singleton
    fun bindsAmityEnableSecureModeRepository(
        amityEnableSecureModeRepositoryImpl: AmityEnableSecureModeRepositoryImpl
    ): AmityEnableSecureModeRepository

    @Binds
    @Singleton
    fun bindsAmitySetupRepository(
        amitySetupRepositoryImpl: AmitySetupRepositoryImpl
    ): AmitySetupRepository

    @Binds
    fun bindsCommunityGetRegexUseCase(
        communityGetRegexUseCaseImpl: CommunityGetRegexUseCaseImpl
    ): CommunityGetRegexUseCase

    @Binds
    fun bindsPopularFeedConfigUseCase(
        popularFeedConfigUseCaseImpl: PopularFeedConfigUseCaseImpl
    ): PopularFeedConfigUseCase

    @Binds
    fun bindsCommunityGetRegexRepository(
        communityGetRegexRepositoryImpl: CommunityGetRegexRepositoryImpl
    ): CommunityGetRegexRepository

    @Binds
    fun bindsPopularFeedConfigRepository(
        popularFeedConfigRepositoryImpl: PopularFeedConfigRepositoryImpl
    ): PopularFeedConfigRepository

    @Binds
    fun bindsAmitySetupAppLocaleRepository(
        amitySetupAppLocaleRepositoryImpl: AmitySetupAppLocaleRepositoryImpl
    ): AmitySetupAppLocaleRepository

    @Binds
    fun bindsMediaConfigRepository(
        mediaConfigRepositoryImpl: MediaConfigRepositoryImpl
    ): MediaConfigRepository

    @Binds
    fun bindsCommunityGetMediaConfigUseCase(
        mediaConfigUseCaseImpl: CommunityGetMediaConfigUseCaseImpl
    ): CommunityGetMediaConfigUseCase
}
