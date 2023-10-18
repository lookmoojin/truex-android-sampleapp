package com.truedigital.features.music.domain.trending.usecase

import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import com.truedigital.features.music.data.trending.repository.MusicTrendingAlbumCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingRepository
import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetMusicTrendingAlbumUseCase {
    fun execute(): Flow<ArrayList<MusicSearchTrendingAdapter.DataItem>?>
}

class GetMusicTrendingAlbumUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository,
    private val musicTrendingRepository: MusicTrendingRepository,
    private val musicTrendingAlbumCacheRepository: MusicTrendingAlbumCacheRepository
) : GetMusicTrendingAlbumUseCase {

    companion object {
        const val LIMIT_ITEM = 10
        const val KEY_EXTRA_TRENDING_ALBUM_EN = "Trending Album"
        const val KEY_EXTRA_TRENDING_ALBUM_TH = "อัลบั้มยอดนิยม"
    }

    override fun execute(): Flow<ArrayList<MusicSearchTrendingAdapter.DataItem>?> {
        val trendingAlbumCacheList = musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum()
        return if (trendingAlbumCacheList == null) {
            musicTrendingRepository.getMusicTrendingAlbum()
                .map { albumResponse ->
                    val trendingAlbumList = albumResponse?.results?.take(LIMIT_ITEM)?.map {
                        TrendingAlbumModel().apply {
                            this.id = it.albumId
                            this.name = it.name.orEmpty()
                            this.image = it.primaryRelease?.image ?: ""
                            this.artistName = it.artists.map { it.name }.joinToString()
                        }
                    }

                    musicTrendingAlbumCacheRepository.saveCacheTrendingAlbum(
                        trendingAlbumList ?: emptyList()
                    )
                    getDataItemFromTrendingAlbum(getTrendingAlbumTitle(), trendingAlbumList)
                }.catch {
                    musicTrendingAlbumCacheRepository.saveCacheTrendingAlbum(emptyList())
                    emit(null)
                }
        } else {
            flowOf(
                getDataItemFromTrendingAlbum(getTrendingAlbumTitle(), trendingAlbumCacheList)
            )
        }
    }

    private fun getTrendingAlbumTitle(): String {
        return localizationRepository.localization(
            LocalizationModel().apply {
                enWord = KEY_EXTRA_TRENDING_ALBUM_EN
                thWord = KEY_EXTRA_TRENDING_ALBUM_TH
                myWord = KEY_EXTRA_TRENDING_ALBUM_EN
            }
        )
    }

    private fun getDataItemFromTrendingAlbum(
        title: String,
        trendingAlbumModel: List<TrendingAlbumModel>?
    ): ArrayList<MusicSearchTrendingAdapter.DataItem>? {

        return if (!trendingAlbumModel.isNullOrEmpty()) {
            arrayListOf<MusicSearchTrendingAdapter.DataItem>().apply {
                this.add(
                    MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem(
                        MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ALBUM_ID,
                        title
                    )
                )
                this.add(
                    MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem(
                        MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ALBUM_ID,
                        trendingAlbumModel
                    )
                )
            }
        } else {
            null
        }
    }
}
