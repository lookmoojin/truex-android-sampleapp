package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

interface GetTrackPlaylistShelfUseCase {
    fun execute(
        playlistId: String,
        limit: String,
        displayType: String
    ): Flow<List<MusicForYouItemModel>>
}

class GetTrackPlaylistShelfUseCaseImpl @Inject constructor(
    private val musicLandingRepository: MusicLandingRepository,
    private val cacheTrackQueueRepository: CacheTrackQueueRepository
) : GetTrackPlaylistShelfUseCase {

    companion object {
        private const val offset = 1
        private const val count = 12
        private const val count_vertical = 15
        private const val ERROR_PLAYLIST_ID_EMPTY = "playlist id for you shelf error is empty"
        private const val TH = "TH"
    }

    override fun execute(
        playlistId: String,
        limit: String,
        displayType: String
    ): Flow<List<MusicForYouItemModel>> {
        return if (playlistId.isNotEmpty()) {
            val defaultLimit = if (limit.isEmpty()) {
                when (displayType) {
                    MusicConstant.Display.VERTICAL_LIST -> count_vertical
                    else -> count
                }
            } else {
                limit.toInt()
            }
            musicLandingRepository.getPlaylistTrackShelf(playlistId, offset, defaultLimit)
                .onEach { response ->
                    response?.results?.let {
                        cacheTrackQueueRepository.saveCachePlaylistTrackQueue(
                            playlistId.toInt(),
                            it
                        )
                    }
                }
                .map { response ->
                    response?.results?.mapIndexed { index, result ->
                        MusicForYouItemModel.TrackPlaylistShelf(
                            index = index,
                            playlistId = playlistId.toInt(),
                            playlistTrackId = result.playlistTrackId ?: 0,
                            trackId = result.trackId ?: 0,
                            trackIdList = response.results.map { it.trackId ?: 0 },
                            artist = result.artist?.firstOrNull()?.name.orEmpty(),
                            coverImage = result.image,
                            name = result.translations.find { name -> name.language.equals(TH) }?.value
                                ?: result.name,
                            position = when (displayType) {
                                MusicConstant.Display.VERTICAL_LIST -> result.position
                                else -> ""
                            }
                        )
                    } ?: emptyList()
                }
        } else {
            flow { error(ERROR_PLAYLIST_ID_EMPTY) }
        }
    }
}
