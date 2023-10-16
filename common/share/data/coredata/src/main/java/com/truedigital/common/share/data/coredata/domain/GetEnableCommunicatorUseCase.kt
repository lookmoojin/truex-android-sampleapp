package com.truedigital.common.share.data.coredata.domain

import com.google.gson.Gson
import com.truedigital.common.share.data.coredata.data.model.CommunicatorInitialAppModel
import com.truedigital.common.share.data.coredata.domain.model.CommunicatorInitialAppEnableConfigModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.addOnFailureListenerWithNewExecutor
import com.truedigital.core.extensions.addOnSuccessListenerWithNewExecutor
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepositoryImpl.Companion.CONFIG_NODE_NAME_INITIAL_APP
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepositoryImpl.Companion.CONFIG_NODE_NAME_TRUEID
import io.reactivex.Single
import javax.inject.Inject

interface GetEnableCommunicatorUseCase {
    fun execute(): Single<CommunicatorInitialAppEnableConfigModel>
}

class GetEnableCommunicatorUseCaseImpl @Inject constructor(
    private val fireStoreUtil: FirestoreUtil,
    private val localizationRepository: LocalizationRepository
) : GetEnableCommunicatorUseCase {

    override fun execute(): Single<CommunicatorInitialAppEnableConfigModel> {
        val localizedCollectionPath = FirestoreUtil.localizeCollectionPath(
            CONFIG_NODE_NAME_TRUEID, localizationRepository.getAppCountryCode()
        )
        return Single.create { emitter ->
            fireStoreUtil.getFirestore()
                .collection(localizedCollectionPath)
                .document(CONFIG_NODE_NAME_INITIAL_APP)
                .get()
                .addOnFailureListenerWithNewExecutor {
                    emitter.onSuccess(
                        CommunicatorInitialAppEnableConfigModel(false)
                    )
                }
                .addOnSuccessListenerWithNewExecutor { response ->
                    Gson().run {
                        val model: CommunicatorInitialAppModel? = fromJson(
                            toJson(response.data),
                            CommunicatorInitialAppModel::class.java
                        )
                        model?.communicator?.enable.let { enableModel ->
                            val isEnable = enableModel?.android == true
                            val errorMsg1 =
                                enableModel?.run { getLocalizeMessage(errorMsgEn1, errorMsgLocal1) }
                            val errorMsg2 =
                                enableModel?.run { getLocalizeMessage(errorMsgEn2, errorMsgLocal2) }
                            val errorMsg3 =
                                enableModel?.run { getLocalizeMessage(errorMsgEn3, errorMsgLocal3) }
                            emitter.onSuccess(
                                CommunicatorInitialAppEnableConfigModel(
                                    isEnable = isEnable,
                                    image = enableModel?.image ?: "",
                                    errorMessage1 = errorMsg1 ?: "",
                                    errorMessage2 = errorMsg2 ?: "",
                                    errorMessage3 = errorMsg3 ?: ""
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun getLocalizeMessage(messageEn: String, messageLocal: String): String {
        return when (localizationRepository.getAppLanguageCode()) {
            LocalizationRepository.Localization.EN.languageCode -> {
                messageEn
            }
            LocalizationRepository.Localization.MY.languageCode -> {
                messageEn
            }
            else -> {
                messageLocal
            }
        }
    }
}
