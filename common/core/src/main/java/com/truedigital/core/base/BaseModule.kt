package com.truedigital.core.base

import android.content.Context
import android.view.View
import com.truedigital.core.data.CommonViewModel
import com.truedigital.core.data.ShelfSkeleton
import com.truedigital.core.data.ShelfTypeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseModule {
    abstract val slug: String
    abstract val version: String
    var tracking: ((HashMap<String, Any>) -> Unit)? = null

    open val navGraph: Int?
        get() = null
    open val fragmentId: Int?
        get() = null

    abstract fun getView(
        context: Context,
        shelfSkeleton: ShelfSkeleton,
        width: Int,
        openDeeplink: (strDeeplink: String?) -> Unit,
        openMore1x1LabelShelf: ((items: MutableList<CommonViewModel>) -> Unit)? = null
    ): View?

    open fun getShelfFromType(
        context: Context,
        shelf: ShelfTypeModel,
        openDeeplink: ((deeplink: String) -> Unit)?,
        onError: () -> Unit?
    ): View? {
        return null
    }

    open fun sendTracking(fa: HashMap<String, Any>) {
        tracking?.invoke(fa)
    }

    open suspend fun getAsyncContent(
        shelfSkeleton: ShelfSkeleton
    ): Flow<Any?> {
        return flow {
            emit(null)
        }
    }

    open fun getViewHeight(
        context: Context,
        shelfSkeleton: ShelfSkeleton,
        width: Int
    ): Int {
        return 50
    }

    open fun getLoadingView(
        context: Context,
        shelfSkeleton: ShelfSkeleton,
        width: Int
    ): View {
        return View(context)
    }

    open fun getErrorView(
        context: Context,
        shelfSkeleton: ShelfSkeleton,
        width: Int
    ): View {
        return View(context)
    }

    open fun shouldFetchContent(shelfSkeleton: ShelfSkeleton): Boolean {
        return false
    }
}
