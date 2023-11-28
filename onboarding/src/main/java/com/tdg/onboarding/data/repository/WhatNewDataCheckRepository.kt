package com.tdg.onboarding.data.repository

import com.truedigital.core.utils.SharedPrefsInterface
import com.truedigital.core.utils.get
import com.truedigital.core.utils.put
import javax.inject.Inject

interface WhatsNewDataCheckRepository {
    fun saveTimeStamp(timestamp: String)
    fun getTimeStamp(): String
}

class WhatsNewDataCheckRepositoryImpl @Inject constructor(
    private val sharedPrefs: SharedPrefsInterface
) : WhatsNewDataCheckRepository {

    companion object {
        const val KEY_SAVE_TIMESTAMP = "whats_new_timestamp"
    }

    override fun saveTimeStamp(timestamp: String) {
        sharedPrefs.put(KEY_SAVE_TIMESTAMP, timestamp)
    }

    override fun getTimeStamp(): String {
        return sharedPrefs.get(KEY_SAVE_TIMESTAMP, "")
    }
}
