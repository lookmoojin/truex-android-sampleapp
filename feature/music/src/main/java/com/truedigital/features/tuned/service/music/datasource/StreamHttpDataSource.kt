package com.truedigital.features.tuned.service.music.datasource

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.foundation.player.model.MediaAsset
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.ArrayIndexOutOfBoundsException
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.IOException
import javax.inject.Provider

class StreamHttpDataSource(
    private val mediaAsset: MediaAsset,
    httpClientBuilder: OkHttpClient.Builder,
    private val onFinishedDownload: (String, ByteArray) -> Unit = { _, _ -> }
) : DataSource {

    companion object {
        private const val MIN_BYTES = 8
        private const val INITIAL_BUFFER_CAPACITY = 32
        private const val MILL_SEC_10 = 10L
        private const val MILL_SEC_100 = 100L
        private const val SESSION_ID_HEADER = "session_id"
        private const val INDEX_OUT_OF_BOUND = "buffer less than read"
        private const val THROWABLE_DOWNLOAD_ERROR = "throwable download error"
    }

    private var bytesRead = 0L
    private var bytesToRead = C.LENGTH_UNSET.toLong()

    private var httpOutputStream = InternalAccessByteArrayOutputStream()
    private var finishedDownload = false

    private var download: Call? = null

    private var downloadError: IOException? = null

    override fun getUri(): Uri = Uri.parse(mediaAsset.location)

    override fun addTransferListener(transferListener: TransferListener) {
        Unit
    }

    private val httpClient: OkHttpClient = httpClientBuilder.addNetworkInterceptor {
        val originalResponse = it.proceed(it.request())
        val body = originalResponse.body

        bytesToRead = body.contentLength() ?: 0

        var response =
            ProgressResponseBody(body).let { it2 ->
                originalResponse.newBuilder().body(it2)
                    .build()
            }

        // Tell it to actually buffer the source
        response.body.source().apply {
            try {
                request(Long.MAX_VALUE)
                buffer()
            } catch (e: IOException) {
                downloadError = e

                response = Response.Builder()
                    .code(NetworkModule.HTTP_CODE_ENHANCE_YOUR_CALM)
                    .protocol(Protocol.HTTP_1_1)
                    .request(it.request())
                    .message("Enhance your calm")
                    .body(ResponseBody.create(null, ""))
                    .build()
            }
        }

        response
    }.addInterceptor {
        val request = mediaAsset.sessionId?.let { sessionId ->
            it.request().newBuilder()
                .header(SESSION_ID_HEADER, sessionId)
                .build()
        } ?: run {
            it.request()
        }
        it.proceed(request)
    }.build()

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        val request =
            mediaAsset.location?.toHttpUrlOrNull()?.let { Request.Builder().url(it).build() }
        download = request?.let { httpClient.newCall(it) }
        download?.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                finishedDownload = true
                mediaAsset.location?.let { onFinishedDownload(it, httpOutputStream.toByteArray()) }
            }

            override fun onFailure(call: Call, e: IOException) {
                downloadError = e
            }
        })

        // Wait for the server response with the content length
        while (downloadError == null &&
            (bytesToRead == C.LENGTH_UNSET.toLong() && httpOutputStream.size() < MIN_BYTES)
        ) {
            Thread.sleep(MILL_SEC_10)
        }

        if (downloadError != null) {
            Timber.e(downloadError)

            throw downloadError ?: Throwable(THROWABLE_DOWNLOAD_ERROR)
        }

        return bytesToRead
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val bytesRemaining = bytesToRead - bytesRead

        downloadError?.let {
            throw it
        }

        var read: Int
        if (length == 0) {
            read = 0
        } else if (bytesRemaining == 0L) {
            read = C.RESULT_END_OF_INPUT
        } else {
            // Make sure our read wont exceed the length of the file
            read = Math.min(length.toLong(), bytesRemaining).toInt()

            if (buffer.size < read) {
                throw ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUND)
            }

            var hasFinishedReading = false
            while (!hasFinishedReading) {
                val size = httpOutputStream.size()

                if (size >= bytesRead + read) { // if there's at least one byte left
                    System.arraycopy(
                        httpOutputStream.getBuffer(),
                        bytesRead.toInt(),
                        buffer,
                        offset,
                        read
                    )
                    hasFinishedReading = true
                } else if (size > 0 && finishedDownload) {
                    read = C.RESULT_END_OF_INPUT
                    hasFinishedReading = true
                } else {
                    Thread.sleep(MILL_SEC_100)
                }
            }

            if (read != C.RESULT_END_OF_INPUT) {
                bytesRead += read.toLong()
            } else {
                // End of stream reached having not read sufficient data.
                throw EOFException()
            }
        }

        return read
    }

    override fun close() {
        download?.cancel()
    }

    inner class ProgressResponseBody(private val responseBody: ResponseBody) : ResponseBody() {
        var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType? = responseBody.contentType()

        override fun contentLength(): Long = responseBody.contentLength()

        override fun source(): BufferedSource {
            return bufferedSource ?: source(responseBody.source()).buffer().also {
                bufferedSource = it
            }
        }

        private fun source(source: Source): Source = object : ForwardingSource(source) {
            private var totalBytesRead = 0L

            // read() returns the number of bytes read, or -1 if this source is exhausted.
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                var bytesRead = 0L
                try {
                    if (download != null && download?.isCanceled() != true) {
                        bytesRead = super.read(sink, byteCount)
                        if (bytesRead != -1L) {
                            sink.copyTo(httpOutputStream, sink.size - bytesRead, bytesRead)
                            totalBytesRead += bytesRead
                        }
                    }
                } catch (e: IOException) {
                    downloadError = e
                    throw e
                }
                return bytesRead
            }
        }
    }

    // Access directly to buf instead of using the toByteArray() function
    // This helps getting rid of the heavy array copy call which will cause huge performance issue on 4.4
    class InternalAccessByteArrayOutputStream(size: Int = INITIAL_BUFFER_CAPACITY) :
        ByteArrayOutputStream(size) {
        fun getBuffer(): ByteArray = buf
    }

    class Factory(private val httpBuilder: Provider<OkHttpClient.Builder>) :
        PlaybackAssetDataSourceFactory {
        override fun createDataSource(mediaAsset: MediaAsset): DataSource =
            StreamHttpDataSource(mediaAsset, httpBuilder.get())
    }
}
