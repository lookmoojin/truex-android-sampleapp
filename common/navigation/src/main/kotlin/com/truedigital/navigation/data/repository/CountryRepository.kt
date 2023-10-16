package com.truedigital.navigation.data.repository

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.truedigital.core.BuildConfig
import com.truedigital.core.constants.CoreConstants
import com.truedigital.core.extensions.environmentCase
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.navigations.share.data.api.InterCdnApiInterface
import com.truedigital.navigations.share.data.model.CountryResponseItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CountryRepository {
    fun getCountryList(): Flow<List<CountryResponseItem>>
}

class CountryRepositoryImpl @Inject constructor(
    private val api: InterCdnApiInterface,
    private val sharedPrefsUtils: SharedPrefsUtils
) : CountryRepository {

    companion object {
        const val COUNTRY_SAVE_DATA_KEY = "country-save-data-key"
    }

    private val cache = mutableListOf<CountryResponseItem>()

    private val rawCache
        get() = sharedPrefsUtils.get(COUNTRY_SAVE_DATA_KEY) as? String

    override fun getCountryList(): Flow<List<CountryResponseItem>> {
        // get data from in-memory cache
        return if (cache.isNotEmpty()) {
            flow {
                emit(cache)
            }
        } else {
            // call api if no data in in-memory cache
            callGetCountryListFlow()
        }
    }

    private fun callGetCountryListFlow(): Flow<List<CountryResponseItem>> {
        val environment = BuildConfig.ENVIRONMENT.environmentCase(
            staging = {
                CoreConstants.Environment.STAGING
            },
            preProd = {
                CoreConstants.Environment.PREPROD
            },
            prod = {
                CoreConstants.Environment.PROD
            }
        )
        val version = BuildConfig.API_COUNTRY_SCHEMA_VERSION

        return flow {
            val countryList = api.getCountry(
                environment = environment,
                version = version
            )
            updateCountryListCache(countryList)
            emit(countryList)
        }.catch { error ->
            // if call fail, get data from preference instead
            getCountryListFromPreference().let { list ->
                if (list.isNotEmpty()) {
                    emit(list)
                } else {
                    // no data in preference
                    throw error
                }
            }
        }
    }

    private fun getCountryListFromPreference(): List<CountryResponseItem> {
        val tokenType = object : TypeToken<List<CountryResponseItem>>() {}.type
        val response: List<CountryResponseItem>? = Gson().fromJson(rawCache, tokenType)
        return response ?: listOf()
    }

    private fun updateCountryListCache(list: List<CountryResponseItem>) {
        with(cache) {
            clear()
            addAll(list)
        }
        updateCountryListPreference(list)
    }

    private fun updateCountryListPreference(list: List<CountryResponseItem>) {
        sharedPrefsUtils.put(COUNTRY_SAVE_DATA_KEY, Gson().toJson(list))
    }
}
