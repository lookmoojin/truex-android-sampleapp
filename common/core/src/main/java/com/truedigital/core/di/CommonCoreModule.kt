package com.truedigital.core.di

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.coroutines.DefaultCoroutineDispatcherProvider
import com.truedigital.core.manager.location.LocationManager
import com.truedigital.core.manager.location.LocationManagerImpl
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.core.utils.GsonUtil
import com.truedigital.core.utils.SharedPrefsInterface
import com.truedigital.core.utils.SharedPrefsUtils
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class CommonCoreModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDispatchersIO(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    fun provideSharedPrefsUtils(context: Context): SharedPrefsUtils {
        return SharedPrefsUtils(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreUtil(context: Context): DataStoreUtil {
        return DataStoreUtil(context)
    }

    @Provides
    @Singleton
    fun provideSharedPrefsInterface(context: Context): SharedPrefsInterface {
        return SharedPrefsUtils(context)
    }

    @Provides
    fun provideLocationManager(): LocationManager = LocationManagerImpl.instance

    @Provides
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider {
        return DefaultCoroutineDispatcherProvider()
    }

    @Provides
    fun providesGsonUtil(): GsonUtil {
        return GsonUtil.newInstance()
    }
}
