package com.truedigital.navigation.di.multi

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.features.homes.share.domain.deeplink.AuthDecodeDeeplinkUseCaseImpl
import com.truedigital.features.homes.share.domain.deeplink.HomeDecodeDeeplinkUseCaseImpl
import com.truedigital.features.homes.share.domain.deeplink.SearchDecodeDeeplinkUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationHomeDeepLinksModule {

    @Provides
    @IntoSet
    fun bindHomeDecodeDeeplinkUseCase(
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase
    ): DecodeDeeplinkUseCase {
        return HomeDecodeDeeplinkUseCaseImpl(isDomainDeeplinkUrlUseCase)
    }

    @Provides
    @IntoSet
    fun bindSearchDecodeDeeplinkUseCase(
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase
    ): DecodeDeeplinkUseCase {
        return SearchDecodeDeeplinkUseCaseImpl(isDomainDeeplinkUrlUseCase)
    }

    @Provides
    @IntoSet
    fun bindAuthDecodeDeeplinkUseCase(
        loginManagerInterface: LoginManagerInterface,
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase
    ): DecodeDeeplinkUseCase {
        return AuthDecodeDeeplinkUseCaseImpl(
            loginManagerInterface,
            isDomainDeeplinkUrlUseCase
        )
    }
}
