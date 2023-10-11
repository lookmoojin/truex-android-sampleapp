package com.truedigital.features.truecloudv3.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.util.Size
import java.io.File
import javax.inject.Inject

interface BitmapUtil {
    fun createImageThumbnail(file: File): Bitmap
    fun createVideoThumbnail(file: File): Bitmap?
}

class BitmapUtilImpl @Inject constructor() : BitmapUtil {
    companion object {
        private const val THUMBNAIL_SIZE = 500
    }

    override fun createImageThumbnail(file: File): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ThumbnailUtils.createImageThumbnail(
                file,
                Size(THUMBNAIL_SIZE, THUMBNAIL_SIZE),
                CancellationSignal()
            )
        } else {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.path, options)
            val scaleFactor =
                Math.min(options.outWidth / THUMBNAIL_SIZE, options.outHeight / THUMBNAIL_SIZE)
            options.apply {
                inJustDecodeBounds = false
                inSampleSize = scaleFactor
            }
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeFile(file.path, options),
                THUMBNAIL_SIZE,
                THUMBNAIL_SIZE,
                false
            )
        }
    }

    override fun createVideoThumbnail(file: File): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ThumbnailUtils.createVideoThumbnail(
                file,
                Size(THUMBNAIL_SIZE, THUMBNAIL_SIZE),
                CancellationSignal()
            )
        } else {
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(file.path)
            }
            val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            bitmap?.run {
                Bitmap.createScaledBitmap(
                    this,
                    THUMBNAIL_SIZE,
                    THUMBNAIL_SIZE,
                    false
                )
            }
        }
    }
}
