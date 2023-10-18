package com.truedigital.features.music.presentation.mylibrary.mymusic.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.presentation.main.view.MyMusicView

@SuppressLint("NotifyDataSetChanged")
class MusicFavoriteShelfAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class MyFavoriteAction {
        RESUME, PAUSE, REFRESH
    }

    private var myFavoriteAction: MyFavoriteAction = MyFavoriteAction.RESUME

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = MyMusicView(parent.context)
        itemView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return MusicFavoriteShelfViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (myFavoriteAction) {
            MyFavoriteAction.RESUME -> (holder.itemView as? MyMusicView)?.onResume()
            MyFavoriteAction.PAUSE -> (holder.itemView as? MyMusicView)?.onPause()
            MyFavoriteAction.REFRESH -> (holder.itemView as? MyMusicView)?.refreshFavorite()
        }
    }

    override fun getItemCount(): Int = 1

    inner class MusicFavoriteShelfViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    fun onResumeMyFavorite() {
        myFavoriteAction = MyFavoriteAction.RESUME
        notifyDataSetChanged()
    }

    fun onPauseMyFavorite() {
        myFavoriteAction = MyFavoriteAction.PAUSE
        notifyDataSetChanged()
    }

    fun onRefreshMyFavorite() {
        myFavoriteAction = MyFavoriteAction.REFRESH
        notifyDataSetChanged()
    }
}
