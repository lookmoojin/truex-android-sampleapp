package com.truedigital.common.share.componentv3.widget.badge.domain.usecase

import com.truedigital.component.constant.configuration.Constant
import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

interface GetNewCountInboxUseCase {
    fun execute(): Int
}

class GetNewCountInboxUseCaseImpl @Inject constructor(private val sharedPrefs: SharedPrefsUtils) :
    GetNewCountInboxUseCase {

    override fun execute(): Int {
        return sharedPrefs.get(Constant.FEATURE_NEW_COUNT_INBOX_MESSAGE, 0)
    }
}
