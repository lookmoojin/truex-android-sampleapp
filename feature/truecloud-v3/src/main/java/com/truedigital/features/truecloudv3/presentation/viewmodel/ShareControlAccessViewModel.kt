package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.truedigital.features.truecloudv3.R
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.constant.DateFormatConstant
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.core.extensions.toDateFromUTC
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.usecase.GetShareConfigUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetShareLinkUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateShareConfigUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ShareControlAccessViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val getShareLinkUseCase: GetShareLinkUseCase,
    private val getShareConfigUseCase: GetShareConfigUseCase,
    private val updateShareConfigUseCase: UpdateShareConfigUseCase,
    private val contextDataProviderWrapper: ContextDataProviderWrapper
) : ScopedViewModel() {
    private lateinit var trueCloudFilesModel: TrueCloudFilesModel.File

    companion object {
        private const val MAXIMUM_CHARACTER = 24
        private const val MINIMUM_PASSWORD_LENGTH = 6
    }

    private var isPrivate: Boolean? = null
    private var oldPassword: String? = null
    private var expireAt: String? = null
    private var expireAtEnable: Boolean = false
    private var passwordEnable: Boolean = false
    private var isNewPass: Boolean = false

    val onShowSnackbarComplete = SingleLiveEvent<String>()
    val onShowSnackbarError = SingleLiveEvent<String>()
    val onUpdateExpirationView = SingleLiveEvent<Boolean>()
    val onShowDatePickerView = SingleLiveEvent<Boolean>()
    val onExpireAtSwitchOffView = SingleLiveEvent<Boolean>()
    val onShowUpdateButton = SingleLiveEvent<Boolean>()
    val onShowShareButton = SingleLiveEvent<Boolean>()
    val onShowPasswordView = SingleLiveEvent<Boolean>()
    val openShareIntent = SingleLiveEvent<String>()
    val onUpdateExpireTime = SingleLiveEvent<String>()
    val onUpdatePassword = SingleLiveEvent<String>()
    val onUpdatePasswordCount = SingleLiveEvent<String>()
    val onEditPassword = SingleLiveEvent<Unit>()
    val onUpdateView = SingleLiveEvent<Triple<Boolean?, String?, String?>>()

    fun setFileModel(trueCloudFilesModel: TrueCloudFilesModel.File) {
        this.trueCloudFilesModel = trueCloudFilesModel
        getShareConfig()
    }

    fun getShareLink() {
        getShareLinkUseCase.execute(trueCloudFilesModel.id.orEmpty())
            .flowOn(coroutineDispatcher.io())
            .onEach { _shareUrl ->
                openShareIntent.value = _shareUrl
            }
            .launchSafeIn(this)
    }

    fun switchIsPrivate() {
        isPrivate = isPrivate != true
        onShowUpdateButton.value = true
    }

    fun switchExpiration() {
        expireAtEnable = !expireAtEnable
        if (expireAtEnable) {
            onShowDatePickerView.value = true
        } else {
            expireAt = null
            onUpdateExpirationView.value = expireAtEnable
        }
        onShowUpdateButton.value = true
    }

    fun switchSetPassword() {
        passwordEnable = !passwordEnable
        onShowPasswordView.value = passwordEnable
        oldPassword = ""
        isNewPass = true
        onShowUpdateButton.value = true
    }

    fun clickEditPassword() {
        isNewPass = true
        oldPassword = ""
        onEditPassword.value = Unit
    }

    fun passwordChanged(newPassword: String) {
        var characterCount = newPassword.length
        if (characterCount > MAXIMUM_CHARACTER) {
            val limitedText = newPassword.substring(0, MAXIMUM_CHARACTER)
            characterCount = limitedText.length
            onUpdatePassword.value = limitedText
        }
        onUpdatePasswordCount.value = contextDataProviderWrapper.get()
            .getDataContext()
            .getString(R.string.true_cloudv3_d24, characterCount)
    }

    fun updateExpireTime(date: Date) {
        expireAt =
            SimpleDateFormat(
                DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z,
                Locale.getDefault()
            ).format(date)
        val selectedDateTime =
            SimpleDateFormat(DateFormatConstant.dd_MM_yyyy_SLASH, Locale.getDefault()).format(
                date
            )
        onUpdateExpireTime.value = selectedDateTime
        onUpdateExpirationView.value = true
    }

    fun onCancelDatePickerDialog() {
        if (expireAt.isNullOrEmpty()) {
            onExpireAtSwitchOffView.value = true
            expireAtEnable = false
        }
    }

    fun updateConfig(password: String) {
        var finalPassword = password
        if (password.isEmpty()) {
            finalPassword = oldPassword.orEmpty()
        }
        if (passwordEnable && finalPassword.length < MINIMUM_PASSWORD_LENGTH) {
            onShowSnackbarError.value = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_password_must_have_6_24_characters)
        } else {
            updateShareConfigUseCase.execute(
                trueCloudFilesModel.id.orEmpty(),
                isPrivate = isPrivate ?: false,
                password = finalPassword.ifEmpty { null },
                expireAt = expireAt?.ifEmpty { null },
                isNewPass = isNewPass
            )
                .flowOn(coroutineDispatcher.io())
                .onEach { _shareConfigModel ->
                    onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_update_successfully)
                    oldPassword = _shareConfigModel.sharedFile.password
                    isPrivate = _shareConfigModel.sharedFile.isPrivate
                    expireAt = _shareConfigModel.sharedFile.expireAt
                    isNewPass = false
                    var expiredTime = ""
                    if (expireAt?.isNotEmpty() == true) {
                        expiredTime = SimpleDateFormat(
                            DateFormatConstant.dd_MM_yyyy_SLASH,
                            Locale.getDefault()
                        ).format(
                            expireAt.toDateFromUTC()
                        )
                    }
                    expireAtEnable = expireAt?.isNotEmpty() == true
                    passwordEnable = oldPassword?.isNotEmpty() == true
                    onUpdateView.value = Triple(isPrivate, oldPassword, expiredTime)
                    onShowShareButton.value = true
                }
                .catch {
                    onShowSnackbarError.value = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_update_failed)
                }
                .launchSafeIn(this)
        }
    }

    private fun getShareConfig() {
        getShareConfigUseCase.execute(trueCloudFilesModel.id.orEmpty())
            .flowOn(coroutineDispatcher.io())
            .onEach { _shareConfigModel ->
                isPrivate = _shareConfigModel.sharedFile.isPrivate
                oldPassword = _shareConfigModel.sharedFile.password
                expireAt = _shareConfigModel.sharedFile.expireAt
                var expiredTime = ""
                if (expireAt?.isNotEmpty() == true) {
                    expiredTime = SimpleDateFormat(
                        DateFormatConstant.dd_MM_yyyy_SLASH,
                        Locale.getDefault()
                    ).format(
                        expireAt.toDateFromUTC()
                    )
                }
                expireAtEnable = !expireAt.isNullOrEmpty()
                onUpdateView.value = Triple(isPrivate, oldPassword, expiredTime)
                passwordEnable = oldPassword?.isNotEmpty() == true
            }
            .launchSafeIn(this)
    }
}
