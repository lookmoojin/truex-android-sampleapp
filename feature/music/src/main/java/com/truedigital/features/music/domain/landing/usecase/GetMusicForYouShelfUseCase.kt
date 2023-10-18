package com.truedigital.features.music.domain.landing.usecase

import com.google.gson.Gson
import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.landing.model.response.ContentValueResponse
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.music.domain.landing.model.ItemOptionsModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

interface GetMusicForYouShelfUseCase {
    fun execute(apiPath: String): Flow<List<MusicForYouShelfModel>>
}

class GetMusicForYouShelfUseCaseImpl @Inject constructor(
    private val musicLandingRepository: MusicLandingRepository,
    private val mapProductListTypeUseCase: MapProductListTypeUseCase,
    private val cacheTrackQueueRepository: CacheTrackQueueRepository,
    private val localizationRepository: LocalizationRepository
) : GetMusicForYouShelfUseCase {

    companion object {
        private const val H1 = "h1"
        private const val SPACING = "spacing"
        private const val DISPLAY_SUB_TYPE_STANDARD = "standard"
        const val DISPLAY_SHELF = "shelf"
        const val ERROR_API_PATH_EMPTY = "music for you shelf error api path is empty"
    }

    override fun execute(apiPath: String): Flow<List<MusicForYouShelfModel>> {
        return if (apiPath.isNotEmpty()) {
            musicLandingRepository.getMusicForYouShelf(apiPath)
                .onEach {
                    cacheTrackQueueRepository.clearCacheTrackQueue()
                }
                .map {
                    val musicForYouShelfModelList: MutableList<MusicForYouShelfModel> =
                        mutableListOf()
                    val contentValue = Gson().fromJson(it, ContentValueResponse::class.java)
                    contentValue.items?.filter { shelf ->
                        filterShelfType(shelf)
                    }?.filterNot { content ->
                        filterNotContentType(content)
                    }?.scanIndexed(
                        contentValue.items.first()
                    ) { index: Int, beforeResponse: ContentValueResponse.ContentValueItem,
                        response: ContentValueResponse.ContentValueItem ->
                        when (response.type) {
                            H1 -> {
                                response
                            }
                            else -> {
                                val productType =
                                    mapProductListTypeUseCase.execute(response.type)

                                if (beforeResponse.type == H1 && response.type != H1) {
                                    musicForYouShelfModelList.add(
                                        MusicForYouShelfModel(
                                            index = index,
                                            title = localizationRepository.localization(
                                                localizationModel = LocalizationModel().apply {
                                                    enWord = getTitle(
                                                        beforeResponse,
                                                        LocalizationRepository.Localization.EN.languageCode
                                                    )
                                                    thWord = getTitle(
                                                        beforeResponse,
                                                        LocalizationRepository.Localization.TH.languageCode
                                                    )
                                                }
                                            ),
                                            titleFA = getTitle(
                                                beforeResponse,
                                                LocalizationRepository.Localization.EN.languageCode
                                            ),
                                            productListType = productType,
                                            options = mapToItemOption(
                                                response.options,
                                                beforeResponse.options?.displayTitle
                                            )
                                        )
                                    )
                                } else {
                                    musicForYouShelfModelList.add(
                                        MusicForYouShelfModel(
                                            index = index,
                                            productListType = productType,
                                            options = mapToItemOption(
                                                response.options,
                                                beforeResponse.options?.displayTitle
                                            )
                                        )
                                    )
                                }
                                response
                            }
                        }
                    }
                    musicForYouShelfModelList
                }
        } else {
            flow { error(ERROR_API_PATH_EMPTY) }
        }
    }

    private fun getTitle(
        beforeResponse: ContentValueResponse.ContentValueItem,
        languageCode: String
    ): String {
        return beforeResponse.text?.find { title ->
            title.language == languageCode
        }?.value.orEmpty()
    }

    private fun mapToItemOption(
        itemOption: ContentValueResponse.ContentValueItem.ContentValueItemOptions?,
        displayTitle: Boolean?
    ): ItemOptionsModel? {
        return itemOption?.let { option ->
            ItemOptionsModel(
                playlistId = option.playlistId.orEmpty(),
                tag = option.tag.orEmpty(),
                limit = option.listLimit.orEmpty(),
                format = option.format.orEmpty(),
                displayTitle = displayTitle ?: false,
                displayType = option.displayType.orEmpty(),
                targetTime = option.targetTime ?: false
            )
        }
    }

    private fun filterShelfType(shelf: ContentValueResponse.ContentValueItem): Boolean {
        return if (shelf.type != H1) {
            shelf.options?.displayType == DISPLAY_SHELF ||
                shelf.options?.displayType == MusicConstant.Display.VERTICAL_LIST
        } else {
            true
        }
    }

    private fun filterNotContentType(content: ContentValueResponse.ContentValueItem): Boolean {
        return if (content.type != H1) {
            content.options?.displaySubType != DISPLAY_SUB_TYPE_STANDARD
        } else {
            content.type == SPACING
        }
    }
}
