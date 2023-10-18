package com.truedigital.features.truecloudv3.common

enum class TaskActionType(val action: String) {
    DOWNLOAD("download"),
    UPLOAD("upload"),
    UNKNOWN("unknown"),
    AUTO_BACKUP("autoBackup")
}
