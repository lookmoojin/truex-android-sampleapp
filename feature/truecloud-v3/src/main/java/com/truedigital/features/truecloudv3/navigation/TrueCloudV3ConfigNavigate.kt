package com.truedigital.features.truecloudv3.navigation

import androidx.collection.arrayMapOf
import com.truedigital.features.truecloudv3.R
import com.truedigital.navigation.router.Destination

object TrueCloudV3ConfigNavigate : Map<Destination, Int> by arrayMapOf(
    AllFileToImageViewer to R.id.action_FileTrueCloudFragment_to_fileViewerFragment,
    AllFileToPermission to R.id.action_FileTrueCloudFragment_to_trueCloudV3IntroPermissionFragment,
    AllFileToMainOptionBottomSheet to R.id.action_FileTrueCloudFragment_to_optionMainFileBottomSheetDialogFragment,
    AllFileToOptionFileBottomSheet to R.id.action_FileTrueCloudFragment_to_optionFileBottomSheetDialogFragment,
    OptionFileToRenameDialog to R.id.action_optionFileBottomSheetDialogFragment_to_renameDialogFragment,
    OptionFileToFileInfoBottomSheet to
        R.id.action_optionFileBottomSheetDialogFragment_to_fileInfoBottomSheetDialogFragment,
    OptingMainToSortByBottomSheet to
        R.id.action_OptionMainFileBottomSheetDialogFragment_to_sortByBottomSheetDialogFragment,
    OptingMainToCreateNewFolder to R.id.action_OptionMainFileBottomSheetDialogFragment_to_createNewFolderDialogFragment,
    ContactDetailToEditContactFragment to
        R.id.action_TrueCloudV3DetailBottomSheetDialogFragment_to_trueCloudV3EditContactFragment,
    ContactSelectLabelBottomSheetToCustomLabelDialog to
        R.id.action_TrueCloudV3ContactSelectLabelBottomSheetDialogFragment_to_trueCloudV3CustomLabelDialogFragment,
    ContactToContactDetailBottomSheet to
        R.id.action_ContactTrueCloudFragment_to_trueCloudV3DetailBottomSheetDialogFragment,
    ContactToOptionContactBottomSheet to
        R.id.action_ContactTrueCloudFragment_to_trueCloudV3OptionContactBottomSheetDialogFragment,
    ContactToSyncContactBottomSheet to
        R.id.action_ContactTrueCloudFragment_to_trueCloudV3SyncContactBottomSheetDialogFragment,
    ContactToPermission to R.id.action_ContactTrueCloudFragment_to_trueCloudV3IntroPermissionFragment,
    EditContactToContactSelectLabelBottomSheet to
        R.id.action_TrueCloudV3EditContactFragment_to_trueCloudV3ContactSelectLabelBottomSheetDialogFragment,
    FileViewerToOptionFileBottomSheet to R.id.action_FileViewerFragment_to_optionFileBottomSheetDialogFragment,
    IntroTrueCloudToMain to R.id.action_introTrueCloudFragment_to_mainTrueCloudV3Fragment,
    IntroMigrateToMigrating to R.id.action_IntroMigrateDataFragment_to_MigrateDataFragment,
    MainToContact to R.id.action_mainTrueCloudFragment_to_contactFragment,
    MainToCreateNewFolder to R.id.action_mainTrueCloudFragment_to_createNewFolderDialogFragment,
    MainToFiles to R.id.action_mainTrueCloudFragment_to_filesTrueCloudFragment,
    MainToIntroMigrate to R.id.action_mainTrueCloudFragment_to_introMigrateDataFragment,
    MainToMigrate to R.id.action_mainTrueCloudFragment_to_MigrateDataFragment,
    MainToPermission to R.id.action_mainTrueCloudFragment_to_trueCloudV3IntroPermissionFragment,
    MainToSetting to R.id.action_mainTrueCloudFragment_to_settingTrueCloudV3Fragment,
    SettingToMigrating to R.id.action_SettingTrueCloudv3Fragment_to_MigrateDataFragment,
    ShareBottomSheetToShareControlAccess to R.id.action_ShareBottomSheetDialogFragment_to_ShareControlAccessFragment,
    OptionFileToShareBottomSheet to R.id.action_optionFileBottomSheetDialogFragment_to_shareBottomSheetDialogFragment,
    SharedFileToBottomSheet to R.id.action_SharedFileViewerFragment_to_SharedFileBottomSheetDialogFragment,
    SharedFileViewerToMain to R.id.action_sharedFileViewerFragment_to_mainTrueCloudV3Fragment,
    IntroSharedToSharedViewer to R.id.action_introTrueCloudSharedFileFragment_to_sharedFileViewerFragment,
    SelectFileToOptionFileSelectedBottomSheet to
        R.id.action_FileTrueCloudFragment_to_optionFileSelectedBottomSheetDialogFragment,
    OptionFileSelectedToFileLocatorFragment to R.id.action_FileSelectedBottomSheet_to_trueCloudV3FileLocatorFragment,
    MainToTrash to R.id.action_mainTrueCloudV3Fragment_to_trueCloudV3TrashFragment,
    TrashToTrashBottomSheet to R.id.action_trueCloudV3TrashFragment_to_optionTrashFileBottomSheetDialogFragment,
    TrashToMainOptionBottomSheet to R.id.action_trueCloudV3TrashFragment_to_optionMainFileBottomSheetDialogFragment,
    SettingToAutoBackup to R.id.action_SettingTrueCloudv3Fragment_to_trueCloudV3AutoBackupSettingFragment,
    OptionFileToPhotoEditor to R.id.action_optionFileBottomSheetDialogFragment_to_photoEditorFragment,
    PhotoEditorToPhotoEditorTransform to R.id.action_photoEditor_to_photoEditorTransformFragment,
    PhotoEditorToPhotoEditorAdjust to R.id.action_photoEditor_to_photoEditorAdjustFragment,
    PhotoEditorToPhotoEditorFocus to R.id.action_photoEditor_to_photoEditorFocusFragment,
    PhotoEditorToPhotoEditorText to R.id.action_photoEditor_to_photoEditorTextFragment,
)
