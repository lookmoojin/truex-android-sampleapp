package com.truedigital.features.tuned.presentation.tag.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.Constants.ALPHA_255F
import com.truedigital.features.tuned.common.extensions.alert
import com.truedigital.features.tuned.common.extensions.putExtras
import com.truedigital.features.tuned.common.extensions.startActivity
import com.truedigital.features.tuned.common.extensions.statusBarHeight
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.common.extensions.visibilityGone
import com.truedigital.features.tuned.common.extensions.windowWidth
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.databinding.ActivityTagBinding
import com.truedigital.features.tuned.domain.facade.tag.model.TagDisplayType
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.album.view.AlbumActivity
import com.truedigital.features.tuned.presentation.album.view.AlbumAdapter
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
import com.truedigital.features.tuned.presentation.artist.view.ArtistAdapter
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.HorizontalSpacingItemDecoration
import com.truedigital.features.tuned.presentation.components.LifecycleComponentActivity
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.playlist.presenter.PlaylistPresenter
import com.truedigital.features.tuned.presentation.playlist.view.PlaylistActivity
import com.truedigital.features.tuned.presentation.playlist.view.PlaylistAdapter
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.features.tuned.presentation.station.presenter.StationPresenter
import com.truedigital.features.tuned.presentation.station.view.StationActivity
import com.truedigital.features.tuned.presentation.station.view.StationAdapter
import com.truedigital.features.tuned.presentation.tag.presenter.TagPresenter
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import javax.inject.Inject
import kotlin.math.abs

class TagActivity :
    LifecycleComponentActivity(),
    TagPresenter.ViewSurface,
    TagPresenter.RouterSurface {

    companion object {
        private const val INT_3 = 3
        private const val INT_9 = 9
        private const val INT_13 = 13
    }

    @Inject
    lateinit var presenter: TagPresenter

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var config: Configuration

    private val binding: ActivityTagBinding by viewBinding(ActivityTagBinding::inflate)

    private var fadeDistance: Int = 0
    private var scrimColor = Color.TRANSPARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)

        presenter.onInject(this, this)
        with(binding) {
            val containerLp = tagContentContainer.layoutParams as FrameLayout.LayoutParams
            containerLp.bottomMargin =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) -statusBarHeight else 0

            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.navigationIcon?.colorFilter =
                PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

            scrimColor = ContextCompat.getColor(this@TagActivity, R.color.primary)
            var previousOffset = 0
            tagAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                    if (verticalOffset != previousOffset) {
                        var ratio = (abs(verticalOffset) - fadeDistance) /
                            (appBarLayout.totalScrollRange - fadeDistance).toFloat()
                        ratio = minOf(ratio, 1f)
                        ratio = maxOf(ratio, 0f)
                        val color = if (abs(verticalOffset) > fadeDistance) {
                            Color.argb(
                                (ratio * ALPHA_255F).toInt(),
                                Color.red(scrimColor),
                                Color.green(scrimColor),
                                Color.blue(scrimColor)
                            )
                        } else {
                            Color.TRANSPARENT
                        }

                        val drawable = ColorDrawable(color)
                        tagCollapsingToolbar.background = drawable

                        viewOverlay.alpha = 1 - ratio
                        tagContentContainer.alpha = 1 - ratio
                    }
                    previousOffset = verticalOffset
                }
            )

            // Artists
            rvArtists.layoutManager =
                LinearLayoutManager(this@TagActivity, LinearLayoutManager.HORIZONTAL, false)
            rvArtists.adapter = ArtistAdapter(
                imageManager,
                onPageLoadListener = { presenter.onLoadArtists() },
                onClickListener = { presenter.onArtistSelected(it) },
                onLongClickListener = {
                    BottomSheetProductPicker(this@TagActivity) {
                        itemType = ProductPickerType.ARTIST
                        product = it
                    }.show()
                }
            )
            rvArtists.addItemDecoration(HorizontalSpacingItemDecoration())
            rvArtists.isNestedScrollingEnabled = false

            // Albums
            rvAlbums.layoutManager =
                LinearLayoutManager(this@TagActivity, LinearLayoutManager.HORIZONTAL, false)
            rvAlbums.adapter = AlbumAdapter(
                imageManager, config.enableAlbumDetailedDescription,
                onPageLoadListener = { presenter.onLoadAlbums() },
                onClickListener = { presenter.onAlbumSelected(it) },
                onLongClickListener = {
                    BottomSheetProductPicker(this@TagActivity) {
                        itemType = ProductPickerType.ALBUM
                        product = it
                    }.show()
                }
            )
            rvAlbums.addItemDecoration(HorizontalSpacingItemDecoration())
            rvAlbums.isNestedScrollingEnabled = false

            // Playlists
            rvPlaylists.layoutManager =
                LinearLayoutManager(this@TagActivity, LinearLayoutManager.HORIZONTAL, false)
            rvPlaylists.adapter = PlaylistAdapter(
                imageManager,
                onPageLoadListener = { presenter.onLoadPlaylists() },
                onClickListener = { presenter.onPlaylistSelected(it) },
                onLongClickListener = {
                    BottomSheetProductPicker(this@TagActivity) {
                        itemType = ProductPickerType.PLAYLIST
                        product = it
                    }.show()
                }
            )
            rvPlaylists.addItemDecoration(HorizontalSpacingItemDecoration())
            rvPlaylists.isNestedScrollingEnabled = false

            // Stations
            rvStations.layoutManager =
                LinearLayoutManager(this@TagActivity, LinearLayoutManager.HORIZONTAL, false)
            rvStations.adapter = StationAdapter(
                imageManager,
                onPageLoadListener = { presenter.onLoadStations() },
                onClickListener = { presenter.onStationSelected(it) },
                onLongClickListener = {
                    BottomSheetProductPicker(this@TagActivity) {
                        itemType = ProductPickerType.MIX
                        product = it
                    }.show()
                }
            )
            rvStations.addItemDecoration(HorizontalSpacingItemDecoration())
            rvStations.isNestedScrollingEnabled = false

            artistsErrorRetryButton.onClick { presenter.onRetryTaggedArtist() }
            albumsErrorRetryButton.onClick { presenter.onRetryTaggedAlbum() }
            playlistErrorRetryButton.onClick { presenter.onRetryTaggedPlaylist() }
            stationsErrorRetryButton.onClick { presenter.onRetryTaggedStation() }

            artistsLayoutViewMore.setOnClickListener { presenter.onArtistSeeAllSelected() }
            albumsLayoutViewMore.onClick { presenter.onAlbumSeeAllSelected() }
            playlistsLayoutViewMore.onClick { presenter.onPlaylistSeeAllSelected() }
            stationsLayoutViewMore.onClick { presenter.onStationSeeAllSelected() }
        }
        lifecycleComponents.add(PresenterComponent(presenter))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ActivityCompat.finishAfterTransition(this)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    // region View Surface

    override fun showTagContent(
        tag: Tag,
        tagDisplayType: TagDisplayType,
        displayTitle: Boolean
    ) {
        supportActionBar?.title =
            if (displayTitle) tag.displayName.valueForSystemLanguage(this) else ""

        val backgroundWidth = windowWidth
        val backgroundHeight = when (tagDisplayType) {
            TagDisplayType.LANDSCAPE -> backgroundWidth * INT_9 / INT_13
            TagDisplayType.SQUARE -> backgroundWidth
        }
        binding.tagContentContainer.layoutParams.height = backgroundHeight
        binding.viewOverlay.layoutParams.height = if (displayTitle) backgroundHeight / INT_3 else 0

        (tag.images?.valueForSystemLanguage(this) ?: tag.image)?.let {
            imageManager.init(this)
                .load(it)
                .options(backgroundWidth, backgroundHeight)
                .intoBitmap { bitmap ->
                    scrimColor = Palette.Builder(bitmap).generate().getDarkMutedColor(scrimColor)
                    binding.tagContentContainer.background = BitmapDrawable(resources, it)
                }
        }
    }

    override fun showTagContentError() {
        alert {
            setMessage(R.string.error_loading_tag)
            setPositiveButton(R.string.dialog_ok) { _, _ ->
                ActivityCompat.finishAfterTransition(
                    this@TagActivity
                )
            }
            setCancelable(false)
        }
    }

    override fun showTaggedStationsLoading() {
        with(binding) {
            ivStationNavigationArrow.gone()
            tvSeeAllStation.gone()
            stationsLayoutViewMore.isClickable = false

            stationsError.gone()
            stationsProgressBar.visible()
            rvStations.gone()
        }
    }

    override fun showTaggedStations(stations: List<Station>, morePages: Boolean) {
        with(binding) {
            if (config.enableTextSeeAll) tvSeeAllStation.visible()
            else ivStationNavigationArrow.visible()
            stationsLayoutViewMore.isClickable = true

            stationsError.gone()
            stationsProgressBar.visibilityGone()
            rvStations.visible()

            val adapter = rvStations.adapter as StationAdapter
            adapter.items = stations
            adapter.morePages = morePages
        }
    }

    override fun hideStationsSection() {
        with(binding) {
            stationsLayoutBgContainer.gone()
            stationsLayoutViewMore.gone()
            stationsContainer.gone()
        }
    }

    override fun showStationsError() {
        with(binding) {
            ivStationNavigationArrow.gone()
            tvSeeAllStation.gone()
            stationsLayoutViewMore.isClickable = false

            stationsError.visible()
            stationsProgressBar.visibilityGone()
            rvStations.gone()
        }
    }

    override fun showTaggedAlbumsLoading() {
        with(binding) {
            ivAlbumNavigationArrow.gone()
            tvSeeAllAlbum.gone()
            albumsLayoutViewMore.isClickable = false

            albumsError.gone()
            albumsProgressBar.visible()
            rvAlbums.gone()
        }
    }

    override fun showTaggedAlbums(albums: List<Album>, morePages: Boolean) {
        with(binding) {
            if (config.enableTextSeeAll) tvSeeAllAlbum.visible()
            else ivAlbumNavigationArrow.visible()
            albumsLayoutViewMore.isClickable = true

            albumsError.gone()
            albumsProgressBar.visibilityGone()
            rvAlbums.visible()

            val adapter = rvAlbums.adapter as AlbumAdapter
            adapter.items = albums
            adapter.morePages = morePages
        }
    }

    override fun hideAlbumsSection() {
        with(binding) {
            albumsLayoutBgContainer.gone()
            albumsLayoutViewMore.gone()
            albumsContainer.gone()
        }
    }

    override fun showAlbumsError() {
        with(binding) {
            ivAlbumNavigationArrow.gone()
            tvSeeAllAlbum.gone()
            albumsLayoutViewMore.isClickable = false

            albumsError.visible()
            albumsProgressBar.visibilityGone()
            rvAlbums.gone()
        }
    }

    override fun showTaggedArtistsLoading() {
        with(binding) {
            ivArtistNavigationArrow.gone()
            tvSeeAllArtist.gone()
            artistsLayoutViewMore.isClickable = false

            artistsError.gone()
            artistsProgressBar.visible()
            rvArtists.gone()
        }
    }

    override fun showTaggedArtists(artists: List<Artist>, morePages: Boolean) {
        with(binding) {
            if (config.enableTextSeeAll) tvSeeAllArtist.visible()
            else ivArtistNavigationArrow.visible()
            artistsLayoutViewMore.isClickable = true

            artistsError.gone()
            artistsProgressBar.visibilityGone()
            rvArtists.visible()

            val adapter = rvArtists.adapter as ArtistAdapter
            adapter.items = artists
            adapter.morePages = morePages
        }
    }

    override fun hideArtistsSection() {
        with(binding) {
            artistsLayoutBgContainer.gone()
            artistsLayoutViewMore.gone()
            artistsContainer.gone()
        }
    }

    override fun showArtistsError() {
        with(binding) {
            ivArtistNavigationArrow.gone()
            tvSeeAllArtist.gone()
            artistsLayoutViewMore.isClickable = false

            artistsError.visible()
            artistsProgressBar.visibilityGone()
            rvArtists.gone()
        }
    }

    override fun showTaggedPlaylistsLoading() {
        with(binding) {
            ivPlaylistNavigationArrow.gone()
            tvSeeAllPlaylist.gone()
            playlistsLayoutViewMore.isClickable = false

            playlistsError.gone()
            progressBarPlaylist.visible()
            rvPlaylists.gone()
        }
    }

    override fun showTaggedPlaylists(playlists: List<Playlist>, morePages: Boolean) {
        with(binding) {
            if (config.enableTextSeeAll) tvSeeAllPlaylist.visible()
            else ivPlaylistNavigationArrow.visible()
            playlistsLayoutViewMore.isClickable = true

            playlistsError.gone()
            progressBarPlaylist.visibilityGone()
            rvPlaylists.visible()

            val adapter = rvPlaylists.adapter as PlaylistAdapter
            adapter.items = playlists
            adapter.morePages = morePages
        }
    }

    override fun hidePlaylistsSection() {
        with(binding) {
            playlistsLayoutBgContainer.gone()
            playlistsLayoutViewMore.gone()
            playlistsContainer.gone()
        }
    }

    override fun showPlaylistsError() {
        with(binding) {
            ivPlaylistNavigationArrow.gone()
            tvSeeAllPlaylist.gone()
            playlistsLayoutViewMore.isClickable = false

            playlistsError.visible()
            progressBarPlaylist.visibilityGone()
            rvPlaylists.gone()
        }
    }

    // endregion

    // region Router Surface

    override fun navigateToProductListWithTag(productType: ProductListType, tag: String?) {
        startActivity(ProductListActivity::class) {
            val activityTitle = when (productType) {
                ProductListType.TAGGED_PLAYLISTS -> getString(R.string.tag_screen_top_playlists)
                ProductListType.TAGGED_ARTISTS -> getString(R.string.tag_screen_featured_artists)
                ProductListType.TAGGED_ALBUMS -> getString(R.string.tag_screen_new_releases)
                ProductListType.TAGGED_STATIONS -> getString(R.string.tag_screen_trending_station)
                else -> ""
            }
            putExtras(
                ProductListActivity.PRODUCT_LIST_TYPE_KEY to productType.name,
                ProductListActivity.PRODUCT_LIST_TAG_KEY to tag,
                ProductListActivity.ACTIVITY_NAME_KEY to activityTitle
            )
        }
    }

    override fun navigateToTaggedAlbum(album: Album) {
        startActivity(AlbumActivity::class) {
            putExtras(AlbumPresenter.ALBUM_ID_KEY to album.id)
        }
    }

    override fun navigateToTaggedArtist(artist: Artist) {
        startActivity(ArtistActivity::class) {
            putExtras(ArtistPresenter.ARTIST_ID_KEY to artist.id)
        }
    }

    override fun navigateToTaggedPlaylist(playlist: Playlist) {
        startActivity(PlaylistActivity::class) {
            putExtras(PlaylistPresenter.PLAYLIST_ID_KEY to playlist.id)
        }
    }

    override fun navigateToTaggedStation(station: Station) {
        startActivity(StationActivity::class) {
            putExtras(StationPresenter.STATION_KEY to station)
        }
    }
}
