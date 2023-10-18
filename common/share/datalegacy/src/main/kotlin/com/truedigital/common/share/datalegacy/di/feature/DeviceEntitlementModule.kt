package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepository
import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepositoryImpl
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.AddDeviceEntitlementUseCase
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.AddDeviceEntitlementUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.ContainDeviceIdInDeviceListUseCase
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.ContainDeviceIdInDeviceListUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.GetActiveDeviceEntitlementUseCase
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.GetActiveDeviceEntitlementUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.RemoveActiveDeviceEntitlementUseCase
import com.truedigital.common.share.datalegacy.domain.entitlement.usecase.RemoveActiveDeviceEntitlementUseCaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DeviceEntitlementModule {

    @Binds
    @Singleton
    fun bindsDeviceEntitlementRepository(
        deviceEntitlementRepositoryImpl: DeviceEntitlementRepositoryImpl
    ): DeviceEntitlementRepository

    @Binds
    fun bindsContainDeviceIdInDeviceListUseCase(
        containDeviceIdInDeviceListUseCaseImpl: ContainDeviceIdInDeviceListUseCaseImpl
    ): ContainDeviceIdInDeviceListUseCase

    @Binds
    fun bindsGetActiveDeviceEntitlementUseCase(
        getActiveDeviceEntitlementUseCaseImpl: GetActiveDeviceEntitlementUseCaseImpl
    ): GetActiveDeviceEntitlementUseCase

    @Binds
    fun bindsRemoveActiveDeviceEntitlementUseCase(
        removeActiveDeviceEntitlementUseCaseImpl: RemoveActiveDeviceEntitlementUseCaseImpl
    ): RemoveActiveDeviceEntitlementUseCase

    @Binds
    fun bindsAddDeviceEntitlementUseCase(
        addDeviceEntitlementUseCaseImpl: AddDeviceEntitlementUseCaseImpl
    ): AddDeviceEntitlementUseCase
}
