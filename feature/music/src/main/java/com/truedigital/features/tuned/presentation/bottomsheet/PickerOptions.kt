package com.truedigital.features.tuned.presentation.bottomsheet

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.truedigital.features.tuned.R

enum class PickerOptions(
    @DrawableRes
    val resIcon: Int? = null,
    @StringRes
    val resLabel: Int,
    @StringRes
    val resContentDescription: Int? = null
) {
    SHARE(R.drawable.music_ic_share, R.string.bottom_sheet_share_label),
    ADD_TO_COLLECTION(
        R.drawable.music_ic_star_empty,
        R.string.bottom_sheet_add_collection_label,
        R.string.more_button_add_favorite
    ),
    ADD_TO_COLLECTION_DISABLED(
        R.drawable.music_ic_star_empty,
        R.string.bottom_sheet_add_collection_label
    ),
    REMOVE_FROM_COLLECTION(
        R.drawable.music_ic_star_ticked,
        R.string.bottom_sheet_remove_collection_label,
        R.string.more_button_remove_favorite
    ),
    FOLLOW(R.drawable.music_ic_star_empty, R.string.follow_title),
    FOLLOW_DISABLED(R.drawable.music_ic_star_empty, R.string.follow_title),
    UNFOLLOW(R.drawable.music_ic_star_ticked, R.string.unfollow_title),
    CLEAR_VOTE(R.drawable.music_ic_trash, R.string.bottom_sheet_clear_votes_label),
    CLEAR_VOTE_DISABLED(R.drawable.music_ic_trash, R.string.bottom_sheet_clear_votes_label),
    SHOW_ARTIST(
        R.drawable.music_ic_artist,
        R.string.bottom_sheet_show_artist_label,
        R.string.more_button_go_to_artist
    ),
    ADD_SONGS(R.drawable.music_ic_add_song, R.string.bottom_sheet_add_songs_label),
    EDIT_PLAYLIST(R.drawable.music_ic_drag_vertical, R.string.bottom_sheet_edit_playlist_label),
    CHANGE_PLAYLIST_NAME(
        R.drawable.music_ic_pencil,
        R.string.bottom_sheet_change_playlist_name_label
    ),
    REMOVE_PLAYLIST(
        R.drawable.music_ic_trash_thick,
        R.string.bottom_sheet_remove_playlist_label,
        R.string.more_button_delete_playlist
    ),
    ADD_TO_QUEUE(
        R.drawable.music_ic_add_toqueue,
        R.string.bottom_sheet_add_to_queue_label,
        R.string.more_button_add_to_queue
    ),
    REMOVE_FROM_QUEUE(R.drawable.music_ic_trash, R.string.bottom_sheet_remove_from_queue_label),
    ADD_TO_PLAYLIST(R.drawable.music_ic_playlist, R.string.bottom_sheet_add_to_playlist_label),
    ADD_TO_MY_PLAYLIST(
        R.drawable.music_ic_add_to_my_playlist,
        R.string.bottom_sheet_add_to_my_playlist_label
    ),
    REMOVE_FROM_PLAYLIST(
        R.drawable.music_ic_trash_thick,
        R.string.bottom_sheet_remove_from_playlist_label,
        R.string.more_button_remove_song
    ),
    SHOW_ALBUM(
        R.drawable.music_ic_album,
        R.string.bottom_sheet_show_album_label,
        R.string.more_button_go_to_album
    ),
    PLAYER_SETTINGS(
        R.drawable.music_ic_player_settings,
        R.string.bottom_sheet_player_settings_label
    ),
    PLAY_VIDEO(R.drawable.music_ic_video, R.string.bottom_sheet_play_video_label),
    DOWNLOAD(R.drawable.music_ic_download_sync, R.string.bottom_sheet_download_label),
    REMOVE_DOWNLOAD(R.drawable.music_ic_download_ok, R.string.bottom_sheet_remove_download_label),
    DOWNLOAD_IN_PROGRESS(R.drawable.music_ic_sync, R.string.bottom_sheet_downloading_label),
    MAKE_PUBLIC(R.drawable.music_ic_eye, R.string.bottom_sheet_make_public_label),
    MAKE_PRIVATE(R.drawable.music_ic_eye_off, R.string.bottom_sheet_make_private_label),
    REMOVE_FROM_HISTORY(
        R.drawable.music_ic_cross_round,
        R.string.bottom_sheet_remove_from_history_label
    ),
}
