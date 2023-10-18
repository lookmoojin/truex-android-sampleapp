package com.truedigital.features.music.domain.search.usecase

import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import com.truedigital.features.music.data.search.model.response.Artist
import com.truedigital.features.music.data.search.model.response.Hit
import com.truedigital.features.music.data.search.model.response.MusicSearchResponse
import com.truedigital.features.music.data.search.model.response.MusicSearchResponseItem
import com.truedigital.features.music.data.search.repository.MusicSearchRepository
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.extensions.applyMusicItemTheme
import com.truedigital.features.tuned.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetSearchAllUseCase {
    fun execute(query: String, theme: ThemeType): Flow<List<MusicSearchModel>>
}

class GetSearchAllUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository,
    private val musicSearchRepository: MusicSearchRepository
) : GetSearchAllUseCase {

    companion object {
        private const val KEY_SONG = "song"
        private const val KEY_ARTIST = "artist"
        private const val KEY_ALBUM = "album"
        private const val KEY_PLAYLIST = "playlist"
    }

    override fun execute(query: String, theme: ThemeType): Flow<List<MusicSearchModel>> {

        return musicSearchRepository.getSearchQuery(query = query)
            .map { musicSearchResponse ->

                val musicList = mutableListOf<MusicSearchModel>()

                if (musicSearchResponse != null) {

                    val filterBySong = filterItemList(musicSearchResponse, KEY_SONG)
                    val filterByArtist = filterItemList(musicSearchResponse, KEY_ARTIST)
                    val filterByAlbum = filterItemList(musicSearchResponse, KEY_ALBUM)
                    val filterByPlaylist = filterItemList(musicSearchResponse, KEY_PLAYLIST)

                    val musicSortingList: MutableList<MusicSearchResponseItem> = mutableListOf()
                    musicSortingList.addAll(filterBySong)
                    musicSortingList.addAll(filterByArtist)
                    musicSortingList.addAll(filterByAlbum)
                    musicSortingList.addAll(filterByPlaylist)

                    for (i in musicSortingList.indices) {

                        val item = musicSortingList.getOrNull(i)

                        val key = item?.key

                        val headerModel = getHeaderItem(i, key, theme)

                        musicList.add(headerModel)

                        item?.results?.hits?.hits?.let { itemList ->
                            for (music in itemList) {
                                val musicItem = getMusicItem(key, music, theme)
                                musicList.add(musicItem)
                            }
                        }
                    }
                }
                musicList
            }
            .catch {
                emit(mutableListOf())
            }
    }

    private fun filterItemList(
        musicSearchResponse: MusicSearchResponse,
        keySong: String
    ): MutableList<MusicSearchResponseItem> {
        return musicSearchResponse.filter {
            it.key == keySong
        }.toMutableList()
    }

    private fun getMusicItem(
        key: String?,
        music: Hit,
        themeType: ThemeType
    ): MusicSearchModel.MusicItemModel {
        return when (key) {
            KEY_ARTIST -> {
                MusicSearchModel.MusicItemModel(
                    id = music.source?.id.toString(),
                    title = music.source?.name,
                    description = "",
                    thumb = music.source?.meta?.firstOrNull()?.image.orEmpty(),
                    type = MusicType.ARTIST,
                    musicTheme = applyMusicItemTheme(themeType)
                )
            }
            KEY_ALBUM -> {
                MusicSearchModel.MusicItemModel(
                    id = music.source?.id.toString(),
                    title = music.source?.name,
                    description = getArtistName(music.source?.artists),
                    thumb = music.source?.meta?.firstOrNull()?.image.orEmpty(),
                    type = MusicType.ALBUM,
                    musicTheme = applyMusicItemTheme(themeType)
                )
            }
            KEY_PLAYLIST -> {
                MusicSearchModel.MusicItemModel(
                    id = music.source?.id.toString(),
                    title = music.source?.name,
                    description = "",
                    thumb = music.source?.images?.firstOrNull()?.value.orEmpty(),
                    type = MusicType.PLAYLIST,
                    musicTheme = applyMusicItemTheme(themeType)
                )
            }
            else -> { // default song
                MusicSearchModel.MusicItemModel(
                    id = music.source?.id.toString(),
                    title = music.source?.nameTranslations,
                    description = getArtistName(music.source?.artists),
                    thumb = music.source?.meta?.firstOrNull()?.albumImage.orEmpty(),
                    type = MusicType.SONG,
                    musicTheme = applyMusicItemTheme(themeType)
                )
            }
        }
    }

    private fun getArtistName(artists: List<Artist>?): String? {
        return artists?.joinToString(", ") {
            it.name.orEmpty()
        }
    }

    private fun getHeaderItem(
        index: Int,
        key: String?,
        themeType: ThemeType
    ): MusicSearchModel.MusicHeaderModel {
        return when (key) {
            KEY_ARTIST -> {
                MusicSearchModel.MusicHeaderModel(
                    id = index.toString(),
                    title = localizationRepository.localization(
                        LocalizationModel().apply {
                            enWord = "Artists"
                            thWord = "ศิลปิน"
                            myWord = "Artists"
                        }
                    ),
                    type = MusicType.ARTIST,
                    textHeaderColor = getHeaderColor(themeType),
                    textSeemoreColor = getHeaderColor(themeType)
                )
            }
            KEY_ALBUM -> {
                MusicSearchModel.MusicHeaderModel(
                    id = index.toString(),
                    title = localizationRepository.localization(
                        LocalizationModel().apply {
                            enWord = "Albums"
                            thWord = "อัลบั้ม"
                            myWord = "Albums"
                        }
                    ),
                    type = MusicType.ALBUM,
                    textHeaderColor = getHeaderColor(themeType),
                    textSeemoreColor = getHeaderColor(themeType)
                )
            }
            KEY_PLAYLIST -> {
                MusicSearchModel.MusicHeaderModel(
                    id = index.toString(),
                    title = localizationRepository.localization(
                        LocalizationModel().apply {
                            enWord = "Playlists"
                            thWord = "เพลย์ลิสต์"
                            myWord = "Playlists"
                        }
                    ),
                    type = MusicType.PLAYLIST,
                    textHeaderColor = getHeaderColor(themeType),
                    textSeemoreColor = getHeaderColor(themeType)
                )
            }
            else -> { // default song
                MusicSearchModel.MusicHeaderModel(
                    id = index.toString(),
                    title = localizationRepository.localization(
                        LocalizationModel().apply {
                            enWord = "Songs"
                            thWord = "เพลง"
                            myWord = "Songs"
                        }
                    ),
                    type = MusicType.SONG,
                    textHeaderColor = getHeaderColor(themeType),
                    textSeemoreColor = getHeaderColor(themeType)
                )
            }
        }
    }

    private fun getHeaderColor(themeType: ThemeType): Int {
        return when (themeType) {
            ThemeType.LIGHT -> {
                R.color.discover_shelf_text
            }
            ThemeType.DARK -> {
                android.R.color.white
            }
        }
    }
}
