package com.truedigital.features.tuned.injection.module

import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepositoryImpl
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepositoryImpl
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepositoryImpl
import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import com.truedigital.features.tuned.data.setting.repository.SettingRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryBindsModule {

    @Binds
    @Singleton
    fun bindsDeviceRepository(
        deviceRepositoryImpl: DeviceRepositoryImpl
    ): DeviceRepository

    @Binds
    @Singleton
    fun bindsCacheRepository(
        cacheRepositoryImpl: CacheRepositoryImpl
    ): CacheRepository

    @Binds
    @Singleton
    fun bindsSettingRepository(
        settingRepositoryImpl: SettingRepositoryImpl
    ): SettingRepository

    @Binds
    @Singleton
    fun bindsMusicRoomRepository(
        musicRoomRepositoryImpl: MusicRoomRepositoryImpl
    ): MusicRoomRepository
}
