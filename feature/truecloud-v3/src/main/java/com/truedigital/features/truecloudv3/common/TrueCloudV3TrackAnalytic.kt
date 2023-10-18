package com.truedigital.features.truecloudv3.common

object TrueCloudV3TrackAnalytic {
    // screen
    const val SCREEN_TRUE_CLOUD = "truecloud"
    const val SCREEN_IMAGES = "truecloud > images"
    const val SCREEN_VIDEOS = "truecloud > videos"
    const val SCREEN_AUDIO = "truecloud > audio"
    const val SCREEN_CONTACTS = "truecloud > contacts"
    const val SCREEN_ALL_FILES = "truecloud > all_files"
    const val SCREEN_RECENT_UPLOAD = "truecloud > recent_upload"
    const val SCREEN_OTHER = "truecloud > others"
    const val SCREEN_SETTING = "truecloud > settings"

    // event
    const val EVENT_CLICK = "click"
    const val EVENT_UPLOAD = "true_cloud_upload"
    const val EVENT_DOWNLOAD = "true_cloud_download"
    const val EVENT_DELETE = "true_cloud_delete"
    const val EVENT_BACKUP = "true_cloud_backup"

    // link type
    const val LINK_TYPE_BROWSE = "true_cloud_browse"
    const val LINK_TYPE_INTRO = "intro_feature"
    const val LINK_TYPE_BOTTOM_SHEET = "true_cloud_bottom_sheet"

    // link desc
    const val LINK_DESC_IMAGES = "images"
    const val LINK_DESC_VIDEOS = "videos"
    const val LINK_DESC_AUDIOS = "audio"
    const val LINK_DESC_ALL_FILES = "all_files"
    const val LINK_DESC_RECENT_UPLOAD = "recent_upload"
    const val LINK_DESC_CONTACTS = "contacts"
    const val LINK_DESC_OTHER = "others"
    const val LINK_DESC_UPLOAD = "upload"
    const val LINK_DESC_LOGIN = "true_cloud_login"
    const val LINK_DESC_DISMISS = "true_cloud_dismiss"
    const val LINK_DESC_SETTING = "settings"
    const val LINK_DESC_TRASH = "trash"
    const val LINK_DESC_REMOVE_TO_TRASH = "Remove to Trash"
    const val LINK_DESC_RENAME = "Rename"
    const val LINK_DESC_DOWNLOAD = "Download"
    const val LINK_DESC_SEE_INFO = "See image info"
    const val LINK_DESC_SHARE = "Share"
    const val LINK_DESC_VIEW_FILE = "View file"
    const val LINK_DESC_MOVE_TO = "Move to"
    const val LINK_DESC_COPY_TO = "Copy to"
    const val LINK_DESC_SELECT = "Select"
    const val LINK_DESC_SORT_BY = "Sort by"
    const val LINK_DESC_NEW_FOLDER = "Add new folder"
    const val LINK_DESC_SAVE_TO_CLOUD = "Save to my true cloud"
    const val LINK_DESC_SAVE_TO_DEVICE = "Save to my device"
    const val LINK_DESC_COPY_LINK = "Copy link"
    const val LINK_DESC_CONTROL_SHARE = "Control share access"
    const val LINK_DESC_SYNC_ALL_CONTACTS = "Sync all contracts"
    const val LINK_DESC_SELECT_CONTACT_UPLOAD = "Select contracts to upload"
    const val LINK_DESC_DELETE_ALL_CONTRACTS = "Delete all contracts"
    const val LINK_DESC_EXPORT_TO_DEVICE = "Export to device"
    const val LINK_DESC_CUSTOM_LABEL = "Custom"
    const val LINK_DESC_DELETE_PERMANENT = "Delete permanently"
    const val LINK_DESC_RESTORE = "Restore"
    const val LINK_DESC_EDIT_IMAGE = "Edit image"

    // params
    const val PARAMS_FILE_TYPE = "file_types"

    // method
    const val METHOD = "method"
    const val METHOD_ON = "on"
    const val METHOD_OFF = "off"

    fun trackFirebaseOnClickEventWithCategory(fileCategoryType: FileCategoryType): String {
        return when (fileCategoryType) {
            FileCategoryType.RECENT -> LINK_DESC_RECENT_UPLOAD
            FileCategoryType.IMAGE -> LINK_DESC_IMAGES
            FileCategoryType.VIDEO -> LINK_DESC_VIDEOS
            FileCategoryType.AUDIO -> LINK_DESC_AUDIOS
            FileCategoryType.OTHER -> LINK_DESC_OTHER
            FileCategoryType.CONTACT -> LINK_DESC_CONTACTS
            FileCategoryType.UNSUPPORTED_FORMAT -> LINK_DESC_ALL_FILES
        }
    }

    fun getLinkDescFromMimeType(fileMimeType: FileMimeType): String {
        return when (fileMimeType) {
            FileMimeType.IMAGE -> LINK_DESC_IMAGES
            FileMimeType.VIDEO -> LINK_DESC_VIDEOS
            FileMimeType.AUDIO -> LINK_DESC_AUDIOS
            FileMimeType.OTHER -> LINK_DESC_OTHER
            FileMimeType.CONTACT -> LINK_DESC_CONTACTS
            else -> {
                LINK_DESC_OTHER
            }
        }
    }
}
