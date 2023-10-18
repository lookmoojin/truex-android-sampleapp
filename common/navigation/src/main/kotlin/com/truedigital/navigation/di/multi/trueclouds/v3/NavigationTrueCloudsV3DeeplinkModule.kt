package com.truedigital.navigation.di.multi.trueclouds.v3

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.truecloudsv2.share.domain.usecase.deeplinks.v3.TrueCloudV3DecodeDeepLinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationTrueCloudsV3DeeplinkModule {

    @Binds
    @IntoSet
    fun bindsTrueCloudV3DecodeDeepLinkUseCase(
        trueCloudV3DecodeDeepLinkUseCaseImpl: TrueCloudV3DecodeDeepLinkUseCaseImpl,
    ): DecodeDeeplinkUseCase
}
