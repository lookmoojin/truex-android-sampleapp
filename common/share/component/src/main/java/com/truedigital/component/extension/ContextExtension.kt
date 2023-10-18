package com.truedigital.component.extension

import android.content.Context
import com.truedigital.component.R

val Context.isTablet
    get() = this.resources.getBoolean(R.bool.is_tablet)
