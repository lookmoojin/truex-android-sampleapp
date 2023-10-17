package com.truedigital.features.tuned.data.track.model

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.util.LocalisedString
import io.mockk.every
import io.mockk.mockkClass
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrackTest {

    private fun mockTrack(
        artistsList: List<ArtistInfo> = emptyList(),
        originalCredit: String? = null,
        releaseArtists: List<ArtistInfo> = emptyList(),
        video: Track? = null,
        isVideo: Boolean = false,
        translationsList: List<Translation> = emptyList(),
        owner: String? = null,
        label: String? = null,
        contentLanguage: String? = null,
        genreList: List<String>? = null,
        vote: Rating? = null
    ) = Track(
        id = 1,
        playlistTrackId = 1,
        songId = 1,
        releaseId = 1,
        artists = artistsList,
        name = "name",
        originalCredit = originalCredit,
        isExplicit = false,
        trackNumber = 1,
        trackNumberInVolume = 1,
        volumeNumber = 1,
        releaseArtists = releaseArtists,
        sample = "sample",
        isOnCompilation = false,
        releaseName = "releaseName",
        allowDownload = false,
        allowStream = false,
        duration = 3L,
        image = "image",
        hasLyrics = false,
        video = video,
        isVideo = isVideo,
        translationsList = translationsList,
        owner = owner,
        label = label,
        contentLanguage = contentLanguage,
        genreList = genreList,
        vote = vote,
        isDownloaded = false,
        syncProgress = 1F,
        isCached = false
    )

    @Test
    fun testTrackData_defaultData() {
        val mockTrack = mockTrack()
        assertEquals(1, mockTrack.id)
        assertEquals(1, mockTrack.playlistTrackId)
        assertEquals(1, mockTrack.songId)
        assertEquals(1, mockTrack.releaseId)
        assertEquals(0, mockTrack.artists.size)
        assertEquals("name", mockTrack.name)
        assertEquals(null, mockTrack.originalCredit)
        assertEquals(false, mockTrack.isExplicit)
        assertEquals(1, mockTrack.trackNumber)
        assertEquals(1, mockTrack.trackNumberInVolume)
        assertEquals(1, mockTrack.volumeNumber)
        assertEquals(0, mockTrack.releaseArtists.size)
        assertEquals("sample", mockTrack.sample)
        assertEquals(false, mockTrack.isOnCompilation)
        assertEquals("releaseName", mockTrack.releaseName)
        assertEquals(false, mockTrack.allowDownload)
        assertEquals(false, mockTrack.allowStream)
        assertEquals(3L, mockTrack.duration)
        assertEquals("image", mockTrack.image)
        assertEquals(false, mockTrack.hasLyrics)
        assertEquals(null, mockTrack.video)
        assertEquals(false, mockTrack.isVideo)

        assertEquals(0, mockTrack.translationsList.size)
        assertEquals(mockTrack.name, mockTrack.nameTranslations)
        assertEquals(null, mockTrack.owner)
        assertEquals(null, mockTrack.label)
        assertEquals(null, mockTrack.contentLanguage)
        assertEquals(null, mockTrack.genreList)

        assertEquals(null, mockTrack.vote)
        assertEquals(false, mockTrack.isDownloaded)
        assertEquals(1F, mockTrack.syncProgress)
        assertEquals(false, mockTrack.isCached)
    }

    @Test
    fun testTrackData_setData() {
        val mockTrack = mockTrack()
        mockTrack.id = 2
        mockTrack.playlistTrackId = 2
        mockTrack.songId = 2
        mockTrack.releaseId = 2
        mockTrack.artists = listOf(
            ArtistInfo(
                id = 2,
                name = "artistName"
            )
        )
        mockTrack.name = "name2"
        mockTrack.originalCredit = "originalCredit"
        mockTrack.isExplicit = true
        mockTrack.trackNumber = 2
        mockTrack.trackNumberInVolume = 2
        mockTrack.volumeNumber = 2
        mockTrack.releaseArtists = listOf(
            ArtistInfo(
                id = 2,
                name = "artistName"
            )
        )
        mockTrack.sample = "sample2"
        mockTrack.isOnCompilation = true
        mockTrack.releaseName = "releaseName2"
        mockTrack.allowDownload = true
        mockTrack.allowStream = true
        mockTrack.duration = 4L
        mockTrack.image = "image2"
        mockTrack.hasLyrics = true
        mockTrack.video = mockTrack()
        mockTrack.isVideo = true
        mockTrack.translationsList = listOf(
            Translation(
                language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                value = "nameTh"
            )
        )
        mockTrack.owner = "owner"
        mockTrack.label = "label"
        mockTrack.contentLanguage = "contentLanguage"
        mockTrack.genreList = listOf("genre1", "genre2")
        mockTrack.vote = Rating.LIKED
        mockTrack.isDownloaded = true
        mockTrack.syncProgress = 2F
        mockTrack.isCached = true

        assertEquals(2, mockTrack.id)
        assertEquals(2, mockTrack.playlistTrackId)
        assertEquals(2, mockTrack.songId)
        assertEquals(2, mockTrack.releaseId)
        assertEquals(1, mockTrack.artists.size)
        assertEquals("name2", mockTrack.name)
        assertEquals("originalCredit", mockTrack.originalCredit)
        assertEquals(true, mockTrack.isExplicit)
        assertEquals(2, mockTrack.trackNumber)
        assertEquals(2, mockTrack.trackNumberInVolume)
        assertEquals(2, mockTrack.volumeNumber)
        assertEquals(1, mockTrack.releaseArtists.size)
        assertEquals("sample2", mockTrack.sample)
        assertEquals(true, mockTrack.isOnCompilation)
        assertEquals("releaseName2", mockTrack.releaseName)
        assertEquals(true, mockTrack.allowDownload)
        assertEquals(true, mockTrack.allowStream)
        assertEquals(4L, mockTrack.duration)
        assertEquals("image2", mockTrack.image)
        assertEquals(true, mockTrack.hasLyrics)
        assertNotNull(mockTrack.video)
        assertEquals(true, mockTrack.isVideo)

        assertEquals(1, mockTrack.translationsList.size)
        assertEquals("nameTh", mockTrack.nameTranslations)
        assertEquals("owner", mockTrack.owner)
        assertEquals("label", mockTrack.label)
        assertEquals("contentLanguage", mockTrack.contentLanguage)
        assertEquals(2, mockTrack.genreList?.size)

        assertEquals(Rating.LIKED, mockTrack.vote)
        assertEquals(true, mockTrack.isDownloaded)
        assertEquals(2F, mockTrack.syncProgress)
        assertEquals(true, mockTrack.isCached)
    }

    @Test
    fun testTrackData_valueData() {
        val mockTrack = mockTrack(
            artistsList = listOf(
                ArtistInfo(
                    id = 2,
                    name = "artistName"
                )
            ),
            originalCredit = "originalCredit",
            releaseArtists = listOf(
                ArtistInfo(
                    id = 2,
                    name = "artistName"
                )
            ),
            video = mockTrack(),
            translationsList = listOf(
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                    value = "nameTh"
                )
            ),
            owner = "owner",
            label = "label",
            contentLanguage = "contentLanguage",
            genreList = listOf("genre1", "genre2"),
            vote = Rating.LIKED
        )
        assertEquals(1, mockTrack.artists.size)
        assertEquals("originalCredit", mockTrack.originalCredit)
        assertEquals(1, mockTrack.releaseArtists.size)
        assertNotNull(mockTrack.video)

        assertEquals(1, mockTrack.translationsList.size)
        assertEquals("nameTh", mockTrack.nameTranslations)
        assertEquals("owner", mockTrack.owner)
        assertEquals("label", mockTrack.label)
        assertEquals("contentLanguage", mockTrack.contentLanguage)
        assertEquals(2, mockTrack.genreList?.size)

        assertEquals(Rating.LIKED, mockTrack.vote)
    }

    @Test
    fun testNameTranslations_translationValueNull_returnName() {
        val mockTrack = mockTrack(
            translationsList = listOf(
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                    value = null
                )
            )
        )
        assertEquals(mockTrack.name, mockTrack.nameTranslations)
    }

    @Test
    fun testNameTranslations_translationValueEmpty_returnName() {
        val mockTrack = mockTrack(
            translationsList = listOf(
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                    value = ""
                )
            )
        )
        assertEquals(mockTrack.name, mockTrack.nameTranslations)
    }

    @Test
    fun testNameTranslations_translationEn_returnName() {
        val mockTrack = mockTrack(
            translationsList = listOf(
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_EN,
                    value = "nameEn"
                )
            )
        )
        assertEquals(mockTrack.name, mockTrack.nameTranslations)
    }

    @Test
    fun testNameTranslations_translationListMoreSize_returnName() {
        val mockTrack = mockTrack(
            translationsList = listOf(
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_EN,
                    value = "nameEn"
                ),
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                    value = "nameTh"
                )
            )
        )
        assertEquals("nameTh", mockTrack.nameTranslations)
    }

    @Test
    fun testGetArtistString_artistsListNotEmpty_returnArtistString() {
        val mockTrack = mockTrack(
            artistsList = listOf(
                ArtistInfo(
                    id = 1,
                    name = "artistName1"
                ),
                ArtistInfo(
                    id = 2,
                    name = "artistName2"
                )
            )
        )
        assertEquals("artistName1,artistName2", mockTrack.getArtistString("variousString"))
    }

    @Test
    fun testGetArtistString_artistsListEmpty_returnArtistString() {
        val mockTrack = mockTrack(
            artistsList = listOf()
        )
        val mockVariousString = "variousString"
        assertEquals(mockVariousString, mockTrack.getArtistString("variousString"))
    }

    @Test
    fun testGenreString_genreListNotEmpty_returnGenreString() {
        val mockTrack = mockTrack(
            genreList = listOf("genre1", "genre2")
        )
        assertEquals("genre1|genre2", mockTrack.genreString)
    }

    @Test
    fun testGenreString_genreListOneItem_returnGenreString() {
        val mockTrack = mockTrack(
            genreList = listOf("genre1")
        )
        assertEquals("genre1", mockTrack.genreString)
    }

    @Test
    fun testGenreString_genreListEmpty_returnEmptyGenreString() {
        val mockTrack = mockTrack(
            genreList = listOf()
        )
        assertEquals(" ", mockTrack.genreString)
    }

    @Test
    fun testGenreString_genreListNull_returnEmptyGenreString() {
        val mockTrack = mockTrack(
            genreList = null
        )
        assertEquals(" ", mockTrack.genreString)
    }

    @Test
    fun testPlayerSource_mapData() {
        val mockTrack = mockTrack(
            genreList = null,
            isVideo = false
        )
        assertEquals(mockTrack.id, mockTrack.sourceId)
        assertEquals(listOf(LocalisedString("en", mockTrack.image)), mockTrack.sourceImage)
        assertEquals(MediaType.SONGS.name, mockTrack.sourceType)
        assertNull(mockTrack.sourceAlbum)
        assertNull(mockTrack.sourceArtist)
        assertNull(mockTrack.sourceStation)
        assertNull(mockTrack.sourcePlaylist)
        assertEquals(mockTrack, mockTrack.sourceTrack)
        assertFalse(mockTrack.isOffline)
    }

    @Test
    fun testPlayerSource_isVideoTrue() {
        val mockTrack = mockTrack(
            genreList = null,
            isVideo = true
        )
        assertEquals(MediaType.VIDEO.name, mockTrack.sourceType)
    }

    @Test
    fun testResetPlayerSource_mapData() {
        val mockTrack = mockTrack(
            genreList = null,
            isVideo = false
        )
        mockTrack.resetPlayerSource(true)

        assertEquals(mockTrack.id, mockTrack.sourceId)
        assertEquals(listOf(LocalisedString("en", mockTrack.image)), mockTrack.sourceImage)
        assertEquals(MediaType.SONGS.name, mockTrack.sourceType)
        assertNull(mockTrack.sourceAlbum)
        assertNull(mockTrack.sourceArtist)
        assertNull(mockTrack.sourceStation)
        assertNull(mockTrack.sourcePlaylist)
        assertEquals(mockTrack, mockTrack.sourceTrack)
        assertTrue(mockTrack.isOffline)
    }

    @Test
    fun testResetPlayerSource_isVideoTrue() {
        val mockTrack = mockTrack(
            genreList = null,
            isVideo = true
        )
        mockTrack.resetPlayerSource(true)
        assertEquals(MediaType.VIDEO.name, mockTrack.sourceType)
        assertTrue(mockTrack.isOffline)
    }

    @Test
    fun testPlayerSource_default_returnNull() {
        val mockTrack = mockTrack(
            genreList = null,
            isVideo = true
        )
        assertNull(mockTrack.playerSource)
    }

    @Test
    fun testPlayerSource_setData_returnPlayerSource() {
        val mockTrack = mockTrack(
            genreList = null,
            isVideo = true
        )
        mockTrack.playerSource = mockTrack
        assertNotNull(mockTrack.playerSource)
    }

    @Test
    fun testGetDurationString_zero_returnOm() {
        val mockTrack = mockTrack()
        mockTrack.duration = 0L
        assertEquals("0m", mockTrack.getDurationString())
    }

    @Test
    fun testGetDurationString_minuteAndSecond_returnDurationString() {
        val mockTrack = mockTrack()
        mockTrack.duration = 281L

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(4) } returns StringBuilder("4")
        every { mockStringBuilder.append(41) } returns StringBuilder("41")
        every { mockStringBuilder.append("m") } returns StringBuilder("m")
        every { mockStringBuilder.append("s") } returns StringBuilder("s")

        assertEquals("4m41s", mockTrack.getDurationString())
    }

    @Test
    fun testGetDurationString_oneSecond_returnDurationString() {
        val mockTrack = mockTrack()
        mockTrack.duration = 241L

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(4) } returns StringBuilder("4")
        every { mockStringBuilder.append(1) } returns StringBuilder("1")
        every { mockStringBuilder.append("m") } returns StringBuilder("m")
        every { mockStringBuilder.append("s") } returns StringBuilder("s")

        assertEquals("4m1s", mockTrack.getDurationString())
    }

    @Test
    fun testGetDurationString_oneHour_returnDurationString() {
        val mockTrack = mockTrack()
        mockTrack.duration = 3600L

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(1) } returns StringBuilder("1")
        every { mockStringBuilder.append("h") } returns StringBuilder("h")

        assertEquals("1h", mockTrack.getDurationString())
    }

    @Test
    fun testFormattedDuration_minuteAndSecond_returnFormattedDuration() {
        val mockTrack = mockTrack()
        mockTrack.duration = 186L

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(3) } returns StringBuilder("3")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append(6) } returns StringBuilder("6")
        every { mockStringBuilder.append(":") } returns StringBuilder(":")

        assertEquals("3:06", mockTrack.formattedDuration)
    }

    @Test
    fun testFormattedDuration_secondZero_returnFormattedDuration() {
        val mockTrack = mockTrack()
        mockTrack.duration = 180L

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(3) } returns StringBuilder("3")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append(":") } returns StringBuilder(":")

        assertEquals("3:00", mockTrack.formattedDuration)
    }

    @Test
    fun testFormattedDuration_lessThanMinute_returnFormattedDuration() {
        val mockTrack = mockTrack()
        mockTrack.duration = 59L

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(59) } returns StringBuilder("59")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append(":") } returns StringBuilder(":")

        assertEquals("0:59", mockTrack.formattedDuration)
    }

    @Test
    fun testFormattedDuration_tenMinute_returnFormattedDuration() {
        val mockTrack = mockTrack()
        mockTrack.duration = 600L

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(10) } returns StringBuilder("10")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append(":") } returns StringBuilder(":")

        assertEquals("10:00", mockTrack.formattedDuration)
    }

    @Test
    fun testFormattedDuration_oneHour_returnFormattedDuration() {
        val mockTrack = mockTrack()
        mockTrack.duration = 3600L

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(1) } returns StringBuilder("1")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append(":") } returns StringBuilder(":")

        assertEquals("1:00:00", mockTrack.formattedDuration)
    }
}
