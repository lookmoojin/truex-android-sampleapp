package com.truedigital.features.music.presentation.landing

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.ifNotNullOrEmpty
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.music.domain.landing.model.MusicHeroBannerDeeplinkType
import com.truedigital.features.music.domain.landing.usecase.DecodeMusicHeroBannerDeeplinkUseCase
import com.truedigital.features.music.domain.landing.usecase.GetCacheMusicShelfDataUseCase
import com.truedigital.features.music.domain.landing.usecase.GetContentBaseShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetContentItemUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicBaseShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicForYouShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicUserByTagShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetRadioUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagAlbumShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagArtistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagPlaylistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTrackPlaylistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.SaveCacheMusicShelfDataUseCase
import com.truedigital.features.music.domain.model.ListenTopNavigationType
import com.truedigital.features.music.domain.queue.usecase.GetAllTrackQueueUseCase
import com.truedigital.features.music.domain.queue.usecase.GetCacheTrackQueueUseCase
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCaseImpl.Companion.EXTRA_EXTERNAL_BROWSER
import com.truedigital.features.music.navigation.router.MusicLandingToAlbum
import com.truedigital.features.music.navigation.router.MusicLandingToArtist
import com.truedigital.features.music.navigation.router.MusicLandingToExternalBrowser
import com.truedigital.features.music.navigation.router.MusicLandingToPlaylist
import com.truedigital.features.music.navigation.router.MusicLandingToSeeAll
import com.truedigital.features.tuned.data.productlist.model.ProductListType.CONTENT
import com.truedigital.features.tuned.data.productlist.model.ProductListType.TAGGED_ALBUMS
import com.truedigital.features.tuned.data.productlist.model.ProductListType.TAGGED_ARTISTS
import com.truedigital.features.tuned.data.productlist.model.ProductListType.TAGGED_PLAYLISTS
import com.truedigital.features.tuned.data.productlist.model.ProductListType.TAGGED_RADIO
import com.truedigital.features.tuned.data.productlist.model.ProductListType.TAGGED_USER
import com.truedigital.features.tuned.data.productlist.model.ProductListType.TRACKS_PLAYLIST
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.playlist.presenter.PlaylistPresenter
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.foundation.extension.SingleLiveEvent
import com.truedigital.navigation.domain.usecase.SetRouterSecondaryToNavControllerUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MusicLandingViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val decodeMusicHeroBannerDeeplinkUseCase: DecodeMusicHeroBannerDeeplinkUseCase,
    private val router: MusicRouterUseCase,
    private val saveCacheMusicShelfDataUseCase: SaveCacheMusicShelfDataUseCase,
    private val getCacheMusicShelfDataUseCase: GetCacheMusicShelfDataUseCase,
    private val getContentBaseShelfUseCase: GetContentBaseShelfUseCase,
    private val getContentItemUseCase: GetContentItemUseCase,
    private val getMusicBaseShelfUseCase: GetMusicBaseShelfUseCase,
    private val getMusicForYouShelfUseCase: GetMusicForYouShelfUseCase,
    private val getTagAlbumShelfUseCase: GetTagAlbumShelfUseCase,
    private val getTagArtistShelfUseCase: GetTagArtistShelfUseCase,
    private val getTagPlaylistShelfUseCase: GetTagPlaylistShelfUseCase,
    private val getTrackPlaylistShelfUseCase: GetTrackPlaylistShelfUseCase,
    private val getMusicUserByTagShelfUseCase: GetMusicUserByTagShelfUseCase,
    private val getAllTrackQueueUseCase: GetAllTrackQueueUseCase,
    private val getRadioUseCase: GetRadioUseCase,
    private val getCacheTrackQueueUseCase: GetCacheTrackQueueUseCase,
    private val setRouterSecondaryToNavControllerUseCase: SetRouterSecondaryToNavControllerUseCase,
) : ScopedViewModel() {
    private val shelfStack = arrayListOf<Int>()

    private val showLoading = SingleLiveEvent<Unit>()
    private val showError = SingleLiveEvent<Unit>()
    private val showRadioError = SingleLiveEvent<Unit>()
    private val pausePlayer = SingleLiveEvent<Unit>()
    private val showShelf = MutableLiveData<MutableList<MusicForYouShelfModel>>()
    private val getTrack = SingleLiveEvent<Int>()
    private val getTrackPosition = SingleLiveEvent<Int>()
    private val playTrackList = SingleLiveEvent<List<Track>>()
    private val updateTrackList = SingleLiveEvent<List<Track>>()
    private val scrollToTopPage = SingleLiveEvent<Unit>()
    private val playRadio = SingleLiveEvent<MusicForYouItemModel.RadioShelfItem>()
    private val renderContentBySlug = SingleLiveEvent<String>()

    fun onShowLoading(): LiveData<Unit> = showLoading
    fun onShowError(): LiveData<Unit> = showError
    fun onShowRadioError(): LiveData<Unit> = showRadioError
    fun onPausePlayer(): LiveData<Unit> = pausePlayer
    fun onShowShelf(): LiveData<MutableList<MusicForYouShelfModel>> = showShelf
    fun onGetTrack(): LiveData<Int> = getTrack
    fun onTrackPosition(): LiveData<Int> = getTrackPosition
    fun onPlayTrackList(): LiveData<List<Track>> = playTrackList
    fun onUpdateTrackList(): LiveData<List<Track>> = updateTrackList
    fun onScrollToTopPage(): LiveData<Unit> = scrollToTopPage
    fun onPlayRadio(): LiveData<MusicForYouItemModel.RadioShelfItem> = playRadio
    fun onRenderContentBySlug(): LiveData<String> = renderContentBySlug

    fun loadData(baseShelfId: String?, shelfSlug: String?) {
        clearShelfData()
        getCacheMusicShelfDataUseCase.execute(baseShelfId)?.let { (_, shelves) ->
            if (shelves.isNotEmpty()) {
                showShelf.value = shelves
            } else {
                showError.value = Unit
            }
        } ?: run {
            loadDataFromApi(baseShelfId, shelfSlug)
        }
    }

    fun setShelfData(musicForYouShelfModel: MusicForYouShelfModel) {
        if (musicForYouShelfModel.itemList.isNotEmpty()) {
            val shelfList = showShelf.value ?: mutableListOf()
            shelfList.add(musicForYouShelfModel)

            showShelf.value = shelfList.toMutableList()
        }
    }

    fun handlerNavigateWithBundle(contentId: String, contentType: String) {
        if (contentId.isNotEmpty() && contentType.isNotEmpty()) {
            routeToNextPage(contentId = contentId, shelfType = contentType)
        }
    }

    fun performClickItem(itemModel: MusicForYouItemModel) {
        when (itemModel) {
            is MusicForYouItemModel.AlbumShelfItem -> {
                routeToAlbum(itemModel.albumId)
            }
            is MusicForYouItemModel.ArtistShelfItem -> {
                routeToArtist(itemModel.artistId)
            }
            is MusicForYouItemModel.PlaylistShelfItem -> {
                routeToPlaylist(itemModel.playlistId)
            }
            is MusicForYouItemModel.MusicHeroBannerShelfItem -> {
                routeToDeeplink(itemModel.deeplinkPair)
            }
            is MusicForYouItemModel.TrackPlaylistShelf -> {
                getTrackPosition.value = itemModel.index
                getCacheTrackQueueUseCase.execute(itemModel.playlistId)
                    .also { (trackList, isLoadMore) ->
                        playTrackList.value = trackList
                        if (isLoadMore) {
                            loadAllTrackQueue(itemModel.playlistId)
                        }
                    }
            }
            is MusicForYouItemModel.RadioShelfItem -> {
                playRadio.value = itemModel
            }
            else -> {
                // nothing
            }
        }
    }

    fun performClickSeeAll(shelfModel: MusicForYouShelfModel) {
        when (shelfModel.productListType) {
            TAGGED_ALBUMS,
            TAGGED_ARTISTS,
            TAGGED_PLAYLISTS,
            -> {
                router.execute(
                    MusicLandingToSeeAll,
                    getBundleSeeAll(shelfModel),
                )
            }
            TAGGED_RADIO -> {
                val deeplinkType = shelfModel.seeMoreDeeplinkPair?.first
                if (deeplinkType == MusicHeroBannerDeeplinkType.SEE_MORE_RADIO) {
                    renderContentBySlug.value = MusicConstant.Type.RADIO
                }
            }
            TRACKS_PLAYLIST -> {
                shelfModel.options?.let {
                    routeToPlaylist(it.playlistId.toInt())
                }
            }
            else -> {
                // nothing
            }
        }
    }

    fun clearShelfData() {
        showShelf.value?.clear()
    }

    fun setRouterSecondaryToNavController(navController: NavController) {
        setRouterSecondaryToNavControllerUseCase.execute(navController = navController)
    }

    private fun loadDataFromApi(baseShelfId: String?, shelfSlug: String?) {
        if (baseShelfId.isNullOrEmpty().not()) {
            if (shelfSlug == ListenTopNavigationType.FOR_YOU.value) {
                loadForYouBaseShelf(baseShelfId.orEmpty())
            } else {
                loadContentBaseShelf(baseShelfId.orEmpty())
            }
        } else {
            showError.value = Unit
        }
    }

    private fun loadForYouBaseShelf(baseShelfId: String) = launchSafe {
        getMusicBaseShelfUseCase.execute(baseShelfId)
            .flowOn(coroutineDispatcher.io())
            .onStart {
                showLoading.value = Unit
            }
            .flatMapConcat { apiPath ->
                getMusicForYouShelfUseCase.execute(apiPath)
            }
            .flowOn(coroutineDispatcher.io())
            .catch {
                onLoadBaseShelfFail(baseShelfId)
            }
            .map { shelfList ->
                shelfList.forEachIndexed { index, musicForYouShelfModel ->
                    musicForYouShelfModel.shelfIndex = index
                }
                shelfList
            }.collectSafe { shelfList ->
                if (shelfList.isNotEmpty()) {
                    shelfList.forEach { musicForYouShelfModel ->
                        loadItem(baseShelfId, musicForYouShelfModel)
                    }
                } else {
                    onLoadBaseShelfFail(baseShelfId)
                }
            }
    }

    private fun loadContentBaseShelf(baseShelfId: String) = launchSafe {
        getContentBaseShelfUseCase.execute(baseShelfId)
            .flowOn(coroutineDispatcher.io())
            .onStart {
                showLoading.value = Unit
            }
            .catch {
                onLoadBaseShelfFail(baseShelfId)
            }
            .collectSafe { shelfList ->
                if (shelfList.isNotEmpty()) {
                    shelfList.forEach { musicForYouShelfModel ->
                        loadItem(baseShelfId, musicForYouShelfModel)
                    }
                } else {
                    onLoadBaseShelfFail(baseShelfId)
                }
            }
    }

    private fun onLoadBaseShelfFail(baseShelfId: String) {
        saveCacheMusicShelfDataUseCase.execute(baseShelfId, mutableListOf())
        showError.value = Unit
    }

    private fun routeToNextPage(shelfType: String, contentId: String) {
        when (shelfType) {
            MusicConstant.Type.ALBUM -> {
                routeToAlbum(contentId.toIntOrNull() ?: 0)
            }
            MusicConstant.Type.ARTIST -> {
                routeToArtist(contentId.toIntOrNull() ?: 0)
            }
            MusicConstant.Type.PLAYLIST -> {
                routeToPlaylist(contentId.toIntOrNull() ?: 0)
            }
            MusicConstant.Type.SONG -> {
                contentId.toIntOrNull()?.let { getTrack.value = it }
            }
            MusicConstant.Type.RADIO -> {
                playRadio(contentId)
            }
        }
    }

    private fun loadItem(baseShelfId: String, musicForYouShelfModel: MusicForYouShelfModel) =
        launchSafe {
            getShelfUseCase(musicForYouShelfModel)
                .flowOn(coroutineDispatcher.io())
                .onStart {
                    shelfStack.add(musicForYouShelfModel.index)
                }
                .onCompletion {
                    checkShelfStack(baseShelfId)
                }.collectSafe {
                    setShelfData(musicForYouShelfModel.copy(itemList = it))
                }
        }

    private fun getShelfUseCase(model: MusicForYouShelfModel): Flow<List<MusicForYouItemModel>> {
        return when (model.productListType) {
            TAGGED_ALBUMS -> getTagAlbumShelfUseCase.execute(
                model.options?.tag.orEmpty(),
                model.options?.limit.orEmpty(),
            )
            TAGGED_ARTISTS -> getTagArtistShelfUseCase.execute(
                model.options?.tag.orEmpty(),
                model.options?.limit.orEmpty(),
            )
            TAGGED_PLAYLISTS -> getTagPlaylistShelfUseCase.execute(
                model.options?.tag.orEmpty(),
                model.options?.limit.orEmpty(),
            )
            TAGGED_USER -> getMusicUserByTagShelfUseCase.execute(model.options?.tag.orEmpty())
                .map { (itemList, productType, seeMoreDeeplink) ->
                    model.apply {
                        this.productListType = productType
                        this.seeMoreDeeplinkPair = decodeMusicHeroBannerDeeplinkUseCase.execute(
                            seeMoreDeeplink,
                        )
                    }
                    itemList
                }
            TRACKS_PLAYLIST -> getTrackPlaylistShelfUseCase.execute(
                model.options?.playlistId.orEmpty(),
                model.options?.limit.orEmpty(),
                model.options?.displayType.orEmpty(),
            )
            CONTENT -> getContentItemUseCase.execute(model.shelfId, model.shelfType)
            else -> {
                flow {
                    error("error : other productListType")
                }
            }
        }
    }

    private fun checkShelfStack(baseShelfId: String) {
        shelfStack.removeLastOrNull()
        if (shelfStack.size == 0 && (showShelf.value?.size ?: 0) == 0) {
            showError.value = Unit
        } else if (shelfStack.size == 0 && (showShelf.value?.size ?: 0) > 0) {
            saveCacheMusicShelfDataUseCase.execute(baseShelfId, showShelf.value ?: mutableListOf())
            scrollToTopPage.value = Unit
        }
    }

    private fun loadAllTrackQueue(playlistId: Int) {
        launchSafe {
            getAllTrackQueueUseCase.execute(playlistId)
                .flowOn(coroutineDispatcher.io())
                .collectSafe { trackList ->
                    if (trackList.isNotEmpty()) {
                        updateTrackList.value = trackList
                    }
                }
        }
    }

    private fun playRadio(id: String) {
        if (id.isNotEmpty()) {
            getRadio(id)
        } else {
            onRadioError()
        }
    }

    private fun getRadio(id: String) = launchSafe {
        getRadioUseCase.execute(id)
            .flowOn(coroutineDispatcher.io())
            .catch {
                onRadioError()
            }.collectSafe { radio ->
                radio?.let {
                    playRadio.value = it
                } ?: run {
                    onRadioError()
                }
            }
    }

    private fun onRadioError() {
        pausePlayer.value = Unit
        showRadioError.value = Unit
    }

    private fun getBundleSeeAll(
        shelfModel: MusicForYouShelfModel,
    ): Bundle {
        return Bundle().apply {
            putString(
                ProductListActivity.PRODUCT_LIST_TYPE_KEY,
                shelfModel.productListType.name,
            )
            putString(
                ProductListActivity.ACTIVITY_NAME_KEY,
                shelfModel.title,
            )
            putString(
                ProductListActivity.PRODUCT_LIST_TAG_KEY,
                shelfModel.options?.tag,
            )
            putString(
                ProductListActivity.TAG_DISPLAY_TYPE_KEY,
                shelfModel.options?.format,
            )
            putBoolean(
                ProductListActivity.DISPLAY_TITLE_KEY,
                shelfModel.options?.displayTitle ?: false,
            )
            putBoolean(
                ProductListActivity.TARGET_TIME_KEY,
                shelfModel.options?.targetTime ?: false,
            )
        }
    }

    private fun routeToPlaylist(id: Int) {
        router.execute(
            MusicLandingToPlaylist,
            Bundle().apply {
                putInt(PlaylistPresenter.PLAYLIST_ID_KEY, id)
            },
        )
    }

    private fun routeToAlbum(id: Int) {
        router.execute(
            MusicLandingToAlbum,
            Bundle().apply {
                putInt(AlbumPresenter.ALBUM_ID_KEY, id)
            },
        )
    }

    private fun routeToArtist(id: Int) {
        router.execute(
            MusicLandingToArtist,
            Bundle().apply {
                putInt(
                    ArtistPresenter.ARTIST_ID_KEY,
                    id,
                )
            },
        )
    }

    private fun routeToDeeplink(deeplinkPair: Pair<MusicHeroBannerDeeplinkType, String>) {
        when (deeplinkPair.first) {
            MusicHeroBannerDeeplinkType.ALBUM -> {
                routeToAlbum(deeplinkPair.second.toInt())
            }
            MusicHeroBannerDeeplinkType.ARTIST -> {
                routeToArtist(deeplinkPair.second.toInt())
            }
            MusicHeroBannerDeeplinkType.EXTERNAL_BROWSER -> {
                routeToExternalBrowser(deeplinkPair.second)
            }
            MusicHeroBannerDeeplinkType.INTERNAL_DEEPLINK -> {
                router.execute(deeplinkPair.second)
            }
            MusicHeroBannerDeeplinkType.RADIO -> {
                playRadio(deeplinkPair.second)
            }
            MusicHeroBannerDeeplinkType.SEE_MORE_RADIO -> {
                renderContentBySlug.value = MusicConstant.Type.RADIO
            }
            MusicHeroBannerDeeplinkType.PLAYLIST -> {
                routeToPlaylist(deeplinkPair.second.toInt())
            }
            MusicHeroBannerDeeplinkType.SONG -> {
                getTrack.value = deeplinkPair.second.toInt()
            }
        }
    }

    private fun routeToExternalBrowser(url: String) {
        url.ifNotNullOrEmpty {
            router.execute(
                MusicLandingToExternalBrowser,
                Bundle().apply {
                    putString(EXTRA_EXTERNAL_BROWSER, url)
                },
            )
        }
    }
}
