package com.truedigital.features.tuned.presentation.station.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.databinding.ItemMusicLoadingBinding
import com.truedigital.features.tuned.databinding.ItemStationBinding
import com.truedigital.foundation.extension.onClick

class StationAdapter(
    val imageManager: ImageManager,
    val onPageLoadListener: (() -> Unit)? = null,
    val onClickListener: (Station) -> Unit,
    val onLongClickListener: ((Station) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_LOADING = 0
        const val ITEM_TYPE_STATION = 1
    }

    var items: List<Station> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    private var hasPaging = onPageLoadListener != null
    var morePages = false

    override fun getItemViewType(position: Int): Int = if (position == items.size) {
        ITEM_TYPE_LOADING
    } else {
        ITEM_TYPE_STATION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_STATION -> StationViewHolder(
                ItemStationBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                onClickListener,
                onLongClickListener
            )

            else -> LoadingViewHolder(
                ItemMusicLoadingBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StationViewHolder) {
            holder.bind(items[position])
        } else {
            onPageLoadListener?.invoke()
        }
    }

    override fun getItemCount(): Int = items.size + if (morePages && hasPaging) 1 else 0

    inner class StationViewHolder(
        val binding: ItemStationBinding,
        val onClickListener: (Station) -> Unit,
        val onLongClickListener: ((Station) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.apply {
                onClick { onClickListener(items[layoutPosition]) }
                setOnLongClickListener {
                    onLongClickListener?.invoke(items[layoutPosition])
                    true
                }
            }
        }

        fun bind(station: Station) = with(binding) {
            stationName.text = station.name.valueForSystemLanguage(binding.root.context)
            stationImage.setImageResource(R.drawable.placeholder_station)
            when (station.type) {
                Station.StationType.SINGLE_ARTIST -> {
                    stationDescription.text =
                        binding.root.context.getString(R.string.station_artist_shuffle_description)
                    stationImageOverlay.foreground = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.overlay_artist_shuffle
                    )
                }

                Station.StationType.ARTIST -> {
                    stationDescription.text =
                        binding.root.context.getString(R.string.station_artist_description)
                    stationImageOverlay.foreground = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.overlay_artist_and_similar
                    )
                }

                Station.StationType.USER -> {
                    stationDescription.text =
                        binding.root.context.getString(R.string.station_user_description)
                    stationImageOverlay.foreground = null
                }

                else -> {
                    stationDescription.text =
                        binding.root.context.getString(R.string.station_description)
                    stationImageOverlay.foreground = null
                }
            }

            station.coverImage.valueForSystemLanguage(binding.root.context)?.let {
                val size =
                    binding.root.resources.getDimensionPixelSize(R.dimen.station_image_size)
                imageManager.init(binding.root.context)
                    .load(it)
                    .options(size)
                    .intoRoundedCorner(stationImage)
            }
        }
    }

    inner class LoadingViewHolder(val binding: ItemMusicLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.apply {
                layoutParams.width =
                    itemView.resources.getDimensionPixelSize(R.dimen.station_image_size)
                requestLayout()
            }
        }
    }
}
