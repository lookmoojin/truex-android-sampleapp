package com.truedigital.features.truecloudv3.data.repository

import androidx.datastore.preferences.core.longPreferencesKey
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.utils.DataStoreInterface
import com.truedigital.features.truecloudv3.common.MigrationStatus
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_MIGRATION_MIGRATE
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_MIGRATION_UPDATE
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.MigrateResponse
import com.truedigital.features.truecloudv3.data.model.MigrationStatusRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

interface MigrateDataRepository {
    fun migrate(): Flow<MigrateResponse>
    fun patchStatus(): Flow<MigrateResponse>
    suspend fun addFailDisplayTime(time: Long)
    suspend fun getFailDisplayTime(): Long
}

class MigrateDataRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository,
    private val dataStoreInterface: DataStoreInterface
) : MigrateDataRepository {

    companion object {
        private const val KEY_TURE_CLOUD_V3_FAIL_DISPLAY_TIME = "KEY_TURE_CLOUD_V3_FAIL_DISPLAY_TIME"
        private const val KEY_STATUS = "status"
        private const val DEFAULT_FAIL_TIME = 0L
    }

    override fun migrate(): Flow<MigrateResponse> {
        return flow {
            trueCloudV3Interface.migrateData(
                ssoid = userRepository.getSsoId(),
                obj = JSONObject()
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_MIGRATION_MIGRATE)
                }
            }
        }
    }

    override fun patchStatus(): Flow<MigrateResponse> {
        return flow {
            val objStatus = JSONObject()
            objStatus.put(KEY_STATUS, MigrationStatus.PENDING.key)
            val migrateStatus = MigrationStatusRequest(MigrationStatus.PENDING.key)
            trueCloudV3Interface.updateMigrationStatus(
                ssoid = userRepository.getSsoId(),
                obj = migrateStatus
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_MIGRATION_UPDATE)
                }
            }
        }
    }

    override suspend fun addFailDisplayTime(time: Long) {
        dataStoreInterface.putPreference(longPreferencesKey(KEY_TURE_CLOUD_V3_FAIL_DISPLAY_TIME), time)
    }

    override suspend fun getFailDisplayTime(): Long {
        return dataStoreInterface.getSinglePreference(
            longPreferencesKey(
                KEY_TURE_CLOUD_V3_FAIL_DISPLAY_TIME
            ),
            DEFAULT_FAIL_TIME
        )
    }
}
