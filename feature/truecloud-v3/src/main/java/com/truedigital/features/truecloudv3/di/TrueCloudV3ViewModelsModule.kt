package com.truedigital.features.truecloudv3.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truedigital.features.truecloudv3.presentation.viewmodel.AutoBackupViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.ContactViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.CreateContactCustomLabelDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.CreateContactDetailBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.CreateContactSelectLabelBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.FileInfoBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.FileViewerViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.FilesTrueCloudViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.IntroMigrateDataViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.IntroPermissionViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.IntroTrueCloudSharedFileViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.IntroTrueCloudViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.MainTrueCloudV3ViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.MigrateDataViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.OptionFileBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.OptionFileSelectedBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.OptionMainFileBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.OptionTrashFileBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.RenameDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.SaveSharedFileBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.SettingTrueCloudV3ViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.ShareBottomSheetDialogViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.ShareControlAccessViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.SharedFileViewerViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3ContactEditViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3FileViewerViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorAdjustViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorFileViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorFocusViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorTextViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorTransformViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.VideoItemViewerViewModel
import com.truedigital.foundation.di.scopes.ViewModelKey
import com.truedigital.foundation.presentations.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TrueCloudV3ViewModelsModule {

    @Binds
    fun bindsViewModelFactory(
        viewModelFactory: ViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ContactViewModel::class)
    fun bindsContactViewModel(
        contactViewModelImpl: ContactViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrueCloudV3ContactEditViewModel::class)
    fun bindsTrueCloudV3ContactEditViewModel(
        trueCloudV3ContactEditViewModelImpl: TrueCloudV3ContactEditViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateContactDetailBottomSheetDialogViewModel::class)
    fun bindsCreateContactDetailBottomSheetDialogViewModel(
        createContactDetailBottomSheetDialogViewModelImpl: CreateContactDetailBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainTrueCloudV3ViewModel::class)
    fun bindsMainTrueCloudV3ViewModel(
        mainTrueCloudV3ViewModelImpl: MainTrueCloudV3ViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OptionMainFileBottomSheetDialogViewModel::class)
    fun bindsOptionMainFileBottomSheetDialogViewModel(
        optionMainFileBottomSheetDialogViewModelImpl: OptionMainFileBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OptionFileBottomSheetDialogViewModel::class)
    fun bindsOptionFileBottomSheetDialogViewModel(
        optionFileBottomSheetDialogViewModelImpl: OptionFileBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RenameDialogViewModel::class)
    fun bindsRenameDialogViewModel(
        renameDialogViewModelImpl: RenameDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FilesTrueCloudViewModel::class)
    fun bindsFilesTrueCloudViewModel(
        filesTrueCloudViewModelImpl: FilesTrueCloudViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroPermissionViewModel::class)
    fun bindsIntroPermissionViewModel(
        introPermissionViewModelImpl: IntroPermissionViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateContactCustomLabelDialogViewModel::class)
    fun bindsCreateContactCustomLabelDialogViewModel(
        createContactCustomLabelDialogViewModelImpl: CreateContactCustomLabelDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateContactSelectLabelBottomSheetDialogViewModel::class)
    fun bindsCreateContactSelectLabelBottomSheetDialogViewModel(
        createContactSelectLabelBottomSheetDialogViewModelImpl: CreateContactSelectLabelBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroMigrateDataViewModel::class)
    fun bindsIntroMigrateDataViewModel(
        introMigrateDataViewModelImpl: IntroMigrateDataViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MigrateDataViewModel::class)
    fun bindsMigrateDataViewModel(
        migrateDataViewModelImpl: MigrateDataViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingTrueCloudV3ViewModel::class)
    fun bindsSettingTrueCloudV3ViewModel(
        settingTrueCloudV3ViewModelImpl: SettingTrueCloudV3ViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroTrueCloudViewModel::class)
    fun bindsIntroTrueCloudViewModel(
        introTrueCloudViewModelImpl: IntroTrueCloudViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FileViewerViewModel::class)
    fun bindsFileViewerViewModel(
        fileViewerViewModelImpl: FileViewerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FileInfoBottomSheetDialogViewModel::class)
    fun bindsFileInfoBottomSheetDialogViewModel(
        fileInfoBottomSheetDialogViewModel: FileInfoBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShareBottomSheetDialogViewModel::class)
    fun bindsShareBottomSheetDialogViewModel(
        shareBottomSheetDialogViewModel: ShareBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShareControlAccessViewModel::class)
    fun bindsShareControlAccessViewModel(
        shareControlAccessViewModel: ShareControlAccessViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SharedFileViewerViewModel::class)
    fun bindsSharedFileViewerViewModel(
        sharedFileViewerViewModel: SharedFileViewerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SaveSharedFileBottomSheetDialogViewModel::class)
    fun bindsSaveSharedFileBottomSheetDialogViewModel(
        saveSharedFileBottomSheetDialogViewModel: SaveSharedFileBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroTrueCloudSharedFileViewModel::class)
    fun bindsIntroTrueCloudSharedFileViewModel(
        introTrueCloudSharedFileViewModel: IntroTrueCloudSharedFileViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OptionFileSelectedBottomSheetDialogViewModel::class)
    fun bindsOptionFileSelectedBottomSheetDialogViewModel(
        optionFileSelectedBottomSheetDialogViewModelImpl: OptionFileSelectedBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OptionTrashFileBottomSheetDialogViewModel::class)
    fun bindsOptionTrashFileBottomSheetDialogViewModel(
        optionTrashFileBottomSheetDialogViewModel: OptionTrashFileBottomSheetDialogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrueCloudV3FileViewerViewModel::class)
    fun bindTrueCloudV3FileViewerViewModel(
        trueCloudV3FileViewerViewModel: TrueCloudV3FileViewerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AutoBackupViewModel::class)
    fun bindsAutoBackupViewModel(
        autoBackupViewModel: AutoBackupViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrueCloudV3PhotoEditorViewModel::class)
    fun bindTrueCloudV3PhotoEditorViewModel(
        trueCloudV3PhotoEditorViewModel: TrueCloudV3PhotoEditorViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoItemViewerViewModel::class)
    fun bindVideoItemViewerViewModel(
        videoItemViewerViewModel: VideoItemViewerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrueCloudV3PhotoEditorTransformViewModel::class)
    fun bindTrueCloudV3PhotoEditorTransformViewModel(
        trueCloudV3PhotoEditorTransformViewModel: TrueCloudV3PhotoEditorTransformViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrueCloudV3PhotoEditorAdjustViewModel::class)
    fun bindTrueCloudV3PhotoEditorAdjustViewModel(
        trueCloudV3PhotoEditorAdjustViewModel: TrueCloudV3PhotoEditorAdjustViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrueCloudV3PhotoEditorFocusViewModel::class)
    fun bindTrueCloudV3PhotoEditorFocusViewModel(
        trueCloudV3PhotoEditorFocusViewModel: TrueCloudV3PhotoEditorFocusViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrueCloudV3PhotoEditorTextViewModel::class)
    fun bindTrueCloudV3PhotoEditorTextViewModel(
        trueCloudV3PhotoEditorTextViewModel: TrueCloudV3PhotoEditorTextViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrueCloudV3PhotoEditorFileViewModel::class)
    fun bindTrueCloudV3PhotoEditorFileViewModel(
        trueCloudV3PhotoEditorFileViewModel: TrueCloudV3PhotoEditorFileViewModel
    ): ViewModel
}
