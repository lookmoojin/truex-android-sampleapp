package com.truedigital.features.truecloudv3.di

import com.truedigital.features.truecloudv3.domain.usecase.CheckContactUpdateUseCase
import com.truedigital.features.truecloudv3.domain.usecase.CheckContactUpdateUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.DownloadContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadContactUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.ExportContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ExportContactUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetContactDataFromSelectorUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactDataFromSelectorUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetContactListFromPathUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactListFromPathUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetGroupAlphabetContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetGroupAlphabetContactUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetGroupContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetGroupContactUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetLastUpdateContactPathUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetLastUpdateContactPathUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.HasContactSyncedUseCase
import com.truedigital.features.truecloudv3.domain.usecase.HasContactSyncedUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.SetContactSyncedUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SetContactSyncedUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface TrueCloudV3ContactBindsModule {

    @Binds
    fun bindsExportContactUseCase(
        exportContactUseCaseImpl: ExportContactUseCaseImpl
    ): ExportContactUseCase

    @Binds
    fun bindsGetContactListFromPathUseCase(
        getContactListFromPathUseCaseImpl: GetContactListFromPathUseCaseImpl
    ): GetContactListFromPathUseCase

    @Binds
    fun bindsGetContactUseCase(
        getContactUseCaseImpl: GetContactUseCaseImpl
    ): GetContactUseCase

    @Binds
    fun bindsGetGroupAlphabetContactUseCase(
        getGroupAlphabetContactUseCaseImpl: GetGroupAlphabetContactUseCaseImpl
    ): GetGroupAlphabetContactUseCase

    @Binds
    fun bindsGetGroupContactUseCase(
        getGroupContactUseCaseImpl: GetGroupContactUseCaseImpl
    ): GetGroupContactUseCase

    @Binds
    fun bindsGetLastUpdateContactPathUseCase(
        getLastUpdateContactPathUseCaseImpl: GetLastUpdateContactPathUseCaseImpl
    ): GetLastUpdateContactPathUseCase

    @Binds
    fun bindsCheckContactUpdateUseCase(
        checkContactUpdateUseCaseImpl: CheckContactUpdateUseCaseImpl
    ): CheckContactUpdateUseCase

    @Binds
    fun bindsDownloadContactUseCase(
        downloadContactUseCaseImpl: DownloadContactUseCaseImpl
    ): DownloadContactUseCase

    @Binds
    fun bindsGetContactDataFromSelectorUseCase(
        getContactDataFromSelectorUseCaseImpl: GetContactDataFromSelectorUseCaseImpl
    ): GetContactDataFromSelectorUseCase

    @Binds
    fun bindsSetContactSyncedUseCase(
        setContactSyncedUseCaseImpl: SetContactSyncedUseCaseImpl
    ): SetContactSyncedUseCase

    @Binds
    fun bindsHasContactSyncedUseCase(
        hasContactSyncedUseCaseImpl: HasContactSyncedUseCaseImpl
    ): HasContactSyncedUseCase
}
