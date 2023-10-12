package com.truedigital.core.domain.usecase

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.domain.usecase.model.DataPinnedDomainsModel
import com.truedigital.core.extensions.fromJson
import com.truedigital.core.utils.SharedPrefsUtils
import timber.log.Timber
import javax.inject.Inject

interface GetPinnedDomainsUseCase {
    fun execute(): List<DataPinnedDomainsModel>
}

class GetPinnedDomainsUseCaseImpl @Inject constructor(private val sharedPrefsUtils: SharedPrefsUtils) :
    GetPinnedDomainsUseCase {
    override fun execute(): List<DataPinnedDomainsModel> {
        val jsonString = sharedPrefsUtils.get(FireBaseConstant.FIREBASE_PINNED_DOMAINS, "")
        return if (jsonString.isNotEmpty()) {
            try {
                Gson().fromJson<List<DataPinnedDomainsModel>>(jsonString)
            } catch (exception: JsonSyntaxException) {
                Timber.e(exception)
                listOf<DataPinnedDomainsModel>()
            }
        } else {
            listOf<DataPinnedDomainsModel>()
        }
    }
}
