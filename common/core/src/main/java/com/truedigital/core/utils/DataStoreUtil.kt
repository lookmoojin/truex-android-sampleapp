package com.truedigital.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.newrelic.agent.android.NewRelic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val USER_DATASTORE = "datastore"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

class DataStoreUtil @Inject constructor(val context: Context) : DataStoreInterface {

    override suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        context.dataStore.data.catch { exception ->
            val handlingExceptionMap = mapOf(
                "Key" to "DataStoreException",
                "Value" to "${exception.message}"
            )
            NewRelic.recordHandledException(
                Exception(exception.localizedMessage),
                handlingExceptionMap
            )

            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val result = preferences[key] ?: defaultValue
            result
        }

    override suspend fun <T> getSinglePreference(key: Preferences.Key<T>, defaultValue: T): T {
        val data = context.dataStore.data.firstOrNull()
        return data?.get(key) ?: defaultValue
    }

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun <T> removePreference(key: Preferences.Key<T>) {
        context.dataStore.edit {
            it.remove(key)
        }
    }

    override suspend fun clearAllPreference() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

interface DataStoreInterface {
    suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T>
    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T)
    suspend fun <T> removePreference(key: Preferences.Key<T>)
    suspend fun <T> getSinglePreference(key: Preferences.Key<T>, defaultValue: T): T
    suspend fun clearAllPreference()
}
