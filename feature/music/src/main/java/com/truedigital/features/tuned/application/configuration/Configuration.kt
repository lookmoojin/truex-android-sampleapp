package com.truedigital.features.tuned.application.configuration

data class Configuration(
    val thumborUrl: String,
    val servicesUrl: String,
    val metadataUrl: String,
    val authUrl: String,
    val storeId: String,
    val applicationId: Int
) {
    companion object {
        const val APP_ID_TRUE = 38
    }

    val isTrue = applicationId == APP_ID_TRUE
    val enableMobileDataByDefault = true
    val enablePlaylist = true
    val enablePlaylistEditing = false
    val enableVideo = true
    val enableShare = false
    val enableFavourites = true
    val enableTabUnderline = false
    val enableArtistCount = false
    val enableAlbumDetailedDescription = true
    val enableTextSeeAll = true
    val enableShareAndFavIcon = false
    val enableRadioButton = false
    val enableHintOverlay = false
    val enableArtist = true
    val enableAlbum = true
    val enableSongFavourite = true
}
