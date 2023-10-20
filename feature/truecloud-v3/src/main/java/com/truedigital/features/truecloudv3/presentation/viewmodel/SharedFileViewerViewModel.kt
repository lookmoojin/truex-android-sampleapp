package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.truedigital.features.truecloudv3.R
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_ACCESSTOKEN
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SHARED_FILE
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.SharedFileModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetPrivateSharedFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetPublicSharedFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetSharedFileAccessTokenUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageSpaceUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateTaskUploadStatusUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileUseCase
import com.truedigital.features.truecloudv3.navigation.SharedFileToBottomSheet
import com.truedigital.features.truecloudv3.navigation.SharedFileViewerToMain
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3SharedFileViewerRouterUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.jetbrains.annotations.VisibleForTesting
import timber.log.Timber
import javax.inject.Inject

class SharedFileViewerViewModel @Inject constructor(
    private val uploadFileUseCase: UploadFileUseCase,
    private val trueCloudLoginManagerInterface: LoginManagerInterface,
    private val getStorageSpaceUseCase: GetStorageSpaceUseCase,
    private val getPrivateSharedFileUseCase: GetPrivateSharedFileUseCase,
    private val getPublicSharedFileUseCase: GetPublicSharedFileUseCase,
    private val getSharedFileAccessTokenUseCase: GetSharedFileAccessTokenUseCase,
    private val router: TrueCloudV3SharedFileViewerRouterUseCase,
    private val setRouterToNavControllerUseCase: SetRouterToNavControllerUseCase,
    private val completeUploadUseCase: CompleteUploadUseCase,
    private val removeTaskUseCase: RemoveTaskUseCase,
    private val updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val contextDataProviderWrapper: ContextDataProviderWrapper
) : ScopedViewModel() {

    var sharedObjId: String = ""

    companion object {
        const val ACTION_GET_SHARED_FILE = "GET_SHARED_FILE"
        const val ACTION_GET_STORAGE = "ACTION_GET_STORAGE"
        const val UNAUTHORIZED_STATUS_CODE = 401
        const val FORBIDDEN_STATUS_CODE = 403
        const val OK_STATUS_CODE = 200
        const val ERROR_CODE = 500
    }

    val onShowSnackBarError = SingleLiveEvent<String>()
    val onBackPressed = SingleLiveEvent<Boolean>()
    val sharedObject = MutableLiveData<SharedFileModel?>()
    val sharedObjectAccessToken = MutableLiveData<String?>(null)
    val onLoadDataError = SingleLiveEvent<Pair<String, String>>()
    val onUploadError = MutableLiveData<String>()
    val onShowSnackbarComplete = SingleLiveEvent<String>()
    val onRequirePassword = SingleLiveEvent<Unit>()
    val onLinkExpired = SingleLiveEvent<Unit>()
    val onSuccess = SingleLiveEvent<SharedFileModel?>()
    val linkStatus = SingleLiveEvent<Int?>()
    val onSaveFileToDevice = SingleLiveEvent<SharedFileModel?>()
    val onSaveFileToCloud = SingleLiveEvent<SharedFileModel?>()

    @VisibleForTesting
    var rootFolderId: String? = null

    fun onClickBack() {
        onBackPressed.value = true
    }

    fun onClickMore() {
        router.execute(
            SharedFileToBottomSheet,
            Bundle().apply {
                putString(KEY_BUNDLE_TRUE_CLOUD_SHARED_FILE, sharedObjId)
                putString(KEY_BUNDLE_TRUE_CLOUD_FILE_ACCESSTOKEN, sharedObjectAccessToken.value)
            }
        )
    }

    fun setRouterToNavController(navController: NavController) {
        setRouterToNavControllerUseCase.execute(navController)
    }

    fun checkResponseStatus(response: SharedFileModel): Int {
        when (response.status) {
            UNAUTHORIZED_STATUS_CODE -> {
                onRequirePassword.value = Unit
                return UNAUTHORIZED_STATUS_CODE
            }

            FORBIDDEN_STATUS_CODE -> {
                onLinkExpired.value = Unit
                return FORBIDDEN_STATUS_CODE
            }

            OK_STATUS_CODE -> {
                sharedObject.value = response
                onSuccess.value = response
                return OK_STATUS_CODE
            }

            else -> {
                onLoadDataError.value = Pair(
                    response.statusMessage.orEmpty(),
                    ACTION_GET_SHARED_FILE
                )
                return ERROR_CODE
            }
        }
    }

    fun submitPassword(inputPassword: String?) {
        val password = inputPassword?.trim()
        if (password.isNullOrEmpty()) {
            onShowSnackBarError.value = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_view_private_file_wrong_password)
            return
        }
        getSharedFileAccessTokenUseCase.execute(sharedObjId, password)
            .flowOn(coroutineDispatcher.io())
            .onEach { accessToken ->
                if (accessToken == null) {
                    onShowSnackBarError.value = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_view_private_file_wrong_password)
                } else {
                    sharedObjectAccessToken.value = accessToken
                }
            }.catch {
                sharedObjectAccessToken.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_view_private_file_wrong_password)
            }
            .launchSafeIn(this)
    }

    fun loadSharedObjectData(accessToken: String) {
        getPrivateSharedFileUseCase.execute(sharedObjId, accessToken)
            .flowOn(coroutineDispatcher.io())
            .onEach { sharedFile ->
                checkResponseStatus(sharedFile)
                linkStatus.value = sharedFile.status
            }
            .launchIn(this)
    }

    fun validateObjectAccess() {
        getPublicSharedFileUseCase.execute(sharedObjId)
            .flowOn(coroutineDispatcher.io())
            .onEach { sharedFile ->
                checkResponseStatus(sharedFile)
                linkStatus.value = sharedFile.status
            }
            .launchIn(this)
    }

    fun setSharedObjectId(id: String) {
        sharedObjId = id
    }

    fun checkRetryState(action: String) {
        if (action == ACTION_GET_STORAGE) {
            validateObjectAccess()
        }
    }

    fun checkAuthenticationState() {
        when {
            trueCloudLoginManagerInterface.isLoggedIn() -> {
                getStorage()
            }

            else -> {
                openAuthenticationPage()
            }
        }
    }

    fun getStorage() {
        getStorageSpaceUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                rootFolderId = result.rootFolder?.id.orEmpty()
            }
            .launchSafeIn(this)
    }

    fun completeUpload(id: Int) {
        completeUploadUseCase.execute(id)
            ?.flowOn(coroutineDispatcher.io())
            ?.onEach {
                removeTaskUseCase.execute(id)
                onShowSnackbarComplete.value =
                    contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_save_shared_to_my_cloud_success)
            }?.launchSafeIn(this)
    }

    fun uploadFile(uri: Uri, uploadType: TaskActionType = TaskActionType.UPLOAD) {
        uploadFileUseCase.execute(listOf(uri), rootFolderId.orEmpty(), uploadType).map {
            when (it.getState()) {
                TrueCloudV3TransferState.COMPLETED -> {
                    completeUpload(it.getId())
                }

                TrueCloudV3TransferState.FAILED -> {
                    updateTaskUploadStatusUseCase.execute(
                        it.getId(),
                        TaskStatusType.FAILED
                    )
                }

                else -> {
                    // Do nothing
                }
            }
            it.setTransferListener(transferlistener)
        }.launchSafeIn(this)
    }

    @VisibleForTesting
    val transferlistener = object : TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
        override fun onStateChanged(id: Int, state: TrueCloudV3TransferState?) {
            launchSafe {
                when (state) {
                    TrueCloudV3TransferState.COMPLETED -> {
                        completeUpload(id)
                    }

                    TrueCloudV3TransferState.CANCELED -> {
                        removeTaskUseCase.execute(id)
                    }

                    TrueCloudV3TransferState.WAITING,
                    TrueCloudV3TransferState.WAITING_FOR_NETWORK,
                    TrueCloudV3TransferState.RESUMED_WAITING -> {
                        updateTaskUploadStatusUseCase.execute(
                            id,
                            TaskStatusType.WAITING
                        )
                    }

                    TrueCloudV3TransferState.PAUSED -> {
                        updateTaskUploadStatusUseCase.execute(
                            id,
                            TaskStatusType.PAUSE
                        )
                    }

                    TrueCloudV3TransferState.FAILED -> {
                        updateTaskUploadStatusUseCase.execute(
                            id,
                            TaskStatusType.FAILED
                        )
                    }

                    else -> {
                        updateTaskUploadStatusUseCase.execute(
                            id,
                            TaskStatusType.UNKNOWN
                        )
                    }
                }
            }
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            Timber.i("id : $id, onProgressChanged bytesCurrent : $bytesCurrent , bytesTotal : $bytesTotal")
        }

        override fun onError(id: Int, ex: Exception?) {
            launchSafe {
                updateTaskUploadStatusUseCase.execute(id, TaskStatusType.FAILED)
            }
        }
    }

    fun navigateBackToMain() {
        router.execute(SharedFileViewerToMain)
    }

    fun saveFileToCloud(objectPair: Pair<String?, String?>) {
        val sharedObject = objectPair.first
        val sharedObjectAccessToken = objectPair.second
        checkAuthenticationState()
        sharedObject?.let { id ->
            if (sharedObjectAccessToken == null) {
                getPublicSharedFileUseCase.execute(id)
                    .flowOn(coroutineDispatcher.io())
                    .onEach { sharedFile ->
                        onSaveFileToCloud.value = sharedFile
                    }.launchIn(this)
            } else {
                getPrivateSharedFileUseCase.execute(id, sharedObjectAccessToken)
                    .flowOn(coroutineDispatcher.io())
                    .onEach { sharedFile ->
                        onSaveFileToCloud.value = sharedFile
                    }.launchIn(this)
            }
        }
    }

    fun saveFileToDevice(objectPair: Pair<String?, String?>) {
        val sharedObject = objectPair.first
        val sharedObjectAccessToken = objectPair.second
        sharedObject?.let { id ->
            if (sharedObjectAccessToken == null) {
                getPublicSharedFileUseCase.execute(id)
                    .flowOn(coroutineDispatcher.io())
                    .onEach { sharedFile ->
                        onSaveFileToDevice.value = sharedFile
                    }.launchIn(this)
            } else {
                getPrivateSharedFileUseCase.execute(id, sharedObjectAccessToken)
                    .flowOn(coroutineDispatcher.io())
                    .onEach { sharedFile ->
                        onSaveFileToDevice.value = sharedFile
                    }.launchIn(this)
            }
        }
    }

    private fun openAuthenticationPage() {
        trueCloudLoginManagerInterface.login(
            object : AuthLoginListener() {
                override fun onLoginSuccess() {
                    getStorage()
                }
            },
            true
        )
    }
}
