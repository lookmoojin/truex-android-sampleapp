package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfRequestModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallParametersModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallShelfItemModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase.ConvertWeMallResponseUseCase
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase.GetWeMallShelfContentUseCase
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.core.extensions.launchSafeIn
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import java.util.Locale
import javax.inject.Inject

class WeMallShelfWidgetViewModel @Inject constructor(
    private val convertWeMallResponseUseCase: ConvertWeMallResponseUseCase,
    private val getWeMallShelfContentUseCase: GetWeMallShelfContentUseCase,
    private val deviceRepository: DeviceRepository,
    private val localizationRepository: LocalizationRepository,
    private val userRepository: UserRepository,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val analyticManager: AnalyticManager
) : ScopedViewModel() {
    private var parametersModel: WeMallParametersModel? = null
    val weMallShelfLayout =
        MutableLiveData<WeMallParametersModel?>()
    val weMallItem =
        MutableLiveData<WeMallShelfItemModel>()
    val responseItem =
        MutableLiveData<WeMallShelfResponseModel>()

    private var isCalled = false

    fun getWeMallShelfLayout(
        component: String
    ) {
        if (!isCalled) {
            isCalled = true
            val requestModel = getWeMallRequestModel(component)
            val token = userRepository.getAccessToken()
            getWeMallShelfContentUseCase.execute(token, requestModel)
                .flowOn(coroutineDispatcher.io())
                .onEach { response ->
                    response?.let { _data ->
                        parametersModel?.let { _ ->
                            responseItem.value = _data
                        }
                    }
                }
                .launchSafeIn(this)
        }
    }

    fun transformData(_data: WeMallShelfResponseModel) {
        weMallShelfLayout.value = parametersModel
        convertWeMallResponseUseCase.execute(
            content = _data,
            parametersModel = parametersModel
        )
            .flowOn(coroutineDispatcher.io())
            .onEach { shelfItem ->
                weMallItem.value = shelfItem
            }
            .launchSafeIn(viewModelScope)
    }

    private fun getWeMallRequestModel(component: String): WeMallShelfRequestModel {
        if (component.isNotEmpty()) {
            parametersModel = Gson().fromJson(component, WeMallParametersModel::class.java)
            return WeMallShelfRequestModel(
                deviceID = deviceRepository.getAndroidId().ifEmpty { "null" },
                ssoID = userRepository.getSsoId().ifEmpty { "nonlogin" },
                lang = localizationRepository.getAppLanguageCodeForEnTh()
                    .toLowerCase(Locale.ENGLISH),
                categoryName = parametersModel?.setting?.category.orEmpty(),
                limit = parametersModel?.setting?.limit ?: "0"
            )
        }
        return WeMallShelfRequestModel()
    }

    fun trackFirebaseEvent(event: HashMap<String, Any>) {
        analyticManager.trackEvent(event)
    }

    override fun onCleared() {
        super.onCleared()
        cancelJob()
    }

    fun cancelJob() {
        coroutineContext.cancelChildren()
        coroutineContext.cancel()
    }
}
