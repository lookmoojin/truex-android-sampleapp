package com.truedigital.features.music.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.databinding.ItemMusicSearchTopMenuRowBinding

class HorizontalConcatAdapter(
    private val musicSearchTopMenuAdapter: MusicSearchTopMenuAdapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var currentActivePosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMusicSearchTopMenuRowBinding.inflate(inflater, parent, false)
        binding.musicSearchTopMenuConcatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        return ConcatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConcatViewHolder -> holder.bind(musicSearchTopMenuAdapter)
            else -> throw IllegalArgumentException("No viewholder to show this data")
        }
    }

    override fun getItemCount(): Int = 1

    inner class ConcatViewHolder(private val binding: ItemMusicSearchTopMenuRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(musicSearchTopMenuAdapter: MusicSearchTopMenuAdapter) {
            binding.musicSearchTopMenuConcatRecyclerView.apply {
                adapter = musicSearchTopMenuAdapter
                smoothScrollToPosition(currentActivePosition)
            }
        }
    }

    fun updateActivePosition(position: Int) {
        currentActivePosition = position
    }
}
