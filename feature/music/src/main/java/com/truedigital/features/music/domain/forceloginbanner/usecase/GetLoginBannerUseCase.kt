package com.truedigital.features.music.domain.forceloginbanner.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.music.data.forceloginbanner.model.LoginBannerItemConfigModel
import com.truedigital.features.music.data.forceloginbanner.repository.MusicConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetLoginBannerUseCase {
    fun execute(): Flow<String>
}

class GetLoginBannerUseCaseImpl @Inject constructor(
    private val musicConfigRepository: MusicConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetLoginBannerUseCase {

    companion object {
        private const val LANG_TH = "th"
        private const val LANG_EN = "en"
    }

    override fun execute(): Flow<String> = flow {
        getLoginBanner().map {
            mapToCurrentLang(it)
        }.collectSafe {
            emit(it)
        }
    }

    private fun getLoginBanner(): Flow<LoginBannerItemConfigModel?> {
        return musicConfigRepository.getLoginBannerConfig()
    }

    private fun mapToCurrentLang(loginBannerItemConfigModel: LoginBannerItemConfigModel?): String {
        return when (localizationRepository.getAppLanguageCodeForEnTh()) {
            LANG_TH -> loginBannerItemConfigModel?.imageTH ?: ""
            LANG_EN -> loginBannerItemConfigModel?.imageEN ?: ""
            else -> ""
        }
    }
}
