package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.constant.MusicViewType
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetContentItemUseCase {
    fun execute(shelfId: String, shelfType: MusicShelfType): Flow<List<MusicForYouItemModel>>
}

class GetContentItemUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository,
    private val mapRadioUseCase: MapRadioUseCase,
    private val musicLandingRepository: MusicLandingRepository
) : GetContentItemUseCase {

    companion object {
        private const val FIELDS = "setting"
    }

    override fun execute(
        shelfId: String,
        shelfType: MusicShelfType
    ): Flow<List<MusicForYouItemModel>> {
        return musicLandingRepository.getCmsShelfList(
            shelfId = shelfId,
            country = localizationRepository.getAppCountryCode(),
            lang = localizationRepository.getAppLanguageCode(),
            fields = FIELDS
        ).map { data ->
            if (data?.setting?.viewType == MusicViewType.RADIO.value) {
                data.shelfList?.let { shelf ->
                    mapRadioItems(shelf, shelfType)
                } ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    private fun mapRadioItems(
        shelfList: List<Shelf>,
        shelfType: MusicShelfType
    ): List<MusicForYouItemModel.RadioShelfItem> {
        return shelfList.mapIndexed { index, shelf ->
            mapRadioUseCase.execute(
                id = shelf.id.orEmpty(),
                index = index,
                setting = shelf.setting,
                viewType = shelf.setting?.viewType.orEmpty(),
                contentType = shelf.contentType,
                shelfType = shelfType
            )
        }
    }
}
