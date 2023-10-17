package com.truedigital.features.music.presentation.myplaylist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.presentation.myplaylist.viewholder.MyPlaylistTrackViewHolder
import com.truedigital.features.tuned.data.track.model.Track
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class MyPlaylistTrackAdapter @AssistedInject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    @Assisted("onItemClicked") private val onItemClicked: (Int) -> Unit,
    @Assisted("onSeeMoreTrackClicked") private val onSeeMoreTrackClicked: (Track) -> Unit
) : ListAdapter<Track, MyPlaylistTrackViewHolder>(MyPlaylistTrackDiffCallback()) {

    private val adapterScope = CoroutineScope(coroutineDispatcher.io())

    @AssistedFactory
    interface MyPlaylistTrackAdapterFactory {
        fun create(
            @Assisted("onItemClicked") onItemClicked: (Int) -> Unit,
            @Assisted("onSeeMoreTrackClicked") onSeeMoreTrackClicked: (Track) -> Unit
        ): MyPlaylistTrackAdapter
    }

    fun updateCurrentPlayingTrack(trackList: List<Track>, trackId: Int?) {
        adapterScope.launchSafe {
            val trackItems = trackList.map { track ->
                track.copy(isPlayingTrack = track.id == trackId)
            }

            withContext(coroutineDispatcher.main()) {
                submitList(trackItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlaylistTrackViewHolder {
        return MyPlaylistTrackViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyPlaylistTrackViewHolder, position: Int) {
        holder.bind(getItem(position), onSeeMoreTrackClicked)
        holder.itemView.setOnClickListener {
            onItemClicked.invoke(holder.absoluteAdapterPosition - 1)
        }
    }

    class MyPlaylistTrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }
}
