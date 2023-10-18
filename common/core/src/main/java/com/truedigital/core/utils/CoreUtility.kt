package com.truedigital.core.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.truedigital.core.base.BaseModule
import com.truedigital.core.extensions.launchSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

object CoreUtility {
    private const val TRUE_ID_MODULE_PREFIX = "trueid_module"
    private val TAG = CoreUtility::class.java.canonicalName as String
    private var modulesCache: HashMap<String, BaseModule>? = null
    private var navGraph: HashMap<String, Int>? = null
    private var idFragment: HashMap<String, Int>? = null

    fun getModule(context: Context, vType: String): BaseModule? {
        if (modulesCache.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launchSafe {
                initialModuleInstance(context)
            }
        }
        val modName: String = vType
        return modulesCache?.get(modName)
    }

    fun getNavGraph(slug: String): Int? {
        return navGraph?.get(slug)
    }

    fun getArrayNavGraph(): MutableCollection<Int>? {
        return navGraph?.values
    }

    fun getIdFragment(slug: String): Int {
        return idFragment?.get(slug) ?: 0
    }

    private fun initialModuleInstance(context: Context) {
        val timeBegin = System.currentTimeMillis()

        val app: ApplicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        // Init module map
        modulesCache = HashMap()
        navGraph = HashMap()
        idFragment = HashMap()

        app.metaData.keySet().forEach {
            if (it.startsWith(TRUE_ID_MODULE_PREFIX)) {
                val className = app.metaData.getString(it, "")
                if (className.isNotEmpty()) {
                    try {
                        val module = Class.forName(className).kotlin.let { kClass ->
                            kClass.objectInstance ?: kClass.java.newInstance()
                        }
                        if (module is BaseModule) {
                            // module is extends BaseModule
                            modulesCache?.set(module.slug, module)
                            module.fragmentId?.let { id ->
                                idFragment?.set(module.slug, id)
                            }
                            module.navGraph?.let { navGraphId ->
                                navGraph?.set(module.slug, navGraphId)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val timeEnd = System.currentTimeMillis()
        val timeElapsed = timeEnd - timeBegin
        Timber.d(TAG, "generateModuleInstance() cost $timeElapsed ms")
    }
}
