package com.truedigital.features.tuned.presentation.common

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.truedigital.features.tuned.presentation.components.LifecycleComponentView
import kotlin.reflect.KClass

class ViewPagerAdapter(val context: Context) : PagerAdapter() {

    var onPageLoadedListener: ((View) -> Unit)? = null

    var items: List<Page> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    private val cachedViews: HashMap<Int, LifecycleComponentView> = hashMapOf()

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        return cachedViews[position] ?: run {
            // Use reflection to instantiate view
            items[position].view.java.getConstructor(Context::class.java).newInstance(context)
                .also { view ->
                    view.layoutParams = container.layoutParams
                    view.onStart(items[position].arguments)
                    view.onResume()
                    cachedViews[position] = view
                    container.addView(view)
                    onPageLoadedListener?.invoke(view)
                }
        }
    }

    override fun getPageTitle(position: Int): CharSequence = items[position].title

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        val view = item as LifecycleComponentView
        view.onStop()
        container.removeView(view)
        cachedViews.remove(position)
    }

    override fun isViewFromObject(view: View, item: Any): Boolean = (item as View) == view

    override fun getCount(): Int = items.size

    fun getView(position: Int): LifecycleComponentView? = cachedViews[position]
    fun getViews(): List<LifecycleComponentView> = cachedViews.map { it.value }

    data class Page(
        val title: String,
        val view: KClass<out LifecycleComponentView>,
        val arguments: Bundle? = null,
        val icon: Int? = null
    )
}
