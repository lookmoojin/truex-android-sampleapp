package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tdg.truecloud.R
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.core.extensions.toDateFromUTC
import com.truedigital.features.truecloudv3.domain.model.MigrationModel
import com.truedigital.features.truecloudv3.domain.usecase.MigrateDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.PatchMigrateStatusUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MigrateDataViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val migrateDataUseCase: MigrateDataUseCase,
    private val patchMigrateStatusUseCase: PatchMigrateStatusUseCase,
    private val getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase,
    private val contextDataProviderWrapper: ContextDataProviderWrapper
) : ScopedViewModel() {
    val onMigrating = MutableLiveData<String>()
    val onMigrateCanceled = SingleLiveEvent<Unit>()
    val onMigrateCancelFailed = SingleLiveEvent<String>()

    companion object {
        private const val DEFAULT_HOUR_FORMAT = 1
        private const val DEFAULT_MINUTE = 1
    }

    fun callMigrate() {
        migrateDataUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach { _migrationModel ->
                onMigrating.value = getRemainingMsg(_migrationModel)
            }
            .launchSafeIn(viewModelScope)
    }

    fun callMigrateCancel() {
        patchMigrateStatusUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach {
                onMigrateCanceled.value = Unit
            }
            .catch {
                onMigrateCancelFailed.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_migration_cancel_failed)
            }
            .launchSafeIn(viewModelScope)
    }

    private suspend fun getRemainingMsg(migration: MigrationModel): String {
        if (migration.estimatedTimeToComplete == null) {
            return contextDataProviderWrapper.get().getResources()
                .getString(R.string.true_cloudv3_migration_time_remaining_na)
        }
        val timestamp = migration.estimatedTimeToComplete.toDateFromUTC().time
        val now = getCurrentDateTimeUseCase.execute().firstOrNull() ?: 0
        val remaining = (timestamp - now)
        val hour = TimeUnit.MILLISECONDS.toHours(remaining).toInt()
        return if (hour >= DEFAULT_HOUR_FORMAT) {
            contextDataProviderWrapper.get().getResources().getQuantityString(
                R.plurals.migrate_remaining_hour,
                hour,
                hour
            )
        } else {
            var minute = TimeUnit.MILLISECONDS.toMinutes(remaining).toInt()
            if (minute < DEFAULT_MINUTE) {
                minute = DEFAULT_MINUTE
            }
            contextDataProviderWrapper.get().getResources().getQuantityString(
                R.plurals.migrate_remaining_minute,
                minute,
                minute
            )
        }
    }
}
