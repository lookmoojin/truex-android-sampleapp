package com.truedigital.features.music.data.forceloginbanner.repository

import androidx.annotation.VisibleForTesting
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.toObject
import com.truedigital.features.music.data.forceloginbanner.model.AdsBannerPlayerModel
import com.truedigital.features.music.data.forceloginbanner.model.LoginBannerItemConfigModel
import com.truedigital.features.music.data.forceloginbanner.model.MusicConfigModel
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface MusicConfigRepository {
    fun getAdsBannerPlayerConfig(): Flow<AdsBannerPlayerModel?>
    fun getLoginBannerConfig(): Flow<LoginBannerItemConfigModel?>
    fun getRootShelfConfig(): Flow<String>
    fun clearMusicConfigModel()
    fun setMusicConfigModel(musicConfig: MusicConfigModel)
}

class MusicConfigRepositoryImpl @Inject constructor(
    private val firebaseUtil: FirestoreUtil,
    private val localizationRepository: LocalizationRepository
) : MusicConfigRepository {

    companion object {
        private const val NODE_NAME_TRUEID_APP = "trueid_app"
        private const val NODE_NAME_FEATURE_CONFIG = "feature_config"
        private const val NODE_NAME_MUSIC = "music"
        private const val NODE_NAME_CONFIG = "config"

        private var musicConfigModel: MusicConfigModel? = null
    }

    override fun getAdsBannerPlayerConfig(): Flow<AdsBannerPlayerModel?> {
        return musicConfigModel?.let {
            flowOf(it.adsBannerPlayer)
        } ?: loadConfig().map {
            it?.adsBannerPlayer
        }
    }

    override fun getLoginBannerConfig(): Flow<LoginBannerItemConfigModel?> {
        return musicConfigModel?.let {
            flowOf(it.loginBannerItemConfigModel)
        } ?: loadConfig().map {
            it?.loginBannerItemConfigModel
        }
    }

    override fun getRootShelfConfig(): Flow<String> {
        return musicConfigModel?.let {
            flowOf(it.rootShelf.android)
        } ?: loadConfig().map {
            it?.rootShelf?.android.orEmpty()
        }
    }

    private fun loadConfig(): Flow<MusicConfigModel?> {
        val localizedCollectionPath = FirestoreUtil.localizeCollectionPath(
            NODE_NAME_TRUEID_APP, localizationRepository.getAppCountryCode()
        )
        return flow {
            val dataSnapshot = firebaseUtil.getFirestore()
                .collection(localizedCollectionPath)
                .document(NODE_NAME_FEATURE_CONFIG)
                .collection(NODE_NAME_MUSIC)
                .document(NODE_NAME_CONFIG)
                .get()
                .await()
            runCatching {
                dataSnapshot.toObject<MusicConfigModel>()
            }.onSuccess {
                musicConfigModel = it
                emit(it)
            }.onFailure {
                emit(null)
            }
        }
    }

    @VisibleForTesting
    override fun clearMusicConfigModel() {
        musicConfigModel = null
    }

    @VisibleForTesting
    override fun setMusicConfigModel(musicConfig: MusicConfigModel) {
        musicConfigModel = musicConfig
    }
}
