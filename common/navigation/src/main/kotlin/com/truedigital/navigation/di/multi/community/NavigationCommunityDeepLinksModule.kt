package com.truedigital.navigation.di.multi.community

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.community.share.domain.usecase.deeplinks.CommunityDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationCommunityDeepLinksModule {

    @Binds
    @IntoSet
    fun bindsCommunityDecodeDeeplinkUseCase(
        communityDecodeDeeplinkUseCaseImpl: CommunityDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
