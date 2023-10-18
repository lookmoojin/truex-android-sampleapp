package com.truedigital.features.tuned.presentation.components

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.truedigital.features.tuned.presentation.common.ViewPagerAdapter

/**
 * If you have a view pager, use this to propagate lifecycle events to its children
 */
class ViewPagerComponent(val viewPagerAdapter: ViewPagerAdapter, val tabLayout: TabLayout) :
    LifecycleComponent {
    init {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                Unit
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                viewPagerAdapter.getView(tab.position)?.onPause()
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPagerAdapter.getView(tab.position)?.onResume()
            }
        })
    }

    override fun onResume() {
        viewPagerAdapter.getView(tabLayout.selectedTabPosition)?.onResume()
    }

    override fun onPause() {
        viewPagerAdapter.getViews().forEach { it.onPause() }
    }

    override fun onStart(arguments: Bundle) {
        Unit
    }

    override fun onStop() {
        viewPagerAdapter.getViews().forEach { it.onStop() }
    }
}
