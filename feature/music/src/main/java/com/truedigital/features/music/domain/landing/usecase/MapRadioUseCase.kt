package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import javax.inject.Inject

interface MapRadioUseCase {
    fun execute(
        id: String,
        index: Int,
        setting: Setting?,
        viewType: String? = null,
        contentType: String? = null,
        shelfType: MusicShelfType? = null
    ): MusicForYouItemModel.RadioShelfItem
}

class MapRadioUseCaseImpl @Inject constructor(
    private val getRadioMediaAssetIdUseCase: GetRadioMediaAssetIdUseCase,
    private val localizationRepository: LocalizationRepository
) : MapRadioUseCase {

    override fun execute(
        id: String,
        index: Int,
        setting: Setting?,
        viewType: String?,
        contentType: String?,
        shelfType: MusicShelfType?
    ): MusicForYouItemModel.RadioShelfItem {
        return MusicForYouItemModel.RadioShelfItem(
            mediaAssetId = getRadioMediaAssetIdUseCase.execute(id),
            index = index,
            radioId = id,
            description = localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = setting?.descriptionEn.orEmpty()
                    thWord = setting?.descriptionTh.orEmpty()
                    myWord = setting?.descriptionEn.orEmpty()
                }
            ),
            titleTh = setting?.titleTh.orEmpty(),
            titleEn = setting?.titleEn.orEmpty(),
            title = localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = setting?.titleEn.orEmpty()
                    thWord = setting?.titleTh.orEmpty()
                    myWord = setting?.titleEn.orEmpty()
                }
            ),
            thumbnail = setting?.thumbnail.orEmpty(),
            viewType = viewType.orEmpty(),
            shelfType = shelfType ?: MusicShelfType.HORIZONTAL,
            contentType = contentType.orEmpty(),
            streamUrl = setting?.streamUrl.orEmpty()
        )
    }
}
