package com.truedigital.core.domain.usecase

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.domain.usecase.model.DataRocketModel
import com.truedigital.core.extensions.fromJson
import com.truedigital.core.utils.SharedPrefsUtils
import timber.log.Timber
import javax.inject.Inject

interface GetRocketUseCase {
    fun execute(): DataRocketModel
}

class GetRocketUseCaseImpl @Inject constructor(private val sharedPrefsUtils: SharedPrefsUtils) :
    GetRocketUseCase {
    override fun execute(): DataRocketModel {
        val jsonString = sharedPrefsUtils.get(FireBaseConstant.FIREBASE_ROCKET, "")
        return if (jsonString.isNotEmpty()) {
            try {
                Gson().fromJson<DataRocketModel>(jsonString)
            } catch (exception: JsonSyntaxException) {
                Timber.e(exception)
                DataRocketModel()
            }
        } else {
            DataRocketModel()
        }
    }
}
