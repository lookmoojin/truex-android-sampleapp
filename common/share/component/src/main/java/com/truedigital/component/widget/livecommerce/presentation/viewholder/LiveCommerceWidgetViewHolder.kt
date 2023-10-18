package com.truedigital.component.widget.livecommerce.presentation.viewholder

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.component.R
import com.truedigital.component.databinding.ItemComponentLivecommerceBinding
import com.truedigital.component.widget.livecommerce.domain.model.CommerceActiveLiveStreamModel
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick

typealias OnLiveCommerceItemClicked = (
    position: Int,
    item: CommerceActiveLiveStreamModel
) -> Unit

class LiveCommerceWidgetViewHolder(
    private val binding: ItemComponentLivecommerceBinding,
    private val onLiveCommerceItemClicked: OnLiveCommerceItemClicked?,
    private val onTrackFirebaseEvent: ((HashMap<String, Any>) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: CommerceActiveLiveStreamModel) {
        with(binding) {
            thumbnailImageView.load(
                binding.root.context,
                model.thumbnailField,
                placeholder = R.drawable.ic_livecommerce_placeholder,
                scaleType = ImageView.ScaleType.CENTER_CROP
            )
            commerceTextViewTitle.text = model.title
            commerceTextViewSubtitle.text = model.description
            commerceTextViewProfileName.text = model.displayName

            commerceProfileImageView.load(
                binding.root.context,
                model.profileImageUrl,
                placeholder = R.drawable.ic_profile_toolbar,
                scaleType = ImageView.ScaleType.CENTER_CROP
            )

            root.onClick {
                onTrackFirebaseEvent?.invoke(hashMapOf())
                onLiveCommerceItemClicked?.invoke(bindingAdapterPosition, model)
            }
        }
    }
}
