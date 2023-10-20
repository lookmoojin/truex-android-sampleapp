package com.truedigital.features.truecloudv3.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderContactNumberItemBinding
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.foundation.extension.onClick

class ContactPhoneNumberAdapter : RecyclerView.Adapter<ContactPhoneNumberViewHolder>() {
    private var phoneNumber: MutableList<ContactPhoneNumberModel> = mutableListOf()
    var onCallClicked: ((ContactPhoneNumberModel) -> Unit)? = null
    var onCopyClicked: ((ContactPhoneNumberModel) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactPhoneNumberViewHolder {
        return ContactPhoneNumberViewHolder(
            binding = TrueCloudv3ViewholderContactNumberItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onCallClicked = onCallClicked,
            onCopyClicked = onCopyClicked
        )
    }

    override fun onBindViewHolder(holder: ContactPhoneNumberViewHolder, position: Int) {
        phoneNumber[position].let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int {
        return phoneNumber.size
    }

    fun addPhoneNumber(numberList: List<ContactPhoneNumberModel>) {
        phoneNumber.clear()
        phoneNumber.addAll(numberList)
        notifyDataSetChanged()
    }
}

class ContactPhoneNumberViewHolder(
    private val binding: TrueCloudv3ViewholderContactNumberItemBinding,
    private val onCopyClicked: ((ContactPhoneNumberModel) -> Unit)?,
    private val onCallClicked: ((ContactPhoneNumberModel) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ContactPhoneNumberModel) {
        binding.apply {
            trueCloudPhoneNumberTextView.text = item.number
            trueCloudPhoneTypeTextView.text = item.type
            trueCloudCallImageView.onClick {
                onCallClicked?.invoke(item)
            }
            trueCloudCopyImageView.onClick {
                onCopyClicked?.invoke(item)
            }
        }
    }
}
