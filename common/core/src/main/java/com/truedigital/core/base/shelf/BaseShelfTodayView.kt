package com.truedigital.core.base.shelf

import android.content.Context
import android.util.AttributeSet
import com.truedigital.core.base.BaseView
import com.truedigital.core.data.CommonViewModel
import com.truedigital.core.data.ShelfSkeleton

abstract class BaseShelfTodayView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr) {

    open fun assignContent(
        shelf: ShelfSkeleton,
        width: Int,
        shelfItemClicked: (strDeeplink: String?) -> Unit,
        itemMoreClick: ((items: MutableList<CommonViewModel>) -> Unit)?
    ) {}
}
