package com.truedigital.features.music.presentation.mylibrary.mymusic.viewholder

import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.presentation.mylibrary.mymusic.adapter.MyPlaylistAdapter
import com.truedigital.features.tuned.databinding.ItemMyPlaylistWrapperBinding

class MyPlaylistWrapperViewHolder(
    private val binding: ItemMyPlaylistWrapperBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        myPlaylistAdapter: MyPlaylistAdapter,
        lastScrollX: Int,
        onScrolled: (Int) -> Unit
    ) = with(binding) {

        rvMyPlaylistWrapper.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = myPlaylistAdapter
        }
        rvMyPlaylistWrapper.doOnPreDraw {
            rvMyPlaylistWrapper.scrollBy(lastScrollX, 0)
        }
        rvMyPlaylistWrapper.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onScrolled(recyclerView.computeHorizontalScrollOffset())
            }
        })
    }
}
