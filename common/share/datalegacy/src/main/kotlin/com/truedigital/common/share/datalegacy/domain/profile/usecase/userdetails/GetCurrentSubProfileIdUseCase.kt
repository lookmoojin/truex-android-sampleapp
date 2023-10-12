package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.truedigital.common.share.datalegacy.data.repository.profile.model.accounts.AccountGetAllProfilesBodyDataResponse
import com.truedigital.core.utils.GsonUtil
import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

const val KEY_SUB_PROFILE_DATA = "KEY_SUB_PROFILE_DATA"

interface GetCurrentSubProfileUseCase {
    fun execute(): AccountGetAllProfilesBodyDataResponse?
}

class GetCurrentSubProfileUseCaseImpl @Inject constructor(
    private val sharedPrefs: SharedPrefsUtils
) :
    GetCurrentSubProfileUseCase {

    override fun execute(): AccountGetAllProfilesBodyDataResponse? {
        return try {
            GsonUtil.newInstance().getValue<AccountGetAllProfilesBodyDataResponse>(
                sharedPrefs.get<String>(KEY_SUB_PROFILE_DATA).orEmpty(),
                AccountGetAllProfilesBodyDataResponse::class.java
            )
        } catch (_: Exception) {
            null
        }
    }
}
