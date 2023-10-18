package com.truedigital.features.tuned.presentation.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.truedigital.component.base.ContextWrapper
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.connectivityManager
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl
import com.truedigital.features.tuned.injection.module.SharePreferenceModule
import com.truedigital.features.tuned.presentation.popups.view.LossOfNetworkDialog
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

/**
 *
 * Includes Localisation and Loss of network functions
 *
 */
open class TunedActivity : AppCompatActivity() {
    companion object {
        const val LOSS_OF_NETWORK_INTENT = "com.truedigital.features.tuned.LOSS_OF_NETWORK"
        private var activityStacks: MutableList<ActivityStack> = mutableListOf()
    }

    @field:[Inject Named(SharePreferenceModule.KVS_USER)]
    lateinit var obfuscatedKeyValueStoreInterface: Provider<ObfuscatedKeyValueStoreInterface>

    @Inject
    lateinit var localizationRepository: LocalizationRepository

    private var isReceiverRegistered = false
    private var uniqueId = System.currentTimeMillis()
    private var className: String? = null

    init {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
    }

    // for showing LossOfNetworkDialog during an API call
    private val lossOfNetworkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            showLossOfNetworkDialog()
        }
    }

    // for showing and hiding LossOfNetworkDialog as user's internet connection status changes
    private val connectivityChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val userExists = obfuscatedKeyValueStoreInterface.get()
                .contains(MusicUserRepositoryImpl.CURRENT_USER_KEY)
            val hasConnection =
                context.connectivityManager().activeNetworkInfo?.isConnected ?: false

            if (userExists) {
                if (hasConnection) {
                    hideLossOfNetworkDialog()
                } else {
                    showLossOfNetworkDialog()
                }
            }
        }
    }

    private var dialog: LossOfNetworkDialog? = null

    private fun showLossOfNetworkDialog() {
        dialog = dialog ?: LossOfNetworkDialog(this)
        if (dialog?.isShowing != true) dialog?.show()
    }

    private fun hideLossOfNetworkDialog() {
        dialog?.let {
            if (it.isShowing) it.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        registerNetworkReceiver()
        className = localClassName.split(".").last()
        className?.let {
            val stack = ActivityStack(uniqueId, it)
            activityStacks.add(stack)

            Timber.d("Current Stack + : ")
            activityStacks.forEach { Timber.d(it.toString()) }
        }
    }

    override fun onResume() {
        super.onResume()
        registerNetworkReceiver()
    }

    override fun onPause() {
        unRegisterNetworkReceiver()
        super.onPause()
    }

    override fun onDestroy() {
        unRegisterNetworkReceiver()
        className?.let { _className ->
            val stack = ActivityStack(uniqueId, _className)
            activityStacks.remove(stack)

            Timber.d("Current Stack - : ")
            activityStacks.forEach { Timber.d(it.toString()) }
        }
        super.onDestroy()
    }

    override fun attachBaseContext(newBase: Context) {
        val locale = localizationRepository.getAppLocale()
        val newContext = ContextWrapper.wrap(newBase, locale)
        super.attachBaseContext(newContext)
    }

    fun isNestedActivity(): Boolean {
        val startIndex = activityStacks.indexOfFirst { it.name.contains("LandingActivity") }
        return if (startIndex == -1) {
            false
        } else {
            activityStacks.size - startIndex > 2
        }
    }

    private fun registerNetworkReceiver() {
        if (!isReceiverRegistered) {
            registerReceiver(lossOfNetworkReceiver, IntentFilter(LOSS_OF_NETWORK_INTENT))
            registerReceiver(
                connectivityChangeReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            )
            isReceiverRegistered = true
        }
    }

    private fun unRegisterNetworkReceiver() {
        if (isReceiverRegistered) {
            unregisterReceiver(lossOfNetworkReceiver)
            unregisterReceiver(connectivityChangeReceiver)
            isReceiverRegistered = false
        }
    }
}
