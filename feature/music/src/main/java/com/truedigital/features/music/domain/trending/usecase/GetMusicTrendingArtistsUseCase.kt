package com.truedigital.features.music.domain.trending.usecase

import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import com.truedigital.features.music.data.trending.repository.MusicTrendingArtistCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingRepository
import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetMusicTrendingArtistsUseCase {
    fun execute(): Flow<ArrayList<MusicSearchTrendingAdapter.DataItem>?>
}

class GetMusicTrendingArtistsUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository,
    private val musicTrendingRepository: MusicTrendingRepository,
    private val musicTrendingArtistCacheRepository: MusicTrendingArtistCacheRepository
) : GetMusicTrendingArtistsUseCase {

    companion object {
        const val LIMIT_ITEM = 10
        const val KEY_EXTRA_TRENDING_ARTISTS_EN = "Trending Artists"
        const val KEY_EXTRA_TRENDING_ARTISTS_TH = "ศิลปินยอดนิยม"
    }

    override fun execute(): Flow<ArrayList<MusicSearchTrendingAdapter.DataItem>?> {
        val trendingArtistCacheList = musicTrendingArtistCacheRepository.loadCacheTrendingArtist()
        return if (trendingArtistCacheList == null) {
            musicTrendingRepository.getMusicTrendingArtists()
                .map { artistResponse ->
                    val trendingArtistsList = artistResponse?.results?.take(LIMIT_ITEM)?.map {
                        TrendingArtistModel().apply {
                            this.id = it.artistId
                            this.name = it.name.orEmpty()
                            this.image = it.image.orEmpty()
                        }
                    }
                    musicTrendingArtistCacheRepository.saveCacheTrendingArtist(
                        trendingArtistsList ?: emptyList()
                    )
                    getDataItemFromTrendingArtist(getTrendingArtistTitle(), trendingArtistsList)
                }.catch {
                    musicTrendingArtistCacheRepository.saveCacheTrendingArtist(emptyList())
                    emit(null)
                }
        } else {
            flowOf(
                getDataItemFromTrendingArtist(getTrendingArtistTitle(), trendingArtistCacheList)
            )
        }
    }

    private fun getTrendingArtistTitle(): String {
        return localizationRepository.localization(
            LocalizationModel().apply {
                enWord = KEY_EXTRA_TRENDING_ARTISTS_EN
                thWord = KEY_EXTRA_TRENDING_ARTISTS_TH
                myWord = KEY_EXTRA_TRENDING_ARTISTS_EN
            }
        )
    }

    private fun getDataItemFromTrendingArtist(
        title: String,
        trendingPlaylistModel: List<TrendingArtistModel>?
    ): ArrayList<MusicSearchTrendingAdapter.DataItem>? {

        return if (!trendingPlaylistModel.isNullOrEmpty()) {
            arrayListOf<MusicSearchTrendingAdapter.DataItem>().apply {
                this.add(
                    MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem(
                        MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID,
                        title
                    )
                )
                this.add(
                    MusicSearchTrendingAdapter.DataItem.TrendingArtistItem(
                        MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ARTIST_ID,
                        trendingPlaylistModel
                    )
                )
            }
        } else {
            null
        }
    }
}
