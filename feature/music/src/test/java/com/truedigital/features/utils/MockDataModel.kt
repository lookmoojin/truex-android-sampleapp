package com.truedigital.features.utils

import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailData
import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailResponse
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ad.model.AdProvider
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.device.model.Device
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.user.model.Gender
import com.truedigital.features.tuned.data.user.model.Settings
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.model.UserModel
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.data.util.PagedResults
import com.truedigital.features.tuned.injection.module.NetworkModule.Companion.HTTP_CODE_RESOURCE_NOT_FOUND
import com.truedigital.features.tuned.injection.module.NetworkModule.Companion.HTTP_CODE_UNAUTHORISED
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.util.Date

object MockDataModel {

    val mockArtist = Artist(
        id = 1,
        name = "artist_name",
        image = "artist_image"
    )

    val mockRelease = Release(
        id = 1,
        albumId = 1,
        artists = listOf(),
        name = "release_name",
        isExplicit = false,
        numberOfVolumes = 5,
        trackIds = listOf(),
        duration = 1000,
        volumes = listOf(),
        image = "release_image",
        webPath = "",
        copyRight = "",
        label = null,
        originalReleaseDate = null,
        physicalReleaseDate = null,
        digitalReleaseDate = null,
        saleAvailabilityDateTime = null,
        streamAvailabilityDateTime = null,
        allowDownload = true,
        allowStream = true
    )

    val mockAlbum = Album(
        id = 1,
        name = "album_name",
        artists = listOf(),
        primaryRelease = null,
        releaseIds = listOf()
    )

    val mockPlaylist = Playlist(
        id = 1,
        name = listOf(),
        description = listOf(),
        creatorId = 1,
        creatorName = "name",
        creatorImage = "image",
        trackCount = 10,
        publicTrackCount = 10,
        duration = 9999,
        createDate = Date(),
        updateDate = Date(),
        trackIds = listOf(),
        coverImage = listOf(),
        isVideo = false,
        isPublic = true,
        typedTags = null,
        isOwner = false
    )

    val mockTrack = Track(
        id = 1,
        playlistTrackId = 1,
        songId = 1,
        releaseId = 1,
        artists = listOf(),
        name = "name",
        originalCredit = "originalCredit",
        isExplicit = false,
        trackNumber = 1,
        trackNumberInVolume = 1,
        volumeNumber = 1,
        releaseArtists = listOf(),
        sample = "sample",
        isOnCompilation = false,
        releaseName = "releaseName",
        allowDownload = false,
        allowStream = true,
        duration = 3L,
        image = "image",
        hasLyrics = false,
        video = null,
        isVideo = false,
        vote = null,
        isDownloaded = false,
        syncProgress = 1F,
        isCached = false,
        translationsList = listOf(
            Translation(
                language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                value = "nameTh"
            )
        )
    )

    val mockStation = Station(
        id = 1,
        type = Station.StationType.PRESET,
        name = listOf(LocalisedString(language = "TH", value = "nameTh")),
        description = listOf(LocalisedString(language = "TH", value = "descriptionTh")),
        coverImage = listOf(LocalisedString(language = "TH", value = "coverImageTh")),
        bannerImage = listOf(LocalisedString(language = "TH", value = "bannerImageTh")),
        bannerURL = "bannerURL",
        isActive = true
    )

    val mockTag = Tag(
        id = 1234,
        name = "Tag",
        image = "tag_image",
        images = listOf(),
        displayName = listOf()
    )

    val mockPlaylistPage = PagedResults(
        offset = 1,
        count = 100,
        total = 1,
        results = listOf(mockPlaylist)
    )

    val mockMyPlaylist = listOf(
        MusicMyPlaylistModel.MyPlaylistModel(
            playlistId = 1,
            title = "title",
            coverImage = "coverImage",
            trackCount = 20
        )
    )

    val mockStakkar = Stakkar(
        id = 1,
        publisherImage = "publisherImage",
        publisherName = "publisherName",
        type = Stakkar.MediaType.AUDIO,
        links = listOf(),
        bannerUrl = null,
        bannerImage = null,
        hideDialog = false
    )

    val mockUserTuned = User(
        userId = 1,
        displayName = "Test",
        firstName = "Test",
        lastName = "Test",
        primaryEmail = "Email",
        isPrimaryEmailValidated = true,
        image = "image",
        backgroundImage = "image",
        followers = listOf(),
        following = listOf(),
        isPublic = true,
        optedIn = true,
        blocked = listOf(),
        language = "th",
        subscriptions = listOf(),
        devices = listOf(),
        isFacebookUser = true,
        circle = "",
        birthYear = 1,
        gender = "",
        logins = listOf(),
        action = "action",
        audioQuality = "audioQuality",
        contentLanguages = listOf(),
        country = "",
        isVerified = false,
        isTwitterUser = false
    )

    val mockSetting = Settings(
        allowStreams = false,
        limitSkips = false,
        adFirstMinutes = 1,
        adIntervalMinutes = 1,
        allowPurchase = false,
        allowAlbumNavigation = true,
        allowOffline = false,
        allowSync = false,
        syncCutOffDays = 1,
        maxSkipsPerHour = 1,
        limitPlays = false,
        monthlyPlayLimit = 1,
        adProvider = AdProvider.NONE,
        tracksPerAd = 1,
        interstitialId = "",
        facebookUrl = null,
        twitterUrl = null,
        instagramUrl = null,
        youtubeUrl = null,
        supportEmail = "",
        dmcaEnabled = false,
        offlineMaximumDuration = 0L
    )

    val mockDevice = Device(
        displayName = "Test",
        uniqueId = "uniqueId",
        token = "token",
        type = "type",
        operatingSystem = "operatingSystem",
        operatingSystemVersion = "1.0",
        appVersion = "2.41.0",
        country = "TH",
        language = "TH",
        manufacturer = "",
        timezoneOffset = 7,
        carrier = "",
        brand = "",
        referrer = ""
    )

    val mockTranslationTH = Translation(
        language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
        value = "thaiValue"
    )

    val mockConfiguration = Configuration(
        thumborUrl = "thumborUrl",
        servicesUrl = "servicesUrl",
        metadataUrl = "metadataUrl",
        authUrl = "authUrl",
        storeId = "storeId",
        applicationId = 100
    )

    val mockUser = User(
        userId = 1,
        displayName = "Test",
        firstName = "Test",
        lastName = "Test",
        primaryEmail = "Email",
        isPrimaryEmailValidated = true,
        image = "image",
        backgroundImage = "image",
        followers = listOf(),
        following = listOf(),
        isPublic = true,
        optedIn = true,
        blocked = listOf(),
        language = "th",
        subscriptions = listOf(),
        devices = listOf(),
        isFacebookUser = true,
        circle = "circle",
        birthYear = 1,
        gender = "",
        logins = listOf(),
        action = "action",
        audioQuality = "audioQuality",
        contentLanguages = listOf(),
        country = "",
        isVerified = false,
        isTwitterUser = false
    )

    val mockUserModel = UserModel(
        displayName = "displayName",
        firstName = "firstName",
        lastName = "lastName",
        email = "email",
        gender = Gender.MALE,
        age = "20"
    )

    val mockVote = Vote(
        id = 10,
        vote = "vote",
        type = "type",
        actionDate = Date()
    )

    val mockLikeTrack = LikedTrack(
        type = Rating.DISLIKED,
        track = mockTrack,
        artists = listOf(mockArtist)
    )

    val mockRadioShelf = MusicForYouItemModel.RadioShelfItem(
        index = 1,
        mediaAssetId = 1,
        radioId = "radioId",
        contentType = "radio",
        description = "description",
        thumbnail = "thumbnail",
        titleEn = "titleEn",
        titleTh = "titleTh",
        title = "title",
        viewType = "viewType",
        shelfType = MusicShelfType.VERTICAL,
        streamUrl = "streamUrl"
    )

    val mockPlaybackState = PlaybackState(
        trackId = 1,
        state = "state",
        fileSource = "fileSource",
        elapsedSeconds = 10L,
        source = "source",
        sourceId = 1,
        guid = "guid"
    )

    val mockContentDetailData = ContentDetailData().apply {
        this.id = "id"
        this.contentType = "contentType"
    }

    val mockContentDetailResponse = ContentDetailResponse().apply {
        this.code = 200
        this.data = mockContentDetailData
    }

    val mockHttpExceptionCodeResourceNotFound = HttpException(
        Response.error<Any>(
            HTTP_CODE_RESOURCE_NOT_FOUND,
            "".toResponseBody("application/json".toMediaTypeOrNull())
        )
    )

    val mockHttpExceptionCodeUnauthorised = HttpException(
        Response.error<Any>(
            HTTP_CODE_UNAUTHORISED,
            "".toResponseBody("application/json".toMediaTypeOrNull())
        )
    )
}
