package com.truedigital.features.truecloudv3.data.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.utils.DataStoreInterface
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.GetContactResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3ContactData
import com.truedigital.features.truecloudv3.provider.ContactProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ContactRepository {
    fun getContact(): Flow<MutableList<TrueCloudV3ContactData>>
    fun getContactFromNetwork(): Flow<GetContactResponse>
    suspend fun getUpdateAt(): String
    suspend fun setUpdateAt(updateAt: String)
    suspend fun hasContactSynced(): Boolean
    suspend fun setContactSynced(statusSync: Boolean)
}

class ContactRepositoryImpl @Inject constructor(
    private val contactProvider: ContactProvider,
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository,
    private val dataStoreInterface: DataStoreInterface
) : ContactRepository {

    companion object {
        private const val KEY_TURE_CLOUD_V3_UPDATE_AT = "KEY_TURE_CLOUD_V3_UPDATE_AT"
        private const val KEY_TURE_CLOUD_V3_SYNCED_STATUS = "KEY_TURE_CLOUD_V3_SYNCED_STATUS"
        private const val EMPTY_STRING = ""
    }

    override fun getContact(): Flow<MutableList<TrueCloudV3ContactData>> {
        return flow {
            emit(contactProvider.getContacts())
        }
    }

    override fun getContactFromNetwork(): Flow<GetContactResponse> {
        return flow {
            trueCloudV3Interface.getContact(
                ssoid = userRepository.getSsoId()
            )
                .run {
                    val responseBody = body()
                    if (isSuccessful && responseBody != null) {
                        emit(responseBody)
                    } else {
                        error(code())
                    }
                }
        }
    }

    override suspend fun getUpdateAt(): String {
        return dataStoreInterface.getSinglePreference(
            stringPreferencesKey(
                KEY_TURE_CLOUD_V3_UPDATE_AT
            ),
            EMPTY_STRING
        )
    }

    override suspend fun setUpdateAt(updateAt: String) {
        return dataStoreInterface.putPreference(
            stringPreferencesKey(KEY_TURE_CLOUD_V3_UPDATE_AT),
            updateAt
        )
    }

    override suspend fun hasContactSynced(): Boolean {
        return dataStoreInterface.getSinglePreference(
            booleanPreferencesKey(
                KEY_TURE_CLOUD_V3_SYNCED_STATUS
            ),
            false
        )
    }

    override suspend fun setContactSynced(statusSync: Boolean) {
        dataStoreInterface.putPreference(booleanPreferencesKey(KEY_TURE_CLOUD_V3_SYNCED_STATUS), statusSync)
    }
}
