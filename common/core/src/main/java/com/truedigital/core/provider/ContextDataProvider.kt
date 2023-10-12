package com.truedigital.core.provider

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.util.Locale
import javax.inject.Inject

interface ContextDataProvider {

    var activityData: Activity?

    fun setDataActivity(activity: Activity)

    fun getDataActivity(): Activity?

    fun getString(id: Int): String

    fun getColor(id: Int): Int

    fun getDrawable(id: Int): Drawable?

    fun getDataContext(): Context

    fun getSystemService(serviceKey: String): Any

    fun getLocalStorageAbsolutePath(): String

    fun getAssets(): AssetManager

    fun getDimension(id: Int): Int

    fun getResources(): Resources

    fun updateContextLocale(newLocale: String)

    fun getContentResolver(): ContentResolver
}

class ContextDataProviderImp @Inject constructor(var context: Context) : ContextDataProvider {

    override var activityData: Activity? = null

    override fun setDataActivity(activity: Activity) {
        this.activityData = activity
    }

    override fun getDataActivity(): Activity? {
        return activityData
    }

    override fun getDataContext(): Context = context

    override fun getString(id: Int): String = context.getString(id)

    override fun getColor(id: Int): Int = ContextCompat.getColor(context, id)

    override fun getDrawable(id: Int): Drawable? = ContextCompat.getDrawable(context, id)

    override fun getSystemService(serviceKey: String): Any = context.getSystemService(serviceKey)

    override fun getLocalStorageAbsolutePath(): String = context.filesDir.absolutePath

    override fun getAssets(): AssetManager = context.assets

    override fun getDimension(id: Int): Int = context.resources.getDimension(id).toInt()

    override fun getResources(): Resources = context.resources

    override fun updateContextLocale(newLocale: String) {
        val res = context.resources
        val configuration = res.configuration
        configuration.setLocale(Locale(newLocale))
        this.context = this.context.createConfigurationContext(configuration)
    }

    override fun getContentResolver(): ContentResolver = context.contentResolver
}
