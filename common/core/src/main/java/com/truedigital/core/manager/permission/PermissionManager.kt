package com.truedigital.core.manager.permission

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.truedigital.foundation.extension.addTo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.disposables.CompositeDisposable

interface PermissionManager {

    interface PermissionAction {
        fun checkPermission(permission: String, listener: PermissionAskListener?)
        fun checkPermission(permissions: Array<String>): Boolean
        fun checkPermissionConditions(permissions: Array<String>): ArrayList<String>
        fun requestPermission(permission: String, listener: PermissionAskListener?)
        fun requestPermission(permissions: Array<String>, listener: PermissionAskListener?)
        fun openApplicationPermissionSetting()
        fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean
    }

    interface PermissionAskListener {
        /*
        * Callback on permission "Never show again" checked and denied
        * */
        fun onPermissionDisabled()

        /*
        * Callback on permission granted
        * */
        fun onPermissionGranted()

        /*
        * Callback on permission denied
        * */
        fun onPermissionDenied()
    }
}

class PermissionManagerImpl @AssistedInject constructor(
    @Assisted private val fragmentActivity: FragmentActivity
) : PermissionManager.PermissionAction {

    private val activity = fragmentActivity
    private val rxPermissions = RxPermissions(fragmentActivity)
    private val compositeDisposable = CompositeDisposable()

    @AssistedFactory
    interface PermissionManagerFactory {
        fun create(fragmentActivity: FragmentActivity): PermissionManagerImpl
    }

    override fun requestPermission(
        permission: String,
        listener: PermissionManager.PermissionAskListener?
    ) {
        rxPermissions.request(permission)
            .subscribe(
                { granted ->
                    if (granted) {
                        listener?.onPermissionGranted()
                    } else {
                        checkPermission(permission = permission, listener = listener)
                    }
                },
                {}
            )
            .addTo(composite = compositeDisposable)
    }

    override fun requestPermission(
        permissions: Array<String>,
        listener: PermissionManager.PermissionAskListener?
    ) {
        rxPermissions.request(*permissions)
            .subscribe(
                { granted ->
                    if (granted) {
                        listener?.onPermissionGranted()
                    } else {
                        if (shouldShowRequestPermissionRationale(permissions)) {
                            listener?.onPermissionDenied()
                        } else {
                            listener?.onPermissionDisabled()
                        }
                    }
                },
                {}
            )
            .addTo(composite = compositeDisposable)
    }

    override fun checkPermission(
        permission: String,
        listener: PermissionManager.PermissionAskListener?
    ) {
        if (shouldAskPermission(permission)) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                /**
                 * If user checked Never Ask me again
                 */
                listener?.onPermissionDisabled()
            } else {
                listener?.onPermissionDenied()
            }
        } else {
            listener?.onPermissionGranted()
        }
    }

    override fun checkPermissionConditions(permissions: Array<String>): ArrayList<String> {
        if (shouldAskPermission(permissions)) {
            return checkIfThereIsRationaleToShow(permissions)
        }
        return arrayListOf()
    }

    private fun checkIfThereIsRationaleToShow(permissions: Array<String>): ArrayList<String> {
        val showList = ArrayList<String>()
        permissions.forEach { permission ->
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showList.add(permission)
            }
        }
        return showList
    }

    override fun checkPermission(permissions: Array<String>): Boolean {
        return shouldAskPermission(permissions)
    }

    override fun openApplicationPermissionSetting() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    override fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return false
            }
        }
        return true
    }

    private fun shouldAskPermission(permission: String): Boolean {
        val permissionResult = ActivityCompat.checkSelfPermission(activity, permission)
        return permissionResult != PackageManager.PERMISSION_GRANTED
    }

    private fun shouldAskPermission(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            val permissionResult = ActivityCompat.checkSelfPermission(activity, permission)
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }
}
