package com.truedigital.common.share.amityserviceconfig.di

import com.amity.socialcloud.uikit.common.utils.AmityAppLocale
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import dagger.Module
import dagger.Provides

@Module
class AmityServiceProvidesModule {

    @Provides
    fun providesAmitySocialUISettings(): AmitySocialUISettings {
        return AmitySocialUISettings
    }

    @Provides
    fun providesAmityAppLocale(): AmityAppLocale {
        return AmityAppLocale
    }
}
