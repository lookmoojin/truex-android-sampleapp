package com.truedigital.foundation.extension

import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout

fun AppBarLayout.handlerScrolling(isEnabled: Boolean) {
    this.post {
        if (ViewCompat.isLaidOut(this)) {
            val params = this.layoutParams as? CoordinatorLayout.LayoutParams
            val behavior = params?.behavior as? AppBarLayout.Behavior
            behavior?.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(@NonNull appBarLayout: AppBarLayout): Boolean {
                    return isEnabled
                }
            })
        }
    }
}
