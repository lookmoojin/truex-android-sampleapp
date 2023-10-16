package com.truedigital.navigation.di.multi.homelanding

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.homelandings.share.domain.usecase.deeplinks.OnBoardingDefaultPageDecodeDeeplinkUseCaseImpl
import com.truedigital.features.homelandings.share.domain.usecase.deeplinks.SetHomeLandingDecodeDeeplinkUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationHomeLandingDeepLinksModule {

    @Provides
    @IntoSet
    fun bindOnBoardingDefaultPageDecodeDeeplinkUseCase(): DecodeDeeplinkUseCase {
        return OnBoardingDefaultPageDecodeDeeplinkUseCaseImpl()
    }

    @Provides
    @IntoSet
    fun bindSetHomeLandingDecodeDeeplinkUseCase(): DecodeDeeplinkUseCase {
        return SetHomeLandingDecodeDeeplinkUseCaseImpl()
    }
}
