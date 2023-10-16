package com.truedigital.navigation.di.multi.gamification

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.features.gamifications.share.domain.usecase.deeplink.exclusive.ExclusiveDecodeDeeplinkUseCaseImpl
import com.truedigital.features.gamifications.share.domain.usecase.deeplink.gamecenter.GameDecodeDeepLinkUseCaseImpl
import com.truedigital.features.gamifications.share.domain.usecase.deeplink.gamecenter.GetEnableGamificationUseCase
import com.truedigital.features.gamifications.share.domain.usecase.deeplink.gamecenter.GetGameCenterTabUseCase
import com.truedigital.features.gamifications.share.domain.usecase.deeplink.mission.MissionDecodeDeepLinkUseCaseImpl
import com.truedigital.features.gamifications.share.domain.usecase.deeplink.userinbox.UserInboxDecodeDeepLinkUseCaseImpl
import com.truedigital.features.gamifications.share.domain.usecase.firestore.CheckEnableMissionUseCase
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationGamificationDeepLinksModule {

    @Provides
    @IntoSet
    fun bindUserInboxDecodeDeepLinkUseCase(
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase
    ): DecodeDeeplinkUseCase {
        return UserInboxDecodeDeepLinkUseCaseImpl(isDomainDeeplinkUrlUseCase)
    }

    @Provides
    @IntoSet
    fun bindMissionDecodeDeepLinkUseCase(
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase,
        checkEnableMissionUseCase: CheckEnableMissionUseCase
    ): DecodeDeeplinkUseCase {
        return MissionDecodeDeepLinkUseCaseImpl(
            isDomainDeeplinkUrlUseCase,
            checkEnableMissionUseCase
        )
    }

    @Provides
    @IntoSet
    fun bindGameDecodeDeepLinkUseCase(
        getEnableGamificationUseCase: GetEnableGamificationUseCase,
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase,
        getGameCenterTabUseCase: GetGameCenterTabUseCase
    ): DecodeDeeplinkUseCase {
        return GameDecodeDeepLinkUseCaseImpl(
            getEnableGamificationUseCase,
            isDomainDeeplinkUrlUseCase,
            getGameCenterTabUseCase
        )
    }

    @Provides
    @IntoSet
    fun bindExclusiveDecodeDeeplinkUseCase(): DecodeDeeplinkUseCase {
        return ExclusiveDecodeDeeplinkUseCaseImpl()
    }
}
