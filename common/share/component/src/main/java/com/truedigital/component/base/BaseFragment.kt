package com.truedigital.component.base

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.truedigital.common.share.datalegacy.navigation.CoreArgs
import com.truedigital.common.share.datalegacy.navigation.NavigationManager
import com.truedigital.common.share.datalegacy.navigation.navArguments
import com.truedigital.component.base.authentication.bus.SendRequestOrientationEvent
import com.truedigital.component.dialog.MIAlertDialogFragment
import com.truedigital.component.dialog.TrueIDProgressDialog
import com.truedigital.core.bus.MIBusProvider
import com.truedigital.core.view.CoreFragment
import com.truedigital.foundation.view.scene.FoundationActivity

abstract class BaseAbstractFragment : CoreFragment {
    constructor() : super()
    constructor(layoutId: Int) : super(layoutId)

    abstract fun wantToEnableRotateScreen(): Boolean
    abstract fun enableTrueWifiMiniView(): Boolean
    abstract fun enableBackToTopView(): Boolean
    abstract fun isFromDiscoverShelf(): Boolean
}

open class BaseFragment : BaseAbstractFragment, OrientationChangeEventListener, FragmentInterface {

    constructor() : super()
    constructor(layoutId: Int) : super(layoutId)

    /**
     * =============================================================================================
     * Define Variable
     * =============================================================================================
     */
    private var progressDialog: TrueIDProgressDialog? = null
    private var alertDialog: MIAlertDialogFragment? = null
    private var orientationChangeEvent: OrientationChangeEvent? = null

    protected val coreArgs: CoreArgs by navArguments()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orientationChangeEvent = context?.let { OrientationChangeEvent(it, this) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationManager.setActionReplaceFragment()
    }

    override fun onResume() {
        super.onResume()
        if (wantToEnableRotateScreen()) {
            enableOrientationChangeEvent()
        }
        hideKeyboard(view)
    }

    override fun onPause() {
        super.onPause()
        if (wantToEnableRotateScreen()) {
            disableOrientationChangeEvent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NavigationManager.setActionPopFragment()
    }

    override fun wantToEnableRotateScreen(): Boolean {
        return false
    }

    override fun enableTrueWifiMiniView(): Boolean {
        return false
    }

    override fun enableBackToTopView(): Boolean {
        return false
    }

    override fun isFromDiscoverShelf(): Boolean {
        return false
    }

    /**
     * =============================================================================================
     * Orientation detector
     * =============================================================================================
     */
    protected fun enableOrientationChangeEvent() {
        if (orientationChangeEvent?.canDetectOrientation() == true) {
            orientationChangeEvent?.enable()
        }
    }

    protected fun disableOrientationChangeEvent() {
        orientationChangeEvent?.disable()
    }

    /**
     * =============================================================================================
     * UI: Progress Dialog
     * =============================================================================================
     */
    open fun showProgressDialog() {
        activity?.let { activity ->
            if (!activity.isFinishing) {
                if (progressDialog?.isShowing == true) {
                    hideProgressDialog()
                }
                context?.let {
                    progressDialog = TrueIDProgressDialog(it).apply {
                        setCancelable(false)
                        show()
                    }
                }
            }
        }
    }

    protected fun showProgressDialog(msg: String) {
        activity?.let { activity ->
            if (!activity.isFinishing) {
                if (progressDialog?.isShowing == true) {
                    hideProgressDialog()
                }

                context?.let { context ->
                    progressDialog = TrueIDProgressDialog(context).apply {
                        setCancelable(false)
                    }
                }

                if (msg.isNotEmpty()) {
                    progressDialog?.show(msg)
                } else {
                    progressDialog?.show()
                }
            }
        }
    }

    open fun hideProgressDialog() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    /**
     * =============================================================================================
     * UI : Alert Fragment
     * =============================================================================================
     */
    protected fun showAlertDialog(title: String, message: String, negativeTitle: String) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setCancelable(false)
            .setMessage("")
            .setNegativeButton(negativeTitle) { dialog, _ -> dialog.dismiss() }
            .show()
        val messageTv = alertDialog.findViewById<TextView>(android.R.id.message)
        try {
            messageTv.text = message
            messageTv.gravity = Gravity.CENTER
        } catch (e: NullPointerException) {
            alertDialog.setMessage(message)
        }
        alertDialog.show()
    }

    protected fun showAlertDialog(
        title: String,
        message: String,
        positiveTitle: String,
        negativeTitle: String,
        listener: MIAlertDialogFragment.DialogListener
    ) {
        hideAlertDialog()
        fragmentManager?.let {
            alertDialog =
                MIAlertDialogFragment.newInstance(title, message, positiveTitle, negativeTitle)
                    .apply {
                        setDialogListener(listener)
                        show(it, MIAlertDialogFragment::class.java.canonicalName)
                    }
        }
    }

    protected fun showAlertDialog(
        title: String,
        message: String,
        positiveTitle: String,
        listener: MIAlertDialogFragment.DialogListener
    ) {
        hideAlertDialog()
        fragmentManager?.let {
            alertDialog = MIAlertDialogFragment.newInstance(title, message, positiveTitle)
                .apply {
                    setDialogListener(listener)
                    show(it, MIAlertDialogFragment::class.java.canonicalName)
                }
        }
    }

    protected fun hideAlertDialog() {
        alertDialog?.dismiss()
    }

    protected fun hideKeyboard(view: View?) {
        if (view != null) {
            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        }
    }

    protected fun restartApplication() {
        context?.let { context ->
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.let {
                val componentName = intent.component
                val mainIntent = Intent.makeRestartActivityTask(componentName)
                context.startActivity(mainIntent)
            }
        }
    }

    open fun backPressed() {
        activity?.onBackPressed()
    }

    fun backToHomeFragment() {
        if (activity is FoundationActivity) {
            for (i in 1..(activity as FoundationActivity).backStackEntryCount()) {
                backPressed()
            }
        }
    }

    /**
     * =============================================================================================
     * Listener
     * =============================================================================================
     */

    override fun onChangeToPortrait() {
        MIBusProvider.bus.post(SendRequestOrientationEvent(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT))
    }

    override fun onChangeToLandscape() {
        MIBusProvider.bus.post(SendRequestOrientationEvent(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE))
    }
}
