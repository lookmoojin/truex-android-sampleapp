package com.truedigital.component.extension

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import java.lang.Exception

/**
 * Created by nut.tang on 1/8/2018 AD.
 */

const val RECYCLERVIEW_LOAD_MORE_VISIBLE_THRESHOLD = 5

interface RecyclerViewLoadMoreListener {
    fun onLoadMore()
}

fun RecyclerView.registerLoadMore(
    adapter: Adapter<RecyclerView.ViewHolder>,
    listener: RecyclerViewLoadMoreListener
) {

    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (recyclerView.layoutManager !is LinearLayoutManager) {
                throw Exception("Only LinearLayoutManager support here")
            }

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

            if (adapter.itemCount <= (lastVisibleItem + RECYCLERVIEW_LOAD_MORE_VISIBLE_THRESHOLD)) {
                if (adapter.itemCount == 0) {
                    // Before loaded data, Need not to load more.
                    return
                }
                listener.onLoadMore()
            }
        }
    })
}
