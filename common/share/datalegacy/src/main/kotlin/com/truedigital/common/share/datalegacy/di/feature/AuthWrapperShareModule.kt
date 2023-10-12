package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.domain.login.usecase.GetLoginUrlUseCase
import com.truedigital.common.share.datalegacy.domain.webview.usecase.GetSystemWebViewMinimumVersionUseCase
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.manager.ApplicationPackageManager
import com.truedigital.core.provider.ContextDataProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AuthWrapperShareModule {

    @Provides
    @Singleton
    fun provideAuthManagerWrapper(
        context: ContextDataProvider,
        applicationPackageManager: ApplicationPackageManager,
        localizationRepository: LocalizationRepository,
        getSystemWebViewMinimumVersionUseCase: GetSystemWebViewMinimumVersionUseCase,
        getLoginUrlUseCase: GetLoginUrlUseCase
    ): AuthManagerWrapper {
        return AuthManagerWrapper(
            context,
            applicationPackageManager,
            localizationRepository,
            getSystemWebViewMinimumVersionUseCase,
            getLoginUrlUseCase
        )
    }
}
