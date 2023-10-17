package com.truedigital.features.tuned.injection.module

import android.content.Context
import com.truedigital.features.tuned.BuildConfig
import com.truedigital.features.tuned.application.configuration.Configuration
import dagger.Module
import dagger.Provides
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.util.Properties
import javax.inject.Singleton

@Module
class ConfigurationModule {

    @Provides
    @Singleton
    fun provideConfiguration(context: Context): Configuration {
        val properties = Properties()
        var input: InputStream? = null

        try {
            input = context.resources.assets.open(BuildConfig.PROPERTIES)
            // load properties from file
            properties.load(input)
        } catch (e: IOException) {
            Timber.e(e)
        } finally {
            input?.close()
        }

        return Configuration(
            properties["thumborURL"] as String,
            properties["servicesApiURL"] as String,
            properties["metadataApiURL"] as String,
            properties["authApiURL"] as String,
            properties["storeId"] as String,
            (properties["applicationId"] as String).toInt()
        )
    }
}
