package com.truedigital.core.domain.usecase

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.domain.usecase.model.DataAnimalModel
import com.truedigital.core.extensions.fromJson
import com.truedigital.core.utils.SharedPrefsUtils
import timber.log.Timber
import javax.inject.Inject

interface GetAnimalUseCase {
    fun execute(): DataAnimalModel
}

class GetAnimalUseCaseImpl @Inject constructor(private val sharedPrefsUtils: SharedPrefsUtils) :
    GetAnimalUseCase {
    override fun execute(): DataAnimalModel {
        val jsonString = sharedPrefsUtils.get(FireBaseConstant.FIREBASE_ANIMALS, "")
        return if (jsonString.isNotEmpty()) {
            try {
                Gson().fromJson<DataAnimalModel>(jsonString)
            } catch (exception: JsonSyntaxException) {
                Timber.e(exception)
                DataAnimalModel()
            }
        } else {
            DataAnimalModel()
        }
    }
}
