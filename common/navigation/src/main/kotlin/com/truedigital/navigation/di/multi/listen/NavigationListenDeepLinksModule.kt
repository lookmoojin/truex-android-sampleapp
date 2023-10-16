package com.truedigital.navigation.di.multi.listen

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.features.listens.share.domain.usecase.deeplinks.GetListenConfigInitialAppUseCase
import com.truedigital.features.listens.share.domain.usecase.deeplinks.ListenDecodeDeeplinkUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationListenDeepLinksModule {

    @Provides
    @IntoSet
    fun bindHomeDecodeDeeplinkUseCase(
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase,
        getListenConfigInitialAppUseCase: GetListenConfigInitialAppUseCase
    ): DecodeDeeplinkUseCase {
        return ListenDecodeDeeplinkUseCaseImpl(
            isDomainDeeplinkUrlUseCase,
            getListenConfigInitialAppUseCase
        )
    }
}
