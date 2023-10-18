package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.componentv3.databinding.ItemComponentWemallBinding
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallShelfItemModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation.viewholder.WeMallShelfViewHolder

class WeMallShelfAdapter(
    private val context: Context,
    private val onWeMallItemClicked: ((strDeeplink: String?) -> Unit)?,
    private val onTrackFirebaseEvent: ((HashMap<String, Any>) -> Unit)?,
    private val parentShelfId: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: MutableList<WeMallShelfItemModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        return WeMallShelfViewHolder(
            ItemComponentWemallBinding.inflate(
                layoutInflater,
                parent,
                false
            ),
            onWeMallItemClicked = onWeMallItemClicked,
            onTrackFirebaseEvent = onTrackFirebaseEvent
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as WeMallShelfViewHolder
        viewHolder.bind(list[position], context, position, parentShelfId)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItem(newList: MutableList<WeMallShelfItemModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun addItem(newList: WeMallShelfItemModel) {
        list.add(newList)
        this.notifyItemInserted(list.size - 1)
    }
}
