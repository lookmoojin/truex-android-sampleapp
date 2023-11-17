package com.truedigital.features.truecloudv3.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.truedigital.core.provider.ContextDataProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import javax.inject.Inject

interface TrueCloudV3FileUtil {
    fun getMimeType(uri: Uri, contentResolver: ContentResolver): String?
    fun readContentToFile(uri: Uri, contextDataProvider: ContextDataProvider): File?
    fun getPathFromUri(contextDataProvider: ContextDataProvider, uri: Uri): String?
    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String?
}

class TrueCloudV3FileUtilImpl @Inject constructor() : TrueCloudV3FileUtil {

    companion object {
        private const val BUFFER_SIZE = 1024
    }

    override fun getMimeType(uri: Uri, contentResolver: ContentResolver): String? {
        val mimeType = if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            val cr: ContentResolver = contentResolver
            cr.getType(uri)
        } else {
            val fileExtension: String = MimeTypeMap.getFileExtensionFromUrl(
                uri.toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault())
            )
        }
        return mimeType
    }

    override fun readContentToFile(uri: Uri, contextDataProvider: ContextDataProvider): File? {
        val path = contextDataProvider.getDataContext()
            .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.path
            ?: Environment.getExternalStorageDirectory().path + File.separator + "/temp_true_cloud"

        val displayName = getDisplayName(uri, contextDataProvider)
        if (!displayName.isNullOrEmpty()) {
            val dir = File(path)
            if (!dir.exists() && !dir.isDirectory) {
                dir.mkdirs()
            }
            val file = File(dir.path, displayName)
            file.createNewFile()
            contextDataProvider.getContentResolver().openInputStream(uri).use { `in` ->
                FileOutputStream(file, false).use { out ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var len: Int? = null
                    while (`in`?.read(buffer)?.also { len = it } != -1) {
                        len?.let { out.write(buffer, 0, it) }
                    }
                    return file
                }
            }
        }
        return null
    }

    override fun getPathFromUri(contextDataProvider: ContextDataProvider, uri: Uri): String? {
        var path: String? = null
        val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(
                contextDataProvider.getDataContext(),
                uri
            )
        ) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    path = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                path = getDownloadDocPath(contextDataProvider, uri)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                path = getDataColumn(
                    contextDataProvider.getDataContext(),
                    contentUri,
                    selection,
                    selectionArgs
                )
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            path = if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                contextDataProvider.getDataContext(),
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            path = uri.path
        }
        return path
    }

    override fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = uri?.let {
                context.getContentResolver().query(
                    it, projection, selection, selectionArgs,
                    null
                )
            }
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun getDownloadDocPath(contextDataProvider: ContextDataProvider, uri: Uri): String? {
        var id = DocumentsContract.getDocumentId(uri)
        if (id != null) {
            try {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                id = getDataColumn(
                    contextDataProvider.getDataContext(),
                    contentUri,
                    null,
                    null
                )
            } catch (error: NumberFormatException) {
                if (id.startsWith("raw:")) {
                    id.replaceFirst("raw:".toRegex(), "")
                } else {
                    id = null
                }
            }
        }
        return id
    }

    private fun getDisplayName(uri: Uri, contextDataProvider: ContextDataProvider): String? {
        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        contextDataProvider.getContentResolver().query(uri, projection, null, null, null)
            ?.use { cursor ->
                val columnIndex: Int =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            }
        return uri.path
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}
