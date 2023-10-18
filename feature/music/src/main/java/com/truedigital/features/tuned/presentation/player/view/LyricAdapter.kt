package com.truedigital.features.tuned.presentation.player.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.data.track.model.LyricContentType
import com.truedigital.features.tuned.data.track.model.LyricRow
import com.truedigital.features.tuned.databinding.ItemLyricBinding

class LyricAdapter(
    val paddingLines: Int = 4,
    val enableTitles: Boolean = true,
    val enableEmptyLines: Boolean = false,
    val onChangeLyric: (Int) -> Unit
) : RecyclerView.Adapter<LyricAdapter.LyricRowViewHolder>() {

    @Suppress("SetterBackingFieldAssignment")
    var items: List<LyricRow> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            value.forEach {
                if (
                    (
                        it.type == LyricContentType.TITLE ||
                            it.type == LyricContentType.ARTIST ||
                            it.type == LyricContentType.ALBUM
                        ) && enableTitles
                ) {
                    (items as MutableList).add(it)
                }
                if (it.type == LyricContentType.EMPTY && enableEmptyLines) {
                    (items as MutableList).add(it)
                }
                if (it.type == LyricContentType.LYRIC) {
                    (items as MutableList).add(it)
                }
            }
            // add 4 empty lines for padding and scrolling,
            // the scroll will scroll to currentLyricIndex - 4
            // so the current lyric will be in middle of screen
            for (count in 1..paddingLines) {
                (items as MutableList).add(0, LyricRow(LyricContentType.EMPTY, 0L, ""))
                (items as MutableList).add(LyricRow(LyricContentType.EMPTY, 0L, ""))
            }
            notifyDataSetChanged()
        }

    private var currentLyricRowStart: Long = 0L
    private var nextLyricRowStart: Long = 1L
    private var currentLyricIndex: Int = -1
        set(value) {
            if (value != -1 && value != currentLyricIndex) {
                if (currentLyricIndex != -1) {
                    notifyItemChanged(currentLyricIndex)
                }
                field = value
                notifyItemChanged(value)
                onChangeLyric(value)
            }
            // scroll
        }

    fun setCurrentTime(mills: Long) {
        if (mills in currentLyricRowStart until nextLyricRowStart) return
        var lastLyricRowStart = 0L
        var lastLyricRowIndex = -1
        // if the mills is larger then the nextLyricRowStart,
        // we only need to loop from the next index to find the next row, otherwise, start from 0
        val startIndex = if (mills >= nextLyricRowStart) currentLyricIndex + 1 else 0
        for (index in startIndex until items.size) {
            val lyricRow = items[index]
            if (lyricRow.type == LyricContentType.LYRIC) {
                if (mills >= lyricRow.startTime) {
                    lastLyricRowStart = lyricRow.startTime
                    lastLyricRowIndex = index
                }
                if (mills < lyricRow.startTime || index == items.size - 1) {
                    currentLyricIndex = lastLyricRowIndex
                    currentLyricRowStart = lastLyricRowStart
                    nextLyricRowStart = lyricRow.startTime
                    break
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricRowViewHolder {
        val view = ItemLyricBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LyricRowViewHolder(view)
    }

    override fun onBindViewHolder(holder: LyricRowViewHolder, position: Int) {
        val lyricRow = items[position]
        with(holder) {
            binding.lyricTextView.text = when (lyricRow.type) {
                LyricContentType.LYRIC -> lyricRow.content
                LyricContentType.EMPTY -> ""
                LyricContentType.ARTIST -> "Artist: ${lyricRow.content}"
                LyricContentType.ALBUM -> "Album: ${lyricRow.content}"
                LyricContentType.TITLE -> "Title: ${lyricRow.content}"
            }

            if (position == currentLyricIndex) {
                binding.lyricTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, android.R.color.white)
                )
            } else {
                binding.lyricTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.text_color_lyric)
                )
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class LyricRowViewHolder(val binding: ItemLyricBinding) :
        RecyclerView.ViewHolder(binding.root)
}
