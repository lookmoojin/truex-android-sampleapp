package com.truedigital.common.share.currentdate.usecase

import android.provider.Settings
import com.truedigital.common.share.currentdate.repository.DateTimeRepository
import com.truedigital.core.provider.ContextDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Step Get DateTime
 * 1. Get from "device system" require auto sync date time
 * 2. Get from "Google"
 * 3. Get from "Dmp"
 * 4. Get from "firebase"
 * 5. Get from "device system" don't care about sync
 * */
interface GetCurrentDateTimeUseCase {
    fun execute(isRequireLocalTime: Boolean = true): Flow<Long>
}

class GetCurrentDateTimeUseCaseImpl @Inject constructor(
    private val contextDataProvider: ContextDataProvider,
    private val dateTimeRepository: DateTimeRepository
) : GetCurrentDateTimeUseCase {

    private companion object {
        const val DEFAULT_FLAG_AUTO_TIME = 0
        const val MIN_TIME_STAMP = 0
        const val OPEN_FLAG_AUTO_TIME = 1
    }

    override fun execute(isRequireLocalTime: Boolean): Flow<Long> {
        return try {
            if (isOpenAutoDateTime()) {
                flowOf(dateTimeRepository.getLocalDateTime())
            } else {
                getGoogleDateTime(isRequireLocalTime)
            }
        } catch (exception: Exception) {
            error(exception)
        }
    }

    private fun getGoogleDateTime(isRequireLocalTime: Boolean): Flow<Long> {
        return dateTimeRepository.getGoogleDateTime().catch {
            emitAll(getDmpDateTime(isRequireLocalTime))
        }
    }

    private fun getDmpDateTime(isRequireLocalTime: Boolean): Flow<Long> {
        return dateTimeRepository.getServerDateTime().catch {
            emitAll(getFireBaseDateTime(isRequireLocalTime))
        }.flatMapMerge { ts ->
            if (ts < MIN_TIME_STAMP) {
                getFireBaseDateTime(isRequireLocalTime)
            } else {
                flowOf(ts)
            }
        }
    }

    private fun getFireBaseDateTime(isRequireLocalTime: Boolean): Flow<Long> {
        return dateTimeRepository.getFirebaseDateTime()
            .catch { exception ->
                if (isRequireLocalTime) {
                    emit(dateTimeRepository.getLocalDateTime())
                } else {
                    error(exception)
                }
            }
    }

    private fun isOpenAutoDateTime(): Boolean {
        return Settings.Global.getInt(
            contextDataProvider.getDataContext().contentResolver,
            Settings.Global.AUTO_TIME,
            DEFAULT_FLAG_AUTO_TIME
        ) == OPEN_FLAG_AUTO_TIME
    }
}
