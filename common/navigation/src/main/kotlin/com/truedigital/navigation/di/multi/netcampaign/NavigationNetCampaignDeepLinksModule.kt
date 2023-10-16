package com.truedigital.navigation.di.multi.netcampaign

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.features.netcampaigns.share.domain.usecase.GetNetCampaignFeatureEnableUseCase
import com.truedigital.features.netcampaigns.share.domain.usecase.deeplinks.NetCampaignDecodeDeepLinkUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationNetCampaignDeepLinksModule {

    @Provides
    @IntoSet
    fun bindNetCampaignDecodeDeepLinkUseCase(
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase,
        getNetCampaignFeatureEnableUseCase: GetNetCampaignFeatureEnableUseCase
    ): DecodeDeeplinkUseCase {
        return NetCampaignDecodeDeepLinkUseCaseImpl(
            isDomainDeeplinkUrlUseCase,
            getNetCampaignFeatureEnableUseCase
        )
    }
}
