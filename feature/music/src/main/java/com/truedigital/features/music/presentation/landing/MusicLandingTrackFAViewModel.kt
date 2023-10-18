package com.truedigital.features.music.presentation.landing

import androidx.lifecycle.ViewModel
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.landing.model.FAMusicLandingShelfModel
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.music.domain.landing.model.MusicLandingFASelectContentModel
import com.truedigital.features.music.domain.landing.usecase.GetDataForTrackFAMusicLandingPageUseCase
import javax.inject.Inject

class MusicLandingTrackFAViewModel @Inject constructor(
    private val getDataForTrackFAMusicLandingPageUseCase: GetDataForTrackFAMusicLandingPageUseCase,
    private val analyticManager: AnalyticManager
) : ViewModel() {
    private var isFirstLoad: Boolean = true

    fun onScrollShelves(baseShelfId: String, musicForYouShelf: MusicForYouShelfModel) {
        val faData = getDataForTrackFAMusicLandingPageUseCase.execute(
            baseShelfId = baseShelfId,
            musicForYouShelf = musicForYouShelf,
            isFirstLoad = isFirstLoad
        )
        trackFAShelf(faData)
        isFirstLoad = false
    }

    private fun trackFAShelf(data: FAMusicLandingShelfModel?) {
        data?.let {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_VIEW_ITEM_LIST,
                    MeasurementConstant.Key.KEY_SHELF_NAME to it.shelfName,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to it.shelfIndex
                )
            )
        }
    }

    fun trackFASelectContent(
        itemModel: MusicForYouItemModel,
        selectContentModel: MusicLandingFASelectContentModel
    ) {
        when (itemModel) {
            is MusicForYouItemModel.AlbumShelfItem -> {
                trackingFASelectContent(
                    selectContentModel.copy(
                        itemId = itemModel.releaseId.toString(),
                        title = itemModel.albumName.orEmpty(),
                        contentType = MusicConstant.Type.ALBUM
                    )
                )
            }
            is MusicForYouItemModel.ArtistShelfItem -> {
                trackingFASelectContent(
                    selectContentModel.copy(
                        itemId = itemModel.artistId.toString(),
                        title = itemModel.name.orEmpty(),
                        contentType = MusicConstant.Type.ARTIST
                    )
                )
            }
            is MusicForYouItemModel.PlaylistShelfItem -> {
                trackingFASelectContent(
                    selectContentModel.copy(
                        itemId = itemModel.playlistId.toString(),
                        title = itemModel.nameEn.orEmpty(),
                        contentType = MusicConstant.Type.PLAYLIST
                    )
                )
            }
            is MusicForYouItemModel.TrackPlaylistShelf -> {
                trackingFASelectContent(
                    selectContentModel.copy(
                        itemId = itemModel.trackId.toString(),
                        title = itemModel.name.orEmpty(),
                        contentType = MusicConstant.Type.MUSIC
                    )
                )
            }
            is MusicForYouItemModel.MusicHeroBannerShelfItem -> {
                trackingFASelectContent(
                    selectContentModel.copy(
                        itemId = itemModel.heroBannerId,
                        title = itemModel.title,
                        contentType = itemModel.contentType
                    )
                )
            }
            is MusicForYouItemModel.RadioShelfItem -> {
                trackingFASelectContent(
                    selectContentModel.copy(
                        itemId = itemModel.radioId,
                        title = itemModel.titleEn,
                        contentType = itemModel.contentType
                    )
                )
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun trackingFASelectContent(data: MusicLandingFASelectContentModel) {
        analyticManager.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                MeasurementConstant.Key.KEY_CMS_ID to data.itemId,
                MeasurementConstant.Key.KEY_TITLE to data.title.take(MusicConstant.FA.CHARACTERS_LIMIT),
                MeasurementConstant.Key.KEY_CONTENT_TYPE to data.contentType,
                MeasurementConstant.Key.KEY_SHELF_NAME to data.shelfName.take(MusicConstant.FA.CHARACTERS_LIMIT),
                MeasurementConstant.Key.KEY_ITEM_INDEX to data.itemIndex,
                MeasurementConstant.Key.KEY_SHELF_INDEX to data.shelfIndex
            )
        )
    }
}
