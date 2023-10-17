package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Label
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.album.model.Volume
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.playlist.model.PlaylistTrack
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.tag.model.TypedTag
import com.truedigital.features.tuned.data.tag.repository.TagRepository
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.domain.facade.tag.TagFacade
import com.truedigital.features.tuned.domain.facade.tag.TagFacadeImpl
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class TagFacadeImplTest {

    private lateinit var tagFacade: TagFacade
    private val tagRepository: TagRepository = mock()

    @BeforeEach
    fun setup() {
        tagFacade = TagFacadeImpl(tagRepository)
    }

    @Test
    fun testGetTagByNameWithNormalString() {
        // arrange
        val mockedData = Tag(
            id = 1,
            name = "nameTag",
            image = "image",
            images = listOf(LocalisedString(language = "languageImages", value = "valueImages")),
            displayName = listOf(
                LocalisedString(
                    language = "languageDisplayName",
                    value = "valueDisplayName"
                )
            )
        )
        val mockedResult = Single.just(
            mockedData
        )
        whenever(tagRepository.getTagByName("nameTag")).doReturn(mockedResult)

        // action
        val result = tagFacade.getTagByName("nameTag").test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }

    @Test
    fun testGetTagByNameWithNormalStringAndImagesNullDisplayNameEmpty() {
        // arrange
        val mockedData = Tag(
            id = 1,
            name = "nameTag",
            image = "image",
            images = null,
            displayName = emptyList()
        )
        val mockedResult = Single.just(
            mockedData
        )
        whenever(tagRepository.getTagByName("nameTag")).doReturn(mockedResult)

        // action
        val result = tagFacade.getTagByName("nameTag").test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }

    @Test
    fun testGetTagByNameWithNormalStringAndImagesEmptyDisplayNameEmpty() {
        // arrange
        val mockedData = Tag(
            id = 1,
            name = "nameTag",
            image = "image",
            images = emptyList(),
            displayName = listOf()
        )
        val mockedResult = Single.just(
            mockedData
        )
        whenever(tagRepository.getTagByName("nameTag")).doReturn(mockedResult)

        // action
        val result = tagFacade.getTagByName("nameTag").test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }

    @Test
    fun testGetTagByNameWithNormalStringAndOnlyImagesEmpty() {
        // arrange
        val mockedData = Tag(
            id = 1,
            name = "nameTag",
            image = "image",
            images = emptyList(),
            displayName = listOf(
                LocalisedString(
                    language = "languageDisplayName",
                    value = "valueDisplayName"
                )
            )
        )
        val mockedResult = Single.just(mockedData)
        whenever(tagRepository.getTagByName("nameTag")).doReturn(mockedResult)

        // action
        val result = tagFacade.getTagByName("nameTag").test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }

    @Test
    fun testGetTagByNameWithNormalStringAndOnlyImagesNull() {
        // arrange
        val mockedData = Tag(
            id = 1,
            name = "nameTag",
            image = "image",
            images = null,
            displayName = listOf(
                LocalisedString(
                    language = "languageDisplayName",
                    value = "valueDisplayName"
                )
            )
        )
        val mockedResult = Single.just(
            mockedData
        )
        whenever(tagRepository.getTagByName("nameTag")).doReturn(mockedResult)

        // action
        val result = tagFacade.getTagByName("nameTag").test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }

    @Test
    fun testLoadStationsWithTag() {
        // arrange
        val mockedData: List<Station> =
            listOf(
                Station(
                    id = 1,
                    type = Station.StationType.ARTIST,
                    description = listOf(LocalisedString(language = "language", value = "value")),
                    name = listOf(LocalisedString(language = "language", value = "value")),
                    coverImage = listOf(LocalisedString(language = "language", value = "value")),
                    bannerImage = listOf(LocalisedString(language = "language", value = "value")),
                    bannerURL = "bannerURL",
                    isActive = true
                )
            )

        val mockedResult = Single.just(
            mockedData
        )
        whenever(tagRepository.getStationsByTag("nameTag", 0, 10)).doReturn(mockedResult)

        // action
        val result = tagFacade.loadStationsWithTag("nameTag", 0, 10).test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }

    @Test
    fun testLoadArtistsWithTag() {
        // arrange
        val mockedData: List<Artist> =
            listOf(
                Artist(id = 1, name = "name", image = null)
            )

        val mockedResult = Single.just(
            mockedData
        )
        whenever(tagRepository.getArtistsByTag("nameTag", 0, 10)).doReturn(mockedResult)

        // action
        val result = tagFacade.loadArtistsWithTag("nameTag", 0, 10).test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }

    @Test
    fun testLoadAlbumWithTag() {
        // arrange
        val mockedData: List<Album> =
            listOf(
                Album(
                    id = 1,
                    name = "name",
                    artists = listOf(ArtistInfo(1, "name")),
                    primaryRelease = Release(
                        id = 1,
                        albumId = 1,
                        name = "name",
                        isExplicit = true,
                        artists = listOf(ArtistInfo(1, "name")),
                        numberOfVolumes = 2,
                        trackIds = listOf(1, 2, 3),
                        duration = 1,
                        volumes = listOf(Volume(1, 10)),
                        image = "image",
                        webPath = "webPath",
                        copyRight = "copyRight",
                        label = Label(1, "name"),
                        originalReleaseDate = Date(System.currentTimeMillis()),
                        physicalReleaseDate = Date(System.currentTimeMillis()),
                        digitalReleaseDate = Date(System.currentTimeMillis()),
                        saleAvailabilityDateTime = Date(System.currentTimeMillis()),
                        streamAvailabilityDateTime = Date(System.currentTimeMillis()),
                        allowDownload = true,
                        allowStream = true
                    ),
                    releaseIds = listOf(1, 2, 3)
                )
            )

        val mockedResult = Single.just(
            mockedData
        )
        whenever(tagRepository.getAlbumsByTag("nameTag", 0, 10)).doReturn(mockedResult)

        // action
        val result = tagFacade.loadAlbumsWithTag("nameTag", 0, 10).test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }

    @Test
    fun testLoadPlaylistsWithTag() {
        // arrange
        val mockedData: List<Playlist> =
            listOf(
                Playlist(
                    id = 1,
                    name = listOf(LocalisedString(language = "language", value = "value")),
                    description = listOf(LocalisedString(language = "language", value = "value")),
                    creatorId = 1,
                    creatorName = "creatorName",
                    creatorImage = "creatorImage",
                    trackCount = 10,
                    publicTrackCount = 10,
                    duration = 3000,
                    createDate = Date(System.currentTimeMillis()),
                    updateDate = Date(System.currentTimeMillis()),
                    trackIds = listOf(PlaylistTrack(1, 100)),
                    coverImage = listOf(LocalisedString(language = "language", value = "value")),
                    isVideo = true,
                    isPublic = true,
                    typedTags = listOf(
                        TypedTag(
                            "name",
                            listOf(LocalisedString(language = "language", value = "value"))
                        )
                    ),
                    isOwner = true
                )
            )

        val mockedResult = Single.just(mockedData)
        whenever(tagRepository.getPlaylistsByTag("nameTag", 0, 10, TrackRequestType.ALL))
            .doReturn(mockedResult)

        // action
        val result = tagFacade.loadPlaylistsWithTag("nameTag", 0, 10)
            .test()

        // assert
        result.assertComplete()
        result.assertValue {
            it == mockedData
        }
    }
}
