package com.truedigital.navigation.di.multi.comunicators

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.common.share.data.coredata.domain.GetEnableCommunicatorUseCase
import com.truedigital.features.communicator.common.core.domain.repository.communicator.CommunicatorDeeplinkRepository
import com.truedigital.features.communicator.common.core.domain.repository.communicator.CommunicatorPhonelinkRepository
import com.truedigital.features.communicator.common.core.domain.usecase.communicator.CommunicatorSetDataCachingUseCase
import com.truedigital.features.communicator.common.core.domain.usecase.communicator.share.contact.GetContactFromContentContactUriUseCase
import com.truedigital.features.communicator.common.core.domain.usecase.deeplinks.communicator.CommunicatorDecodeDeeplinkUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationCommunicatorDeepLinksModule {

    @Provides
    @IntoSet
    fun bindCommunicatorDecodeDeeplinkUseCaseImpl(
        communicatorSetDataCachingUseCase: CommunicatorSetDataCachingUseCase,
        communicatorEnableConfigurationUseCase: GetEnableCommunicatorUseCase,
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase,
        communicatorDeeplinkRepository: CommunicatorDeeplinkRepository,
        communicatorPhonelinkRepository: CommunicatorPhonelinkRepository,
        getContactFromContentContactUriUseCase: GetContactFromContentContactUriUseCase
    ): DecodeDeeplinkUseCase {
        return CommunicatorDecodeDeeplinkUseCaseImpl(
            communicatorSetDataCachingUseCase,
            communicatorEnableConfigurationUseCase,
            isDomainDeeplinkUrlUseCase,
            communicatorDeeplinkRepository,
            communicatorPhonelinkRepository,
            getContactFromContentContactUriUseCase
        )
    }
}
