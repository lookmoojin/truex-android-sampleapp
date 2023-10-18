package com.truedigital.features.music.presentation.mylibrary.mymusic.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.presentation.mylibrary.mymusic.viewholder.MyPlaylistWrapperViewHolder
import com.truedigital.features.tuned.databinding.ItemMyPlaylistWrapperBinding

class MyPlaylistWrapperAdapter(
    private val myPlaylistAdapter: MyPlaylistAdapter
) : RecyclerView.Adapter<MyPlaylistWrapperViewHolder>() {

    companion object {
        private const val KEY_SCROLL_X = "horizontal.wrapper.adapter.key_scroll_x"
    }

    private var lastScrollX = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlaylistWrapperViewHolder {
        return MyPlaylistWrapperViewHolder(
            ItemMyPlaylistWrapperBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyPlaylistWrapperViewHolder, position: Int) {
        holder.bind(myPlaylistAdapter, lastScrollX) { x ->
            lastScrollX = x
        }
    }

    override fun getItemCount(): Int = 1

    fun onSaveState(outState: Bundle) {
        outState.putInt(KEY_SCROLL_X, lastScrollX)
    }

    fun onRestoreState(savedState: Bundle) {
        lastScrollX = savedState.getInt(KEY_SCROLL_X)
    }
}
