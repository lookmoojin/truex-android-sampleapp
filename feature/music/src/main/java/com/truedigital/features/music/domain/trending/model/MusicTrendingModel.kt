package com.truedigital.features.music.domain.trending.model

abstract class TrendingModel {
    var id: Int? = null
    var name: String = ""
    var image: String = ""
}

class TrendingArtistModel : TrendingModel()

class TrendingPlaylistModel : TrendingModel()

class TrendingAlbumModel : TrendingModel() {
    var artistName: String = ""
}
