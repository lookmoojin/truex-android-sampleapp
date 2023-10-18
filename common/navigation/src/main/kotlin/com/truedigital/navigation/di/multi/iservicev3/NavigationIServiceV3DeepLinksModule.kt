package com.truedigital.navigation.di.multi.iservicev3

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.features.iservicev3.share.domain.usecase.GetEnableIServiceUseCase
import com.truedigital.features.iservicev3.share.domain.usecase.deeplinks.IServiceDecodeDeeplinkUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationIServiceV3DeepLinksModule {

    @Provides
    @IntoSet
    fun bindIServiceDecodeDeeplinkUseCase(
        getEnableIServiceUseCase: GetEnableIServiceUseCase,
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase
    ): DecodeDeeplinkUseCase {
        return IServiceDecodeDeeplinkUseCaseImpl(
            getEnableIServiceUseCase,
            isDomainDeeplinkUrlUseCase
        )
    }
}
