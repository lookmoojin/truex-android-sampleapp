package com.truedigital.component.base.core

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.livefront.bridge.Bridge
import com.newrelic.agent.android.NewRelic.recordHandledException
import com.truedigital.component.base.ContextWrapper
import com.truedigital.component.dialog.MIAlertDialogFragment
import com.truedigital.component.dialog.TrueIDProgressDialog
import com.truedigital.component.injections.TIDComponent
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.foundation.view.scene.FoundationActivity
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class CoreActivity : FoundationActivity() {

    companion object {
        private lateinit var progressDialog: TrueIDProgressDialog
    }

    @Inject
    lateinit var localizationRepository: LocalizationRepository

    open fun isOpenSubscriptionWebview(): Boolean = false
    private var alertDialog: MIAlertDialogFragment? = null

    protected val compositeDisposable = CompositeDisposable()

    override fun attachBaseContext(newBase: Context) {
        TIDComponent.getInstance().inject(this)
        val locale = localizationRepository.getAppLocale()
        val newContext = ContextWrapper.wrap(newBase, locale)
        super.attachBaseContext(newContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        try {
            super.onCreate(savedInstanceState)
        } catch (e: RuntimeException) {
            val handlingExceptionMap = mapOf(
                "Key" to "CoreActivity",
                "Value" to "Problem when user deny permissions and launch app again with ${e.message}"
            )
            recordHandledException(Exception(e), handlingExceptionMap)
        }

        Bridge.restoreInstanceState(this, savedInstanceState)

        progressDialog = TrueIDProgressDialog(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        try {
            super.onSaveInstanceState(outState)
        } catch (e: RuntimeException) {
            val handlingExceptionMap = mapOf(
                "Key" to "CoreActivity",
                "Value" to "Problem when user deny permissions and launch app again with ${e.message}"
            )
            recordHandledException(Exception(e), handlingExceptionMap)
        }
        Bridge.saveInstanceState(this, outState)
    }

    override fun onDestroy() {
        hideProgressDialog()
        compositeDisposable.clear()
        try {
            super.onDestroy()
        } catch (e: RuntimeException) {
            val handlingExceptionMap = mapOf(
                "Key" to "CoreActivity",
                "Value" to "Problem when user deny permissions and launch app again with ${e.message}"
            )
            recordHandledException(Exception(e), handlingExceptionMap)
        }

        Bridge.clear(this)
    }

    fun addFragment(
        fragment: Fragment,
        isBackStack: Boolean = false,
        @IdRes container: Int = android.R.id.content
    ) {
        super.addFragment(container, fragment, isBackStack)
    }

    fun replaceFragment(
        fragment: Fragment,
        isBackStack: Boolean = false,
        @IdRes container: Int = android.R.id.content,
        tag: String? = null
    ) {
        super.replaceFragment(container, fragment, isBackStack, tag)
    }

    open fun showProgressDialog(msg: String, isCancelled: Boolean) {
        if (!isFinishing) {
            hideProgressDialog()
            progressDialog.setCancelable(isCancelled)
            progressDialog.show(msg)
        }
    }

    open fun hideProgressDialog() {
        if (!this.isFinishing && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    open fun showAlertDialog(
        title: String,
        message: String,
        positiveTitle: String,
        negativeTitle: String,
        listener: MIAlertDialogFragment.DialogListener
    ) {
        if (!isFinishing) {
            hideAlertDialog()
            alertDialog = if (negativeTitle.isEmpty()) {
                MIAlertDialogFragment.newInstance(title, message, positiveTitle)
            } else {
                MIAlertDialogFragment.newInstance(title, message, positiveTitle, negativeTitle)
            }.apply {
                setDialogListener(listener)
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(alertDialog!!, "MIAlertDialogFragment")
            transaction.commitAllowingStateLoss()
        }
    }

    open fun hideAlertDialog() {
        if (alertDialog != null) {
            alertDialog?.dismissAllowingStateLoss()
        }
    }

    open fun getNavController(): NavController? {
        return null
    }
}
