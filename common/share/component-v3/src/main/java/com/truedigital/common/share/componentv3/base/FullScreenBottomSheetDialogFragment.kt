package com.truedigital.common.share.componentv3.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.common.share.componentv3.R

open class FullScreenBottomSheetDialogFragment(
    @LayoutRes val layoutId: Int
) : BottomSheetDialogFragment() {

    constructor() : this(0)

    init {
        activity?.let {
            if (it.isFinishing || it.isDestroyed) return@let
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (layoutId != 0) {
            inflater.inflate(layoutId, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                this@FullScreenBottomSheetDialogFragment.onBackPressed()
            }
        }
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)?.let { parentLayout ->
                val behaviour = BottomSheetBehavior.from(parentLayout)
                parentLayout.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                behaviour.peekHeight = parentLayout.context.resources.displayMetrics.heightPixels
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun show(fragmentManager: FragmentManager, tag: String?) {
        if (this.isAdded) return
        super.show(fragmentManager, tag)
    }

    fun findNavController(@IdRes id: Int): NavController {
        return (childFragmentManager.findFragmentById(id) as? NavHostFragment)?.findNavController()
            ?: throw IllegalStateException("Fragment $this does not have a NavController set")
    }

    protected fun dismissDialog(): Boolean {
        dismiss()
        return true
    }

    protected open fun onBackPressed() {
        dismiss()
    }
}
