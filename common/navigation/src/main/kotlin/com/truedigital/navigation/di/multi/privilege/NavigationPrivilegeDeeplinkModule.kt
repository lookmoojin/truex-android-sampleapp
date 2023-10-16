package com.truedigital.navigation.di.multi.privilege

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.feature.privileges.deeplink.domain.usecase.deeplinks.PrivilegeDecodeDeeplinkUseCaseImpl
import com.truedigital.feature.privileges.deeplink.domain.usecase.deeplinks.PrivilegeDecodeOldDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationPrivilegeDeeplinkModule {

    @Binds
    @IntoSet
    fun bindsPrivilegeDecodeDeeplinkUseCase(
        privilegeDecodeDeeplinkUseCaseImpl: PrivilegeDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase

    @Binds
    @IntoSet
    fun bindsPrivilegeDecodeOldDeeplinkUseCase(
        privilegeDecodeOldDeeplinkUseCaseImpl: PrivilegeDecodeOldDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
