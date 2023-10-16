package com.truedigital.common.share.componentv3.widget.ads.domain.usecase

import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepository
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetAdsPreLoadConfigUseCase {
    fun execute(): Flow<Boolean>
}

class GetAdsPreLoadConfigUseCaseImpl @Inject constructor(
    private val getCommunityTabConfigRepository: GetCommunityTabConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetAdsPreLoadConfigUseCase {
    override fun execute(): Flow<Boolean> {
        return flow {
            getCommunityTabConfigRepository
                .getCommunityTabConfig(
                    localizationRepository
                        .getAppCountryCode()
                )?.let { communityTabConfigModel ->
                    communityTabConfigModel.todayPage?.adsPreLoad?.enable?.let { config ->
                        emit(config.isEnable)
                    } ?: emit(false)
                } ?: emit(false)
        }
    }
}
