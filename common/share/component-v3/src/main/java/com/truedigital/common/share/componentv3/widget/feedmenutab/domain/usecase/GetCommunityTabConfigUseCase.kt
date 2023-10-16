package com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase

import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepository
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.model.CommunityTabConfigModel
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.model.CommunityTabDataModel
import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetCommunityTabConfigUseCase {
    fun execute(): Flow<CommunityTabDataModel?>
}

class GetCommunityTabConfigUseCaseImpl @Inject constructor(
    private val getCommunityTabConfigRepository: GetCommunityTabConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetCommunityTabConfigUseCase {

    override fun execute(): Flow<CommunityTabDataModel?> {
        return flow {
            getCommunityTabConfigRepository
                .getCommunityTabConfig(
                    localizationRepository
                        .getAppCountryCode()
                )?.let { communityTabConfigModel ->
                    communityTabConfigModel.communityTab?.let { config ->
                        if (config.enable?.isEnable == true &&
                            config.title != null
                        ) {
                            val communityTabData = getCommunityTabDataModel(communityTabConfigModel)
                            emit(communityTabData)
                        } else {
                            emit(null)
                        }
                    } ?: emit(null)
                } ?: emit(null)
        }
    }

    private fun getCommunityTabDataModel(communityTabConfigModel: CommunityTabConfigModel): CommunityTabDataModel {
        return CommunityTabDataModel().apply {
            this.communityTitle =
                localizationRepository.localize(
                    LocalizationModel().apply {
                        this.phWord = communityTabConfigModel.communityTab?.title?.ph.orEmpty()
                        this.enWord = communityTabConfigModel.communityTab?.title?.en.orEmpty()
                        this.myWord = communityTabConfigModel.communityTab?.title?.my.orEmpty()
                        this.thWord = communityTabConfigModel.communityTab?.title?.th.orEmpty()
                        this.idWord = communityTabConfigModel.communityTab?.title?.id.orEmpty()
                        this.vnWord = communityTabConfigModel.communityTab?.title?.vi.orEmpty()
                        this.kmWord = communityTabConfigModel.communityTab?.title?.kh.orEmpty()
                    }
                )
            this.forYouTitle =
                localizationRepository.localize(
                    LocalizationModel().apply {
                        this.phWord = communityTabConfigModel.forYouTab?.title?.ph.orEmpty()
                        this.enWord = communityTabConfigModel.forYouTab?.title?.en.orEmpty()
                        this.myWord = communityTabConfigModel.forYouTab?.title?.my.orEmpty()
                        this.thWord = communityTabConfigModel.forYouTab?.title?.th.orEmpty()
                        this.idWord = communityTabConfigModel.forYouTab?.title?.id.orEmpty()
                        this.vnWord = communityTabConfigModel.forYouTab?.title?.vi.orEmpty()
                        this.kmWord = communityTabConfigModel.forYouTab?.title?.kh.orEmpty()
                    }
                )
        }
    }
}
