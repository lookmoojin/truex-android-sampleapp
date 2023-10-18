package com.truedigital.foundation.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import java.security.MessageDigest

class ColorFilterTransformation(private val mBitmapPool: BitmapPool, private val mColor: Int) : Transformation<Bitmap> {

    constructor(context: Context, color: Int) : this(Glide.get(context).bitmapPool, color)

    override fun transform(context: Context, resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap> {
        val source = resource.get()

        val width = source.width
        val height = source.height

        val config = if (source.config != null) source.config else Bitmap.Config.ARGB_8888
        var bitmap: Bitmap? = mBitmapPool.get(width, height, config)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, config)
        }

        val canvas = Canvas(bitmap!!)
        val paint = Paint().apply {
            isAntiAlias = true
            colorFilter = PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_ATOP)
        }
        canvas.drawBitmap(source, 0f, 0f, paint)

        return BitmapResource.obtain(bitmap, mBitmapPool)!!
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}
}
