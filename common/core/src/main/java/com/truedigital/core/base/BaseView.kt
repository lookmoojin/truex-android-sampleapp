package com.truedigital.core.base

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.truedigital.core.data.ShelfSkeleton

/**
 * Created by Chitipat Mueanpaopong on 5/5/21.
 */
abstract class BaseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var tracking: ((HashMap<String, Any>) -> Unit)? = null
    open fun assignContent(
        shelfSkeleton: ShelfSkeleton,
        width: Int,
        shelfItemClicked: (strDeeplink: String?) -> Unit
    ) {}
    fun tracking(fa: HashMap<String, Any>) {
        tracking?.invoke(fa)
    }
}
