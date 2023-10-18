package com.truedigital.navigation.di.multi.truemoney

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.payment.truemoney.domain.usecase.deeplinks.TrueMoneyDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationTrueMoneyDeepLinksModule {
    @Binds
    @IntoSet
    fun bindsTrueMoneyDecodeDeeplinkUseCase(
        trueMoneyDecodeDeeplinkUseCaseImpl: TrueMoneyDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
