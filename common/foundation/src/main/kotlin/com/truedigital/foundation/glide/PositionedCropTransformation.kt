package com.truedigital.foundation.glide

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.PAINT_FLAGS
import java.security.MessageDigest

class PositionedCropTransformation(private var xPercentage: Float, private var yPercentage: Float) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        val toReuse = pool.get(
            outWidth, outHeight,
            if (toTransform.config != null)
                toTransform.config
            else
                Bitmap.Config.ARGB_8888
        )
        val transformed = crop(toReuse, toTransform, outWidth, outHeight, xPercentage, yPercentage)
        if (toReuse != transformed) {
            toReuse.recycle()
        }
        return transformed
    }

    /**
     * A potentially expensive operation to crop the given Bitmap so that it fills the given dimensions. This operation
     * is significantly less expensive in terms of memory if a mutable Bitmap with the given dimensions is passed in
     * as well.
     *
     * @param recycled A mutable Bitmap with dimensions width and height that we can load the cropped portion of toCrop
     * into.
     * @param toCrop The Bitmap to resize.
     * @param outWidth The width in pixels of the final Bitmap.
     * @param outHeight The height in pixels of the final Bitmap.
     * @param xPercentage The horizontal percentage of the crop. 0.0f => left, 0.5f => center, 1.0f => right or anything in between 0 and 1
     * @param yPercentage The vertical percentage of the crop. 0.0f => top, 0.5f => center, 1.0f => bottom or anything in between 0 and 1
     * @return The resized Bitmap (will be recycled if recycled is not null).
     */
    private fun crop(recycled: Bitmap?, toTransform: Bitmap?, outWidth: Int, outHeight: Int, xPercentage: Float, yPercentage: Float): Bitmap? {
        if (toTransform == null) {
            return null
        } else if (toTransform.width == outWidth && toTransform.height == outHeight) {
            return toTransform
        }
        // From ImageView/Bitmap.createScaledBitmap.
        val scale: Float
        var dx = 0f
        var dy = 0f
        val m = Matrix()
        if (toTransform.width * outHeight > outWidth * toTransform.height) {
            scale = outHeight.toFloat() / toTransform.height.toFloat()
            dx = outWidth - toTransform.width * scale
            dx *= xPercentage
        } else {
            scale = outWidth.toFloat() / toTransform.width.toFloat()
            dy = outHeight - toTransform.height * scale
            dy *= yPercentage
        }
        m.setScale(scale, scale)
        m.postTranslate((dx - 0.5f), (dy - 0.5f))
        val result = recycled
            ?: Bitmap.createBitmap(outWidth, outHeight, getSafeConfig(toTransform))

        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        TransformationUtils.setAlpha(toTransform, result)
        val canvas = Canvas(result)
        val paint = Paint(PAINT_FLAGS)
        canvas.drawBitmap(toTransform, m, paint)
        return result
    }

    private fun getSafeConfig(bitmap: Bitmap): Bitmap.Config {
        return if (bitmap.config != null) bitmap.config else Bitmap.Config.ARGB_8888
    }
}
