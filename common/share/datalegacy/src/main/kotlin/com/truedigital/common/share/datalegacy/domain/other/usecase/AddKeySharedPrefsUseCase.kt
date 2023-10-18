package com.truedigital.common.share.datalegacy.domain.other.usecase

import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

interface AddKeySharedPrefsUseCase {
    fun execute(addKey: String, value: String)
}

class AddKeySharedPrefsUseCaseImpl @Inject constructor(private val sharedPrefs: SharedPrefsUtils) :
    AddKeySharedPrefsUseCase {
    override fun execute(addKey: String, value: String) {
        sharedPrefs.put(addKey, value)
    }
}
