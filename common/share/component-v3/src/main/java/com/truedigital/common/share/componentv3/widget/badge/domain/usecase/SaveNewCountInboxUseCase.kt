package com.truedigital.common.share.componentv3.widget.badge.domain.usecase

import com.truedigital.component.constant.configuration.Constant
import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

interface SaveNewCountInboxUseCase {
    fun execute(count: Int)
}

class SaveNewCountInboxUseCaseImpl @Inject constructor(private val sharedPrefs: SharedPrefsUtils) :
    SaveNewCountInboxUseCase {

    override fun execute(count: Int) {
        sharedPrefs.put(Constant.FEATURE_NEW_COUNT_INBOX_MESSAGE, count)
    }
}
