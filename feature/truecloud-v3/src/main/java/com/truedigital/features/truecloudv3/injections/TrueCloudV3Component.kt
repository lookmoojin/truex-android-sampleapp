package com.truedigital.features.truecloudv3.injections

import com.truedigital.common.share.analytics.injections.AnalyticsSubComponent
import com.truedigital.common.share.currentdate.injections.CurrentDateSubComponent
import com.truedigital.common.share.data.coredata.injections.CoreDataSubComponent
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker
import com.truedigital.features.truecloudv3.di.TrueCloudV3ContactBindsModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3FileBindsModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3IntroBindsModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3IntroMigrateBindsModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3MainBindsModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3MainProvidesModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3PermissionBindsModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3ProvidesModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3RouterModule
import com.truedigital.features.truecloudv3.di.TrueCloudV3ViewModelsModule
import com.truedigital.features.truecloudv3.presentation.AudioItemViewerFragment
import com.truedigital.features.truecloudv3.presentation.AutoBackupSettingFragment
import com.truedigital.features.truecloudv3.presentation.ContactEditFragment
import com.truedigital.features.truecloudv3.presentation.ContactFragment
import com.truedigital.features.truecloudv3.presentation.CreateContactCustomLabelDialogFragment
import com.truedigital.features.truecloudv3.presentation.CreateContactDetailBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.CreateContactSelectLabelBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.FileInfoBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.FileItemViewerFragment
import com.truedigital.features.truecloudv3.presentation.FileLocatorFragment
import com.truedigital.features.truecloudv3.presentation.FileViewerFragment
import com.truedigital.features.truecloudv3.presentation.FilesTrueCloudFragment
import com.truedigital.features.truecloudv3.presentation.ImageItemViewerFragment
import com.truedigital.features.truecloudv3.presentation.IntroMigrateDataFragment
import com.truedigital.features.truecloudv3.presentation.IntroPermissionFragment
import com.truedigital.features.truecloudv3.presentation.IntroTrueCloudFragment
import com.truedigital.features.truecloudv3.presentation.IntroTrueCloudSharedFileFragment
import com.truedigital.features.truecloudv3.presentation.MainTrueCloudV3Fragment
import com.truedigital.features.truecloudv3.presentation.MigrateDataFragment
import com.truedigital.features.truecloudv3.presentation.OptionFileBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.OptionFileSelectedBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.OptionMainFileBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.OptionTrashFileBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.PhotoEditorAdjustFragment
import com.truedigital.features.truecloudv3.presentation.PhotoEditorFocusFragment
import com.truedigital.features.truecloudv3.presentation.PhotoEditorFragment
import com.truedigital.features.truecloudv3.presentation.PhotoEditorTextFragment
import com.truedigital.features.truecloudv3.presentation.PhotoEditorTransformFragment
import com.truedigital.features.truecloudv3.presentation.RenameDialogFragment
import com.truedigital.features.truecloudv3.presentation.SaveSharedFileBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.SettingTrueCloudV3Fragment
import com.truedigital.features.truecloudv3.presentation.ShareBottomSheetDialogFragment
import com.truedigital.features.truecloudv3.presentation.ShareControlAccessFragment
import com.truedigital.features.truecloudv3.presentation.SharedFileViewerFragment
import com.truedigital.features.truecloudv3.presentation.TrashTrueCloudV3Fragment
import com.truedigital.features.truecloudv3.presentation.VideoItemViewerFragment
import com.truedigital.features.truecloudv3.service.TrueCloudV3Service
import com.truedigital.navigation.injections.NavigationSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        TrueCloudV3ProvidesModule::class,
        TrueCloudV3ContactBindsModule::class,
        TrueCloudV3FileBindsModule::class,
        TrueCloudV3MainBindsModule::class,
        TrueCloudV3MainProvidesModule::class,
        TrueCloudV3PermissionBindsModule::class,
        TrueCloudV3IntroBindsModule::class,
        TrueCloudV3IntroMigrateBindsModule::class,
        TrueCloudV3RouterModule::class,
        TrueCloudV3ViewModelsModule::class
    ],
    dependencies = [
        AnalyticsSubComponent::class,
        CoreSubComponent::class,
        CoreDataSubComponent::class,
        CurrentDateSubComponent::class,
        DataLegacySubComponent::class,
        FirestoreConfigSubComponent::class,
        NavigationSubComponent::class
    ]
)
interface TrueCloudV3Component {

    companion object {

        private lateinit var trueCloudV3Component: TrueCloudV3Component

        fun initialize(trueCloudV3Component: TrueCloudV3Component) {
            this.trueCloudV3Component = trueCloudV3Component
        }

        fun getInstance(): TrueCloudV3Component {
            if (!(::trueCloudV3Component.isInitialized)) {
                error("TrueCloudV3Component not initialize")
            }
            return trueCloudV3Component
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            analyticsSubComponent: AnalyticsSubComponent,
            coreSubComponent: CoreSubComponent,
            coreDataSubComponent: CoreDataSubComponent,
            currentDateSubComponent: CurrentDateSubComponent,
            dataLegacySubComponent: DataLegacySubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent,
            navigationSubComponent: NavigationSubComponent
        ): TrueCloudV3Component
    }

    fun inject(service: TrueCloudV3Service)

    fun inject(fragment: ContactFragment)
    fun inject(fragment: ContactEditFragment)
    fun inject(fragment: CreateContactCustomLabelDialogFragment)
    fun inject(fragment: CreateContactDetailBottomSheetDialogFragment)
    fun inject(fragment: CreateContactSelectLabelBottomSheetDialogFragment)
    fun inject(fragment: FilesTrueCloudFragment)
    fun inject(fragment: MigrateDataFragment)
    fun inject(fragment: IntroMigrateDataFragment)
    fun inject(fragment: IntroPermissionFragment)
    fun inject(fragment: MainTrueCloudV3Fragment)
    fun inject(fragment: OptionFileBottomSheetDialogFragment)
    fun inject(fragment: OptionMainFileBottomSheetDialogFragment)
    fun inject(fragment: RenameDialogFragment)
    fun inject(fragment: SettingTrueCloudV3Fragment)
    fun inject(fragment: IntroTrueCloudFragment)
    fun inject(fragment: FileViewerFragment)
    fun inject(fragment: FileInfoBottomSheetDialogFragment)
    fun inject(fragment: ShareBottomSheetDialogFragment)
    fun inject(fragment: ShareControlAccessFragment)
    fun inject(fragment: OptionFileSelectedBottomSheetDialogFragment)
    fun inject(fragment: FileLocatorFragment)
    fun inject(fragment: TrashTrueCloudV3Fragment)
    fun inject(fragment: OptionTrashFileBottomSheetDialogFragment)
    fun inject(fragment: ImageItemViewerFragment)
    fun inject(fragment: FileItemViewerFragment)
    fun inject(fragment: AudioItemViewerFragment)
    fun inject(fragment: VideoItemViewerFragment)
    fun inject(fragment: PhotoEditorFragment)
    fun inject(fragment: PhotoEditorTransformFragment)
    fun inject(fragment: PhotoEditorAdjustFragment)
    fun inject(fragment: PhotoEditorFocusFragment)
    fun inject(fragment: PhotoEditorTextFragment)

    fun inject(worker: TrueCloudV3DownloadWorker)
    fun inject(fragment: SaveSharedFileBottomSheetDialogFragment)
    fun inject(fragment: SharedFileViewerFragment)
    fun inject(fragment: IntroTrueCloudSharedFileFragment)
    fun inject(fragment: AutoBackupSettingFragment)
}
