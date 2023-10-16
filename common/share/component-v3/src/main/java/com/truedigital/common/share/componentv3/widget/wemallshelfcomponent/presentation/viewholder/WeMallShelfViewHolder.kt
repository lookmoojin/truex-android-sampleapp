package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation.viewholder

import android.content.Context
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemComponentWemallBinding
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallShelfItemModel
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick

class WeMallShelfViewHolder(
    private val binding: ItemComponentWemallBinding,
    private val onWeMallItemClicked: ((strDeeplink: String?) -> Unit)?,
    private val onTrackFirebaseEvent: ((HashMap<String, Any>) -> Unit)?
) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val CONTENT_DESCRIPTION_ITEM_PREFIX = "commerce_item_"
    }

    fun bind(model: WeMallShelfItemModel, context: Context, index: Int, parentShelfId: String) {
        binding.root.contentDescription = Companion.CONTENT_DESCRIPTION_ITEM_PREFIX + model.categoryName
        binding.thumbnailImageView.load(
            context = context,
            url = model.thumb,
            placeholder = R.mipmap.placeholder_trueidwhite_vertical,
            scaleType = ImageView.ScaleType.CENTER_CROP
        )

        binding.cardView.onClick {
            onWeMallItemClicked?.let { _itemClick ->
                _itemClick.invoke(getUrl(model.itemUrl, model.categoryUrl))
            }
            onTrackFirebaseEvent?.let { _onTrackFirebaseEvent ->
                _onTrackFirebaseEvent.invoke(
                    hashMapOf(
                        MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                        MeasurementConstant.Key.KEY_CMS_ID to model.cmsId.ifEmpty { " " },
                        MeasurementConstant.Key.KEY_TITLE to model.title.take(100).ifEmpty { " " },
                        MeasurementConstant.Key.KEY_CONTENT_TYPE to "wemalldiscovery",
                        MeasurementConstant.Key.KEY_CATEGORY to model.categoryName.ifEmpty { " " },
                        MeasurementConstant.Key.KEY_SHELF_NAME to model.shelfName.take(100)
                            .ifEmpty { " " },
                        MeasurementConstant.Key.KEY_SHELF_CODE to parentShelfId,
                        MeasurementConstant.Key.KEY_ITEM_INDEX to index.toString()
                    )
                )
            }
        }
    }

    fun getUrl(itemUrl: String, categoryUrl: String): String {
        return if (itemUrl.isNotEmpty())
            "https://home.trueid.net/webview-dynamic-token?website=$itemUrl"
        else
            "https://home.trueid.net/webview-dynamic-token?website=$categoryUrl"
    }
}
