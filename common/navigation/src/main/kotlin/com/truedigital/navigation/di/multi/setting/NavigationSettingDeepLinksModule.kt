package com.truedigital.navigation.di.multi.setting

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.setting.share.domain.usecase.deeplinks.SettingsDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationSettingDeepLinksModule {

    @Binds
    @IntoSet
    fun bindsSettingsDecodeDeeplinkUseCase(
        settingsDecodeDeeplinkUseCaseImpl: SettingsDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
