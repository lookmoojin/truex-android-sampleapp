package com.truedigital.features.tuned.data.download

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.annotation.CheckResult
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ImageManager @Inject constructor(
    private val context: Context,
    private val httpClient: OkHttpClient,
    private val config: Configuration,
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface
) {
    companion object {
        private var thumborUrl: String? = null
        private const val THUMBOR_URL_KEY = "thumbor_url"
        private const val JPEG_QUALITY = 70
        private const val FILTER_IDENTIFIER = "filters:format(jpeg):quality"
        private const val INIT_FAILED = "init failed"
        private const val EMPTY_TARGET_IMAGE = "empty target image"
    }

    private var glide: RequestManager? = null
    private var url: String? = null
    private var isFullUrl = false
    private var isCustomUrl = true

    init {
        thumborUrl = sharedPreferences.get(THUMBOR_URL_KEY)
    }

    // use the correct init method to take advantage of the glide cleaning and recycling based on lifecycle
    @CheckResult
    fun init(activity: Activity): ImageManager {
        this.glide = Glide.with(activity)
        url = null
        isFullUrl = false
        return this
    }

    @CheckResult
    fun init(view: View): ImageManager {
        this.glide = try {
            Glide.with(view)
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            null
        }
        url = null
        isFullUrl = false
        return this
    }

    // try not to pass context directly, however, this is used for dialog and service
    @CheckResult
    fun init(context: Context): ImageManager {
        this.glide = try {
            Glide.with(context)
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            null
        }
        url = null
        isFullUrl = false
        return this
    }

    @CheckResult
    fun load(url: String): ImageManager {
        this.url = url
        isFullUrl = url.contains(FILTER_IDENTIFIER)
        return this
    }

    @CheckResult
    fun options(
        width: Int,
        height: Int = width,
        mode: String = "",
        filters: Array<String>? = null,
        isCustomUrl: Boolean = true
    ): ImageManager {
        this.isCustomUrl = isCustomUrl

        if (url == null) throw NullPointerException("Please call load() method first")
        if (url?.isBlank() == true) return this
        if (isCustomUrl.not()) {
            this.glide?.apply {
                this.setDefaultRequestOptions(RequestOptions().override(width, height))
            }
            return this
        }

        val modeString = getModeString(mode)
        val filterString = getFilterString(filters)
        val formatString = getFormatString()

        val uri = Uri.parse(url)
        if (thumborUrl != null) {
            url = if (isFullUrl) {
                // inject filters into fullUrlFilters so that all filters
                // passed into ImageManager will apply to final image
                val fullUrlFilters = url?.substring(url?.indexOf(FILTER_IDENTIFIER) ?: -1)
                    ?.replaceFirst(")/", ")$filterString/")
                "$thumborUrl/${width}x$height/$modeString$fullUrlFilters"
            } else {
                "$thumborUrl/${width}x$height/${modeString}filters$formatString$filterString/${uri.host}${uri.path}"
            }
        } else {
            Timber.d("thumbor url not initialized, getting thumbor")
            thumborUrl = try {
                getThumborUrl()
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
            Timber.d("thumbor url initialization completed $thumborUrl")
            url = if (isFullUrl) {
                val fullUrlFilters = url?.indexOf(FILTER_IDENTIFIER)?.let { url?.substring(it) }
                "$thumborUrl/${width}x$height/$modeString$fullUrlFilters"
            } else {
                "$thumborUrl/${width}x$height/${modeString}filters$formatString$filterString/${uri.host}${uri.path}"
            }
        }
        return this
    }

    private fun getModeString(mode: String): String {
        return if (mode.isNotBlank()) "$mode/" else ""
    }

    private fun getFilterString(filters: Array<String>? = null): String {
        return if (!filters.isNullOrEmpty()) {
            filters.joinToString(":", ":")
        } else {
            ""
        }
    }

    private fun getFormatString(): String {
        return if (url?.endsWith("png") == true) {
            ":format(png)"
        } else {
            ":format(jpeg):quality($JPEG_QUALITY)"
        }
    }

    @CheckResult
    fun extractUrl(urlCallback: (String) -> Unit): ImageManager {
        url?.let { urlCallback.invoke(it) }
            ?: throw NullPointerException("Please call load() method first")
        return this
    }

    @SuppressLint("CheckResult")
    fun into(view: ImageView, callback: ((Boolean) -> Unit)? = null) {
        if (url?.isBlank() == true) {
            Timber.d(EMPTY_TARGET_IMAGE)
            return
        }
        if (glide == null || thumborUrl == null) {
            Timber.d(INIT_FAILED)
            return
        }

        Timber.d("thumbor url: $url")
        glide?.load(url)
            ?.diskCacheStrategy(DiskCacheStrategy.ALL)
            // crossfading causing issue on 4.4 image disappearing on scroll
            ?.transition(DrawableTransitionOptions.withCrossFade())
            ?.listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    callback?.invoke(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    callback?.invoke(true)
                    return false
                }
            })
            ?.into(view)
    }

    @SuppressLint("CheckResult")
    fun intoRoundedCorner(
        view: ImageView,
        cornerSize: Int = context.resources.getDimensionPixelSize(R.dimen.product_rounded_corner_size),
        callback: ((Boolean) -> Unit)? = null
    ) {
        if (url?.isBlank() == true) {
            Timber.d(EMPTY_TARGET_IMAGE)
            return
        }
        if (glide == null || thumborUrl == null) {
            Timber.d(INIT_FAILED)
            return
        }

        Timber.d("thumbor url: $url")
        glide?.asBitmap()?.load(url)
            ?.diskCacheStrategy(DiskCacheStrategy.ALL)
            ?.into(object : CustomTarget<Bitmap>() {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    callback?.invoke(false)
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val roundedImage =
                        RoundedBitmapDrawableFactory.create(context.resources, resource)
                    roundedImage.cornerRadius = cornerSize.toFloat()
                    view.setImageDrawable(roundedImage)
                    callback?.invoke(true)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Unit
                }
            })
    }

    @SuppressLint("CheckResult")
    fun intoBitmap(callback: ((Bitmap) -> Unit)? = null) {
        if (url?.isBlank() == true) {
            Timber.d(EMPTY_TARGET_IMAGE)
            return
        }
        if ((glide == null || thumborUrl == null) && isCustomUrl) {
            Timber.d(INIT_FAILED)
            return
        }

        Timber.d("thumbor url: $url")
        glide?.asBitmap()?.load(url)
            ?.diskCacheStrategy(DiskCacheStrategy.ALL)
            ?.into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    callback?.invoke(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Unit
                }
            })
    }

    fun preloadToDisk() {
        if (url?.isBlank() == true) {
            Timber.d(EMPTY_TARGET_IMAGE)
            return
        }
        if (glide == null || thumborUrl == null) {
            Timber.d(INIT_FAILED)
            return
        }
        Timber.d("thumbor url: $url")
        glide?.downloadOnly()
            ?.load(url)
            ?.submit()
    }

    private fun getThumborUrl(): String =
        ResponseBodySingle(
            httpClient.newCall(
                Request.Builder().url(config.thumborUrl).build()
            )
        ).map {
            var url = it.string()
            url = url.substring(1, url.length - 1)
            sharedPreferences.put(THUMBOR_URL_KEY, url)
            url
        }.subscribeOn(Schedulers.io())
            .blockingGet()

    private inner class ResponseBodySingle(val originalCall: Call) : Single<ResponseBody>() {
        override fun subscribeActual(observer: SingleObserver<in ResponseBody>) {
            val call = originalCall.clone()
            val callback = CallCallback(call, observer)
            observer.onSubscribe(callback)
            call.enqueue(callback)
        }

        private inner class CallCallback(
            val call: Call,
            val observer: SingleObserver<in ResponseBody>
        ) : Disposable, Callback {
            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled()) return

                try {
                    response.body?.let { observer.onSuccess(it) }
                } catch (e: Exception) {
                    Timber.e(e)
                    try {
                        observer.onError(e)
                    } catch (inner: Throwable) {
                        Exceptions.throwIfFatal(inner)
                        RxJavaPlugins.onError(CompositeException(e, inner))
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                if (call.isCanceled()) return

                try {
                    observer.onError(e)
                } catch (inner: Throwable) {
                    Exceptions.throwIfFatal(inner)
                    RxJavaPlugins.onError(CompositeException(e, inner))
                }
            }

            override fun isDisposed(): Boolean = call.isCanceled()

            override fun dispose() {
                call.cancel()
            }
        }
    }
}
