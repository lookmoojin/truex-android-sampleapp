package com.truedigital.features.truecloudv3.extension

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.view.children
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.truedigital.features.truecloudv3.R
import ja.burhanrashid52.photoeditor.DrawingView
import ja.burhanrashid52.photoeditor.PhotoEditorView
import kotlin.math.roundToInt

private const val PHOTO_EDITOR_IMG_SRC_ID = 1
private const val PHOTO_EDITOR_IMG_FILTER_ID = 3

fun View.snackBar(message: String, color: Int) {
    val customView = CompoundButton.inflate(context, R.layout.true_cloudv3_view_snackbar, null)
    customView.findViewById<TextView>(R.id.trueCloudSnackBarTextView).apply {
        text = message
        backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, color)
        )
        if (color == R.color.true_cloudv3_color_toast_success) {
            setCompoundDrawablesWithIntrinsicBounds(com.truedigital.common.share.componentv3.R.drawable.ic_check_circle, 0, 0, 0)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(com.truedigital.common.share.componentv3.R.drawable.ic_cross_circle, 0, 0, 0)
        }
    }
    val snackBar = Snackbar.make(this, "", Snackbar.LENGTH_SHORT)
    (snackBar.view as? Snackbar.SnackbarLayout)?.apply {
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        addView(customView, 0)
    }
    snackBar.show()
}

fun PhotoEditorView.getEditBitmap(): Bitmap? {
    val realBitmap = (this.source.drawable as? BitmapDrawable)?.bitmap
    return if (realBitmap != null) {
        val mergeBitmap = createBitmap(realBitmap.width, realBitmap.height)
        val mergeCanvas = Canvas(mergeBitmap)
        mergeCanvas.drawBitmap(realBitmap, 0F, 0F, null)

        this.children.filter {
            it.id !in listOf(PHOTO_EDITOR_IMG_SRC_ID, PHOTO_EDITOR_IMG_FILTER_ID)
        }.filter { view ->
            view.isVisible && view.width > 0 && view.height > 0
        }.forEach { view ->
            val viewBitmap = view.toBitmap()
            if (view is DrawingView) {
                mergeCanvas.drawBitmap(
                    viewBitmap.scale(realBitmap.width, realBitmap.height, false),
                    0F,
                    0F,
                    null,
                )
            } else {
                val scaleBitmap = viewBitmap.scale(
                    (view.width * view.scaleX).roundToInt(),
                    (view.height * view.scaleY).roundToInt(),
                ).rotate(view.rotation)
                val sourceBitmap = createBitmap(this.source.width, this.source.height)
                val sourceCanvas = Canvas(sourceBitmap)
                sourceCanvas.drawBitmap(
                    scaleBitmap,
                    view.x - (scaleBitmap.width - view.width) / 2F - this.source.x,
                    view.y - (scaleBitmap.height - view.height) / 2F - this.source.y,
                    null,
                )

                mergeCanvas.drawBitmap(
                    sourceBitmap.scale(realBitmap.width, realBitmap.height, false),
                    0F,
                    0F,
                    null,
                )
                scaleBitmap.recycle()
                sourceBitmap.recycle()
            }
            viewBitmap.recycle()
        }

        mergeBitmap
    } else {
        null
    }
}

private fun View.toBitmap(width: Int = this.width, height: Int = this.height): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

private fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
