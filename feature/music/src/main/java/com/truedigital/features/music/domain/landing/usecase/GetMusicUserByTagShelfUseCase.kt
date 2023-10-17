package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetMusicUserByTagShelfUseCase {
    fun execute(tag: String): Flow<Triple<List<MusicForYouItemModel>, ProductListType, String>>
}

class GetMusicUserByTagShelfUseCaseImpl @Inject constructor(
    private val musicLandingRepository: MusicLandingRepository,
    private val mapRadioUseCase: MapRadioUseCase,
    private val cmsShelvesRepository: CmsShelvesRepository,
    private val localizationRepository: LocalizationRepository,
    private val decodeMusicHeroBannerDeeplinkUseCase: DecodeMusicHeroBannerDeeplinkUseCase
) : GetMusicUserByTagShelfUseCase {

    companion object {
        private const val FIELDS = "setting"
        const val CONTENT_TYPE_HILIGHT = "hilight"
        const val CONTENT_TYPE_MISC = "misc"
        const val TYPE_BY_SHELF_ID = "by_shelfid"
        const val VIEW_TYPE_ADS_SHELF = "ads_shelf"
        const val VIEW_TYPE_BANNER_SHELF = "banner_shelf"
        const val VIEW_TYPE_RADIO = "radio_shelf"
        const val ERROR_CONDITION = "Tag is empty"
    }

    override fun execute(tag: String): Flow<Triple<List<MusicForYouItemModel>, ProductListType, String>> {
        return if (tag.isNotEmpty()) {
            musicLandingRepository.getTagByName(tag)
                .map { response ->
                    response?.displayName?.find { title ->
                        title.language.equals(
                            "en",
                            true
                        )
                    }?.value.orEmpty()
                }
                .filter { shelfId ->
                    shelfId.isNotEmpty()
                }
                .flatMapConcat { shelfId ->
                    cmsShelvesRepository.getCmsPublicContentShelfListData(
                        shelfId = shelfId,
                        country = localizationRepository.getAppCountryCode(),
                        fields = FIELDS,
                        lang = localizationRepository.getAppLanguageCode()
                    )
                }
                .map { data ->
                    mapItem(data)
                }
        } else {
            flow { error(ERROR_CONDITION) }
        }
    }

    private fun mapItem(data: Data): Triple<List<MusicForYouItemModel>, ProductListType, String> {
        return if (data.setting?.type.equals(TYPE_BY_SHELF_ID, true)) {
            val viewType = data.setting?.viewType
            if (viewType.equals(VIEW_TYPE_BANNER_SHELF, true)) {
                mapHeroBannerItem(data.shelfList ?: emptyList())
            } else if (viewType.equals(VIEW_TYPE_ADS_SHELF, true)) {
                mapAdsBannerItem(data.setting)
            } else if (viewType.equals(VIEW_TYPE_RADIO, true)) {
                mapRadioItem(data)
            } else {
                Triple(emptyList(), ProductListType.TAGGED_USER, "")
            }
        } else {
            Triple(emptyList(), ProductListType.TAGGED_USER, "")
        }
    }

    private fun mapRadioItem(data: Data): Triple<List<MusicForYouItemModel>, ProductListType, String> {
        val radioItemList = data.shelfList?.mapIndexed { index, shelf ->
            val setting = shelf.setting
            mapRadioUseCase.execute(
                id = shelf.id.orEmpty(),
                index = index,
                setting = setting,
                viewType = data.setting?.viewType.orEmpty(),
                contentType = shelf.contentType
            )
        } ?: emptyList()
        return Triple(radioItemList, ProductListType.TAGGED_RADIO, data.setting?.seemore.orEmpty())
    }

    private fun mapHeroBannerItem(shelfList: List<Shelf>): Triple<List<MusicForYouItemModel>, ProductListType, String> {
        val musicForYouItemList = shelfList.mapIndexed { index, shelf ->
            val deeplinkPair = decodeMusicHeroBannerDeeplinkUseCase.execute(
                shelf.setting?.deepLink.orEmpty()
            )

            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = index,
                heroBannerId = shelf.id.orEmpty(),
                coverImage = shelf.thumb,
                deeplinkPair = deeplinkPair,
                title = shelf.title.orEmpty(),
                contentType = shelf.contentType.orEmpty()
            )
        }
        return Triple(musicForYouItemList, ProductListType.TAGGED_USER, "")
    }

    private fun mapAdsBannerItem(setting: Setting?): Triple<List<MusicForYouItemModel>, ProductListType, String> {
        val musicForYouItemList = if (setting?.adsId.isNullOrEmpty().not() &&
            (setting?.mobileSize.isNullOrEmpty().not() || setting?.tabletSize.isNullOrEmpty().not())
        ) {
            listOf(
                MusicForYouItemModel.AdsBannerShelfItem(
                    itemId = 1,
                    adsId = setting?.adsId,
                    mobileSize = setting?.mobileSize,
                    tabletSize = setting?.tabletSize
                )
            )
        } else {
            emptyList()
        }

        return Triple(musicForYouItemList, ProductListType.TAGGED_ADS, "")
    }
}
