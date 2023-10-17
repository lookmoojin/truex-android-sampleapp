package com.truedigital.features.music.data.search.datasource

import com.nhaarman.mockitokotlin2.mock
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.domain.search.usecase.GetSearchAlbumUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchAllUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchArtistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchPlaylistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchSongUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class MusicSearchDataSourceFactoryTest {

    private lateinit var musicSearchDataSourceFactory: MusicSearchDataSourceFactory
    private val getSearchAllUseCase: GetSearchAllUseCase = mock()
    private val getSearchAlbumUseCase: GetSearchAlbumUseCase = mock()
    private val getSearchArtistUseCase: GetSearchArtistUseCase = mock()
    private val getSearchPlaylistUseCase: GetSearchPlaylistUseCase = mock()
    private val getSearchSongUseCase: GetSearchSongUseCase = mock()
    private val keyword: String = "keyword"
    private val theme: ThemeType = mock()
    private val type: MusicType = mock()

    @BeforeEach
    fun setup() {
        musicSearchDataSourceFactory = MusicSearchDataSourceFactory(
            getSearchAllUseCase,
            getSearchAlbumUseCase,
            getSearchArtistUseCase,
            getSearchPlaylistUseCase,
            getSearchSongUseCase,
            keyword,
            theme,
            type
        )
    }

    @Test
    fun testOnCreate_returnDatasource() {
        musicSearchDataSourceFactory.create()
        assertNotNull(musicSearchDataSourceFactory.getDatasource())
    }

    @Test
    fun testGetDatasource_returnDatasource() {
        assertNotNull(musicSearchDataSourceFactory.getDatasource())
    }
}
