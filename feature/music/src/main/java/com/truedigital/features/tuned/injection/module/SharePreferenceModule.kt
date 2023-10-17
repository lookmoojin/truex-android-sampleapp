package com.truedigital.features.tuned.injection.module

import android.content.Context
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStore
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class SharePreferenceModule {

    companion object {
        const val KVS_AUTH_TOKEN = "_authentication_token"
        const val KVS_DEVICE = "_device"
        const val KVS_IMAGE_MANAGER = "_image_manager"
        const val KEY_SETTING_PREFERENCES = "_settings_preferences"
        const val KVS_USER = "_user_preferences"
    }

    @Provides
    @Singleton
    @Named(KVS_AUTH_TOKEN)
    fun provideAuthTokenPreferences(context: Context): ObfuscatedKeyValueStoreInterface =
        ObfuscatedKeyValueStore(context, context.packageName + KVS_AUTH_TOKEN)

    @Provides
    @Singleton
    @Named(KVS_DEVICE)
    fun provideDevicePreferences(context: Context): ObfuscatedKeyValueStoreInterface =
        ObfuscatedKeyValueStore(context, context.packageName + KVS_DEVICE)

    @Provides
    @Singleton
    @Named(KVS_IMAGE_MANAGER)
    fun provideImagePreferences(context: Context): ObfuscatedKeyValueStoreInterface =
        ObfuscatedKeyValueStore(context, context.packageName + KVS_IMAGE_MANAGER)

    @Provides
    @Singleton
    @Named(KEY_SETTING_PREFERENCES)
    fun provideObfuscatedKeyValueStore(context: Context): ObfuscatedKeyValueStoreInterface =
        ObfuscatedKeyValueStore(context, context.packageName + KEY_SETTING_PREFERENCES)

    @Provides
    @Singleton
    @Named(KVS_USER)
    fun provideUserPreferences(context: Context): ObfuscatedKeyValueStoreInterface =
        ObfuscatedKeyValueStore(context, context.packageName + KVS_USER)
}
