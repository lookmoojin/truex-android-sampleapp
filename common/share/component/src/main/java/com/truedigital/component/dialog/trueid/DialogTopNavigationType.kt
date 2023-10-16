package com.truedigital.component.dialog.trueid

import androidx.annotation.StyleRes
import com.truedigital.component.R

enum class DialogTopNavigationType(@StyleRes val animation: Int) {
    BACK_BUTTON(animation = R.style.dialog_animation_push),
    CLOSE_BUTTON(animation = R.style.dialog_animation_up),
    NO_BUTTON(animation = R.style.dialog_animation_fade)
}
