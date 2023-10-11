package com.truedigital.features.truecloudv3.presentation.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderContactItemBinding
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class ContactViewHolder(
    private val binding: TrueCloudv3ViewholderContactItemBinding,
    private val onItemClicked: ((ContactTrueCloudModel) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ContactTrueCloudModel) {
        binding.apply {
            if (item.picture.isNotEmpty()) {
                val imageBytes = Base64.decode(item.picture, Base64.DEFAULT)
                defaultContactPicture.invisible()
                userContactPicture.visible()
                userContactPicture.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                )
            } else {
                defaultContactPicture.visible()
                userContactPicture.invisible()
            }
            trueCloudNameTextView.text = item.firstName
            root.onClick {
                onItemClicked?.invoke(item)
            }
        }
    }
}
