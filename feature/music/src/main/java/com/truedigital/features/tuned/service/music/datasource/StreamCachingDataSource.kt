package com.truedigital.features.tuned.service.music.datasource

import android.net.Uri
import com.facebook.crypto.Crypto
import com.facebook.crypto.Entity
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import com.truedigital.foundation.player.model.MediaAsset
import okhttp3.OkHttpClient
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Provider

/**
 *
 * A custom data source that will either read from a url and cache to the local file system,
 * or read from a cache on the local file system
 *
 * We want to start reading from the data stream before its finished loading, so we load
 * the data asynchronously, which means sometimes we need to wait on the data before continuing
 *
 */
class StreamCachingDataSource(
    private val mediaAsset: MediaAsset,
    private val crypto: Crypto,
    private val httpClientBuilder: OkHttpClient.Builder
) : DataSource {

    private lateinit var dataSource: DataSource

    override fun getUri(): Uri = Uri.parse(mediaAsset.location)

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        dataSource = if (mediaAsset.location?.startsWith("file") == true) {
            StreamEncryptedFileDataSource(mediaAsset, crypto)
        } else {
            StreamHttpDataSource(mediaAsset, httpClientBuilder) { _, data ->
                mediaAsset.cachePath?.let {
                    val fileStream = BufferedOutputStream(FileOutputStream(it))
                    // change to commented line to disable DRM
                    crypto.getCipherOutputStream(fileStream, Entity.create(it)).use {
                        it.write(data)
                    }
                    //                    fileStream.write(data)
                }
            }
        }

        return dataSource.open(dataSpec)
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int =
        dataSource.read(buffer, offset, length)

    @Throws(IOException::class)
    override fun close() = dataSource.close()

    class Factory(
        private val httpBuilder: Provider<OkHttpClient.Builder>,
        private val crypto: Crypto
    ) : PlaybackAssetDataSourceFactory {
        override fun createDataSource(mediaAsset: MediaAsset): DataSource =
            StreamCachingDataSource(mediaAsset, crypto, httpBuilder.get())
    }

    override fun addTransferListener(transferListener: TransferListener) {
        Unit
    }
}
