package com.truedigital.foundation.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.truedigital.foundation.R

const val RESIZE = "resize"
const val RESIZE_LARGE = "resize_large"
const val RESIZE_SQUARE = "resize_square"
const val RESIZE_NONE = "resize_none"

fun RequestManager.loadWithResize(
    context: Context,
    url: String?,
    resizeType: String = RESIZE
): RequestBuilder<Drawable> {
    val imageUrl = resizeImageUrl(context, url, resizeType)
    return this.load(imageUrl)
}

fun RequestBuilder<Bitmap>.loadWithResize(
    context: Context,
    url: String?,
    resizeType: String = RESIZE
): RequestBuilder<Bitmap> {
    val imageUrl = resizeImageUrl(context, url, resizeType)
    return this.load(imageUrl)
}

private fun resizeImageUrl(context: Context, url: String?, resizeType: String): String? {
    return when {
        url?.isEmpty() == true -> {
            ""
        }
        url?.contains("_original") == true && !url.contains(".gif") -> {
            when (resizeType) {
                RESIZE_LARGE -> url.replace(
                    "_original",
                    context.getString(R.string.cms_size_image_large)
                )
                RESIZE_SQUARE -> url.replace(
                    "_original",
                    context.getString(R.string.cms_size_image_square)
                )
                RESIZE_NONE -> url
                else -> url.replace("_original", context.getString(R.string.cms_size_image))
            }
        }
        else -> {
            url
        }
    }
}
