package com.truedigital.features.truecloudv3.di

import com.truedigital.features.truecloudv3.data.repository.BuildVersionCheckRepository
import com.truedigital.features.truecloudv3.data.repository.BuildVersionCheckRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.CompleteUploadRepository
import com.truedigital.features.truecloudv3.data.repository.CompleteUploadRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import com.truedigital.features.truecloudv3.data.repository.ContactRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.CreateFolderRepository
import com.truedigital.features.truecloudv3.data.repository.CreateFolderRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.DeleteFileRepository
import com.truedigital.features.truecloudv3.data.repository.DeleteFileRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.DownloadFileRepository
import com.truedigital.features.truecloudv3.data.repository.DownloadFileRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.FileRepository
import com.truedigital.features.truecloudv3.data.repository.FileRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.GetShareLinkRepository
import com.truedigital.features.truecloudv3.data.repository.GetShareLinkRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.GetSharedFileRepository
import com.truedigital.features.truecloudv3.data.repository.GetSharedFileRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.GetStorageSpaceRepository
import com.truedigital.features.truecloudv3.data.repository.GetStorageSpaceRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudObjectRepository
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudObjectRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudStorageListRepository
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudStorageListRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.RenameFileRepository
import com.truedigital.features.truecloudv3.data.repository.RenameFileRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.SearchFileRepository
import com.truedigital.features.truecloudv3.data.repository.SearchFileRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.TrashRepository
import com.truedigital.features.truecloudv3.data.repository.TrashRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepositoryImpl
import com.truedigital.features.truecloudv3.domain.usecase.AddUploadTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.AddUploadTaskUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.CompleteReplaceUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.CompleteReplaceUploadUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadContactUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.CreateFolderUserCase
import com.truedigital.features.truecloudv3.domain.usecase.CreateFolderUserCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.DeleteFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DeleteFileUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.DeleteTrashDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DeleteTrashDataUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.DownloadCoverImageUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadCoverImageUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.DownloadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.EmptyTrashDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.EmptyTrashDataUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.FileLocatorUseCase
import com.truedigital.features.truecloudv3.domain.usecase.FileLocatorUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetNewUploadTaskListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetNewUploadTaskListUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetNumberOfFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetNumberOfFileUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetObjectInfoUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetObjectInfoUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetPrivateSharedFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetPrivateSharedFileUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetPublicSharedFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetPublicSharedFileUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetShareConfigUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetShareConfigUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetShareLinkUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetShareLinkUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetSharedFileAccessTokenUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetSharedFileAccessTokenUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageSpaceUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageSpaceUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetTrashListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetTrashListUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskListUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.MoveToTrashUseCase
import com.truedigital.features.truecloudv3.domain.usecase.MoveToTrashUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.ProvideWorkManagerUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ProvideWorkManagerUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.RemoveAllTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveAllTaskUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.RemoveTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveTaskUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.RenameFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RenameFileUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.ReplaceFileUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ReplaceFileUploadUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.RestoreTrashDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RestoreTrashDataUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.RetryUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RetryUploadUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.ScanFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ScanFileUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.SearchInAllFilesUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SearchInAllFilesUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.SearchWithCategoryUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SearchWithCategoryUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.TrueCloudV3UpdateInprogressTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.TrueCloudV3UpdateInprogressTaskUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.UpdateShareConfigUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateShareConfigUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.UpdateTaskUploadStatusUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateTaskUploadStatusUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.UploadContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadContactUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileWithPathUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileWithPathUseCaseImpl
import com.truedigital.features.truecloudv3.domain.usecase.UploadQueueUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadQueueUseCaseImpl
import com.truedigital.features.truecloudv3.provider.AmazonS3ClientProvider
import com.truedigital.features.truecloudv3.provider.AmazonS3ClientProviderImpl
import com.truedigital.features.truecloudv3.provider.ContactProvider
import com.truedigital.features.truecloudv3.provider.ContactProviderImpl
import com.truedigital.features.truecloudv3.provider.ContactsUriProvider
import com.truedigital.features.truecloudv3.provider.ContactsUriProviderImpl
import com.truedigital.features.truecloudv3.provider.GsonProvider
import com.truedigital.features.truecloudv3.provider.GsonProviderImpl
import com.truedigital.features.truecloudv3.provider.NotificationProvider
import com.truedigital.features.truecloudv3.provider.NotificationProviderImpl
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProvider
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProviderImpl
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProviderImpl
import com.truedigital.features.truecloudv3.util.BitmapUtil
import com.truedigital.features.truecloudv3.util.BitmapUtilImpl
import com.truedigital.features.truecloudv3.util.TrueCloudV3FileUtil
import com.truedigital.features.truecloudv3.util.TrueCloudV3FileUtilImpl
import dagger.Binds
import dagger.Module

@Module
interface TrueCloudV3MainBindsModule {

    @Binds
    fun bindsSecureTokenServiceProvider(
        secureTokenServiceProviderImpl: SecureTokenServiceProviderImpl
    ): SecureTokenServiceProvider

    @Binds
    fun bindsTrueCloudV3TransferUtilityProvider(
        trueCloudV3TransferUtilityProviderImpl: TrueCloudV3TransferUtilityProviderImpl
    ): TrueCloudV3TransferUtilityProvider

    @Binds
    fun bindsGsonProvider(
        gsonProviderImpl: GsonProviderImpl
    ): GsonProvider

    @Binds
    fun bindsContactProvider(
        contactProviderImpl: ContactProviderImpl
    ): ContactProvider

    @Binds
    fun bindsNotificationProvider(
        notificationProviderImpl: NotificationProviderImpl
    ): NotificationProvider

    @Binds
    fun bindsContactsUriProvider(
        contactsUriProviderImpl: ContactsUriProviderImpl
    ): ContactsUriProvider

    @Binds
    fun bindsAmazonS3ClientProvider(
        amazonS3ClientProviderImpl: AmazonS3ClientProviderImpl
    ): AmazonS3ClientProvider

    @Binds
    fun bindsTrueCloudV3FileUtil(
        trueCloudV3FileUtilImpl: TrueCloudV3FileUtilImpl
    ): TrueCloudV3FileUtil

    @Binds
    fun bindsBitmapUtil(
        bitmapUtilImpl: BitmapUtilImpl
    ): BitmapUtil

    @Binds
    fun bindsBuildVersionCheckRepository(
        buildVersionCheckRepositoryImpl: BuildVersionCheckRepositoryImpl
    ): BuildVersionCheckRepository

    @Binds
    fun bindsGetStorageSpaceRepository(
        getStorageSpaceRepositoryImpl: GetStorageSpaceRepositoryImpl
    ): GetStorageSpaceRepository

    @Binds
    fun bindsCreateFolderRepository(
        createFolderRepositoryImpl: CreateFolderRepositoryImpl
    ): CreateFolderRepository

    @Binds
    fun bindsGetTrueCloudStorageListRepository(
        getTrueCloudStorageListRepositoryImpl: GetTrueCloudStorageListRepositoryImpl
    ): GetTrueCloudStorageListRepository

    @Binds
    fun bindsUploadFileRepository(
        uploadFileRepositoryImpl: UploadFileRepositoryImpl
    ): UploadFileRepository

    @Binds
    fun bindsCacheUploadTaskRepository(
        cacheUploadTaskRepositoryImpl: CacheUploadTaskRepositoryImpl
    ): CacheUploadTaskRepository

    @Binds
    fun bindsCompleteUploadRepository(
        completeUploadRepositoryImpl: CompleteUploadRepositoryImpl
    ): CompleteUploadRepository

    @Binds
    fun bindsDownloadFileRepository(
        downloadFileRepositoryImpl: DownloadFileRepositoryImpl
    ): DownloadFileRepository

    @Binds
    fun bindsDeleteFileRepository(
        deleteFileRepositoryImpl: DeleteFileRepositoryImpl
    ): DeleteFileRepository

    @Binds
    fun bindsRenameFileRepository(
        renameFileRepositoryImpl: RenameFileRepositoryImpl
    ): RenameFileRepository

    @Binds
    fun bindsContactRepository(
        contactRepositoryImpl: ContactRepositoryImpl
    ): ContactRepository

    @Binds
    fun bindsGetShareLinkRepository(
        getShareLinkRepositoryImpl: GetShareLinkRepositoryImpl
    ): GetShareLinkRepository

    @Binds
    fun bindGetTrueCloudObjectRepository(
        getTrueCloudObjectRepositoryImpl: GetTrueCloudObjectRepositoryImpl
    ): GetTrueCloudObjectRepository

    @Binds
    fun bindsProvideWorkManagerUseCase(
        provideWorkManagerUseCaseImpl: ProvideWorkManagerUseCaseImpl
    ): ProvideWorkManagerUseCase

    @Binds
    fun bindsGetStorageSpaceUseCase(
        getStorageSpaceUseCaseImpl: GetStorageSpaceUseCaseImpl
    ): GetStorageSpaceUseCase

    @Binds
    fun bindsCompleteUploadUseCase(
        completeUploadUseCaseImpl: CompleteUploadUseCaseImpl
    ): CompleteUploadUseCase

    @Binds
    fun bindsCompleteReplaceUploadUseCase(
        completeReplaceUploadUseCaseImpl: CompleteReplaceUploadUseCaseImpl
    ): CompleteReplaceUploadUseCase

    @Binds
    fun bindsAddUploadTaskUseCase(
        addUploadTaskUseCaseImpl: AddUploadTaskUseCaseImpl
    ): AddUploadTaskUseCase

    @Binds
    fun bindsTrueCloudV3UpdateInprogressTaskUseCase(
        trueCloudV3UpdateInprogressTaskUseCaseImpl: TrueCloudV3UpdateInprogressTaskUseCaseImpl
    ): TrueCloudV3UpdateInprogressTaskUseCase

    @Binds
    fun bindsCompleteUploadContactUseCase(
        completeUploadContactUseCaseImpl: CompleteUploadContactUseCaseImpl
    ): CompleteUploadContactUseCase

    @Binds
    fun bindsUploadFileUseCase(
        uploadFileUseCaseImpl: UploadFileUseCaseImpl
    ): UploadFileUseCase

    @Binds
    fun bindsUploadFileWithPathUseCase(
        uploadFileWithPathUseCaseImpl: UploadFileWithPathUseCaseImpl
    ): UploadFileWithPathUseCase

    @Binds
    fun bindsUploadContactUseCase(
        uploadContactUseCaseImpl: UploadContactUseCaseImpl
    ): UploadContactUseCase

    @Binds
    fun bindsGetUploadTaskUseCase(
        getUploadTaskUseCaseImpl: GetUploadTaskUseCaseImpl
    ): GetUploadTaskUseCase

    @Binds
    fun bindsGetNewUploadTaskListUseCase(
        getNewUploadTaskListUseCaseImpl: GetNewUploadTaskListUseCaseImpl
    ): GetNewUploadTaskListUseCase

    @Binds
    fun bindsGetUploadTaskListUseCase(
        getUploadTaskListUseCaseImpl: GetUploadTaskListUseCaseImpl
    ): GetUploadTaskListUseCase

    @Binds
    fun bindsDownloadUseCase(
        downloadUseCaseImpl: DownloadUseCaseImpl
    ): DownloadUseCase

    @Binds
    fun bindsRemoveAllTaskUseCase(
        removeAllTaskUseCaseImpl: RemoveAllTaskUseCaseImpl
    ): RemoveAllTaskUseCase

    @Binds
    fun bindsRemoveTaskUseCase(
        removeTaskUseCaseImpl: RemoveTaskUseCaseImpl
    ): RemoveTaskUseCase

    @Binds
    fun bindsUpdateTaskUploadStatusUseCase(
        updateTaskUploadStatusUseCaseImpl: UpdateTaskUploadStatusUseCaseImpl
    ): UpdateTaskUploadStatusUseCase

    @Binds
    fun bindsCreateFolderUserCase(
        createFolderUserCaseImpl: CreateFolderUserCaseImpl
    ): CreateFolderUserCase

    @Binds
    fun bindsDownloadCoverImageUseCase(
        downloadCoverImageUseCaseImpl: DownloadCoverImageUseCaseImpl
    ): DownloadCoverImageUseCase

    @Binds
    fun bindsRenameFileUseCase(
        renameFileUseCaseImpl: RenameFileUseCaseImpl
    ): RenameFileUseCase

    @Binds
    fun bindsDeleteFileUseCase(
        deleteFileUseCaseImpl: DeleteFileUseCaseImpl
    ): DeleteFileUseCase

    @Binds
    fun bindsRetryUploadUseCase(
        retryUploadUseCaseImpl: RetryUploadUseCaseImpl
    ): RetryUploadUseCase

    @Binds
    fun bindsReplaceFileUploadUseCase(
        replaceFileUploadUseCaseImpl: ReplaceFileUploadUseCaseImpl
    ): ReplaceFileUploadUseCase

    @Binds
    fun bindsGetNumberOfFileUseCase(
        getNumberOfFileUseCaseImpl: GetNumberOfFileUseCaseImpl
    ): GetNumberOfFileUseCase

    @Binds
    fun bindsGetShareLinkUseCase(
        getShareLinkUseCaseImpl: GetShareLinkUseCaseImpl
    ): GetShareLinkUseCase

    @Binds
    fun bindsGetShareConfigUseCase(
        getShareConfigUseCaseImpl: GetShareConfigUseCaseImpl
    ): GetShareConfigUseCase

    @Binds
    fun bindsUpdateShareConfigUseCase(
        updateShareConfigUseCaseImpl: UpdateShareConfigUseCaseImpl
    ): UpdateShareConfigUseCase

    @Binds
    fun bindsGetPublicSharedFileUseCase(
        getPublicSharedFileUseCaseImpl: GetPublicSharedFileUseCaseImpl
    ): GetPublicSharedFileUseCase

    @Binds
    fun bindsGetPrivateSharedFileUseCase(
        getPrivateSharedFileUseCaseImpl: GetPrivateSharedFileUseCaseImpl
    ): GetPrivateSharedFileUseCase

    @Binds
    fun bindsGetSharedFileAccessTokenUseCase(
        getSharedFileAccessTokenUseCaseImpl: GetSharedFileAccessTokenUseCaseImpl
    ): GetSharedFileAccessTokenUseCase

    @Binds
    fun bindsGetObjectInfoUseCase(
        getObjectInfoUseCase: GetObjectInfoUseCaseImpl
    ): GetObjectInfoUseCase

    @Binds
    fun bindsGetShareFileRepository(
        getShareFileRepositoryImpl: GetSharedFileRepositoryImpl
    ): GetSharedFileRepository

    @Binds
    fun bindsFileRepository(
        fileRepositoryImpl: FileRepositoryImpl
    ): FileRepository

    @Binds
    fun bindsFileLocatorUseCase(
        fileLocatorUseCaseImpl: FileLocatorUseCaseImpl
    ): FileLocatorUseCase

    @Binds
    fun bindsMoveToTrashUseCase(
        moveToTrashUseCaseImpl: MoveToTrashUseCaseImpl
    ): MoveToTrashUseCase

    @Binds
    fun bindsGetTrashListUseCase(
        getTrashListUseCaseImpl: GetTrashListUseCaseImpl
    ): GetTrashListUseCase

    @Binds
    fun bindsTrashRepository(
        trashRepositoryImpl: TrashRepositoryImpl
    ): TrashRepository

    @Binds
    fun bindsRestoreTrashDataUseCase(
        restoreTrashDataUseCaseImpl: RestoreTrashDataUseCaseImpl
    ): RestoreTrashDataUseCase

    @Binds
    fun bindsDeleteTrashDataUseCase(
        deleteTrashDataUseCaseImpl: DeleteTrashDataUseCaseImpl
    ): DeleteTrashDataUseCase

    @Binds
    fun bindsEmptyTrashDataUseCase(
        emptyTrashDataUseCaseImpl: EmptyTrashDataUseCaseImpl
    ): EmptyTrashDataUseCase

    @Binds
    fun bindsScanFileUseCase(
        scanFileUseCaseImpl: ScanFileUseCaseImpl
    ): ScanFileUseCase

    @Binds
    fun bindsSearchFileRepository(
        searchFileRepository: SearchFileRepositoryImpl
    ): SearchFileRepository

    @Binds
    fun bindsSearchInAllFilesUseCase(
        searchInAllFilesUseCase: SearchInAllFilesUseCaseImpl
    ): SearchInAllFilesUseCase

    @Binds
    fun bindsSearchWithCategoryUseCase(
        searchWithCategoryUseCase: SearchWithCategoryUseCaseImpl
    ): SearchWithCategoryUseCase

    @Binds
    fun bindsUploadQueueUseCase(
        uploadQueueUseCase: UploadQueueUseCaseImpl
    ): UploadQueueUseCase
}
