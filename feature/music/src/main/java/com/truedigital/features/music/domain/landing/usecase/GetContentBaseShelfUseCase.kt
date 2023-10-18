package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetContentBaseShelfUseCase {
    fun execute(baseShelfId: String): Flow<List<MusicForYouShelfModel>>
}

class GetContentBaseShelfUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository,
    private val musicLandingRepository: MusicLandingRepository
) : GetContentBaseShelfUseCase {

    companion object {
        private const val FIELDS = "setting"
        private val viewTypeFilter = listOf(MusicShelfType.GRID_2.value)
    }

    override fun execute(baseShelfId: String): Flow<List<MusicForYouShelfModel>> {
        return musicLandingRepository.getCmsShelfList(
            shelfId = baseShelfId,
            country = localizationRepository.getAppCountryCode(),
            lang = localizationRepository.getAppLanguageCode(),
            fields = FIELDS
        ).map { data ->
            data?.shelfList?.filter {
                it.setting?.shelfCode.isNullOrEmpty().not() &&
                    viewTypeFilter.contains(it.setting?.viewType)
            }
        }.map { shelfList ->
            shelfList?.mapIndexed { index, shelf ->
                MusicForYouShelfModel(
                    index = index,
                    shelfIndex = index,
                    shelfId = shelf.setting?.shelfCode.orEmpty(),
                    title = localizationRepository.localization(
                        LocalizationModel().apply {
                            enWord = shelf.setting?.titleEn.orEmpty()
                            thWord = shelf.setting?.titleTh.orEmpty()
                        }
                    ),
                    titleFA = shelf.setting?.titleEn.orEmpty(),
                    productListType = ProductListType.CONTENT,
                    shelfType = mapShelfType(shelf.setting?.viewType)
                )
            } ?: emptyList()
        }
    }

    private fun mapShelfType(shelfType: String?): MusicShelfType {
        return when (shelfType) {
            MusicShelfType.GRID_2.value -> MusicShelfType.GRID_2
            MusicShelfType.HORIZONTAL.value -> MusicShelfType.HORIZONTAL
            MusicShelfType.VERTICAL.value -> MusicShelfType.VERTICAL
            else -> MusicShelfType.HORIZONTAL
        }
    }
}
