package com.truedigital.core.base.shelf

import android.content.Context
import android.util.AttributeSet
import com.truedigital.core.base.BaseView
import com.truedigital.core.data.CommonShelfModel

abstract class BaseShelfView<T : CommonShelfModel>(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr) {

    open fun assignContent(
        shelf: T,
        width: Int,
        shelfItemClicked: (strDeeplink: String?) -> Unit
    ) {}
}
