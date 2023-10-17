package com.truedigital.features.tuned.presentation.components

import android.os.Bundle
import com.truedigital.core.constant.AppConfig
import com.truedigital.features.tuned.presentation.common.TunedActivity

open class LifecycleComponentActivity : TunedActivity() {
    protected val lifecycleComponents: MutableList<LifecycleComponent> = mutableListOf()

    companion object {
        private const val UID = 123456
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val arguments = intent.extras ?: Bundle()
        savedInstanceState?.let {
            arguments.putAll(it)
        }

        // pass TrueId Login info
//        arguments.putBoolean(AppConfig.IS_LOG_IN, isLogin())
        arguments.putInt(AppConfig.UID, UID)

        lifecycleComponents.forEach { it.onStart(arguments) }
    }

    override fun onDestroy() {
        lifecycleComponents.forEach { it.onStop() }
        super.onDestroy()
    }

    override fun onPause() {
        lifecycleComponents.forEach { it.onPause() }
        super.onPause()
    }

    override fun onResume() {
        lifecycleComponents.forEach { it.onResume() }
        super.onResume()
    }

    override fun onBackPressed() {
        if (!lifecycleComponents.any { it.onBack() }) {
            super.onBackPressed()
        }
    }
}
