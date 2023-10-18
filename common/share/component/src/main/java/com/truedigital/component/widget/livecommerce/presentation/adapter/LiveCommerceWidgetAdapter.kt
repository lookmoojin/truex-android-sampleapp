package com.truedigital.component.widget.livecommerce.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.component.databinding.ItemComponentLivecommerceBinding
import com.truedigital.component.widget.livecommerce.domain.model.CommerceActiveLiveStreamModel
import com.truedigital.component.widget.livecommerce.presentation.viewholder.LiveCommerceWidgetViewHolder
import com.truedigital.component.widget.livecommerce.presentation.viewholder.OnLiveCommerceItemClicked

class LiveCommerceWidgetAdapter(
    private val context: Context,
    private val onLiveCommerceItemClicked: OnLiveCommerceItemClicked?,
    private val onTrackFirebaseEvent: ((HashMap<String, Any>) -> Unit)?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listData: MutableList<CommerceActiveLiveStreamModel> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        return LiveCommerceWidgetViewHolder(
            ItemComponentLivecommerceBinding.inflate(
                layoutInflater,
                parent,
                false
            ),
            onLiveCommerceItemClicked = onLiveCommerceItemClicked,
            onTrackFirebaseEvent = onTrackFirebaseEvent
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as LiveCommerceWidgetViewHolder
        viewHolder.bind(
            model = listData[position]
        )
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun setItem(newList: List<CommerceActiveLiveStreamModel>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }

    fun addItem(newList: CommerceActiveLiveStreamModel) {
        listData.add(newList)
        this.notifyItemInserted(listData.size - 1)
    }
}
