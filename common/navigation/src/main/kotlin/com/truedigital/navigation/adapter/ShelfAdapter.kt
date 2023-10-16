package com.truedigital.navigation.adapter

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.data.CommonViewModel
import com.truedigital.core.data.CommonViewModel.ShelfComponent.Companion.TAG_INLINE_BANNER_HIDDEN
import com.truedigital.core.data.ShelfSkeleton
import com.truedigital.core.data.ViewRenderPosition
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.utils.CoreUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber

class ShelfAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val onLoadContent: (shelfSkeleton: ShelfSkeleton) -> Unit,
    private val openDeeplink: (strDeeplink: String?) -> Unit,
    private val openMore1x1LabelShelf: ((items: MutableList<CommonViewModel>) -> Unit)? = null,
    private val trackFirebaseEvent: ((HashMap<String, Any>) -> Unit)? = null,
    private val trackFirebaseClickEvent: ((HashMap<String, Any>) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val TAG = ShelfAdapter::class.java.simpleName
        private val latestTagAnalytics: MutableList<String> = mutableListOf()
        private const val PERSONALIZE_TYPE = "personalize"
        private const val BY_PERSONALIZE_TYPE = "by_personalize"
    }

    private val skeletonList = mutableListOf<ShelfSkeleton>()
    private val tempShelfId = mutableListOf<String>()
    private val contentPosition: MutableList<ViewRenderPosition> = mutableListOf()
    private var height: Int = -1
    private var width: Int = -1

    init {
        recyclerView.post {
            height = Resources.getSystem().displayMetrics.heightPixels
            width = Resources.getSystem().displayMetrics.widthPixels
            if (height <= 0 || width <= 0) {
                Timber.e(
                    TAG,
                    "Can't render item, Because the recycler incorrect size x=$width y=$height"
                )
            }

            val layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(getScrollListenerCallback(layoutManager))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val skeleton = skeletonList.filterIndexed { index, skeleton ->
            skeleton.getStableId(position = index) == viewType
        }.firstOrNull() ?: return ViewHolder(View(context))

        val module = CoreUtility.getModule(context, skeleton.moduleName.orEmpty())

        module?.apply {
            tracking = {
                trackFirebaseEvent?.invoke(it)
                trackFirebaseClickEvent?.invoke(it)
            }

            if (skeleton.isContentFetching) {
                // View not ready.
                return ViewHolder(
                    getLoadingView(
                        context = context,
                        shelfSkeleton = skeleton,
                        width = width
                    )
                )
            }

            if (skeleton.isContentError) {
                // View Error.
                return ViewHolder(
                    getErrorView(
                        context = context,
                        shelfSkeleton = skeleton,
                        width = width
                    )
                )
            }

            // View Ready
            val itemView = getView(
                context = context,
                shelfSkeleton = skeleton,
                width = width,
                openDeeplink,
                openMore1x1LabelShelf
            ) ?: View(context)

            return ViewHolder(itemView)
        }

        return ViewHolder(View(context))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemCount(): Int = skeletonList.size

    override fun getItemId(position: Int): Long {
        return skeletonList[position].getStableId(position = position).toLong()
    }

    override fun getItemViewType(position: Int): Int {
        val skeleton = skeletonList[position]

        val parent = skeletonList.find { it.shelfId == skeleton.parentShelfId }
        if (parent != null && parent.isContentFetching && parent.isFetchContent) {
            // Has parent view Header, Footer
            // Parent still loading content
            // Hide view
            return -1
        }

        return skeleton.getStableId(position = position)
    }

    // Calculate view position
    private fun initContentPosition() {
        var previousOffset = 0
        contentPosition.clear()
        contentPosition.addAll(
            skeletonList.map { skeleton ->
                CoreUtility.getModule(context, skeleton.moduleName.orEmpty())?.let { module ->
                    val viewHeight = module.getViewHeight(context, skeleton, width)
                    val viewRenderPosition =
                        ViewRenderPosition(previousOffset, previousOffset + viewHeight)
                    previousOffset += viewHeight
                    return@map viewRenderPosition
                } ?: return@map ViewRenderPosition(0, 0)
            }
        )
    }

    fun setData(contentList: List<ShelfSkeleton>) {
        this.skeletonList.clear()
        this.tempShelfId.clear()
        this.skeletonList.addAll(contentList)
        initContentPosition()
        notifyDataSetChanged()
    }

    fun update(shelfSkeleton: ShelfSkeleton) {
        val index = skeletonList.indexOfFirst { it.shelfId == shelfSkeleton.shelfId }
        if (index >= 0) {
            skeletonList[index] = shelfSkeleton
            notifyItemChanged(index)
        }
    }

    fun add(content: ShelfSkeleton) {
        skeletonList.add(content)
        notifyItemInserted(itemCount - 1)
    }

    fun getItem(): List<ShelfSkeleton> {
        return this.skeletonList.toList()
    }

    // Get candidate visible content.
    fun getContentForRender(
        viewabilityHeight: Int,
        scrollPosition: Int = 0
    ): List<ShelfSkeleton> {

        val viewAbilityArea = scrollPosition + (viewabilityHeight * 2)

        val candidateContentList = mutableListOf<ShelfSkeleton>()
        contentPosition.forEachIndexed { index: Int, value: ViewRenderPosition ->
            if (value.isActivePosition(scrollPosition, viewAbilityArea)) {
                candidateContentList.add(skeletonList[index])
            }
        }

        return candidateContentList.filter {
            it.isContentFetching
        }
    }

    // Get the first content position top for renderer.
    fun getScrollPosition(pastVisibleItems: Int): Int {
        if (pastVisibleItems > skeletonList.size) {
            return -1
        }

        if (pastVisibleItems < 0) {
            return contentPosition.firstOrNull()?.start ?: -1
        }

        return contentPosition[pastVisibleItems].start
    }

    fun clearStoreTrackAnalyticList() {
        latestTagAnalytics.clear()
    }

    private var scrollJob: Job? = null

    private fun getScrollListenerCallback(mLayoutManager: LinearLayoutManager): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                scrollJob = CoroutineScope(Dispatchers.Main).launchSafe {
                    val visibleItemCount = mLayoutManager.childCount
                    val pastVisibleItems =
                        if (mLayoutManager.findFirstCompletelyVisibleItemPosition() >= 0) mLayoutManager.findFirstCompletelyVisibleItemPosition()
                        else 0
                    val visibleOffset = visibleItemCount + pastVisibleItems

                    // Calculate scroll position
                    val scrollPosition = getScrollPosition(pastVisibleItems)

                    if (scrollPosition < 0) {
                        return@launchSafe
                    }

                    for (i in pastVisibleItems..visibleOffset) {
                        if (i >= skeletonList.size) {
                            return@launchSafe
                        }
                        // Find Content
                        val shelfSkeleton: ShelfSkeleton = this@ShelfAdapter.skeletonList[i]

                        // Content isn't fetch.
                        if (!shelfSkeleton.isContentReady) {
                            val contentCandidateList = getContentForRender(height, scrollPosition)
                            if (contentCandidateList.isNotEmpty()) {
                                contentCandidateList
                                    .forEach {
                                        if (!tempShelfId.contains(it.shelfId)) {
                                            it.shelfId?.let { id ->
                                                tempShelfId.add(id)
                                                onLoadContent(it)
                                            }
                                        }
                                    }
                                return@launchSafe
                            }
                        }
                    }
                }

                try {
                    val firstCompletelyVisiblePosition =
                        mLayoutManager.findFirstCompletelyVisibleItemPosition()
                    val lastVisiblePosition = mLayoutManager.findLastVisibleItemPosition()
                    if (lastVisiblePosition <= skeletonList.lastIndex) {
                        for (i in firstCompletelyVisiblePosition..lastVisiblePosition) {
                            val view =
                                mLayoutManager.findViewByPosition(i)
                            processToTrackingFAScroll(skeletonList[i], view)
                        }
                    } else {
                        val view = mLayoutManager.findViewByPosition(firstCompletelyVisiblePosition)
                        processToTrackingFAScroll(
                            skeletonList[firstCompletelyVisiblePosition],
                            view
                        )
                    }
                } catch (e: IndexOutOfBoundsException) {
                    // Do nothing
                }
            }
        }
    }

    private fun processToTrackingFAScroll(shelf: ShelfSkeleton, view: View?) {
        val shelfId = shelf.shelfId ?: return

        if (!latestTagAnalytics.contains(shelfId) &&
            isValidTrackingShelf(shelf, view) &&
            (
                shelf.typeCallApi != PERSONALIZE_TYPE &&
                    shelf.typeCallApi != BY_PERSONALIZE_TYPE
                )
        ) {
            latestTagAnalytics.add(shelfId)
            mapFAScrollShelfTrackEvent(
                shelf.shelfIndex,
                shelf
            ).let { trackFirebaseEvent?.invoke(it) }
        }
    }

    private fun mapFAScrollShelfTrackEvent(
        index: Int,
        shelf: ShelfSkeleton
    ): HashMap<String, Any> {
        val shelfName = if (shelf.shelfName.isNullOrEmpty()) shelf.shelfNameTitle.orEmpty()
        else shelf.shelfName.orEmpty()
        val schemaId = if (shelf.componentSetting?.get("placement_id") == null) {
            ""
        } else {
            shelf.componentSetting?.get("placement_id")?.asString.orEmpty()
        }
        return hashMapOf<String, Any>(
            MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_VIEW_ITEM_LIST,
            MeasurementConstant.Key.KEY_SHELF_CODE to shelf.shelfId.orEmpty(),
            MeasurementConstant.Key.KEY_SHELF_NAME to shelfName,
            MeasurementConstant.Key.KEY_SHELF_SLUG to "",
            MeasurementConstant.Key.KEY_SHELF_INDEX to index,
            MeasurementConstant.Key.KEY_CONTENT_LIST_ID to "",
            MeasurementConstant.Key.KEY_RECOMMENDATION_SCHEMA_ID to schemaId
        ).apply {
            shelf.extraEventData?.let {
                this.putAll(it)
            }
        }
    }

    private fun isValidTrackingShelf(shelf: ShelfSkeleton, view: View?): Boolean {
        return !shelf.isTopNav &&
            !isInlineBannerHidden(view) &&
            !shelf.isHeaderFooter
    }

    private fun isInlineBannerHidden(view: View?): Boolean {
        return try {
            view?.tag?.toString() == TAG_INLINE_BANNER_HIDDEN
        } catch (e: NullPointerException) {
            Timber.e(e)
            false
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
