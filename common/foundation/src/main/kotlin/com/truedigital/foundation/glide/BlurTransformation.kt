package com.truedigital.foundation.glide

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest
import kotlin.math.roundToInt

class BlurTransformation(private val context: Context) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        return fastBlur(
            context,
            toTransform,
            (outWidth * 1f / toTransform.width).coerceAtMost(outHeight * 1f / toTransform.height),
            15f
        )
    }

    private fun fastBlur(context: Context, image: Bitmap, scale: Float, radius: Float): Bitmap? {
        val width = (image.width * scale).roundToInt()
        val height = (image.height * scale).roundToInt()

        val inputBitmap = Bitmap.createScaledBitmap(
            image, width, height,
            false
        )
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        val rs: RenderScript = RenderScript.create(context)
        val theIntrinsic: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(
            rs,
            Element.U8_4(rs)
        )
        val tmpIn: Allocation = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut: Allocation = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(radius)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)

        return outputBitmap
    }
}
