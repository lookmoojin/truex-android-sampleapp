package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.net.Uri
import androidx.lifecycle.LiveData
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.foundation.extension.LiveEvent
import java.util.UUID
import javax.inject.Inject

class TrueCloudV3PhotoEditorFileViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val fileProvider: FileProvider,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
) : ScopedViewModel() {

    companion object {
        private const val IMAGE_COMPRESS_LEVEL = 100
        private const val PHOTO_EDITOR_CACHE_DIR = "/true_cloud_cache_photo_editor"
    }

    private val _onGenerateUri = LiveEvent<Uri>()
    val onGenerateUri: LiveData<Uri> get() = _onGenerateUri

    private var imageWidth = 0
    private var imageHeight = 0

    fun setObjectFile(uri: Uri) {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(uri.path, options)
        imageWidth = options.outWidth
        imageHeight = options.outHeight
    }

    fun onConfirmClick(bitmap: Bitmap) {
        launchSafe(coroutineDispatcher.io()) {
            val newBitmap = centerCropBitmap(bitmap)
            val cacheDir =
                contextDataProviderWrapper.get()
                    .getDataContext().cacheDir.absolutePath + PHOTO_EDITOR_CACHE_DIR

            val file = fileProvider.getFile(cacheDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val fileData = fileProvider.getFile(file.path, "${UUID.randomUUID()}.jpg")
            fileData.outputStream().use { outputStream ->
                newBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    IMAGE_COMPRESS_LEVEL,
                    outputStream,
                )
                outputStream.flush()
            }
            newBitmap.recycle()
            _onGenerateUri.postValue(Uri.fromFile(fileData))
        }
    }

    // Hack for image from photo editor has black bg.
    // https://github.com/burhanrashid52/PhotoEditor/issues/221
    private fun centerCropBitmap(srcBitmap: Bitmap): Bitmap {
        val sourceWidth: Int = srcBitmap.width
        val sourceHeight: Int = srcBitmap.height

        val xScale = imageWidth.toFloat() / sourceWidth
        val yScale = imageHeight.toFloat() / sourceHeight
        val scale = xScale.coerceAtLeast(yScale)
        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        val left: Float = (imageWidth - scaledWidth) / 2
        val top: Float = (imageHeight - scaledHeight) / 2

        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        val dest = Bitmap.createBitmap(imageWidth, imageHeight, srcBitmap.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(srcBitmap, null, targetRect, null)
        srcBitmap.recycle()

        return dest
    }
}
