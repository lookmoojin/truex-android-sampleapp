package com.truedigital.features.truecloudv3.di

import com.truedigital.features.truecloudv3.navigation.router.ContactDetailRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.ContactDetailRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.ContactEditRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.ContactEditRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.ContactListRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.ContactListRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.ContactSelectLabelRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.ContactSelectLabelRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.ControlAccessRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.ControlAccessRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.IntroMigrateDataRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.IntroMigrateDataRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.IntroTrueCloudRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.IntroTrueCloudRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.MainTrueCloudV3RouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.MainTrueCloudV3RouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.SettingTrueCloudV3RouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.SettingTrueCloudV3RouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3FileViewerRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3FileViewerRouterUseCaseImpl
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3SharedFileViewerRouterUseCase
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3SharedFileViewerRouterUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface TrueCloudV3RouterModule {

    @Binds
    fun bindsContactListRouterUseCase(
        contactListRouterUseCaseImpl: ContactListRouterUseCaseImpl
    ): ContactListRouterUseCase

    @Binds
    fun bindsContactDetailRouterUseCase(
        contactDetailRouterUseCaseImpl: ContactDetailRouterUseCaseImpl
    ): ContactDetailRouterUseCase

    @Binds
    fun bindsContactEditRouterUseCase(
        contactEditRouterUseCaseImpl: ContactEditRouterUseCaseImpl
    ): ContactEditRouterUseCase

    @Binds
    fun bindsMainTrueCloudV3RouterUseCase(
        mainTrueCloudV3RouterUseCaseImpl: MainTrueCloudV3RouterUseCaseImpl
    ): MainTrueCloudV3RouterUseCase

    @Binds
    fun bindsFileTrueCloudRouterUseCase(
        fileTrueCloudRouterUseCaseImpl: FileTrueCloudRouterUseCaseImpl
    ): FileTrueCloudRouterUseCase

    @Binds
    fun bindsTrueCloudV3FileViewerRouterUseCase(
        trueCloudV3ImageViewerRouterUseCaseImpl: TrueCloudV3FileViewerRouterUseCaseImpl
    ): TrueCloudV3FileViewerRouterUseCase

    @Binds
    fun bindsContactSelectLabelRouterUseCase(
        contactSelectLabelRouterUseCaseImpl: ContactSelectLabelRouterUseCaseImpl
    ): ContactSelectLabelRouterUseCase

    @Binds
    fun bindsIntroMigrateDataRouterUseCase(
        introMigrateDataRouterUseCaseImpl: IntroMigrateDataRouterUseCaseImpl
    ): IntroMigrateDataRouterUseCase

    @Binds
    fun bindsSettingTrueCloudV3RouterUseCase(
        settingTrueCloudV3RouterUseCaseImpl: SettingTrueCloudV3RouterUseCaseImpl
    ): SettingTrueCloudV3RouterUseCase

    @Binds
    fun bindsIntroTrueCloudRouterUseCase(
        introTrueCloudRouterUseCaseImpl: IntroTrueCloudRouterUseCaseImpl
    ): IntroTrueCloudRouterUseCase

    @Binds
    fun bindsControlAccessRouterUseCase(
        controlAccessRouterUseCaseImpl: ControlAccessRouterUseCaseImpl
    ): ControlAccessRouterUseCase

    @Binds
    fun bindsTrueCloudV3SharedFileViewerRouterUseCase(
        trueCloudV3SharedFileViewerRouterUseCaseImpl: TrueCloudV3SharedFileViewerRouterUseCaseImpl
    ): TrueCloudV3SharedFileViewerRouterUseCase
}
