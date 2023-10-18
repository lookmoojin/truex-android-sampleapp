package com.truedigital.features.tuned.presentation.bottomsheet.view

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.addtomyplaylist.AddToMyPlaylistDialogFragment
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.Constants.INT_5
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.common.extensions.put
import com.truedigital.features.tuned.common.extensions.putExtras
import com.truedigital.features.tuned.common.extensions.share
import com.truedigital.features.tuned.common.extensions.startActivity
import com.truedigital.features.tuned.common.extensions.statusBarHeight
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.common.extensions.visibilityGone
import com.truedigital.features.tuned.common.extensions.windowHeight
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.profile.model.Profile
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.DialogBottomSheetProductBinding
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.album.view.AlbumActivity
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerCollectionStatus
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.presenter.BottomSheetProductPresenter
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.presentation.player.view.PlayerSettingActivity
import com.truedigital.features.tuned.presentation.popups.view.LoadingDialog
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import timber.log.Timber
import javax.inject.Inject

class BottomSheetProductPicker(
    context: Context,
    builder: BottomSheetProductPicker.() -> Unit
) : BottomSheetDialog(context),
    BottomSheetProductPresenter.ViewSurface,
    BottomSheetProductPresenter.RouterSurface {

    // mandatory field
    var itemType: ProductPickerType? = null

    // at least one of the following fields needs to be presented
    var product: Product? = null
    var itemId: Int? = null

    // optional field
    var isInCollectionStatus: ProductPickerCollectionStatus = ProductPickerCollectionStatus.NONE
    var onItemClicked: ((PickerOptions, ProductPickerType?, Product?) -> Unit)? = null
    var onOptionSelected: ((PickerOptions) -> Boolean)? = null

    // Click favorite by item
    var onFavoriteItemClick: ((isFavourited: Boolean, isSuccess: Boolean) -> Unit)? = null
    var onDismiss: ((Boolean) -> Unit)? = null // for video player bottomsheet and queue bottomsheet
    var onRemoveFromQueue: (() -> Unit)? = null // for queue bottomsheet
    var onInCollectionStatusChanged: ((isInCollection: Boolean, product: Product) -> Unit)? = null
    var fragmentManager: FragmentManager? = null

    private lateinit var binding: DialogBottomSheetProductBinding

    private var itemTitle: String? = null
    private var willNavigate = false

    init {
        builder()
        if (itemType == null) throw IllegalStateException("itemType required")
        if (product == null && itemId == null) throw IllegalStateException("source required")
        product?.let { itemId = it.id }
    }

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var presenter: BottomSheetProductPresenter

    @Inject
    lateinit var config: Configuration

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this@BottomSheetProductPicker)
        super.onCreate(savedInstanceState)

        binding = DialogBottomSheetProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            // this code is copied from #BottomSheetDialog.onCreate
            // which is suppose to happen after #setContentView
            window?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    // removes the system draw status bar background to allow bottomsheet under status bar
                    ((llBottomSheet.parent.parent.parent as View).layoutParams as FrameLayout.LayoutParams).topMargin =
                        -context.statusBarHeight
                    llBottomSheet.setPadding(0, context.statusBarHeight, 0, 0)
                }
                it.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            setTransparent()
            presenter.onInject(
                this@BottomSheetProductPicker,
                this@BottomSheetProductPicker,
                product,
                itemType
            )

            crossImageView.onClick { dismiss() }
            initRecyclerView()

            if (product == null) {
                llContentContainer.gone()
                progressBar.visible()
            } else
                showProduct()
        }
    }

    override fun onStart() {
        super.onStart()
        val haveCollectionStatus = isInCollectionStatus != ProductPickerCollectionStatus.NONE
        val bundle = Bundle().put(
            BottomSheetProductPresenter.PRODUCT_ID_KEY to itemId,
            BottomSheetProductPresenter.HAVE_COLLECTION_STATUS to haveCollectionStatus
        )
        presenter.onStart(bundle)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun dismiss() {
        onDismiss?.invoke(willNavigate)
        super.dismiss()
    }

    override fun showProduct(fetchedProduct: Product?) {
        fetchedProduct?.let { product = it }
        if (product == null) throw IllegalStateException("product required")

        binding.progressBar.visibilityGone()
        binding.llContentContainer.visible()

        presenter.adjustProductType()
        initInfo()
        initOptions()
    }

    private fun setTransparent() {
        // Changes the BottomSheet to be transparent
        val parentView = binding.llBottomSheet.parent as View
        parentView.setBackgroundColor(Color.TRANSPARENT)
        parentView.layoutParams.height = context.windowHeight
        BottomSheetBehavior.from(parentView).peekHeight = context.windowHeight
    }

    private fun initRecyclerView() = with(binding) {
        rvOptions.setHasFixedSize(true)
        rvOptions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvOptions.isNestedScrollingEnabled = false
        rvOptions.adapter = BottomSheetOptionsAdapter(useDarkTheme = true) { option ->
            onItemClicked?.invoke(option, itemType, product)?.let {
                when (option) {
                    PickerOptions.ADD_TO_COLLECTION,
                    PickerOptions.ADD_TO_MY_PLAYLIST,
                    PickerOptions.REMOVE_FROM_COLLECTION,
                    PickerOptions.SHOW_ARTIST,
                    PickerOptions.SHOW_ALBUM -> {
                        presenter.onOptionsSelected(option)
                        dismiss()
                    }

                    else -> {
                        // do nothing
                    }
                }
            } ?: run {
                // use default handler if listener is not set
                val optionHandled = onOptionSelected?.invoke(option) ?: false
                if (!optionHandled) presenter.onOptionsSelected(option)

                // track will not start download if dismiss here
                if (optionHandled || option != PickerOptions.DOWNLOAD) {
                    dismiss()
                }
            }

            willNavigate = when (option) {
                PickerOptions.SHOW_ARTIST,
                PickerOptions.SHOW_ALBUM,
                PickerOptions.PLAYER_SETTINGS -> true

                else -> false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initInfo() = with(binding) {
        val imageSize =
            context.resources.getDimensionPixelSize(R.dimen.bottom_sheet_title_image_size)
        val itemArt: String?

        when (itemType) {
            ProductPickerType.MIX -> {
                val station = product as Station

                itemId = station.id
                itemTitle = station.name.valueForSystemLanguage(context) ?: ""
                itemArt = station.coverImage.valueForSystemLanguage(context)

                titleTextView.text = itemTitle
                subTitleTextView.text = context.getString(R.string.station_description)
                explicitTextView.gone()
                extraInfoTextView.gone()
                productArtImageView.setImageResource(R.drawable.placeholder_station)
                productArtImageView.visible()
                productArtRoundImageView.gone()
                itemArt?.let {
                    imageManager.init(context)
                        .load(it)
                        .options(imageSize)
                        .intoRoundedCorner(productArtImageView)
                }
            }

            ProductPickerType.ARTIST -> {
                val artist = product as Artist

                itemId = artist.id
                itemTitle = artist.name
                itemArt = artist.image

                titleTextView.text = itemTitle
                subTitleTextView.text = context.getString(R.string.artist_label)
                explicitTextView.gone()
                extraInfoTextView.gone()
                productArtRoundImageView.setImageResource(R.drawable.placeholder_artist)
                productArtImageView.gone()
                productArtRoundImageView.visible()
                itemArt?.let {
                    imageManager.init(context)
                        .load(it)
                        .options(imageSize, mode = "smart")
                        .intoBitmap { productArtRoundImageView.setImageBitmap(it) }
                }
            }

            ProductPickerType.ALBUM -> {
                val album = if (product is Album) product as Album else null
                val release = if (product is Release) product as Release else null
                if (album == null && release == null)
                    throw IllegalStateException("product object does not fix product type")

                itemId = album?.id ?: release?.id
                itemTitle = album?.name ?: release?.name
                itemArt = album?.primaryRelease?.image ?: release?.image
                val numTracks = album?.primaryRelease?.trackIds?.size ?: release?.trackIds?.size
                val releaseYear = album?.primaryRelease?.releaseYear() ?: release?.releaseYear()
                val durationString = album?.primaryRelease?.getDurationString()
                    ?: release?.getDurationString()

                titleTextView.text = itemTitle
                subTitleTextView.text =
                    album?.primaryRelease?.getArtistString(context.getString(R.string.various_artists_title))
                        ?: release?.getArtistString(context.getString(R.string.various_artists_title))
                explicitTextView.gone()
                val numberOfTrack = context.resources.getQuantityString(
                    R.plurals.playlist_number_of_tracks,
                    numTracks ?: 0,
                    numTracks
                )
                extraInfoTextView.text = "$numberOfTrack \u2022 $durationString" +
                    "\n$releaseYear"
                productArtImageView.setImageResource(R.drawable.placeholder_album)
                productArtImageView.visible()
                productArtRoundImageView.gone()
                itemArt?.let {
                    imageManager.init(context)
                        .load(it)
                        .options(imageSize)
                        .intoRoundedCorner(productArtImageView)
                }
            }

            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER -> {
                val playlist = product as Playlist

                itemId = playlist.id
                itemTitle = playlist.name.valueForSystemLanguage(context) ?: ""
                itemArt = playlist.coverImage.valueForSystemLanguage(context)
                val sbExtraInfo = StringBuilder()
                if (itemType == ProductPickerType.PLAYLIST && !playlist.creatorName.isNullOrBlank()) {
                    sbExtraInfo.append(
                        context.getString(
                            R.string.playlist_by_user,
                            playlist.creatorName
                        )
                    )
                    sbExtraInfo.append("\n")
                }
                sbExtraInfo.append(playlist.getCreateDateString(context))
                sbExtraInfo.append("\n")
                sbExtraInfo.append(playlist.getDurationString())

                titleTextView.text = itemTitle
                subTitleTextView.text = context.resources.getQuantityString(
                    R.plurals.playlist_number_of_tracks,
                    playlist.trackCount,
                    playlist.trackCount
                )
                explicitTextView.gone()
                extraInfoTextView.text = sbExtraInfo.toString()
                productArtImageView.setImageResource(R.drawable.placeholder_playlist)
                productArtImageView.visible()
                productArtRoundImageView.gone()

                val indicatorImageRes = if (itemType == ProductPickerType.PLAYLIST) {
                    R.drawable.music_ic_app_playlist
                } else {
                    R.drawable.music_ic_user_playlist
                }

                productIndicatorImageView.setImageResource(indicatorImageRes)
                productIndicatorImageView.visible()
                itemArt?.let {
                    imageManager.init(context)
                        .load(it)
                        .options(imageSize)
                        .intoRoundedCorner(productArtImageView)
                }
            }

            ProductPickerType.SONG,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.MY_PLAYLIST_SONG,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE,
            ProductPickerType.VIDEO,
            ProductPickerType.VIDEO_PLAYER -> {
                val track = product as Track

                itemId = track.id
                itemTitle = track.name
                itemArt = track.image

                titleTextView.text = itemTitle
                subTitleTextView.text =
                    track.getArtistString(context.getString(R.string.various_artists_title))
                explicitTextView.visibility = if (track.isExplicit) View.VISIBLE else View.GONE
                if (itemType == ProductPickerType.VIDEO_PLAYER) extraInfoTextView.visibility =
                    View.GONE else extraInfoTextView.text = track.getDurationString()

                val placeholderProductArtRes = when (itemType) {
                    ProductPickerType.VIDEO,
                    ProductPickerType.VIDEO_PLAYER -> R.drawable.placeholder_video

                    else -> R.drawable.placeholder_song
                }
                productArtImageView.setImageResource(placeholderProductArtRes)
                productArtImageView.visible()
                productArtRoundImageView.gone()
                imageManager.init(context)
                    .load(itemArt)
                    .options(imageSize)
                    .intoRoundedCorner(productArtImageView)
            }

            ProductPickerType.PROFILE -> {
                val profile = product as Profile

                itemId = profile.id
                itemTitle = profile.name
                itemArt = profile.image

                titleTextView.text = itemTitle
                subTitleTextView.text = context.getString(
                    R.string.follower_count_title,
                    profile.getFollowerCountString()
                )
                explicitTextView.gone()
                extraInfoTextView.gone()
                productArtRoundImageView.setImageResource(R.drawable.placeholder_profile)
                productArtImageView.gone()
                productArtRoundImageView.visible()
                itemArt?.let {
                    imageManager.init(context)
                        .load(it)
                        .options(imageSize)
                        .intoBitmap { productArtRoundImageView.setImageBitmap(it) }
                }
            }

            else -> null
        }
    }

    private fun initOptions() {
        // get the default options for each type
        val options = getDefaultProductOptions()

        // tweak options list based on the model info we got
        if (!config.enableShare) options.remove(PickerOptions.SHARE)
        if (!config.enablePlaylist || !config.enablePlaylistEditing) options.remove(PickerOptions.ADD_TO_PLAYLIST)
        if (itemType == ProductPickerType.PLAYLIST_VERIFIED_OWNER && !(product as Playlist).isPublic)
            options.remove(PickerOptions.SHARE)

        if (!config.enableFavourites)
            options.remove(PickerOptions.ADD_TO_COLLECTION_DISABLED)

        if (
            (
                BottomSheetProductPresenter.TRACK_ITEM_TYPES.contains(itemType) &&
                    (product as Track).artistString.isEmpty()
                ) ||
            (
                itemType == ProductPickerType.ALBUM && product is Album &&
                    (product as Album).primaryRelease?.artistString?.isEmpty() == true
                ) ||
            (
                itemType == ProductPickerType.ALBUM && product is Release &&
                    (product as Release).artistString.isEmpty()
                )
        )
            options.remove(PickerOptions.SHOW_ARTIST)

        val collectionOptionIndex = getCollectionOptionIndex(options)
        if (collectionOptionIndex >= 0) {
            options.removeAt(collectionOptionIndex)
            options.add(
                collectionOptionIndex,
                when (isInCollectionStatus) {
                    ProductPickerCollectionStatus.PENDING_UPDATE,
                    ProductPickerCollectionStatus.NONE ->
                        if (itemType == ProductPickerType.PROFILE) {
                            PickerOptions.FOLLOW_DISABLED
                        } else {
                            PickerOptions.ADD_TO_COLLECTION_DISABLED
                        }

                    ProductPickerCollectionStatus.IN_COLLECTION ->
                        if (itemType == ProductPickerType.PROFILE) {
                            PickerOptions.UNFOLLOW
                        } else {
                            PickerOptions.REMOVE_FROM_COLLECTION
                        }

                    ProductPickerCollectionStatus.NOT_IN_COLLECTION ->
                        if (itemType == ProductPickerType.PROFILE) {
                            PickerOptions.FOLLOW
                        } else {
                            PickerOptions.ADD_TO_COLLECTION
                        }
                }
            )
        }

        val visibilityOptionIndex = getVisibilityOptionIndex(options)
        if (visibilityOptionIndex >= 0) {
            val isPlaylistPublic = (product as Playlist).isPublic
            options.removeAt(visibilityOptionIndex)
            options.add(
                visibilityOptionIndex,
                if (isPlaylistPublic) PickerOptions.MAKE_PRIVATE else PickerOptions.MAKE_PUBLIC
            )
        }

        val adapter = binding.rvOptions.adapter as BottomSheetOptionsAdapter
        adapter.items = options
    }

    private fun getDefaultProductOptions(): MutableList<PickerOptions> =
        when (itemType) {
            ProductPickerType.ALBUM -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.ADD_TO_QUEUE,
                PickerOptions.SHOW_ARTIST
            )

            ProductPickerType.ALBUM_SONG -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.ADD_TO_MY_PLAYLIST,
                PickerOptions.ADD_TO_QUEUE,
                PickerOptions.SHOW_ARTIST
            )

            ProductPickerType.ARTIST -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.CLEAR_VOTE_DISABLED
            )

            ProductPickerType.ARTIST_SONG -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.ADD_TO_MY_PLAYLIST,
                PickerOptions.ADD_TO_QUEUE,
                PickerOptions.SHOW_ALBUM
            )

            ProductPickerType.MIX -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.CLEAR_VOTE
            )

            ProductPickerType.MY_PLAYLIST_SONG -> mutableListOf(
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.ADD_TO_MY_PLAYLIST,
                PickerOptions.ADD_TO_QUEUE,
                PickerOptions.SHOW_ARTIST,
                PickerOptions.SHOW_ALBUM,
                PickerOptions.REMOVE_FROM_PLAYLIST
            )

            ProductPickerType.PLAYLIST -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.ADD_TO_QUEUE
            )

            ProductPickerType.PLAYLIST_OWNER -> mutableListOf(
                PickerOptions.REMOVE_PLAYLIST,
                PickerOptions.ADD_TO_QUEUE
            )

            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.SONG -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.ADD_TO_MY_PLAYLIST,
                PickerOptions.ADD_TO_QUEUE,
                PickerOptions.SHOW_ARTIST,
                PickerOptions.SHOW_ALBUM
            )

            ProductPickerType.PLAYLIST_VERIFIED_OWNER -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_QUEUE
            )

            ProductPickerType.PROFILE -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.FOLLOW_DISABLED
            )

            ProductPickerType.SONG_PLAYER -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.ADD_TO_MY_PLAYLIST,
                PickerOptions.SHOW_ARTIST,
                PickerOptions.SHOW_ALBUM
            )

            ProductPickerType.SONG_QUEUE -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.REMOVE_FROM_QUEUE,
                PickerOptions.SHOW_ARTIST,
                PickerOptions.SHOW_ALBUM
            )

            ProductPickerType.VIDEO -> mutableListOf(
                PickerOptions.PLAY_VIDEO,
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.SHOW_ARTIST
            )

            ProductPickerType.VIDEO_PLAYER -> mutableListOf(
                PickerOptions.SHARE,
                PickerOptions.ADD_TO_COLLECTION_DISABLED,
                PickerOptions.SHOW_ARTIST
            )

            else -> mutableListOf()
        }

    private fun getCollectionOptionIndex(options: List<PickerOptions>): Int =
        options.indexOf(PickerOptions.ADD_TO_COLLECTION_DISABLED) +
            options.indexOf(PickerOptions.ADD_TO_COLLECTION) +
            options.indexOf(PickerOptions.REMOVE_FROM_COLLECTION) +
            options.indexOf(PickerOptions.FOLLOW_DISABLED) +
            options.indexOf(PickerOptions.FOLLOW) +
            options.indexOf(PickerOptions.UNFOLLOW) + INT_5

    private fun getVisibilityOptionIndex(options: List<PickerOptions>): Int =
        options.indexOf(PickerOptions.MAKE_PUBLIC) +
            options.indexOf(PickerOptions.MAKE_PRIVATE) + 1

    override fun showProductError() {
        dismiss()
        context.toast(R.string.generic_error_message)
    }

    override fun updateProductType(productPickerType: ProductPickerType) {
        itemType = productPickerType
    }

    override fun updateCollectionStatus(status: ProductPickerCollectionStatus) {
        val adapter = binding.rvOptions.adapter as BottomSheetOptionsAdapter
        val options = adapter.items.toMutableList()
        val collectionOptionIndex = getCollectionOptionIndex(options)
        if (collectionOptionIndex >= 0) {
            options.removeAt(collectionOptionIndex)
            options.add(
                collectionOptionIndex,
                when (status) {
                    ProductPickerCollectionStatus.PENDING_UPDATE,
                    ProductPickerCollectionStatus.NONE ->
                        if (itemType == ProductPickerType.PROFILE) {
                            PickerOptions.FOLLOW_DISABLED
                        } else {
                            PickerOptions.ADD_TO_COLLECTION_DISABLED
                        }

                    ProductPickerCollectionStatus.IN_COLLECTION ->
                        if (itemType == ProductPickerType.PROFILE) {
                            PickerOptions.UNFOLLOW
                        } else {
                            PickerOptions.REMOVE_FROM_COLLECTION
                        }

                    ProductPickerCollectionStatus.NOT_IN_COLLECTION ->
                        if (itemType == ProductPickerType.PROFILE) {
                            PickerOptions.FOLLOW
                        } else {
                            PickerOptions.ADD_TO_COLLECTION
                        }
                }
            )
            adapter.items = options
        }
    }

    override fun showArtistClearVote() {
        val adapter = binding.rvOptions.adapter as BottomSheetOptionsAdapter
        val options = adapter.items.toMutableList()

        val clearVoteIndex = options.indexOf(PickerOptions.CLEAR_VOTE_DISABLED)
        if (clearVoteIndex >= 0) {
            options.removeAt(clearVoteIndex)
            options.add(clearVoteIndex, PickerOptions.CLEAR_VOTE)
            adapter.items = options
        }
    }

    override fun showLoading() {
        LoadingDialog.show(context)
    }

    override fun showFavouritedToast(isFavourited: Boolean) {
        when (itemType) {
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.ALBUM,
            ProductPickerType.ARTIST,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.MY_PLAYLIST_SONG,
            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER,
            ProductPickerType.SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE -> {
                onFavoriteItemClick?.invoke(isFavourited, true)
            }

            ProductPickerType.MIX -> context.toast(R.string.starred_station_added_message)
            ProductPickerType.VIDEO,
            ProductPickerType.VIDEO_PLAYER ->
                context.toast(R.string.starred_video_added_message)

            ProductPickerType.PROFILE ->
                context.toast(R.string.starred_profile_added_message)

            else -> Timber.e("This type does not support add to collection")
        }
    }

    override fun showFavouritedError(isFavourited: Boolean) {
        when (itemType) {
            ProductPickerType.ALBUM,
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.ARTIST,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.MY_PLAYLIST_SONG,
            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER,
            ProductPickerType.SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE -> {
                onFavoriteItemClick?.invoke(isFavourited, false)
            }

            else -> context.toast(R.string.collection_error_message)
        }
    }

    override fun inCollectionStatusChanged(isInCollection: Boolean) {
        product?.let { onInCollectionStatusChanged?.invoke(isInCollection, it) }
    }

    override fun showGenericErrorMessage() {
        LoadingDialog.dismiss()
        context.toast(R.string.generic_error_message)
    }

    override fun addToQueue(tracks: List<Track>, isOffline: Boolean) {
        LoadingDialog.dismiss()
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                val musicPlayerService = (binder as MusicPlayerService.PlayerBinder).service
                val trackQueueInfo = musicPlayerService.getCurrentQueueInfo()
                val playingTrack =
                    trackQueueInfo?.queueInDisplayOrder?.get(trackQueueInfo.indexInDisplayOrder)
                // as Release is not a playersource, create an Album object instead here as player source
                val playerSource = if (product is Release) {
                    val release = product as Release
                    Album(
                        release.albumId,
                        release.name,
                        release.artists,
                        release,
                        listOf(release.id)
                    )
                } else product as PlayerSource
                when {
                    playingTrack == null -> playTrack(tracks, playerSource, isOffline)
                    playingTrack.playerSource?.sourceStation != null ->
                        context.toast(R.string.add_to_queue_error)

                    else ->
                        musicPlayerService.addTracks(tracks, playerSource, isOffline)
                }
                context.unbindService(this)
            }
        }
        context.bindServiceMusic(musicServiceConnection)
    }

    private fun playTrack(tracks: List<Track>, playerSource: PlayerSource, isOffline: Boolean) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(context.getMusicServiceIntent())
                        playTracks(tracks, playerSource, isOffline = isOffline)
                    }
                }
                context.unbindService(this)
            }
        }
        context.bindServiceMusic(musicServiceConnection)
    }

    override fun removeFromQueue() {
        onRemoveFromQueue?.invoke()
    }

    override fun playVideo(video: Track) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(context.getMusicServiceIntent())
                        playVideo(video)
                    }
                }
                context.unbindService(this)
            }
        }
        context.bindServiceMusic(musicServiceConnection)
    }

    override fun showVotesCleared() {
        context.toast(R.string.clear_votes_success)
    }

    override fun showClearVotesError() {
        context.toast(R.string.clear_votes_error)
    }

    override fun showAddToMyPlaylist() {
        fragmentManager?.let { _fragmentManager ->
            AddToMyPlaylistDialogFragment.newInstance(product?.id ?: -1).let {
                it.show(_fragmentManager, AddToMyPlaylistDialogFragment.TAG)
                this@BottomSheetProductPicker.dismiss()
            }
        }
    }

    // region RouterSurface

    override fun shareProduct(link: String) {
        LoadingDialog.dismiss()
        when (itemType) {
            ProductPickerType.ALBUM -> {
                context.share(context.getString(R.string.share_album_title, itemTitle), link)
            }

            ProductPickerType.MIX -> {
                context.share(context.getString(R.string.share_station_title, itemTitle), link)
            }

            ProductPickerType.ARTIST -> {
                context.share(context.getString(R.string.share_artist_title, itemTitle), link)
            }

            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER -> {
                context.share(context.getString(R.string.share_playlist_title, itemTitle), link)
            }

            ProductPickerType.SONG,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE ->
                context.share(context.getString(R.string.share_playlist_title, itemTitle), link)

            ProductPickerType.VIDEO,
            ProductPickerType.VIDEO_PLAYER ->
                context.share(context.getString(R.string.share_playlist_title, itemTitle), link)

            ProductPickerType.PROFILE -> {
                context.share(context.getString(R.string.share_profile_title, itemTitle), link)
            }

            else -> {
                // do nothing
            }
        }
    }

    override fun showArtist(artistId: Int) {
        context.startActivity(ArtistActivity::class) {
            putExtras(ArtistPresenter.ARTIST_ID_KEY to artistId)
        }
    }

    override fun showAlbum(albumId: Int) {
        context.startActivity(AlbumActivity::class) {
            putExtras(AlbumPresenter.ALBUM_ID_KEY to albumId)
        }
    }

    override fun showPlayerSetting() {
        context.startActivity(PlayerSettingActivity::class)
    }
}
