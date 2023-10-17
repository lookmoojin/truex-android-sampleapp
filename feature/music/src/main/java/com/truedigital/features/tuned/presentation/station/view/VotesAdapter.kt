package com.truedigital.features.tuned.presentation.station.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.databinding.ItemLikeBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class VotesAdapter(val onClickListener: (LikedTrack) -> Unit) :
    RecyclerView.Adapter<VotesAdapter.LikesViewHolder>() {

    var items: List<LikedTrack> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikesViewHolder {
        return LikesViewHolder(
            ItemLikeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        val like = items[position]
        holder.bind(like, position)
    }

    override fun getItemCount(): Int = items.size

    inner class LikesViewHolder(
        private val binding: ItemLikeBinding,
        val onClickListener: (LikedTrack) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonRemove.onClick {
                binding.progressBar.visible()
                binding.buttonRemove.gone()
                onClickListener(items[layoutPosition])
            }
        }

        fun bind(like: LikedTrack, position: Int) = with(binding) {
            textViewIndex.text = (position + 1).toString()
            textViewSongTitle.text = like.track?.name
            textViewArtist.text =
                like.getArtistString(root.context.getString(R.string.various_artists_title))
            progressBar.gone()
            buttonRemove.visible()
        }
    }
}
