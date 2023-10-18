package com.truedigital.common.share.componentv3.widget.badge.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.CountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetInboxEnableUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetNewCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetTotalUnseenUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.SaveNewCountInboxUseCase
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.foundation.extension.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CountInboxViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val saveNewCountInboxUseCase: SaveNewCountInboxUseCase,
    private val getNewCountInboxUseCase: GetNewCountInboxUseCase,
    private val getInboxEnableUseCase: GetInboxEnableUseCase,
    private val getTotalUnseenUseCase: GetTotalUnseenUseCase,
    private val userRepository: UserRepository,
    countInboxUseCase: CountInboxUseCase
) : ScopedViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val countInbox: MutableLiveData<Unit> = countInboxUseCase.execute()
    private val onGetCountInboxSuccess = MutableLiveData<Int>()
    private val showInboxMessage = MutableLiveData<Boolean>()
    private val showInboxMessageNumber = MutableLiveData<String>()

    companion object {
        private const val DEFAULT_MAXIMUM_COUNT_VIEW = "99+"
    }

    fun showInboxMessageNumber(): LiveData<String> = showInboxMessageNumber

    fun onGetCountInboxSuccess(): LiveData<Int> = onGetCountInboxSuccess

    fun showInboxMessage(): LiveData<Boolean> = showInboxMessage

    fun triggerCountInbox(): LiveData<Unit> = countInbox

    fun checkShowInboxMessageNotCallService() {
        getInboxEnableUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { enableInbox ->
                    if (enableInbox) {
                        val countInbox = getNewCountInboxUseCase.execute()
                        showInboxMessage.value =
                            checkUserLogin(countInbox > 0)
                        showInboxMessageNumber.value = setShowMaximumCountView(countInbox)
                    } else {
                        showInboxMessage.value = false
                    }
                },
                {
                    showInboxMessage.value = checkUserLogin(false)
                    showInboxMessageNumber.value = ""
                }
            )
            .addTo(composite = compositeDisposable)
    }

    fun checkShowInboxMessage() {
        if (userRepository.getSsoId().isEmpty()) {
            return
        }

        getInboxEnableUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { enableInbox ->
                if (enableInbox) {
                    getTotalUnseenUseCase
                        .execute(false)
                        .flowOn(coroutineDispatcher.io())
                        .onEach { countInbox ->
                            saveNewCountInboxUseCase.execute(count = countInbox)
                            showInboxMessage.value = checkUserLogin(countInbox > 0)
                            showInboxMessageNumber.value =
                                setShowMaximumCountView(countInbox)
                        }
                        .catch {
                            showInboxMessage.value = checkUserLogin(false)
                            showInboxMessageNumber.value = ""
                        }
                        .launchSafeIn(this)
                } else {
                    showInboxMessage.value = false
                }
            }
            .addTo(composite = compositeDisposable)
    }

    fun loadCountInbox() {
        getTotalUnseenUseCase
            .execute(true)
            .flowOn(coroutineDispatcher.io())
            .onEach { countInbox ->
                saveNewCountInboxUseCase.execute(count = countInbox)
                onGetCountInboxSuccess.value = countInbox
            }
            .catch {
                onGetCountInboxSuccess.value = 0
            }
            .launchSafeIn(this)
    }

    private fun checkUserLogin(condition: Boolean): Boolean {
        return if (userRepository.getSsoId().isNotEmpty()) {
            condition
        } else {
            false
        }
    }

    private fun setShowMaximumCountView(
        amount: Int,
    ): String {
        return if (amount > 99) {
            DEFAULT_MAXIMUM_COUNT_VIEW
        } else {
            amount.toString()
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
