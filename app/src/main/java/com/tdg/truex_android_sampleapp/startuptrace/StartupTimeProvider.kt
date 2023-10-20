package com.tdg.truex_android_sampleapp.startuptrace

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.firebase.FirebaseApp
import com.tdcm.trueidapp.startuptrace.StartupTrace

class StartupTimeProvider : ContentProvider() {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(): Boolean {
        context?.let {
            FirebaseApp.initializeApp(it)
            StartupTrace.onColdStartInitiated(it)
            mainHandler.post(StartupTrace.StartFromBackgroundRunnable)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}
