package com.truedigital.navigation.di.multi.qrscanner

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.qrscanners.share.domain.usecase.deeplinks.QrScannerDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationQrScannerDeeplinkModule {

    @Binds
    @IntoSet
    fun bindsQrScannerDecodeDeeplinkUseCase(
        qrScannerDecodeDeeplinkUseCaseImpl: QrScannerDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
