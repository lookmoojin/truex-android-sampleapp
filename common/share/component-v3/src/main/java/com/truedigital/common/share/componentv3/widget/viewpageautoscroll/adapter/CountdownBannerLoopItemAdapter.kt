package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.AbstractBannerViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.CountdownHighlightViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.CountdownScheduleCountdownViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.CountdownScheduleLiveViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.CountdownSchedulePreviewViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.onClick

class CountdownBannerLoopItemAdapter(
    private val listItem: MutableList<BannerBaseItemModel>
) : RecyclerView.Adapter<AbstractBannerViewHolder>() {

    companion object {
        const val CONTENT_TYPE_HIGHLIGHT = "hilight"
        const val CONTENT_TYPE_MISC = "misc"
        const val CONTENT_TYPE_SPORT_SCHEDULE = "sportschedule"
        const val CONTENT_TYPE_PREVIEW = "preview"
        const val CONTENT_TYPE_COUNTDOWN = "countdown"

        private const val HIGHLIGHT = 1
        private const val SPORT_SCHEDULE_LIVE = 2
        private const val SPORT_SCHEDULE_PREVIEW = 3
        private const val SPORT_SCHEDULE_COUNTDOWN = 4
    }

    var onItemClicked: ((index: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractBannerViewHolder {
        return when (viewType) {
            HIGHLIGHT -> {
                CountdownHighlightViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_countdown_highlight, parent, false)
                )
            }

            SPORT_SCHEDULE_LIVE -> {
                CountdownScheduleLiveViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_countdown_schedule_live, parent, false)
                )
            }

            SPORT_SCHEDULE_PREVIEW -> {
                CountdownSchedulePreviewViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_countdown_schedule_preview, parent, false)
                )
            }

            SPORT_SCHEDULE_COUNTDOWN -> {
                CountdownScheduleCountdownViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_countdown_schedule_countdown, parent, false)
                )
            }

            else -> {
                CountdownHighlightViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_countdown_highlight, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val index = position % listItem.size
        val item = listItem[index]
        return when (item.sportCountdownModel?.contentType) {
            CONTENT_TYPE_HIGHLIGHT,
            CONTENT_TYPE_MISC -> {
                HIGHLIGHT
            }

            CONTENT_TYPE_SPORT_SCHEDULE -> {
                SPORT_SCHEDULE_LIVE
            }

            CONTENT_TYPE_PREVIEW -> {
                SPORT_SCHEDULE_PREVIEW
            }

            CONTENT_TYPE_COUNTDOWN -> {
                SPORT_SCHEDULE_COUNTDOWN
            }

            else -> {
                HIGHLIGHT
            }
        }
    }

    override fun onBindViewHolder(holder: AbstractBannerViewHolder, position: Int) {
        val index = if (listItem.size > 1) {
            position % listItem.size
        } else {
            position
        }
        val item = listItem[index]
        holder.render(item)
        holder.itemView.contentDescription = item.contentDescription
        holder.itemView.onClick {
            onItemClicked?.invoke(index)
        }
    }

    override fun getItemCount(): Int {
        return if (listItem.size > 1) {
            Int.MAX_VALUE
        } else {
            listItem.size
        }
    }

    fun setItemBanner(list: MutableList<BannerBaseItemModel>) {
        listItem.clear()
        listItem.addAll(list)
    }

    fun getItemSize(): Int {
        return listItem.size
    }
}
