package com.truedigital.common.share.datalegacy.utils.glide

import android.util.Log
import androidx.annotation.NonNull
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Preconditions
import com.newrelic.agent.android.NewRelic
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream

class CustomOkHttpStreamFetcher(
    private val client: Call.Factory,
    private val url: GlideUrl
) : DataFetcher<InputStream>, Callback {

    private var stream: InputStream? = null
    private var responseBody: ResponseBody? = null
    private var callback: DataFetcher.DataCallback<in InputStream>? = null

    @Volatile
    private var call: Call? = null

    companion object {
        private const val TAG = "OkHttpFetcher"
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {

        val requestBuilder: Request.Builder = Request.Builder().url(url.cacheKey)

        for ((key, value) in url.headers) {
            requestBuilder.addHeader(key, value)
        }
        val request: Request = requestBuilder.build()
        this.callback = callback
        call = client.newCall(request)

        call?.enqueue(responseCallback = this)
    }

    override fun onFailure(call: Call, e: IOException) {

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "OkHttp failed to obtain result", e)
        }

        val handlingExceptionMap = mapOf(
            "Key" to "CustomOkHttpStreamFetcher",
            "Value" to "Problem with Glide Loading by OKHttp with ${e.message}"
        )

        NewRelic.recordHandledException(Exception(e), handlingExceptionMap)

        callback?.onLoadFailed(e)
    }

    override fun onResponse(call: Call, response: Response) {
        responseBody = response.body

        if (response.isSuccessful) {
            val contentLength: Long = Preconditions.checkNotNull(responseBody).contentLength()

            responseBody?.let {
                stream = ContentLengthInputStream.obtain(it.byteStream(), contentLength)
            }

            callback?.onDataReady(stream)
        } else {

            callback?.onLoadFailed(HttpException(response.message, response.code))
        }
    }

    override fun cleanup() {
        try {
            stream?.close()
        } catch (e: IOException) {

            val handlingExceptionMap = mapOf(
                "Key" to "CustomOkHttpStreamFetcher",
                "Value" to "Problem with Glide Loading by OKHttp with ${e.message}"
            )

            NewRelic.recordHandledException(Exception(e), handlingExceptionMap)
        }
        responseBody?.close()
        callback = null
    }

    override fun cancel() {
        call?.cancel()
    }

    @NonNull
    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    @NonNull
    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }
}
