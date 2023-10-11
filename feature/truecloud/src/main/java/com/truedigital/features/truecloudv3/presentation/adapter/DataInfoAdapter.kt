package com.truedigital.features.truecloudv3.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderDataInfoItemBinding

class DataInfoAdapter : RecyclerView.Adapter<DataInfoViewHolder>() {
    private var dataInfo: List<Pair<String, String>> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataInfoViewHolder {
        return DataInfoViewHolder(
            TrueCloudv3ViewholderDataInfoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataInfoViewHolder, position: Int) {
        dataInfo[position].let { item ->
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return dataInfo.size
    }

    fun setData(data: List<Pair<String, String>>) {
        dataInfo = data
        notifyDataSetChanged()
    }
}
