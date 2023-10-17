package com.truedigital.features.tuned.presentation.productlist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ItemHorizontalAlbumBinding
import com.truedigital.features.tuned.databinding.ItemHorizontalArtistBinding
import com.truedigital.features.tuned.databinding.ItemHorizontalPlaylistBinding
import com.truedigital.features.tuned.databinding.ItemHorizontalSongBinding
import com.truedigital.features.tuned.databinding.ItemHorizontalStationBinding
import com.truedigital.features.tuned.databinding.ItemHorizontalVideoBinding
import com.truedigital.features.tuned.databinding.ItemMusicLoadingBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible

class ProductListHorizontalAdapter(
    imageManager: ImageManager,
    private val showDetailedDescription: Boolean = false,
    numItemsToDisplay: Int? = null,
    onClickListener: (Product) -> Unit,
    onLongClickListener: ((Product) -> Unit)? = null,
    onMoreSelectedListener: ((Product) -> Unit)? = null,
    onPageLoadListener: (() -> Unit)? = null,
    onRemoveListener: ((Product) -> Unit)? = null
) :
    ProductListBaseAdapter(
        imageManager,
        numItemsToDisplay,
        onClickListener,
        onLongClickListener,
        onMoreSelectedListener,
        onPageLoadListener,
        onRemoveListener
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_TRACK -> {
                ProductListViewHolder(
                    ItemHorizontalSongBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener,
                    onRemoveListener
                )
            }

            ITEM_TYPE_ARTIST -> {
                ProductListViewHolder(
                    ItemHorizontalArtistBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener,
                    onRemoveListener
                )
            }

            ITEM_TYPE_ALBUM,
            ITEM_TYPE_RELEASE -> {
                ProductListViewHolder(
                    ItemHorizontalAlbumBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener,
                    onRemoveListener
                )
            }

            ITEM_TYPE_PLAYLIST -> {
                ProductListViewHolder(
                    ItemHorizontalPlaylistBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener,
                    onRemoveListener
                )
            }

            ITEM_TYPE_STATION -> {
                ProductListViewHolder(
                    ItemHorizontalStationBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener,
                    onRemoveListener
                )
            }

            ITEM_TYPE_VIDEO -> {
                ProductListViewHolder(
                    ItemHorizontalVideoBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener,
                    onRemoveListener
                )
            }

            ITEM_TYPE_TAG -> throw IllegalStateException("Tags does not support horizontal view")
            else -> {
                LoadingViewHolder(
                    ItemMusicLoadingBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    false
                )
            }
        }
    }

    // region populateViews

    override fun bindTrack(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemHorizontalSongBinding)?.let { binding ->
            val viewRoot = binding.root
            val track = items[position] as Track
            viewRoot.alpha = if (track.allowStream) 1f else FLOAT_0_5F

            binding.songImageView.setImageResource(R.drawable.placeholder_song)
            val imageSize =
                viewRoot.resources.getDimensionPixelSize(R.dimen.horizontal_item_image_size)
            imageManager.init(viewRoot)
                .load(track.image)
                .options(imageSize)
                .intoRoundedCorner(binding.songImageView)

            binding.songNameTextView.text = track.name
            binding.songArtistNameTextView.text =
                track.getArtistString(viewRoot.context.getString(R.string.various_artists_title))
            binding.durationTextView.text = track.formattedDuration

            // bind view to swipe item manager
            mItemManger.bindView(viewRoot, position)
        }
    }

    override fun bindArtist(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemHorizontalArtistBinding)?.let { binding ->
            val viewRoot = binding.root
            val artist = items[position] as Artist
            binding.artistNameTextView.text = artist.name

            val imageSize =
                viewRoot.resources.getDimensionPixelSize(R.dimen.horizontal_item_image_size)
            binding.artistImageView.setImageResource(R.drawable.placeholder_artist)
            artist.image?.let { image ->
                imageManager.init(viewRoot)
                    .load(image)
                    .options(imageSize, mode = "smart")
                    .intoBitmap { bitmap ->
                        binding.artistImageView.setImageBitmap(bitmap)
                    }
            }

            // bind view to swipe item manager
            mItemManger.bindView(viewRoot, position)
        }
    }

    override fun bindRelease(viewBinding: ViewBinding, position: Int, album: Album?) {
        (viewBinding as? ItemHorizontalAlbumBinding)?.let { binding ->
            val viewRoot = binding.root
            val release = album?.primaryRelease ?: items[position] as Release
            val imageSize =
                viewRoot.resources.getDimensionPixelSize(R.dimen.horizontal_item_image_size)
            binding.albumImageView.setImageResource(R.drawable.placeholder_album)
            imageManager.init(viewRoot)
                .load(release.image)
                .options(imageSize)
                .intoRoundedCorner(binding.albumImageView)

            binding.albumNameTextView.text = release.name
            if (showDetailedDescription) {
                var detailedDescription = viewRoot.context.resources.getQuantityString(
                    R.plurals.number_of_synced_track_title,
                    release.trackIds.size,
                    release.trackIds.size
                )
                release.releaseYear()?.let { detailedDescription += " • $it" }
                binding.albumDescriptionTextView.text = detailedDescription
            } else {
                binding.albumDescriptionTextView.text =
                    release.getArtistString(viewRoot.context.getString(R.string.various_artists_title))
            }

            // bind view to swipe item manager
            mItemManger.bindView(viewRoot, position)
        }
    }

    override fun bindPlaylist(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemHorizontalPlaylistBinding)?.let { binding ->
            val viewRoot = binding.root
            val playlist = items[position] as Playlist
            val imageSize =
                viewRoot.resources.getDimensionPixelSize(R.dimen.horizontal_item_image_size)
            binding.playlistImageView.setImageResource(R.drawable.placeholder_playlist)
            val image = playlist.coverImage.valueForSystemLanguage(viewRoot.context)
            image?.let {
                imageManager.init(viewRoot)
                    .load(it)
                    .options(imageSize)
                    .intoRoundedCorner(binding.playlistImageView)
            }

            binding.playlistNameTextView.text =
                playlist.name.valueForSystemLanguage(viewRoot.context)
            binding.playlistTrackCountTextView.text = viewRoot.resources.getQuantityString(
                R.plurals.playlist_number_of_tracks,
                playlist.trackCount,
                playlist.trackCount
            )

            if (playlist.isOwner) {
                binding.playlistTypeImageView.setImageResource(R.drawable.music_ic_profile_filled)
            } else {
                binding.playlistTypeImageView.setImageResource(R.drawable.music_ic_star_filled)
            }

            // bind view to swipe item manager
            mItemManger.bindView(viewRoot, position)
        }
    }

    override fun bindStation(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemHorizontalStationBinding)?.let { binding ->
            val viewRoot = binding.root
            val station = items[position] as Station
            val imageSize =
                viewRoot.resources.getDimensionPixelSize(R.dimen.horizontal_item_image_size)
            binding.stationImageView.setImageResource(R.drawable.placeholder_station)
            station.coverImage.valueForSystemLanguage(viewRoot.context)?.let {
                imageManager.init(viewRoot)
                    .load(it)
                    .options(imageSize)
                    .intoRoundedCorner(binding.stationImageView)
            }

            binding.stationNameTextView.text = station.name.valueForSystemLanguage(viewRoot.context)

            when (station.type) {
                Station.StationType.PRESET -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_description)
                    binding.stationImageOverlay.foreground = null
                }

                Station.StationType.SINGLE_ARTIST -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_artist_shuffle_description)
                    binding.stationImageOverlay.foreground = ContextCompat.getDrawable(
                        viewRoot.context,
                        R.drawable.overlay_artist_shuffle
                    )
                }

                Station.StationType.ARTIST -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_artist_description)
                    binding.stationImageOverlay.foreground = ContextCompat.getDrawable(
                        viewRoot.context,
                        R.drawable.overlay_artist_and_similar
                    )
                }

                Station.StationType.USER -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_user_description)
                    binding.stationImageOverlay.foreground = null
                }

                else -> {
                    binding.stationDescriptionTextView.text =
                        viewRoot.context.getString(R.string.station_description)
                    binding.stationImageOverlay.foreground = null
                }
            }

            // bind view to swipe item manager
            mItemManger.bindView(viewRoot, position)
        }
    }

    override fun bindVideo(viewBinding: ViewBinding, position: Int) {
        (viewBinding as? ItemHorizontalVideoBinding)?.let { binding ->
            val viewRoot = binding.root
            val video = items[position] as Track
            viewRoot.alpha = if (video.allowStream) 1f else FLOAT_0_5F

            val imageSize =
                viewRoot.resources.getDimensionPixelSize(R.dimen.horizontal_item_image_size)
            binding.videoImageView.setImageResource(R.drawable.placeholder_video)
            binding.playImageView.gone()
            imageManager.init(viewRoot)
                .load(video.image)
                .options(imageSize)
                .intoRoundedCorner(binding.videoImageView) {
                    if (it) binding.playImageView.visible()
                }

            binding.videoNameTextView.text = video.name
            binding.videoArtistTextView.text =
                video.getArtistString(viewRoot.context.getString(R.string.various_artists_title))

            // bind view to swipe item manager
            mItemManger.bindView(viewRoot, position)
        }
    }

    override fun bindTag(viewBinding: ViewBinding, position: Int) {
        throw IllegalStateException("Tags does not support horizontal view")
    }
}
