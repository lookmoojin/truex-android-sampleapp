package com.truedigital.features.tuned.presentation.productlist.view

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.common.share.componentv3.extension.showSnackBar
import com.truedigital.common.share.componentv3.widget.decoration.SpacingItemDecorator
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.extensions.actionBarHeight
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.common.extensions.putExtras
import com.truedigital.features.tuned.common.extensions.startActivity
import com.truedigital.features.tuned.common.extensions.startActivityForResult
import com.truedigital.features.tuned.common.extensions.statusBarHeight
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.databinding.ActivityProductListBinding
import com.truedigital.features.tuned.domain.facade.tag.model.TagDisplayType
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.album.view.AlbumActivity
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerCollectionStatus
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.presentation.components.LifecycleComponentActivity
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.playlist.presenter.PlaylistPresenter
import com.truedigital.features.tuned.presentation.playlist.view.PlaylistActivity
import com.truedigital.features.tuned.presentation.productlist.presenter.ProductListPresenter
import com.truedigital.features.tuned.presentation.station.presenter.StationPresenter
import com.truedigital.features.tuned.presentation.station.view.StationActivity
import com.truedigital.features.tuned.presentation.tag.presenter.TagPresenter
import com.truedigital.features.tuned.presentation.tag.view.TagActivity
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import javax.inject.Inject
import kotlin.math.ceil

class ProductListActivity :
    LifecycleComponentActivity(),
    ProductListPresenter.ViewSurface,
    ProductListPresenter.RouterSurface {

    companion object {
        const val PRODUCT_LIST_TYPE_KEY = "product_list_type"
        const val FAV_PRODUCT_REQUEST_CODE = 10000
        const val UPDATED_PRODUCT_ID_KEY = "updated_product_id"
        const val UPDATED_PRODUCT_COVER_KEY = "updated_product_cover"
        const val IS_GRID_KEY = "is_grid"
        const val NUM_COLUMNS_KEY = "num_columns"
        const val PRODUCT_LIST_TAG_KEY = "product_list_tag"
        const val PRODUCT_LIST_ID_KEY = "product_list_id"
        const val ACTIVITY_NAME_KEY = "activity_name"
        const val DISPLAY_TITLE_KEY = "display_title"
        const val TAG_DISPLAY_TYPE_KEY = "tag_display_type"
        const val TARGET_TIME_KEY = "targetTime"

        private val GRID_ONLY_TYPE = listOf(ProductListType.DISCOVER_BYTAG)
    }

    @Inject
    lateinit var presenter: ProductListPresenter

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var config: Configuration

    private val binding: ActivityProductListBinding by viewBinding(ActivityProductListBinding::inflate)

    private lateinit var productType: ProductListType
    private var btnChangeList: MenuItem? = null

    private var numColumns = 2
    private var isGridMode = true

    private var displayTagTitle = true
    private var tagDisplayType = TagDisplayType.LANDSCAPE

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window?.statusBarColor = ContextCompat.getColor(this, android.R.color.white)

        val typeString = intent.getStringExtra(PRODUCT_LIST_TYPE_KEY)
        productType = typeString?.let { ProductListType.valueOf(it) } ?: ProductListType.UNSUPPORTED

        isGridMode = intent.getBooleanExtra(IS_GRID_KEY, true)
        numColumns = intent.getIntExtra(NUM_COLUMNS_KEY, numColumns)
        displayTagTitle = intent.getBooleanExtra(DISPLAY_TITLE_KEY, true)
        tagDisplayType = TagDisplayType.getValue(intent.getStringExtra(TAG_DISPLAY_TYPE_KEY))

        // get Number of items that can fit in the screen by dividing screen height by item height
        val gridPageSize = getGridPageSize()
        val horizontalPageSize = getHorizontalPageSize()
        presenter.onInject(this, this, gridPageSize, horizontalPageSize)
        lifecycleComponents.add(PresenterComponent(presenter))

        initToolbar()
        initFavEmptyText()
        setViewMode()
        binding.retryButton.onClick { presenter.onRetryClicked() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_changing_list, menu)
        btnChangeList = menu.findItem(R.id.action_change_list)
        if (!isGridMode) btnChangeList?.setIcon(R.drawable.music_ic_grid)
        btnChangeList?.isVisible = !GRID_ONLY_TYPE.contains(productType)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ActivityCompat.finishAfterTransition(this)
            R.id.action_change_list -> {
                val adapter = (binding.productsRecyclerView.adapter as ProductListBaseAdapter)
                presenter.onChangeDisplayMode(adapter.morePages)
                if (isGridMode) {
                    item.setIcon(R.drawable.music_ic_list)
                } else {
                    item.setIcon(R.drawable.music_ic_grid)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FAV_PRODUCT_REQUEST_CODE -> {
                    val updatedId = data?.getIntExtra(UPDATED_PRODUCT_ID_KEY, -1) ?: -1
                    if (updatedId == -1) return
                    val adapter = (binding.productsRecyclerView.adapter as ProductListBaseAdapter)
                    val index = adapter.items.indexOfFirst { getItemId(it) == updatedId }
                    if (index == -1) return
                    val updatedCover = data?.getStringExtra(UPDATED_PRODUCT_COVER_KEY)
                    if (updatedCover != null) {
                        val item = adapter.items[index]
                        if (item is Playlist) {
                            item.coverImage = listOf(LocalisedString("en", updatedCover))
                            (adapter.items as MutableList)[index] = item
                            adapter.notifyItemChanged(index)
                            presenter.onListItemUpdated(index, item)
                        }
                    } else {
                        (adapter.items as MutableList).removeAt(index)
                        adapter.notifyItemRemoved(index)
                        presenter.onListItemRemoved(index)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val unfavouritedId = intent.getIntExtra(UPDATED_PRODUCT_ID_KEY, -1)
        if (unfavouritedId == -1) return
        val adapter = (binding.productsRecyclerView.adapter as ProductListBaseAdapter)
        val index = adapter.items.indexOfFirst { getItemId(it) == unfavouritedId }
        if (index == -1) return
        (adapter.items as MutableList).removeAt(index)
        adapter.notifyItemRemoved(index)
        presenter.onListItemRemoved(index)
    }

    private fun getGridPageSize(): Int {
        return ceil(
            (
                (Resources.getSystem().displayMetrics.heightPixels - statusBarHeight - actionBarHeight) /
                    resources.getDimension(R.dimen.product_list_item_min_height)
                ).toDouble()
        ).toInt() * numColumns
    }

    private fun getHorizontalPageSize(): Int {
        return ceil(
            (
                (Resources.getSystem().displayMetrics.heightPixels - statusBarHeight - actionBarHeight) /
                    resources.getDimension(R.dimen.my_music_item_min_height)
                ).toDouble()
        ).toInt()
    }

    private fun initToolbar() = with(binding) {
        val activityName = intent.getStringExtra(ACTIVITY_NAME_KEY).orEmpty()
        setSupportActionBar(viewToolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        viewToolbar.apply {
            titleTextView.text = activityName
            titleTextView.contentDescription = getString(R.string.music_see_all_textview_title)
            toolbar.navigationContentDescription = getString(R.string.music_see_all_button_back)
            toolbar.setPadding(
                0,
                0,
                resources.getDimensionPixelSize(R.dimen.margin_action_toolbar),
                0
            )
            toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(this@ProductListActivity, R.color.theme_color_black),
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    private fun getFavouriteTypeText(): String {
        return when (productType) {
            ProductListType.FAV_STATIONS -> {
                getString(R.string.stations)
            }

            ProductListType.FOLLOWED_ARTISTS -> {
                getString(R.string.artists)
            }

            ProductListType.FAV_ALBUMS -> {
                getString(R.string.albums)
            }

            ProductListType.FAV_PLAYLISTS -> {
                getString(R.string.playlists)
            }

            ProductListType.FAV_SONGS -> {
                getString(R.string.songs)
            }

            ProductListType.FAV_VIDEOS -> {
                getString(R.string.videos)
            }

            else -> {
                ""
            }
        }
    }

    private fun initFavEmptyText() {
        val favouriteTypeText = getFavouriteTypeText().lowercase()

        binding.emptyItemTitleTextView.text =
            getString(R.string.no_favourited_title, favouriteTypeText)
        binding.emptyItemDescriptionTextView.text =
            getString(R.string.no_favourited_description, favouriteTypeText)
    }

    // region viewSurface

    override fun changeView() {
        isGridMode = !isGridMode
        setViewMode()
    }

    override fun showProducts(products: List<Product>, morePages: Boolean) {
        binding.layoutLoadingError.gone()
        binding.productsRecyclerView.visible()
        binding.progressBar.gone()
        binding.emptyItemLayout.gone()
        btnChangeList?.isVisible = !GRID_ONLY_TYPE.contains(productType)

        val adapter = binding.productsRecyclerView.adapter as ProductListBaseAdapter
        adapter.morePages = morePages
        adapter.items = products
    }

    override fun showError() {
        binding.layoutLoadingError.visible()
        binding.productsRecyclerView.gone()
        binding.progressBar.gone()
        binding.emptyItemLayout.gone()
        btnChangeList?.isVisible = false
    }

    override fun setViewMode() {
        while (binding.productsRecyclerView.itemDecorationCount > 0) {
            binding.productsRecyclerView.removeItemDecorationAt(0)
        }

        if (isGridMode) {
            val layoutManager = GridLayoutManager(this, numColumns)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val adapter = binding.productsRecyclerView.adapter
                    if (adapter != null && adapter is ProductListGridAdapter) {
                        if (adapter.getItemViewType(position) == ProductListBaseAdapter.ITEM_TYPE_LOADING) {
                            return numColumns
                        }
                    }
                    return 1
                }
            }
            binding.productsRecyclerView.layoutManager = layoutManager
            val halfSpacing =
                resources.getDimensionPixelSize(R.dimen.product_list_grid_half_item_spacing)
            val bottomPadding =
                resources.getDimensionPixelSize(R.dimen.product_list_grid_bottom_padding)
            binding.productsRecyclerView.setPadding(
                halfSpacing,
                halfSpacing,
                halfSpacing,
                bottomPadding
            )
            binding.productsRecyclerView.addItemDecoration(
                SpacingItemDecorator(halfSpacing)
            )
            binding.productsRecyclerView.adapter = ProductListGridAdapter(
                imageManager,
                config.enableAlbumDetailedDescription,
                displayTagTitle,
                tagDisplayType,
                numColumns,
                onClickListener = {
                    presenter.onItemSelected(
                        it,
                        tagDisplayType,
                        displayTagTitle
                    )
                },
                onLongClickListener = { presenter.onItemLongClick(it) },
                onMoreSelectedListener = { presenter.onMoreSelected(it) },
                onPageLoadListener = { presenter.onLoadPage() }
            )
        } else {
            val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(this, R.drawable.separator)
                ?.let { dividerItemDecoration.setDrawable(it) }
            binding.productsRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.productsRecyclerView.addItemDecoration(dividerItemDecoration)
            binding.productsRecyclerView.setPadding(
                0,
                0,
                0,
                resources.getDimensionPixelSize(R.dimen.collapsed_player_height)
            )

            binding.productsRecyclerView.adapter = ProductListHorizontalAdapter(
                imageManager,
                config.enableAlbumDetailedDescription,
                onClickListener = {
                    presenter.onItemSelected(
                        it,
                        tagDisplayType,
                        displayTagTitle
                    )
                },
                onLongClickListener = { presenter.onItemLongClick(it) },
                onMoreSelectedListener = { presenter.onMoreSelected(it) },
                onPageLoadListener = { presenter.onLoadPage() }
            )
        }
    }

    override fun showLoading() {
        binding.layoutLoadingError.gone()
        binding.productsRecyclerView.gone()
        binding.progressBar.visible()
        binding.emptyItemLayout.gone()
        btnChangeList?.isVisible = false
    }

    override fun showFavEmpty() {
        binding.layoutLoadingError.gone()
        binding.productsRecyclerView.gone()
        binding.progressBar.gone()
        binding.emptyItemLayout.visible()
        btnChangeList?.isVisible = false
    }

    override fun isGridMode() = isGridMode

    override fun isFavActivity(): Boolean = productType in ProductListPresenter.FAV_PAGES

    override fun showBottomSheet(item: Product) {
        when (item) {
            is Artist ->
                BottomSheetProductPicker(this) {
                    itemType = ProductPickerType.ARTIST
                    product = item
                    isInCollectionStatus =
                        if (isFavActivity()) {
                            ProductPickerCollectionStatus.IN_COLLECTION
                        } else {
                            ProductPickerCollectionStatus.NONE
                        }
                    onInCollectionStatusChanged = { isInCollection, _ ->
                        if (!isInCollection) removeItemIfNeeded(item.id)
                    }
                    onFavoriteItemClick = { isFavourite, isSuccess ->
                        this@ProductListActivity.presenter.onFavoriteSelect(isFavourite, isSuccess)
                    }
                }.show()

            is Release,
            is Album ->
                BottomSheetProductPicker(this) {
                    itemType = ProductPickerType.ALBUM
                    product = item
                    isInCollectionStatus =
                        if (isFavActivity()) {
                            ProductPickerCollectionStatus.IN_COLLECTION
                        } else {
                            ProductPickerCollectionStatus.NONE
                        }
                    onInCollectionStatusChanged = { isInCollection, _ ->
                        if (!isInCollection) removeItemIfNeeded(item.id)
                    }
                    onOptionSelected = {
                        this@ProductListActivity.presenter.onMoreOptionSelected(it)
                        false
                    }
                    onFavoriteItemClick = { isFavourite, isSuccess ->
                        this@ProductListActivity.presenter.onFavoriteSelect(isFavourite, isSuccess)
                    }
                }.show()

            is Playlist ->
                BottomSheetProductPicker(this) {
                    itemType = ProductPickerType.PLAYLIST
                    product = item
                    isInCollectionStatus =
                        if (isFavActivity()) {
                            ProductPickerCollectionStatus.IN_COLLECTION
                        } else {
                            ProductPickerCollectionStatus.NONE
                        }
                    onInCollectionStatusChanged = { isInCollection, _ ->
                        if (!isInCollection) removeItemIfNeeded(item.id)
                    }
                    onOptionSelected = {
                        this@ProductListActivity.presenter.onMoreOptionSelected(it)
                        false
                    }
                    onFavoriteItemClick = { isFavourite, isSuccess ->
                        this@ProductListActivity.presenter.onFavoriteSelect(isFavourite, isSuccess)
                    }
                }.show()

            is Track -> {
                BottomSheetProductPicker(this) {
                    itemType = if (item.isVideo) ProductPickerType.VIDEO else ProductPickerType.SONG
                    product = item
                    fragmentManager = supportFragmentManager
                    isInCollectionStatus =
                        if (isFavActivity()) {
                            ProductPickerCollectionStatus.IN_COLLECTION
                        } else {
                            ProductPickerCollectionStatus.NONE
                        }
                    onInCollectionStatusChanged = { isInCollection, product ->
                        updateFavouriteItem(isInCollection, product)
                    }
                    onOptionSelected = {
                        this@ProductListActivity.presenter.onMoreOptionSelected(it)
                        false
                    }
                    onFavoriteItemClick = { isFavourite, isSuccess ->
                        this@ProductListActivity.presenter.onFavoriteSelect(isFavourite, isSuccess)
                    }
                }.show()
            }

            is Station ->
                BottomSheetProductPicker(this) {
                    itemType = ProductPickerType.MIX
                    product = item
                    isInCollectionStatus =
                        if (isFavActivity()) {
                            ProductPickerCollectionStatus.IN_COLLECTION
                        } else {
                            ProductPickerCollectionStatus.NONE
                        }
                    onInCollectionStatusChanged = { isInCollection, _ ->
                        if (!isInCollection) removeItemIfNeeded(item.id)
                    }
                }.show()
        }
    }

    // if is favourites page, add/remove item from list to keep list consistent with back end data.
    // For when favourite state is updated from player/bottomSheet
    fun updateFavouriteItem(isFavourite: Boolean, product: Product) {
        if (isFavourite)
            addItemIfNeeded(product)
        else
            removeItemIfNeeded(product.id)
    }

    private fun removeItemIfNeeded(itemId: Int, isOwner: Boolean? = null) {
        if (isFavActivity() || isOwner == true) {
            val adapter = (binding.productsRecyclerView.adapter as ProductListBaseAdapter)
            val index = adapter.items.indexOfFirst { getItemId(it) == itemId }
            if (index == -1) return
            (adapter.items as MutableList).removeAt(index)
            adapter.notifyItemRemoved(index)
            presenter.onListItemRemoved(index)
        }
    }

    private fun addItemIfNeeded(product: Product) {
        if (isFavPageForProduct(product)) {
            binding.layoutLoadingError.gone()
            binding.productsRecyclerView.visible()
            binding.progressBar.gone()
            binding.emptyItemLayout.gone()
            btnChangeList?.isVisible = true

            val adapter = (binding.productsRecyclerView.adapter as ProductListBaseAdapter)
            if (adapter.items.firstOrNull { it.id == product.id } == null) {
                (adapter.items as MutableList).add(0, product)
                adapter.notifyItemInserted(0)
                presenter.onItemAdded(product)
            }
        }
    }

    private fun isFavPageForProduct(product: Product): Boolean =
        when (product) {
            is Artist -> productType == ProductListType.FOLLOWED_ARTISTS
            is Release -> productType == ProductListType.FAV_ALBUMS
            is Playlist -> productType == ProductListType.FAV_PLAYLISTS
            is Station -> productType == ProductListType.FAV_STATIONS
            is Track -> productType == if (product.isVideo) ProductListType.FAV_VIDEOS else ProductListType.FAV_SONGS
            else -> false
        }

    override fun playVideo(video: Track) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(this@ProductListActivity.getMusicServiceIntent())
                        playVideo(video)
                    }
                }
                unbindService(this)
            }
        }
        bindServiceMusic(musicServiceConnection)
    }

    override fun playTracks(tracks: List<Track>, startIndex: Int) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(this@ProductListActivity.getMusicServiceIntent())
                        playTracks(
                            tracks,
                            startIndex = startIndex,
                            isOffline = false,
                            forceSequential = true
                        )
                    }
                }
                unbindService(this)
            }
        }
        bindServiceMusic(musicServiceConnection)
    }

    // endregion

    // region routerSurface

    override fun navigateToArtist(artist: Artist) {
        if (isFavActivity()) {
            startActivityForResult(ArtistActivity::class, FAV_PRODUCT_REQUEST_CODE) {
                putExtras(ArtistPresenter.ARTIST_KEY to artist)
            }
        } else {
            startActivity(ArtistActivity::class) {
                putExtras(ArtistPresenter.ARTIST_KEY to artist)
            }
        }
    }

    override fun navigateToAlbum(id: Int) {
        if (isFavActivity()) {
            startActivityForResult(AlbumActivity::class, FAV_PRODUCT_REQUEST_CODE) {
                putExtras(AlbumPresenter.ALBUM_ID_KEY to id)
            }
        } else {
            startActivity(AlbumActivity::class) {
                putExtras(AlbumPresenter.ALBUM_ID_KEY to id)
            }
        }
    }

    override fun navigateToPlaylist(id: Int) {
        if (isFavActivity()) {
            startActivityForResult(PlaylistActivity::class, FAV_PRODUCT_REQUEST_CODE) {
                putExtras(PlaylistPresenter.PLAYLIST_ID_KEY to id)
            }
        } else {
            startActivity(PlaylistActivity::class) {
                putExtras(PlaylistPresenter.PLAYLIST_ID_KEY to id)
            }
        }
    }

    override fun navigateToStation(station: Station) {
        if (isFavActivity()) {
            startActivityForResult(StationActivity::class, FAV_PRODUCT_REQUEST_CODE) {
                putExtras(StationPresenter.STATION_KEY to station)
            }
        } else {
            startActivity(StationActivity::class) {
                putExtras(StationPresenter.STATION_KEY to station)
            }
        }
    }

    override fun navigateToTag(
        tag: Tag,
        tagDisplayType: TagDisplayType,
        displayTitle: Boolean
    ) {
        startActivity(TagActivity::class) {
            putExtras(
                TagPresenter.TAG_KEY to tag.toString(),
                TagPresenter.TAG_DISPLAY_TYPE_KEY to tagDisplayType.name,
                TagPresenter.DISPLAY_TITLE_KEY to displayTitle
            )
        }
    }

    override fun showAddToQueueToast() {
        binding.root.showSnackBar(R.string.add_to_queue_success, SnackBarType.SUCCESS)
    }

    override fun showAddToFavoriteSuccessToast() {
        binding.root.showSnackBar(R.string.added_to_favorite, SnackBarType.SUCCESS)
    }

    override fun showAddToFavoriteFailToast() {
        binding.root.showSnackBar(R.string.error_added_to_favorite, SnackBarType.ERROR)
    }

    override fun showRemoveFromFavoriteSuccessToast() {
        binding.root.showSnackBar(R.string.removed_to_favorite, SnackBarType.SUCCESS)
    }

    override fun showRemoveFromFavoriteFailToast() {
        binding.root.showSnackBar(R.string.error_removed_to_favorite, SnackBarType.ERROR)
    }

    private fun getItemId(item: Any) =
        when (item) {
            is Artist -> item.id
            is Release -> item.id
            is Album -> item.id
            is Playlist -> item.id
            is Track -> item.id
            is Station -> item.id
            else -> -1
        }
}
