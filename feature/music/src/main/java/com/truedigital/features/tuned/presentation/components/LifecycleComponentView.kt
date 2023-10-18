package com.truedigital.features.tuned.presentation.components

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

open class LifecycleComponentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    protected val lifecycleComponents: MutableList<LifecycleComponent> = mutableListOf()

    open fun onStart(arguments: Bundle?) {
        val args = arguments ?: Bundle()
        if (context is AppCompatActivity) {
            (context as AppCompatActivity).intent.extras?.let { args.putAll(it) }
        }

        lifecycleComponents.forEach { it.onStart(args) }
    }

    open fun onPause() {
        lifecycleComponents.forEach { it.onPause() }
    }

    open fun onResume() {
        lifecycleComponents.forEach { it.onResume() }
    }

    open fun onStop() {
        lifecycleComponents.forEach { it.onStop() }
    }
}
