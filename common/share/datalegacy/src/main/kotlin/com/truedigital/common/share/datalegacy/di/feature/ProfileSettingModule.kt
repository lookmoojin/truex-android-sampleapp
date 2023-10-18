package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetProfileSettingsUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetProfileSettingsUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface ProfileSettingModule {

    @Binds
    fun bindsGetProfileSettingsUseCase(useCase: GetProfileSettingsUseCaseImpl): GetProfileSettingsUseCase
}
