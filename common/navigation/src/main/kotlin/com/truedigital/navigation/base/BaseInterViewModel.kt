package com.truedigital.navigation.base

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truedigital.core.constants.CoreConstants
import com.truedigital.core.data.CommonViewModel
import com.truedigital.core.data.LoadedFrom
import com.truedigital.core.data.LoadingState
import com.truedigital.core.data.ShelfSkeleton
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.utils.CoreUtility
import com.truedigital.foundation.extension.SingleLiveEvent
import com.truedigital.navigations.share.data.model.TabResponseItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class BaseInterViewModel : ViewModel() {

    abstract fun getShelf()

    val onDataLoaded = MutableLiveData<ShelfSkeleton>()
    val content = MutableLiveData<TabResponseItem>()
    val onOpenTopNav = SingleLiveEvent<Unit>()
    val onShowMoreItem1x1Label = SingleLiveEvent<List<CommonViewModel.Items.Item1x1Label>>()
    protected val shelfSkeletonList: MutableList<ShelfSkeleton> = mutableListOf()

    private companion object {
        const val BY_PERSONALIZE_TYPE = "by_personalize"
        const val PERSONALIZE_TYPE = "personalize"
    }

    open val openMore1x1LabelShelf: (items: MutableList<CommonViewModel>) -> Unit = { items ->
        // open more 1x1label
        val filterItem = items.filterIsInstance<CommonViewModel.Items.Item1x1Label>()
        onShowMoreItem1x1Label.value = filterItem
    }
    val openTopNav: (strDeeplink: String?) -> Unit = { strDeeplink ->
        when (strDeeplink) {
            CoreConstants.TopNavViewType.SETTING -> {
                performClickMore()
                onOpenTopNav.postValue(Unit)
            }

            CoreConstants.TopNavViewType.CALL -> {
                performClickCall()
            }

            CoreConstants.TopNavViewType.NOTIFICATION -> {
                performClickNotification()
            }

            CoreConstants.TopNavViewType.COMMUNITY -> {
                performClickCommunity()
            }

            CoreConstants.TopNavViewType.PROFILE -> {
                performClickAvatar()
                onOpenTopNav.postValue(Unit)
            }
        }
    }

    fun setContent(content: TabResponseItem) {
        this.content.value = content
    }

    fun getTitle(): String {
        return content.value?.title.orEmpty()
    }

    open fun performClickAvatar() {}

    open fun performClickCall() {}

    open fun performClickCommunity() {}

    open fun performClickMore() {}

    open fun performClickNotification() {}

    open fun getData(context: Context, shelfSkeleton: ShelfSkeleton) {
        val module = CoreUtility.getModule(context, shelfSkeleton.moduleName.orEmpty())
        module?.apply {
            if (shelfSkeleton.isContentReady) {
                return
            } else if (!shouldFetchContent(shelfSkeleton)) {
                // Ready to render view.
                if (shelfSkeleton.parentShelfId != null || (shelfSkeleton.items != null && shelfSkeleton.items?.size() != 0)) {
                    shelfSkeleton.state = LoadingState.LOADED
                    onDataLoaded.postValue(shelfSkeleton)
                } else {
                    hideShelf(shelfSkeleton)
                }
            } else {
                // Need to fetch some data.
                viewModelScope.launchSafe {
                    getAsyncContent(shelfSkeleton)
                        .flowOn(Dispatchers.IO)
                        .catch {
                            hideShelf(shelfSkeleton)
                        }.collect {
                            shelfSkeleton.state = LoadingState.LOADED
                            shelfSkeleton.loadedFrom = LoadedFrom.API
                            shelfSkeleton.data = it
                            onDataLoaded.value = shelfSkeleton
                        }
                }
            }
        }
    }

    private fun hideShelf(shelfSkeleton: ShelfSkeleton) {
        if ((shelfSkeleton.reRankShelf && shelfSkeleton.items?.size() != 0).or(
                shelfSkeleton.typeCallApi == PERSONALIZE_TYPE ||
                    shelfSkeleton.typeCallApi == BY_PERSONALIZE_TYPE
            )
        ) {
            shelfSkeleton.state = LoadingState.LOADED
        } else {
            // Hide parent skeleton
            shelfSkeleton.state = LoadingState.ERROR
            onDataLoaded.value = shelfSkeleton
            // Update error state to child view.
            shelfSkeletonList.filter {
                it.parentShelfId == shelfSkeleton.shelfId
            }.forEach {
                it.state = LoadingState.ERROR
                onDataLoaded.value = it
            }
        }
    }
}
