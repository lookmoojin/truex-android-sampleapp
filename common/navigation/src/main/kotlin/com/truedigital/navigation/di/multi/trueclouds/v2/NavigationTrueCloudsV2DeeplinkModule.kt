package com.truedigital.navigation.di.multi.trueclouds.v2

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.truecloudsv2.share.domain.usecase.deeplinks.v2.TrueCloudV2DecodeDeepLinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationTrueCloudsV2DeeplinkModule {

    @Binds
    @IntoSet
    fun bindsTrueCloudV2DecodeDeepLinkUseCase(
        trueCloudV2DecodeDeepLinkUseCaseImpl: TrueCloudV2DecodeDeepLinkUseCaseImpl,
    ): DecodeDeeplinkUseCase
}
