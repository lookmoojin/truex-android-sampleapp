package com.truedigital.features.tuned.service.music.datasource

import android.net.Uri
import com.facebook.crypto.Crypto
import com.facebook.crypto.Entity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import com.truedigital.foundation.player.model.MediaAsset
import timber.log.Timber
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.min

class StreamEncryptedFileDataSource(
    private val mediaAsset: MediaAsset,
    private val crypto: Crypto
) : DataSource {
    companion object {
        private const val INDEX_OUT_OF_BOUND = "buffer less than read"
        private const val MILL_SEC_100 = 100L
    }

    private lateinit var cacheInputStream: InputStream
    private lateinit var fileInputStream: InputStream

    private var bytesToRead = 0L
    private var bytesRead = 0L

    override fun getUri(): Uri = Uri.parse(mediaAsset.location)

    override fun addTransferListener(transferListener: TransferListener) {
        Unit
    }

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        val cacheFile = File(uri.path.orEmpty())
        fileInputStream = FileInputStream(cacheFile)
        // change to commented line to disable DRM
        cacheInputStream = crypto.getCipherInputStream(fileInputStream, Entity.create(uri.path))
//        cacheInputStream = fileInputStream
        bytesToRead = cacheFile.length()
        return bytesToRead
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val bytesRemaining = bytesToRead - bytesRead

        val read: Int
        if (length == 0) {
            read = 0
        } else if (bytesRemaining == 0L) {
            read = C.RESULT_END_OF_INPUT
        } else {
            // Make sure our read wont exceed the length of the file
            read = min(length.toLong(), bytesRemaining).toInt()

            if (buffer.size < read) {
                throw ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUND)
            }

            var hasFinishedReading = false
            while (!hasFinishedReading) {
                if (bytesToRead >= bytesRead + read) { // if there's at least one byte left
                    cacheInputStream.read(buffer, offset, read)
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
        try {
            fileInputStream.close()
            cacheInputStream.close()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}
