package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.core.constant.SharedPrefsKeyConstant.PERMISSION_EXTERNAL_STORAGE_DISABLE
import com.truedigital.core.constant.SharedPrefsKeyConstant.PERMISSION_READ_CONTACTS_DISABLE
import com.truedigital.core.utils.SharedPrefsInterface
import com.truedigital.core.utils.get
import com.truedigital.core.utils.put
import javax.inject.Inject

interface PermissionDisableRepository {
    var isDisableExternalStorage: Boolean
    var isDisableReadContact: Boolean
}

class PermissionDisableRepositoryImpl @Inject constructor(
    private val sharedPrefsInterface: SharedPrefsInterface
) : PermissionDisableRepository {

    override var isDisableExternalStorage: Boolean
        get() = sharedPrefsInterface.get(PERMISSION_EXTERNAL_STORAGE_DISABLE, false)
        set(value) {
            sharedPrefsInterface.put(PERMISSION_EXTERNAL_STORAGE_DISABLE, value)
        }
    override var isDisableReadContact: Boolean
        get() = sharedPrefsInterface.get(PERMISSION_READ_CONTACTS_DISABLE, false)
        set(value) {
            sharedPrefsInterface.put(PERMISSION_READ_CONTACTS_DISABLE, value)
        }
}
