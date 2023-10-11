package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.data.repository.PermissionDisableRepository
import com.truedigital.features.truecloudv3.domain.model.DetailDialogModel
import com.truedigital.features.truecloudv3.domain.usecase.NodePermission
import com.truedigital.features.truecloudv3.domain.usecase.TrueCloudV3ConfigPermissionImageUseCase
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class IntroPermissionViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val permissionDisableRepository: PermissionDisableRepository,
    private val configPermissionImageUseCase: TrueCloudV3ConfigPermissionImageUseCase
) : ScopedViewModel() {

    val onRequestPermissionList = MutableLiveData<Pair<Array<String>, DetailDialogModel>>()
    val onShowSettingDialog = MutableLiveData<DetailDialogModel>()
    val onClosePage = MutableLiveData<Unit>()
    val onSetMediaType = MutableLiveData<Unit>()
    val onShowButtonAndImage = MutableLiveData<Pair<String, String>>()

    private var storePermission: Array<String>? = null
    private var storeDetailDialogModel: DetailDialogModel? = null

    fun setStorePermissionList(listPermission: Array<String>) {
        storePermission = listPermission
    }

    fun setStoreDetailDialogModel(detailDialogModel: DetailDialogModel) {
        storeDetailDialogModel = detailDialogModel
    }

    fun getStorePermissionList() {
        storeDetailDialogModel?.let { detailDialogModel ->
            storePermission?.let { storePerm ->
                onRequestPermissionList.value = Pair(
                    first = storePerm,
                    second = detailDialogModel
                )
            }
        }
    }

    fun checkFirstDisablePermission(detailDialogModel: DetailDialogModel) {
        when (detailDialogModel.nodePermission) {
            NodePermission.STORAGE -> {
                if (permissionDisableRepository.isDisableExternalStorage) {
                    onShowSettingDialog.value = detailDialogModel
                } else {
                    permissionDisableRepository.isDisableExternalStorage = true
                    onClosePage.value = Unit
                }
            }
            NodePermission.CONTACT -> {
                if (permissionDisableRepository.isDisableReadContact) {
                    onShowSettingDialog.value = detailDialogModel
                } else {
                    permissionDisableRepository.isDisableReadContact = true
                    onClosePage.value = Unit
                }
            }
            else -> {
                // DO NOTHING
            }
        }
    }

    fun onPermissionDenied() {
        onClosePage.value = Unit
    }

    fun onClickLater() {
        onClosePage.value = Unit
    }

    fun onPermissionGrantedResult() {
        onSetMediaType.value = Unit
        onClosePage.value = Unit
    }

    fun getPermissionImage() {
        storeDetailDialogModel?.let { model ->
            configPermissionImageUseCase.execute(model.nodePermission)
                .flowOn(coroutineDispatcher.io())
                .onEach { (textButton, imageUrl) ->
                    onShowButtonAndImage.value = Pair(textButton, imageUrl)
                }
                .launchSafeIn(this)
        }
    }
}
