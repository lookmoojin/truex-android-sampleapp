package com.truedigital.common.share.componentv3.widget.truepoint.data

import com.truedigital.common.share.componentv3.widget.truepoint.data.model.TruePointConfigModel
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface TruePointWidgetConfigRepository {
    suspend fun getTruePointConfig(countryCode: String): TruePointConfigModel?
}

class TruePointWidgetConfigRepositoryImpl @Inject constructor(private val firestoreUtil: FirestoreUtil) :
    TruePointWidgetConfigRepository {

    companion object {
        const val NODE_NAME_TRUEID = "trueid_app"
        const val NODE_NAME_FEATURE_CONFIG = "feature_config"
        const val NODE_NAME_PRIVILEGE = "privilege"
        const val NODE_NAME_CONFIG = "config"
        const val FIELD_NAME_DISCOVER = "privilege_page"
        const val FIELD_NAME_TRUEPOINT = "true_card_true_point"
        const val FIELD_NAME_SHELF_TITLE = "shelf_title"
        const val FIELD_NAME_TITLE_TH = "shelf_title_th"
        const val FIELD_NAME_TITLE_EN = "shelf_title_en"
    }

    private var currentCountryCode: String = ""
    private var truePointConfigModel: TruePointConfigModel? = null

    override suspend fun getTruePointConfig(countryCode: String): TruePointConfigModel? {
        return if (currentCountryCode == countryCode && truePointConfigModel != null) {
            truePointConfigModel
        } else {
            loadConfig(countryCode).also { config ->
                currentCountryCode = countryCode
                truePointConfigModel = config
            }
        }
    }

    private suspend fun loadConfig(countryCode: String): TruePointConfigModel? {
        val result = runCatching {
            val localizedCollectionPath = FirestoreUtil.localizeCollectionPath(
                NODE_NAME_TRUEID, countryCode
            )
            val dataSnapshot = firestoreUtil.getFirestore()
                .collection(localizedCollectionPath)
                .document(NODE_NAME_FEATURE_CONFIG)
                .collection(NODE_NAME_PRIVILEGE)
                .document(NODE_NAME_CONFIG)
                .get()
                .await()

            val discover = dataSnapshot.data?.get(FIELD_NAME_DISCOVER) as Map<*, *>
            val truePointConfig = discover[FIELD_NAME_TRUEPOINT] as Map<*, *>
            val shelfTitle = truePointConfig[FIELD_NAME_SHELF_TITLE] as Map<*, *>
            return@runCatching TruePointConfigModel(
                titleTh = shelfTitle[FIELD_NAME_TITLE_TH] as String,
                titleEn = shelfTitle[FIELD_NAME_TITLE_EN] as String
            )
        }

        return result.getOrNull()
    }
}
