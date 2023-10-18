package com.truedigital.foundation.view.scene

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.newrelic.agent.android.NewRelic

open class FoundationActivity : AppCompatActivity() {

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        try {
            super.onRestoreInstanceState(savedInstanceState)
        } catch (error: Exception) {
            val exceptionAttributes = mapOf(
                "Key" to "onSaveInstanceState in FoundationActivity",
                "Value" to error.message
            )
            NewRelic.recordHandledException(Exception(error), exceptionAttributes)
        }
    }

    fun replaceFragment(@IdRes container: Int, fragment: Fragment, isBackStack: Boolean, tag: String? = null) {
        supportFragmentManager.beginTransaction()
            .replace(container, fragment, tag)
            .apply {
                if (isBackStack) {
                    addToBackStack(null)
                }
            }
            .commit()
    }

    fun addFragment(@IdRes container: Int, fragment: Fragment, isBackStack: Boolean) {
        supportFragmentManager.beginTransaction()
            .add(container, fragment)
            .apply {
                if (isBackStack) {
                    addToBackStack(null)
                }
            }
            .commit()
    }

    fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    fun backStackEntryCount(): Int {
        return supportFragmentManager.backStackEntryCount
    }
}
