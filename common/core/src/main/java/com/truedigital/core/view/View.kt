package com.truedigital.core.view

import android.view.View

fun View.debounceClick(debounceInterval: Long = 750L, listenerBlock: (View) -> Unit) =
    setOnClickListener(DebounceOnClickListener(debounceInterval, listenerBlock))
