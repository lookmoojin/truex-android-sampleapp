package com.truedigital.features.truecloudv3.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ItemAlphabetScrollBinding
import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel

class AlphabetScrollAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var alphabetList: MutableList<AlphabetItemModel> = mutableListOf()
    var onItemClicked: ((item: AlphabetItemModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlphabetViewHolder(
            TrueCloudv3ItemAlphabetScrollBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClicked
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        alphabetList[position].let { item ->
            (holder as AlphabetViewHolder).let { it.bind(item) }
        }
    }

    override fun getItemCount(): Int {
        return alphabetList.size
    }

    fun setAlphabetList(list: List<AlphabetItemModel>) {
        this.alphabetList.clear()
        this.alphabetList.addAll(list)
        notifyDataSetChanged()
    }
}
