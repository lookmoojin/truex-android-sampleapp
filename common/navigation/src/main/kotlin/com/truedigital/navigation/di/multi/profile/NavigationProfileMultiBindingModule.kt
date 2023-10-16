package com.truedigital.navigation.di.multi.profile

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.profiles.share.domain.usecase.deeplinks.MyAccountDecodeDeepLinkUseCaseImpl
import com.truedigital.features.profiles.share.domain.usecase.deeplinks.ProfileDecodeDeepLinkUseCaseImpl
import com.truedigital.features.profiles.share.domain.usecase.deeplinks.SubscriptionsDecodeDeepLinkUseCaseImpl
import com.truedigital.features.profiles.share.domain.usecase.deeplinks.TmnBindingDecodeDeepLinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationProfileMultiBindingModule {

    @Binds
    @IntoSet
    fun provideMyAccountDecodeDeepLinkUseCase(
        myAccountDecodeDeepLinkUseCaseImpl: MyAccountDecodeDeepLinkUseCaseImpl
    ): DecodeDeeplinkUseCase

    @Binds
    @IntoSet
    fun provideProfileDecodeDeepLinkUseCase(
        profileDecodeDeepLinkUseCaseImpl: ProfileDecodeDeepLinkUseCaseImpl
    ): DecodeDeeplinkUseCase

    @Binds
    @IntoSet
    fun provideSubscriptionsDecodeDeepLinkUseCase(
        subscriptionsDecodeDeepLinkUseCaseImpl: SubscriptionsDecodeDeepLinkUseCaseImpl
    ): DecodeDeeplinkUseCase

    @Binds
    @IntoSet
    fun provideTmnBindingDecodeDeepLinkUseCase(
        tmnBindingDecodeDeepLinkUseCaseImpl: TmnBindingDecodeDeepLinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
