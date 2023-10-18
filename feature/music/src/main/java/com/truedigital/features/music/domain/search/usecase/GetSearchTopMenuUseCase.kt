package com.truedigital.features.music.domain.search.usecase

import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.domain.search.model.TopMenuModel
import com.truedigital.features.tuned.R
import javax.inject.Inject

interface GetSearchTopMenuUseCase {
    suspend fun execute(activePosition: Int = 0, theme: ThemeType): List<TopMenuModel>
}

class GetSearchTopMenuUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository
) : GetSearchTopMenuUseCase {

    override suspend fun execute(activePosition: Int, theme: ThemeType): List<TopMenuModel> {

        val menuSearchAll = TopMenuModel().apply {
            this.id = "1"
            this.name = localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = "All"
                    thWord = "ทั้งหมด"
                    myWord = "All"
                }
            )
            this.nameEn = "All"
            this.type = MusicType.ALL
            this.isActive = false
        }
        val menuSearchSong = TopMenuModel().apply {
            this.id = "2"
            this.name = localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = "Song"
                    thWord = "เพลง"
                    myWord = "Song"
                }
            )
            this.nameEn = "Song"
            this.type = MusicType.SONG
            this.isActive = false
        }
        val menuSearchArtist = TopMenuModel().apply {
            this.id = "3"
            this.name = localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = "Artist"
                    thWord = "ศิลปิน"
                    myWord = "Artist"
                }
            )
            this.nameEn = "Artist"
            this.type = MusicType.ARTIST
            this.isActive = false
        }
        val menuSearchAlbum = TopMenuModel().apply {
            this.id = "4"
            this.name = localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = "Album"
                    thWord = "อัลบั้ม"
                    myWord = "Album"
                }
            )
            this.nameEn = "Album"
            this.type = MusicType.ALBUM
            this.isActive = false
        }
        val menuSearchPlaylist = TopMenuModel().apply {
            this.id = "5"
            this.name = localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = "Playlist"
                    thWord = "เพลย์ลิสต์"
                    myWord = "Playlist"
                }
            )
            this.nameEn = "Playlist"
            this.type = MusicType.PLAYLIST
            this.isActive = false
        }

        val topMenuTextActiveColor = getTopMenuTextActiveColor(theme)
        val topMenuTextColorInactive = getTopMenuTextInactiveColor(theme)
        val topMenuButtonInactiveDrawable = getTopMenuButtonInactiveDrawable(theme)
        val topMenuButtonActiveDrawable = getTopMenuButtonActiveDrawable(theme)

        val topMenuList = listOf(
            menuSearchAll, menuSearchSong, menuSearchArtist, menuSearchAlbum, menuSearchPlaylist
        ).map { topMenu ->
            topMenu.textActiveColor = topMenuTextActiveColor
            topMenu.textInactiveColor = topMenuTextColorInactive
            topMenu.buttonActiveDrawable = topMenuButtonActiveDrawable
            topMenu.buttonInactiveDrawable = topMenuButtonInactiveDrawable
            topMenu
        }
        topMenuList.getOrNull(activePosition)?.isActive = true
        return topMenuList
    }

    private fun getTopMenuTextActiveColor(themeType: ThemeType): Int {
        return when (themeType) {
            ThemeType.LIGHT -> {
                R.color.white
            }
            ThemeType.DARK -> {
                R.color.white
            }
        }
    }

    private fun getTopMenuTextInactiveColor(themeType: ThemeType): Int {
        return when (themeType) {
            ThemeType.LIGHT -> {
                R.color.bg_text_tag_item_search
            }
            ThemeType.DARK -> {
                android.R.color.white
            }
        }
    }

    private fun getTopMenuButtonInactiveDrawable(themeType: ThemeType): Int {
        return when (themeType) {
            ThemeType.LIGHT -> {
                R.drawable.bg_button_round_inactive_light
            }
            ThemeType.DARK -> {
                R.drawable.bg_button_round_inactive_dark
            }
        }
    }

    private fun getTopMenuButtonActiveDrawable(themeType: ThemeType): Int {
        return when (themeType) {
            ThemeType.LIGHT -> {
                R.drawable.bg_button_round_red
            }
            ThemeType.DARK -> {
                R.drawable.bg_button_round_red
            }
        }
    }
}
