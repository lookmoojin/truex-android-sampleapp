package com.truedigital.foundation.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.newrelic.agent.android.NewRelic
import com.truedigital.foundation.glide.BlurTransformation
import com.truedigital.foundation.glide.ColorFilterTransformation
import com.truedigital.foundation.glide.PositionedCropTransformation
import java.io.File

fun ImageView?.load(
    context: Context?,
    url: String?,
    placeholder: Int? = null,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
) {

    when (isValidate(this, context)) {
        true -> initGlide(context!!, url, placeholder, scaleType, resizeType).into(this!!)
        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    initGlide(
                        applicationContext,
                        url,
                        placeholder,
                        scaleType,
                        resizeType
                    ).into(this)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.load()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadCustomSizeCrossFadeWithCallback(
    context: Context?,
    currentUrl: String?,
    lastUrl: String? = null,
    placeholder: Int? = null,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    width: Int,
    height: Int,
    onSuccess: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
) {
    val isValidContext = isValidate(this, context)
    val validContext: Context? = if (isValidContext) context else this?.context?.applicationContext
    this?.let {
        validContext?.let { _context ->
            try {
                initGlide(
                    context = _context,
                    url = currentUrl,
                    placeholder = placeholder,
                    scaleType = scaleType
                )
                    .override(width, height)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .thumbnail(
                        initGlide(
                            context = _context,
                            url = lastUrl,
                            placeholder = placeholder,
                            scaleType = scaleType
                        )
                    )
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Handler(Looper.getMainLooper()).post {
                                onError?.invoke()
                            }
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Handler(Looper.getMainLooper()).post {
                                onSuccess?.invoke()
                            }
                            return false
                        }
                    })
                    .into(this)
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadCustomSizeWithCallback()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadBlur(
    context: Context?,
    url: String?,
    placeholder: Int? = null,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
) {

    when (isValidate(this, context)) {
        true -> initGlideAsBitmap(context!!, url, placeholder, scaleType, resizeType).apply(
            RequestOptions()
                .apply {
                    transform(BlurTransformation(context))
                }
        ).into(this!!)

        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    initGlideAsBitmap(
                        applicationContext,
                        url,
                        placeholder,
                        scaleType,
                        resizeType
                    ).apply(
                        RequestOptions()
                            .apply {
                                transform(BlurTransformation(applicationContext))
                            }
                    ).into(this)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.load()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadWithErrorImage(
    context: Context?,
    url: String?,
    errorImage: Int? = null,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
) {

    when (isValidate(this, context)) {
        true ->
            this?.let { view ->
                initGlideWithErrorImage(
                    context = context!!,
                    url = url,
                    errorImage = errorImage,
                    scaleType = scaleType,
                    resizeType = resizeType
                )
                    .into(view)
            }

        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    initGlideWithErrorImage(
                        applicationContext,
                        url,
                        errorImage,
                        scaleType,
                        resizeType
                    ).into(
                        this
                    )
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadWithErrorImage()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadWithImageCallback(
    context: Context,
    url: String?,
    errorImage: Int? = null,
    onSuccess: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
) {
    val isValidContext = isValidate(this, context)
    val validContext: Context? = if (isValidContext) context else this?.context?.applicationContext

    this?.let {
        validContext?.let {
            try {
                initGlide(
                    context = context,
                    url = url,
                    placeholder = errorImage,
                    scaleType = scaleType,
                    resizeType = resizeType
                ).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Handler(Looper.getMainLooper()).post {
                            onError?.invoke()
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Handler(Looper.getMainLooper()).post {
                            onSuccess?.invoke()
                        }
                        return false
                    }
                }).into(this)
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadWithErrorImage()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadWithErrorImageCallback(
    context: Context,
    url: String?,
    errorImage: Int? = null,
    onSuccess: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
) {
    val isValidContext = isValidate(this, context)
    val validContext: Context? = if (isValidContext) context else this?.context?.applicationContext

    this?.let {
        validContext?.let {
            try {
                initGlideWithErrorImage(
                    context = context,
                    url = url,
                    errorImage = errorImage,
                    scaleType = scaleType,
                    resizeType = resizeType
                ).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Handler(Looper.getMainLooper()).post {
                            onError?.invoke()
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Handler(Looper.getMainLooper()).post {
                            onSuccess?.invoke()
                        }
                        return false
                    }
                }).into(this)
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadWithErrorImage()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadTintColor(
    context: Context?,
    url: String?,
    color: Int,
    placeholder: Int? = null,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
) {

    when (isValidate(this, context)) {
        true -> this?.let {
            loadTintColorWithGlide(
                it,
                context,
                url,
                color,
                placeholder,
                scaleType,
                resizeType
            )
        }

        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    loadTintColorWithGlide(
                        this,
                        applicationContext,
                        url,
                        color,
                        placeholder,
                        scaleType,
                        resizeType
                    )
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadTintColor()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadCircle(
    context: Context?,
    url: String?,
    placeholder: Int?,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    cacheDuration: Int = 0,
    listener: ((Drawable) -> Unit)? = null,
    resizeType: String = RESIZE
) {

    when (isValidate(this, context)) {
        true -> loadCircleWithGlide(
            this,
            context,
            url,
            placeholder,
            scaleType,
            cacheDuration,
            listener,
            resizeType
        )

        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    loadCircleWithGlide(
                        this,
                        applicationContext,
                        url,
                        placeholder,
                        scaleType,
                        cacheDuration,
                        listener,
                        resizeType
                    )
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadCircle()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadFileCircle(
    context: Context?,
    file: File?,
    placeholder: Int?,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    listener: ((Drawable?) -> Unit)? = null
) {
    when (isValidate(this, context)) {
        true -> loadFileCircleWithGlide(this, context, file, placeholder, scaleType, listener)
        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    loadFileCircleWithGlide(
                        this,
                        applicationContext,
                        file,
                        placeholder,
                        scaleType,
                        listener
                    )
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadFileCircle()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

private fun loadFileCircleWithGlide(
    imageView: ImageView?,
    context: Context?,
    file: File?,
    placeholder: Int?,
    scaleType: ImageView.ScaleType,
    listener: ((Drawable?) -> Unit)? = null
) {
    Glide.with(context!!)
        .asBitmap()
        .load(file)
        .apply(
            RequestOptions()
                .apply {
                    diskCacheStrategy(DiskCacheStrategy.NONE)
                    skipMemoryCache(true)
                }
                .apply {
                    placeholder?.let {
                        placeholder(it)
                        error(it)
                    }
                }
                .apply {
                    if (scaleType == ImageView.ScaleType.FIT_CENTER) {
                        this.fitCenter()
                    } else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                        this.centerCrop()
                    }
                }
        )
        .into(object : BitmapImageViewTarget(imageView) {
            override fun setResource(resource: Bitmap?) {
                resource?.let { _resource ->
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory
                        .create(context.resources, _resource)
                        .apply {
                            isCircular = true
                        }
                    view.setImageDrawable(circularBitmapDrawable)
                    listener?.invoke(circularBitmapDrawable)
                } ?: run {
                    listener?.invoke(null)
                }
            }
        })
}

fun ImageView?.loadGif(
    context: Context?,
    resource: Int?,
    placeholder: Int? = null
) {

    when (isValidate(this, context)) {
        true -> this?.let { loadGifWithGlide(it, context, resource, placeholder) }
        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    loadGifWithGlide(this, applicationContext, resource, placeholder)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadGift()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadGif(
    context: Context?,
    url: String?,
    placeholder: Int? = null
) {

    when (isValidate(this, context)) {
        true -> this?.let { loadGifWithGlide(it, context, url, placeholder) }
        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    loadGifWithGlide(this, applicationContext, url, placeholder)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadGift()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadCropTop(
    context: Context?,
    url: String?,
    placeholder: Int? = null,
    resizeType: String = RESIZE
) {

    when (isValidate(this, context)) {
        true -> this?.let { loadCropTopWithGlide(it, context, url, placeholder, resizeType) }
        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    loadCropTopWithGlide(this, applicationContext, url, placeholder, resizeType)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadCropTop()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView.loadCropTop(
    resId: Int,
) {
    Glide.with(this)
        .load(resId)
        .transform(PositionedCropTransformation(0.5f, 0f))
        .into(this)
}

fun ImageView?.loadMultipleTransformation(
    context: Context?,
    url: String?,
    transformations: MultiTransformation<Bitmap>,
    placeholder: Int? = null
) {

    when (isValidate(this, context)) {
        true -> this?.let {
            loadMultipleTransformationWithGlide(
                it,
                context,
                url,
                transformations,
                placeholder
            )
        }

        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    loadCropTopWithGlide(this, applicationContext, url, placeholder)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadCropTop()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadDrawable(
    context: Context?,
    @DrawableRes resourceId: Int
) {

    when (isValidate(this, context)) {
        true ->
            this?.let { imageView ->
                context?.let { context ->
                    Glide.with(context)
                        .load(resourceId)
                        .into(imageView)
                }
            }

        false -> {
            try {
                this?.context?.applicationContext?.let { applicationContext ->
                    Glide.with(applicationContext)
                        .load(resourceId)
                        .into(this)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadDrawable()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun loadAsBitmap(
    context: Context,
    url: String,
    resizeType: String = RESIZE
): Bitmap? {
    // Call this on background thread
    return try {
        Glide.with(context).asBitmap().loadWithResize(context, url, resizeType).submit().get()
    } catch (exception: Exception) {
        val handlingExceptionMap = mapOf(
            "Key" to "ImageViewExtension.loadAsBitmap()",
            "Value" to exception.localizedMessage
        )
        NewRelic.recordHandledException(
            Exception(exception.localizedMessage),
            handlingExceptionMap
        )
        null
    }
}

// You Must call this method on a background thread
fun loadAsDrawable(
    context: Context,
    url: String
): Drawable? {
    return try {
        Glide.with(context).load(url).submit().get()
    } catch (exception: Exception) {
        val handlingExceptionMap = mapOf(
            "Key" to "ImageViewExtension.loadAsDrawable()",
            "Value" to exception.localizedMessage
        )
        NewRelic.recordHandledException(
            Exception(exception.localizedMessage),
            handlingExceptionMap
        )
        null
    }
}

fun loadAsBitmap(
    context: Context?,
    url: String,
    target: SimpleTarget<Bitmap>,
    resizeType: String = RESIZE
) {
    if (context == null || !context.isAvailable()) return

    when (context.isAvailable()) {
        true ->
            initGlideAsBitmap(
                context = context,
                url = url,
                placeholder = null,
                resizeType = resizeType
            )
                .into(target)

        false -> {
            try {
                context.applicationContext?.let { applicationContext ->
                    initGlideAsBitmap(
                        context = applicationContext,
                        url = url,
                        placeholder = null,
                        resizeType = resizeType
                    )
                        .into(target)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadAsBitmap()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun loadAsBitmap(
    context: Context?,
    url: String,
    placeholder: Int? = null,
    target: ImageViewTarget<Bitmap>,
    resizeType: String = RESIZE
) {
    if (context == null || !context.isAvailable()) return

    when (context.isAvailable()) {
        true ->
            initGlideAsBitmap(
                context = context,
                url = url,
                placeholder = null,
                resizeType = resizeType
            )
                .into(target)

        false -> {
            try {
                context.applicationContext?.let { applicationContext ->
                    initGlideAsBitmap(
                        context = applicationContext,
                        url = url,
                        placeholder = null,
                        resizeType = resizeType
                    )
                        .into(target)
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadAsBitmap()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.loadCrossFadeImage(
    context: Context?,
    currentUrl: String,
    lastUrl: String,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
) {
    if (this == null || context == null || !context.isAvailable()) return

    when (context.isAvailable()) {
        true -> loadCrossFadeImageWithGlide(
            this,
            context,
            currentUrl,
            lastUrl,
            scaleType,
            resizeType
        )

        false -> {
            try {
                this.context?.applicationContext?.let { applicationContext ->
                    loadCrossFadeImageWithGlide(
                        this,
                        applicationContext,
                        currentUrl,
                        lastUrl,
                        scaleType, resizeType
                    )
                }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.loadCrossFadeImage()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun ImageView?.cancelLoad() {
    when (this?.context.isAvailable()) {
        true -> this?.let { Glide.with(it.context).clear(it) }
        false -> {
            try {
                this?.context?.applicationContext?.let { Glide.with(it).clear(this) }
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ImageViewExtension.cancelLoad()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
            }
        }
    }
}

fun <T> RequestBuilder<T>.caching(cacheDuration: Int = 60): RequestBuilder<T> {
    return if (cacheDuration > 0) {
        val cacheTimestamp = (System.currentTimeMillis() / (cacheDuration * 60 * 1000))
        this.diskCacheStrategy(DiskCacheStrategy.ALL)
            .signature(ObjectKey(cacheTimestamp))
    } else {
        this.diskCacheStrategy(DiskCacheStrategy.NONE)
    }
}

private fun initGlide(
    context: Context,
    url: String?,
    placeholder: Int? = null,
    scaleType: ImageView.ScaleType? = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
): RequestBuilder<Drawable> {

    return Glide.with(context)
        .loadWithResize(context, url, resizeType)
        .apply(
            RequestOptions()
                .apply {
                    format(DecodeFormat.PREFER_RGB_565)
                    diskCacheStrategy(DiskCacheStrategy.DATA)
                    placeholder?.let {
                        placeholder(it)
                        error(it)
                    }
                }
                .apply {
                    if (scaleType == ImageView.ScaleType.FIT_CENTER) {
                        this.fitCenter()
                    } else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                        this.centerCrop()
                    }
                }
        )
}

private fun initGlideAsBitmap(
    context: Context,
    url: String?,
    placeholder: Int? = null,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
): RequestBuilder<Bitmap> {
    return Glide.with(context)
        .asBitmap()
        .loadWithResize(context, url, resizeType)
        .apply(
            RequestOptions()
                .apply {
                    placeholder?.let {
                        this.placeholder(it)
                        this.error(it)
                    }
                }
                .apply {
                    if (scaleType == ImageView.ScaleType.FIT_CENTER) {
                        this.fitCenter()
                    } else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                        this.centerCrop()
                    }
                }
        )
}

private fun initGlideWithErrorImage(
    context: Context,
    url: String?,
    errorImage: Int? = null,
    scaleType: ImageView.ScaleType? = ImageView.ScaleType.FIT_CENTER,
    resizeType: String = RESIZE
): RequestBuilder<Drawable> {
    return Glide.with(context)
        .loadWithResize(context, url, resizeType)
        .apply(
            RequestOptions()
                .apply {
                    errorImage?.let {
                        error(it)
                    }
                }
                .apply {
                    if (scaleType == ImageView.ScaleType.FIT_CENTER) {
                        this.fitCenter()
                    } else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                        this.centerCrop()
                    }
                }
        )
}

private fun isValidate(imageView: ImageView?, context: Context?): Boolean {
    return (imageView != null && context != null && context.isAvailable())
}

private fun loadCropTopWithGlide(
    imageView: ImageView,
    context: Context?,
    url: String?,
    placeholder: Int?,
    resizeType: String = RESIZE
) {
    Glide.with(context!!)
        .loadWithResize(context, url, resizeType)
        .apply(
            RequestOptions()
                .transform(PositionedCropTransformation(0.5f, 0f))
                .apply {
                    placeholder?.let {
                        placeholder(it)
                        error(it)
                    }
                }
        )
        .into(object : DrawableImageViewTarget(imageView) {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                super.onResourceReady(resource, transition)
                this.setDrawable(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                super.onLoadFailed(errorDrawable)
            }

            override fun onLoadStarted(placeholder: Drawable?) {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                super.onLoadStarted(placeholder)
            }
        })
}

private fun loadMultipleTransformationWithGlide(
    imageView: ImageView,
    context: Context?,
    url: String?,
    transformations: MultiTransformation<Bitmap>,
    placeholder: Int?
) {
    Glide.with(context!!)
        .load(url)
        .apply(
            RequestOptions()
                .transform(transformations)
                .apply {
                    placeholder?.let {
                        placeholder(it)
                        error(it)
                    }
                }
        )
        .into(object : DrawableImageViewTarget(imageView) {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                super.onResourceReady(resource, transition)
                this.setDrawable(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                super.onLoadFailed(errorDrawable)
            }

            override fun onLoadStarted(placeholder: Drawable?) {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                super.onLoadStarted(placeholder)
            }
        })
}

private fun loadCrossFadeImageWithGlide(
    imageView: ImageView,
    context: Context,
    currentUrl: String,
    lastUrl: String,
    scaleType: ImageView.ScaleType,
    resizeType: String = RESIZE
) {

    val requestOption = RequestOptions().apply {
        if (scaleType == ImageView.ScaleType.FIT_CENTER) {
            this.fitCenter()
        } else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            this.centerCrop()
        }
    }

    Glide.with(context)
        .loadWithResize(context, currentUrl, resizeType)
        .transition(DrawableTransitionOptions.withCrossFade())
        .thumbnail(
            Glide.with(context)
                .loadWithResize(context, lastUrl, resizeType)
                .apply(requestOption)
        )
        .apply(requestOption)
        .into(imageView)
}

private fun loadCircleWithGlide(
    imageView: ImageView?,
    context: Context?,
    url: String?,
    placeholder: Int?,
    scaleType: ImageView.ScaleType,
    cacheDuration: Int,
    listener: ((Drawable) -> Unit)? = null,
    resizeType: String = RESIZE
) {
    initGlideAsBitmap(context!!, url, placeholder, scaleType, resizeType)
        .caching(cacheDuration)
        .into(object : BitmapImageViewTarget(imageView) {
            override fun setResource(resource: Bitmap?) {
                val circularBitmapDrawable = RoundedBitmapDrawableFactory
                    .create(context.resources, resource)
                    .apply {
                        isCircular = true
                    }
                view.setImageDrawable(circularBitmapDrawable)
                listener?.invoke(circularBitmapDrawable)
            }
        })
}

private fun loadGifWithGlide(
    imageView: ImageView,
    context: Context?,
    resource: Int?,
    placeholder: Int? = null
) {
    Glide.with(context!!)
        .asGif()
        .load(resource)
        .apply(
            RequestOptions()
                .apply {
                    placeholder?.let {
                        error(it)
                    }
                }
        )
        .into(imageView)
}

private fun loadGifWithGlide(
    imageView: ImageView,
    context: Context?,
    url: String?,
    placeholder: Int? = null
) {
    Glide.with(context!!)
        .asGif()
        .load(url)
        .apply(
            RequestOptions()
                .apply {
                    placeholder?.let {
                        error(it)
                    }
                }
        )
        .into(imageView)
}

private fun loadTintColorWithGlide(
    imageView: ImageView,
    context: Context?,
    url: String?,
    color: Int,
    placeholder: Int? = null,
    scaleType: ImageView.ScaleType,
    resizeType: String = RESIZE
) {
    initGlide(
        context = context!!,
        url = url,
        placeholder = placeholder,
        scaleType = scaleType,
        resizeType = resizeType
    )
        .apply(
            RequestOptions()
                .apply {
                    transform(ColorFilterTransformation(context, color))
                }
        )
        .into(imageView)
}

fun ImageView.loadCircleWithBackgroundColor(
    url: String,
    placeholder: Int = -1,
    error: Int? = null,
    resizeType: String = RESIZE
) {
    Glide.with(this)
        .asBitmap()
        .loadWithResize(context, url, resizeType)
        .placeholder(placeholder)
        .error(error)
        .fitCenter()
        .into(object : BitmapImageViewTarget(this) {
            override fun setResource(resource: Bitmap?) {

                resource?.let {
                    val bitmap = createSquaredBitmap(it)
                    val drawable = RoundedBitmapDrawableFactory.create(
                        this@loadCircleWithBackgroundColor.context.resources, bitmap
                    )
                    drawable.isCircular = true
                    this.setDrawable(drawable)
                }
            }
        })
}

private fun createSquaredBitmap(srcBmp: Bitmap): Bitmap {
    val dstSize = Math.max(srcBmp.width, srcBmp.height)
    val dstBmp = Bitmap.createBitmap(dstSize, dstSize, Bitmap.Config.ARGB_8888)

    // Squared image
    var x = 0
    var y = 0
    if (srcBmp.width > srcBmp.height) { // landScape image
        x = 0
        y = (dstSize - srcBmp.height) / 2
    } else if (srcBmp.width < srcBmp.height) { // portrait image
        x = (dstSize - srcBmp.width) / 2
        y = 0
    }

    val sourceRect = Rect(0, 0, srcBmp.width, srcBmp.height)
    val destinationRect = Rect(x, y, x + srcBmp.width, y + srcBmp.height)
    val canvas = Canvas(dstBmp)
    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(srcBmp, sourceRect, destinationRect, null)
    return dstBmp
}
