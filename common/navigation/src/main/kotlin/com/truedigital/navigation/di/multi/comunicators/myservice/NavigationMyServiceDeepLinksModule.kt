package com.truedigital.navigation.di.multi.comunicators.myservice

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.features.communicator.common.core.domain.usecase.deeplinks.myservice.MyServicesDecodeDeeplinkUseCaseImpl
import com.truedigital.features.communicator.common.core.domain.usecase.deeplinks.myservice.MyServicesSwitchTabDecodeDeeplinkUseCaseImpl
import com.truedigital.features.communicator.common.core.domain.usecase.deeplinks.myservice.SimActivateDecodeDeeplinkUseCaseImpl
import com.truedigital.features.communicator.common.core.domain.usecase.myservices.ConvertDecryptDataToStringUseCase
import com.truedigital.features.communicator.common.core.domain.usecase.myservices.GetEnableMyServicesUseCase
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationMyServiceDeepLinksModule {

    @Provides
    @IntoSet
    fun bindMyServicesDecodeDeeplinkUseCaseImpl(): DecodeDeeplinkUseCase {
        return MyServicesDecodeDeeplinkUseCaseImpl()
    }

    @Provides
    @IntoSet
    fun bindMyServicesSwitchTabDecodeDeeplinkUseCaseImpl(
        getEnableMyServicesUseCase: GetEnableMyServicesUseCase,
        isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase
    ): DecodeDeeplinkUseCase {
        return MyServicesSwitchTabDecodeDeeplinkUseCaseImpl(
            getEnableMyServicesUseCase,
            isDomainDeeplinkUrlUseCase
        )
    }

    @Provides
    @IntoSet
    fun bindSimActivateDecodeDeeplinkUseCaseImpl(
        convertDecryptDataToStringUseCase: ConvertDecryptDataToStringUseCase
    ): DecodeDeeplinkUseCase {
        return SimActivateDecodeDeeplinkUseCaseImpl(
            convertDecryptDataToStringUseCase
        )
    }
}
