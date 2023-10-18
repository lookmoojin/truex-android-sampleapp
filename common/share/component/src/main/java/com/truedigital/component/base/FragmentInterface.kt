package com.truedigital.component.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

interface FragmentInterface {

    fun add(fragmentManager: FragmentManager, fragment: Fragment, replaceLayout: Int) {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(replaceLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    fun replace(fragmentManager: FragmentManager, fragment: Fragment, replaceLayout: Int) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(replaceLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    fun popAllFragment(fragmentManager: FragmentManager) {
        val backStackCount = fragmentManager.backStackEntryCount
        for (i in 0 until backStackCount) {
            fragmentManager.popBackStack()
        }
    }
}
