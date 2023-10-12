package com.truedigital.common.share.datalegacy.utils.glide

import androidx.annotation.NonNull
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.InputStream

class CustomOkHttpUrlLoader(@NonNull private val client: Call.Factory) :
    ModelLoader<GlideUrl?, InputStream?> {

    companion object {
        @Volatile
        private var internalClient: Call.Factory? = null
            get() {
                if (field == null) {
                    synchronized(Factory::class.java) {
                        if (field == null) {
                            field = OkHttpClient()
                        }
                    }
                }
                return field
            }
    }

    override fun handles(model: GlideUrl): Boolean {
        return true
    }

    override fun buildLoadData(
        model: GlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<InputStream?>? {

        return LoadData(model, CustomOkHttpStreamFetcher(client, model))
    }

    class Factory @JvmOverloads constructor(@NonNull private val client: Call.Factory? = internalClient) :
        ModelLoaderFactory<GlideUrl, InputStream> {

        @NonNull
        override fun build(multiFactory: MultiModelLoaderFactory): CustomOkHttpUrlLoader {
            return CustomOkHttpUrlLoader(client!!)
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
