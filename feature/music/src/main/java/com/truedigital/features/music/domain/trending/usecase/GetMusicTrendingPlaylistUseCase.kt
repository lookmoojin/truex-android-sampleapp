package com.truedigital.features.music.domain.trending.usecase

import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.music.data.trending.repository.MusicTrendingPlaylistCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingRepository
import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetMusicTrendingPlaylistUseCase {
    fun execute(): Flow<ArrayList<MusicSearchTrendingAdapter.DataItem>?>
}

class GetMusicTrendingPlaylistUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository,
    private val musicTrendingRepository: MusicTrendingRepository,
    private val musicTrendingPlaylistCacheRepository: MusicTrendingPlaylistCacheRepository
) : GetMusicTrendingPlaylistUseCase {

    companion object {
        const val LIMIT_ITEM = 10
        const val KEY_EXTRA_TRENDING_PLAYLIST_EN = "Trending Playlist"
        const val KEY_EXTRA_TRENDING_PLAYLIST_TH = "เพลย์ลิสต์ยอดนิยม"
        const val TH = "th"
        const val EN = "en"
    }

    override fun execute(): Flow<ArrayList<MusicSearchTrendingAdapter.DataItem>?> {
        val trendingPlaylistCacheList =
            musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist()
        return if (trendingPlaylistCacheList == null) {
            musicTrendingRepository.getMusicTrendingPlaylist()
                .map { playlistResponse ->
                    val trendingPlaylist = playlistResponse?.results?.take(LIMIT_ITEM)?.map {
                        TrendingPlaylistModel().apply {
                            this.id = it.playlistId
                            this.image = translateValue(it.coverImage).orEmpty()
                            this.name = translateValue(it.name).orEmpty()
                        }
                    }

                    musicTrendingPlaylistCacheRepository.saveCacheTrendingPlaylist(
                        trendingPlaylist ?: emptyList()
                    )
                    getDataItemFromTrendingPlaylist(getTrendingPlaylistTitle(), trendingPlaylist)
                }.catch {
                    musicTrendingPlaylistCacheRepository.saveCacheTrendingPlaylist(emptyList())
                    emit(null)
                }
        } else {
            flowOf(
                getDataItemFromTrendingPlaylist(
                    getTrendingPlaylistTitle(),
                    trendingPlaylistCacheList
                )
            )
        }
    }

    private fun translateValue(translationList: List<Translation>?): String? {
        val thaiLanguageValue = translationList?.firstOrNull { it.language == TH }?.value.orEmpty()
        val engLanguageValue = translationList?.firstOrNull { it.language == EN }?.value.orEmpty()

        if (localizationRepository.getAppLanguageCode() == LocalizationRepository.Localization.TH.languageCode &&
            thaiLanguageValue.isEmpty()
        ) {
            return null
        }

        return if (!translationList.isNullOrEmpty()) {
            localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = engLanguageValue
                    thWord = thaiLanguageValue
                    myWord = engLanguageValue
                }
            )
        } else {
            null
        }
    }

    private fun getTrendingPlaylistTitle(): String {
        return localizationRepository.localization(
            LocalizationModel().apply {
                enWord = KEY_EXTRA_TRENDING_PLAYLIST_EN
                thWord = KEY_EXTRA_TRENDING_PLAYLIST_TH
                myWord = KEY_EXTRA_TRENDING_PLAYLIST_EN
            }
        )
    }

    private fun getDataItemFromTrendingPlaylist(
        title: String,
        trendingPlaylistModel: List<TrendingPlaylistModel>?
    ): ArrayList<MusicSearchTrendingAdapter.DataItem>? {
        return if (!trendingPlaylistModel.isNullOrEmpty()) {
            arrayListOf<MusicSearchTrendingAdapter.DataItem>().apply {
                this.add(
                    MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem(
                        MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID,
                        title
                    )
                )
                this.add(
                    MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem(
                        MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID,
                        trendingPlaylistModel
                    )
                )
            }
        } else {
            null
        }
    }
}
