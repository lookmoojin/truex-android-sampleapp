package com.truedigital.features.tuned.presentation.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
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
 * >> (Copy from TuneActivity)
 * Includes Localisation and Loss of network functions
 */
open class TunedFragment(layoutId: Int) : Fragment(layoutId) {
    companion object {
        const val LOSS_OF_NETWORK_INTENT = "com.truedigital.features.tuned.LOSS_OF_NETWORK"
        private var activityStacks: MutableList<ActivityStack> = mutableListOf()
    }

    @field:[Inject Named(SharePreferenceModule.KVS_USER)]
    lateinit var obfuscatedKeyValueStoreInterface: Provider<ObfuscatedKeyValueStoreInterface>

    private var isReceiverRegistered = false
    private var uniqueId = System.currentTimeMillis()
    private var className: String? = null
    private var lossOfNetworkDialog: LossOfNetworkDialog? = null

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

    private fun showLossOfNetworkDialog() {
        lossOfNetworkDialog = lossOfNetworkDialog ?: LossOfNetworkDialog(requireContext())
        lossOfNetworkDialog?.let { dialog ->
            if (!dialog.isShowing) dialog.show()
        }
    }

    private fun hideLossOfNetworkDialog() {
        lossOfNetworkDialog?.let { dialog ->
            if (dialog.isShowing) dialog.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        registerNetworkReceiver()
        className = activity?.localClassName?.split(".")?.last()
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

        className?.let {
            val stack = ActivityStack(uniqueId, it)
            activityStacks.remove(stack)

            Timber.d("Current Stack - : ")
            activityStacks.forEach { Timber.d(it.toString()) }
        }
        super.onDestroy()
    }

    private fun registerNetworkReceiver() {
        if (!isReceiverRegistered) {
            activity?.apply {
                registerReceiver(lossOfNetworkReceiver, IntentFilter(LOSS_OF_NETWORK_INTENT))
                registerReceiver(
                    connectivityChangeReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
            isReceiverRegistered = true
        }
    }

    private fun unRegisterNetworkReceiver() {
        if (isReceiverRegistered) {
            activity?.apply {
                unregisterReceiver(lossOfNetworkReceiver)
                unregisterReceiver(connectivityChangeReceiver)
            }
            isReceiverRegistered = false
        }
    }
}
