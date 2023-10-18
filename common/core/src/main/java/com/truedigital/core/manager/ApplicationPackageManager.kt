package com.truedigital.core.manager

import android.content.Context
import android.content.pm.PackageManager
import javax.inject.Inject

enum class ApplicationPackageName(val appName: String) {
    SystemWebView("com.google.android.webview"),
    SystemWebViewChrome("com.android.chrome")
}

interface ApplicationPackageManager {
    fun isPackageExisted(context: Context, targetPackage: String): Boolean
    fun getPackageVersion(context: Context, targetPackage: String): String
}

class ApplicationPackageManagerImpl @Inject constructor() : ApplicationPackageManager {
    override fun isPackageExisted(context: Context, targetPackage: String): Boolean {
        try {
            context.packageManager.getPackageInfo(targetPackage, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

    override fun getPackageVersion(context: Context, targetPackage: String): String {
        return try {
            context.packageManager.getPackageInfo(targetPackage, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }
}
