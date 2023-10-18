package com.truedigital.features.truecloudv3.extension

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.truedigital.features.truecloudv3.common.FileCategoryType
import java.util.Locale

fun Fragment.checkPermissionAlready(permission: String): Boolean {
    return this.context?.let { fragmentContext ->
        ContextCompat.checkSelfPermission(
            fragmentContext,
            permission
        )
    } == PackageManager.PERMISSION_GRANTED
}

fun Fragment.checkStoragePermissionAlready(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        checkPermissionAlready(Manifest.permission.READ_MEDIA_AUDIO) &&
                checkPermissionAlready(Manifest.permission.READ_MEDIA_VIDEO) &&
                checkPermissionAlready(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        checkPermissionAlready(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                checkPermissionAlready(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}

fun Fragment.checkContactStoragePermissionAlready(): Boolean {
    return checkStoragePermissionAlready() && checkPermissionAlready(Manifest.permission.READ_CONTACTS)
}

fun Fragment.actionGetContent(mimeTypes: Array<String>): Intent {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        type = "*/*"
        putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    return intent
}

fun Fragment.getStoragePermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}

fun Fragment.getNotificationPermission(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}

fun Fragment.actionGetContentWithMimeType(categoryType: FileCategoryType): Intent {
    val mimeType = categoryType.fileMimeType.mimeType
    lateinit var intent: Intent
    val manufacturer = Build.MANUFACTURER.uppercase(Locale.getDefault())
    if (manufacturer.contains("SAMSUNG")) {
        intent = Intent("com.sec.android.app.myfiles.PICK_DATA")
        intent.putExtra("CONTENT_TYPE", mimeType)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.type = mimeType
    } else {
        /*
      Remaining devices
     */
        intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = categoryType.fileMimeType.mimeType
    }
    if (categoryType == FileCategoryType.OTHER) {
        val mimeTypes = arrayOf(
            "text/*",
            "application/pdf",
            "application/doc",
            "application/docx",
            "application/pptx",
            "application/xlsx"
        )
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
    }
    intent.putExtra(
        Intent.EXTRA_ALLOW_MULTIPLE,
        true
    )
    return intent
}
