package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.domain.verifydevice.usecase.VerifyTrustDeviceOwnerUseCase
import com.truedigital.common.share.datalegacy.domain.verifydevice.usecase.VerifyTrustDeviceOwnerUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface VerifyDeviceModule {

    @Binds
    fun bindsVerifyTrustDeviceOwnerUseCase(useCase: VerifyTrustDeviceOwnerUseCaseImpl): VerifyTrustDeviceOwnerUseCase
}
