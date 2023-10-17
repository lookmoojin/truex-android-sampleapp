package com.truedigital.features.music.data.search.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.domain.search.usecase.GetSearchAlbumUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchAllUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchArtistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchPlaylistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchSongUseCase

class MusicSearchDataSourceFactory(
    private val getSearchAllUseCase: GetSearchAllUseCase,
    private val getSearchAlbumUseCase: GetSearchAlbumUseCase,
    private val getSearchArtistUseCase: GetSearchArtistUseCase,
    private val getSearchPlaylistUseCase: GetSearchPlaylistUseCase,
    private val getSearchSongUseCase: GetSearchSongUseCase,
    private val keyword: String,
    private val theme: ThemeType,
    private val type: MusicType
) : DataSource.Factory<Int, MusicSearchModel>() {

    private val dataSource = MutableLiveData<MusicSearchDataSource>()

    override fun create(): DataSource<Int, MusicSearchModel> {
        val musicSearchDataSource = MusicSearchDataSource(
            getSearchAllUseCase,
            getSearchAlbumUseCase,
            getSearchArtistUseCase,
            getSearchPlaylistUseCase,
            getSearchSongUseCase,
            keyword,
            theme,
            type
        )
        dataSource.postValue(musicSearchDataSource)
        return musicSearchDataSource
    }

    fun getDatasource(): LiveData<MusicSearchDataSource> {
        return dataSource
    }
}
