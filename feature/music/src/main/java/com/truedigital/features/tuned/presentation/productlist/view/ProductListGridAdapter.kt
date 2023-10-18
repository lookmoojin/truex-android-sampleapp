package com.truedigital.features.tuned.presentation.productlist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.common.Constants.FLOAT_16F
import com.truedigital.features.tuned.common.Constants.INT_13
import com.truedigital.features.tuned.common.Constants.INT_2
import com.truedigital.features.tuned.common.Constants.INT_3
import com.truedigital.features.tuned.common.Constants.INT_9
import com.truedigital.features.tuned.common.extensions.dp
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.common.extensions.windowWidth
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ItemGridAlbumBinding
import com.truedigital.features.tuned.databinding.ItemGridArtistBinding
import com.truedigital.features.tuned.databinding.ItemGridPlaylistBinding
import com.truedigital.features.tuned.databinding.ItemGridSongBinding
import com.truedigital.features.tuned.databinding.ItemGridStationBinding
import com.truedigital.features.tuned.databinding.ItemGridVideoBinding
import com.truedigital.features.tuned.databinding.ItemMusicLoadingBinding
import com.truedigital.features.tuned.databinding.ItemTagBinding
import com.truedigital.features.tuned.domain.facade.tag.model.TagDisplayType
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible

class ProductListGridAdapter(
    imageManager: ImageManager,
    private val showDetailedDescription: Boolean,
    private val displayTagTitle: Boolean = true,
    private val tagDisplayType: TagDisplayType = TagDisplayType.LANDSCAPE,
    private val numColumns: Int,
    numItemsToDisplay: Int? = null,
    onClickListener: (Product) -> Unit,
    onLongClickListener: ((Product) -> Unit)? = null,
    onMoreSelectedListener: ((Product) -> Unit)? = null,
    onPageLoadListener: (() -> Unit)? = null
) :
    ProductListBaseAdapter(
        imageManager,
        numItemsToDisplay,
        onClickListener,
        onLongClickListener,
        onMoreSelectedListener,
        onPageLoadListener
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_TRACK -> {
                ProductListViewHolder(
                    ItemGridSongBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener
                )
            }

            ITEM_TYPE_ARTIST -> {
                ProductListViewHolder(
                    ItemGridArtistBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener
                )
            }

            ITEM_TYPE_ALBUM,
            ITEM_TYPE_RELEASE -> {
                ProductListViewHolder(
                    ItemGridAlbumBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener
                )
            }

            ITEM_TYPE_PLAYLIST -> {
                ProductListViewHolder(
                    ItemGridPlaylistBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener
                )
            }

            ITEM_TYPE_STATION -> {
                ProductListViewHolder(
                    ItemGridStationBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener
                )
            }

            ITEM_TYPE_VIDEO -> {
                ProductListViewHolder(
                    ItemGridVideoBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener
                )
            }

            ITEM_TYPE_TAG -> {
                ProductListViewHolder(
                    ItemTagBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener
                )
            }

            else -> {
                LoadingViewHolder(
                    ItemMusicLoadingBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    true
                )
            }
        }
    }

    override fun bindTrack(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemGridSongBinding)?.let { binding ->
            val viewRoot = binding.root
            val track = items[position] as Track
            binding.root.alpha = if (track.allowStream) 1f else FLOAT_0_5F

            val imageSize =
                (viewRoot.context.windowWidth - viewRoot.resources.dp(FLOAT_16F) * INT_3) / INT_2
            binding.songImageView.setImageResource(R.drawable.placeholder_song)
            imageManager.init(viewBinding.root)
                .load(track.image)
                .options(imageSize)
                .intoRoundedCorner(binding.songImageView)

            binding.songNameTextView.text = track.name
            binding.artistNameTextView.text =
                track.getArtistString(viewRoot.context.getString(R.string.various_artists_title))
        }
    }

    override fun bindArtist(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemGridArtistBinding)?.let { binding ->
            val viewRoot = binding.root
            val artist = items[position] as Artist
            binding.artistNameTextView.text = artist.name

            val imageSize =
                (viewRoot.context.windowWidth - viewRoot.resources.dp(FLOAT_16F) * INT_3) / INT_2
            val lp = binding.artistImageView.layoutParams as FrameLayout.LayoutParams
            lp.width = imageSize
            lp.height = imageSize
            binding.artistImageView.layoutParams = lp
            binding.artistImageView.setImageResource(R.drawable.placeholder_artist)
            artist.image?.let { image ->
                imageManager.init(viewRoot)
                    .load(image)
                    .options(imageSize, mode = "smart")
                    .intoBitmap { bitmap ->
                        binding.artistImageView.setImageBitmap(bitmap)
                    }
            }
        }
    }

    override fun bindRelease(viewBinding: ViewBinding, position: Int, album: Album?) {
        (viewBinding as? ItemGridAlbumBinding)?.let { binding ->
            val viewRoot = binding.root
            val release = album?.primaryRelease ?: items[position] as Release

            val imageSize =
                (viewRoot.context.windowWidth - viewRoot.resources.dp(FLOAT_16F) * INT_3) / INT_2
            binding.albumImageView.setImageResource(R.drawable.placeholder_album)
            imageManager.init(viewRoot)
                .load(release.image)
                .options(imageSize)
                .intoRoundedCorner(binding.albumImageView)

            binding.albumNameTextView.text = release.name

            if (showDetailedDescription) {
                var detailedDescription = binding.root.context.resources.getQuantityString(
                    R.plurals.number_of_synced_track_title,
                    release.trackIds.size,
                    release.trackIds.size
                )
                release.releaseYear()?.let { detailedDescription += " • $it" }
                binding.albumDescriptionTextView.text = detailedDescription
            } else {
                binding.albumDescriptionTextView.text =
                    release.getArtistString(binding.root.context.getString(R.string.various_artists_title))
            }
        }
    }

    override fun bindPlaylist(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemGridPlaylistBinding)?.let { binding ->
            val viewRoot = binding.root
            val playlist = items[position] as Playlist

            binding.playlistImageView.setImageResource(R.drawable.placeholder_playlist)
            val image = playlist.coverImage.valueForSystemLanguage(viewRoot.context)
            image?.let {
                val imageSize =
                    (viewRoot.context.windowWidth - viewRoot.resources.dp(FLOAT_16F) * INT_3) / INT_2
                imageManager.init(viewRoot)
                    .load(it)
                    .options(imageSize)
                    .intoRoundedCorner(binding.playlistImageView)
            }

            binding.playlistNameTextView.text =
                playlist.name.valueForSystemLanguage(viewRoot.context)
            binding.playlistNumTracksTextView.text = viewRoot.resources.getQuantityString(
                R.plurals.playlist_number_of_tracks,
                playlist.trackCount,
                playlist.trackCount
            )

            if (playlist.isOwner) {
                binding.playlistTypeImageView.setImageResource(R.drawable.music_ic_profile_filled)
            } else {
                binding.playlistTypeImageView.setImageResource(R.drawable.music_ic_star_filled)
            }
        }
    }

    override fun bindStation(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemGridStationBinding)?.let { binding ->
            val viewRoot = binding.root
            val station = items[position] as Station

            val imageSize =
                (viewRoot.context.windowWidth - viewRoot.resources.dp(FLOAT_16F) * INT_3) / INT_2
            binding.stationImageView.setImageResource(R.drawable.placeholder_station)

            // stationImageOverlay is not getting its height correctly on some devices to set it here
            binding.stationImageOverlayImageView.layoutParams.height = imageSize

            station.coverImage.valueForSystemLanguage(viewRoot.context)?.let {
                imageManager.init(viewRoot)
                    .load(it)
                    .options(imageSize)
                    .intoRoundedCorner(binding.stationImageView)
            }

            binding.stationNameTextView.text = station.name.valueForSystemLanguage(viewRoot.context)

            when (station.type) {
                Station.StationType.SINGLE_ARTIST -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_artist_shuffle_description)
                    binding.stationImageOverlayImageView.setImageResource(
                        R.drawable.overlay_artist_shuffle
                    )
                }

                Station.StationType.ARTIST -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_artist_description)
                    binding.stationImageOverlayImageView.setImageResource(
                        R.drawable.overlay_artist_and_similar
                    )
                }

                Station.StationType.USER -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_user_description)
                    binding.stationImageOverlayImageView.setImageResource(0)
                }

                else -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_description)
                    binding.stationImageOverlayImageView.setImageResource(0)
                }
            }
        }
    }

    override fun bindVideo(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemGridVideoBinding)?.let { binding ->
            val viewRoot = binding.root
            val video = items[position] as Track
            viewRoot.alpha = if (video.allowStream) 1f else FLOAT_0_5F

            val imageSize =
                (viewRoot.context.windowWidth - viewRoot.resources.dp(FLOAT_16F) * INT_3) / INT_2
            binding.videoImageView.setImageResource(R.drawable.placeholder_video)
            binding.playImageView.gone()
            imageManager.init(viewRoot)
                .load(video.image)
                .options(imageSize)
                .intoRoundedCorner(binding.videoImageView) {
                    binding.playImageView.visible()
                }

            binding.videoNameTextView.text = video.name
            binding.artistNameTextView.text =
                video.getArtistString(viewRoot.context.getString(R.string.various_artists_title))
        }
    }

    override fun bindTag(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemTagBinding)?.let { binding ->
            val viewRoot = binding.root
            val tag = items[position] as Tag
            val cornerSize = viewRoot.resources.getDimensionPixelSize(R.dimen.tag_corner_size)
            val tagWidth: Int
            val tagHeight: Int
            when (tagDisplayType) {
                TagDisplayType.LANDSCAPE -> {
                    tagWidth = (
                        viewRoot.context.windowWidth - viewRoot.context.resources.dp(FLOAT_16F) *
                            2 - viewRoot.context.resources.dp(FLOAT_16F) * (numColumns - 1)
                        ) / numColumns
                    tagHeight = tagWidth * INT_9 / INT_13
                }

                TagDisplayType.SQUARE -> {
                    tagWidth = (
                        viewRoot.context.windowWidth - viewRoot.context.resources.dp(FLOAT_16F) *
                            2 - viewRoot.context.resources.dp(FLOAT_16F) * (numColumns - 1)
                        ) / numColumns
                    tagHeight = tagWidth
                }
            }

            val lp = binding.flTag.layoutParams
            lp.width = tagWidth
            lp.height = tagHeight

            binding.tagImageView.setImageResource(R.drawable.music_bg_tag)

            (tag.images?.valueForSystemLanguage(viewRoot.context) ?: tag.image)?.let {
                imageManager.init(viewRoot)
                    .load(it)
                    .options(tagWidth, tagHeight)
                    .intoRoundedCorner(binding.tagImageView, cornerSize)
            }

            if (displayTagTitle) {
                binding.tagOverlayImageView.visible()
                binding.tagNameTextView.visible()
                binding.tagNameTextView.text =
                    tag.displayName.valueForSystemLanguage(viewRoot.context)
            } else {
                binding.tagOverlayImageView.gone()
                binding.tagNameTextView.gone()
            }
        }
    }
}
