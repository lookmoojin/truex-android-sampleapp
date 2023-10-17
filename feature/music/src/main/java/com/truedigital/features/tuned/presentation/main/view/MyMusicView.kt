package com.truedigital.features.tuned.presentation.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.extensions.putExtras
import com.truedigital.features.tuned.common.extensions.startActivity
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.databinding.ViewMyMusicBinding
import com.truedigital.features.tuned.presentation.components.LifecycleComponentView
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.main.presenter.MyMusicPresenter
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.extension.visibleOrGone
import javax.inject.Inject

class MyMusicView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LifecycleComponentView(context, attrs),
    MyMusicPresenter.ViewSurface,
    MyMusicPresenter.RouterSurface {

    private val binding: ViewMyMusicBinding by lazy {
        ViewMyMusicBinding.inflate(LayoutInflater.from(context), this, false)
    }

    @Inject
    lateinit var presenter: MyMusicPresenter

    @Inject
    lateinit var config: Configuration

    init {
        MusicComponent.getInstance().getInstanceComponent().inject(this)

        addView(binding.root)
        presenter.onInject(this, this)

        with(binding) {
            albumItem.onClick { presenter.onFavouriteAlbumsSelected() }
            artistItem.onClick { presenter.onFollowedArtistsSelected() }
            playlistItem.onClick { presenter.onFavouritePlaylistsSelected() }
            songItem.onClick { presenter.onFavouriteSongsSelected() }

            albumContainer.visibleOrGone(config.enableFavourites && config.enableAlbum)
            artistContainer.visibleOrGone(config.enableFavourites && config.enableArtist)
            playlistContainer.visibleOrGone(config.enableFavourites && config.enablePlaylist)
            songContainer.visibleOrGone(config.enableFavourites && config.enableSongFavourite)
        }

        lifecycleComponents.add(PresenterComponent(presenter))
    }

    override fun showArtistCount(count: Int) = with(binding) {
        artistCountTextView.visible()
        artistCountTextView.text = context.getString(R.string.item_count_label, count)
    }

    override fun hideArtistCount() = with(binding) {
        artistCountTextView.gone()
    }

    override fun showAlbumCount(count: Int) = with(binding) {
        albumCountTextView.visible()
        albumCountTextView.text = context.getString(R.string.item_count_label, count)
    }

    override fun hideAlbumCount() = with(binding) {
        albumCountTextView.gone()
    }

    override fun showSongCount(count: Int) = with(binding) {
        songCountTextView.visible()
        songCountTextView.text = context.getString(R.string.item_count_label, count)
    }

    override fun hideSongCount() = with(binding) {
        songCountTextView.gone()
    }

    override fun showPlaylistCount(count: Int) = with(binding) {
        playlistCountTextView.visible()
        playlistCountTextView.text = context.getString(R.string.item_count_label, count)
    }

    override fun hidePlaylistCount() = with(binding) {
        playlistCountTextView.gone()
    }

    override fun refreshFavorite() {
        presenter.refresh()
    }

    override fun navigateToFollowedArtists() {
        context.startActivity(ProductListActivity::class) {
            putExtras(
                ProductListActivity.PRODUCT_LIST_TYPE_KEY to ProductListType.FOLLOWED_ARTISTS.name,
                ProductListActivity.ACTIVITY_NAME_KEY to context.getString(R.string.artists)
            )
        }
    }

    override fun navigateToFavouriteAlbums() {
        context.startActivity(ProductListActivity::class) {
            putExtras(
                ProductListActivity.PRODUCT_LIST_TYPE_KEY to ProductListType.FAV_ALBUMS.name,
                ProductListActivity.ACTIVITY_NAME_KEY to context.getString(R.string.albums)
            )
        }
    }

    override fun navigateToFavouritePlaylists() {
        context.startActivity(ProductListActivity::class) {
            putExtras(
                ProductListActivity.PRODUCT_LIST_TYPE_KEY to ProductListType.FAV_PLAYLISTS.name,
                ProductListActivity.ACTIVITY_NAME_KEY to context.getString(R.string.playlists)
            )
        }
    }

    override fun navigateToFavouriteSongs() {
        context.startActivity(ProductListActivity::class) {
            putExtras(
                ProductListActivity.PRODUCT_LIST_TYPE_KEY to ProductListType.FAV_SONGS.name,
                ProductListActivity.ACTIVITY_NAME_KEY to context.getString(R.string.songs),
                ProductListActivity.IS_GRID_KEY to false
            )
        }
    }
}
