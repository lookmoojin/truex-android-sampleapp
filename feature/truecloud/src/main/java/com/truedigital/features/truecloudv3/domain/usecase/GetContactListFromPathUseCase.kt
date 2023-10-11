package com.truedigital.features.truecloudv3.domain.usecase

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.inject.Inject

interface GetContactListFromPathUseCase {
    fun execute(path: String): Flow<List<ContactTrueCloudModel>>
}

class GetContactListFromPathUseCaseImpl @Inject constructor() : GetContactListFromPathUseCase {
    override fun execute(path: String): Flow<List<ContactTrueCloudModel>> {
        return flow {
            val file = File(path)
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            val rawData = stringBuilder.toString()
            val tokenType = object : TypeToken<MutableList<ContactTrueCloudModel>>() {}.type
            val response: MutableList<ContactTrueCloudModel> =
                Gson().fromJson(rawData, tokenType) ?: mutableListOf()
            emit(response)
        }
    }
}
