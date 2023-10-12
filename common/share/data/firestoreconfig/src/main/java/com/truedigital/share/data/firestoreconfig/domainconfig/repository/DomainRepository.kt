package com.truedigital.share.data.firestoreconfig.domainconfig.repository

import com.newrelic.agent.android.NewRelic
import com.truedigital.core.extensions.addOnFailureListenerWithNewExecutor
import com.truedigital.core.extensions.addOnSuccessListenerWithNewExecutor
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.data.firestoreconfig.domainconfig.model.ApiServiceData
import javax.inject.Inject

interface DomainRepository {
    fun loadApiConfig(versionName: String, countryCode: String)
    fun getApiServiceData(serviceName: String): ApiServiceData?
}

class DomainRepositoryImpl @Inject constructor(
    val firestoreUtil: FirestoreUtil,
    val domainCacheRepository: DomainCacheRepository
) : DomainRepository {

    companion object {
        const val CONFIG_NODE_NAME_TRUEID = "trueid_app"
        const val CONFIG_NODE_NAME_DOMAIN = "domain"
        const val CONFIG_NODE_NAME_ANDROID = "android"
        const val SERVICE_KEY_NAME_LOGIN_URL = "loginurl"
        const val SERVICE_KEY_NAME_NON_LOGIN_URL = "nonloginurl"
        const val SERVICE_KEY_NAME_TOKENL = "token"
        const val SERVICE_KEY_NAME_USE_JWT = "usejwt"

        var serviceDataList = mutableListOf<ApiServiceData>()
        var isSaveSuccess = false
    }

    override fun loadApiConfig(
        versionName: String,
        countryCode: String
    ) {
        val localizedCollectionPath = FirestoreUtil.localizeCollectionPath(
            CONFIG_NODE_NAME_TRUEID, countryCode
        )
        firestoreUtil.getFirestore()
            .collection(localizedCollectionPath)
            .document(CONFIG_NODE_NAME_DOMAIN)
            .collection(versionName)
            .document(CONFIG_NODE_NAME_ANDROID)
            .get()
            .addOnFailureListenerWithNewExecutor {
                serviceDataList.clear()
            }
            .addOnSuccessListenerWithNewExecutor { response ->
                if (isSaveSuccess.not()) {
                    isSaveSuccess = true
                    serviceDataList.clear()
                    response.data?.forEach { map ->
                        val serviceData = map.value as Map<*, *>
                        val apiServiceData = ApiServiceData()
                        apiServiceData.serviceName = map.key
                        serviceData.forEach { data ->
                            when (data.key) {
                                SERVICE_KEY_NAME_LOGIN_URL ->
                                    apiServiceData.loginUrl = data.value as String?

                                SERVICE_KEY_NAME_NON_LOGIN_URL ->
                                    apiServiceData.nonLoginUrl = data.value as String?

                                SERVICE_KEY_NAME_TOKENL ->
                                    apiServiceData.apiToken = data.value as String?

                                SERVICE_KEY_NAME_USE_JWT ->
                                    apiServiceData.useJwt = data.value.toString() == "yes"
                            }
                        }

                        /**
                         * Try-catch this block due to error ArrayIndexOutOfBoundsException
                         * TODO: Need to find the better way to handle this error
                         * */
                        try {
                            serviceDataList.add(apiServiceData)
                        } catch (error: Exception) {
                            isSaveSuccess = false
                            val handlingExceptionMap = mapOf(
                                "Key" to "DomainRepository - serviceDataList.add(apiServiceData)",
                                "Value" to "Exception error with $error"
                            )
                            NewRelic.recordHandledException(error, handlingExceptionMap)
                        }
                    }

                    /**
                     * Try-catch this block due to error ArrayIndexOutOfBoundsException
                     * TODO: Need to find the better way to handle this error
                     * */
                    try {
                        if (isSaveSuccess) {
                            domainCacheRepository.saveCache(serviceDataList)
                        }
                    } catch (error: Exception) {
                        val handlingExceptionMap = mapOf(
                            "Key" to "DomainRepository - domainCacheRepository.saveCache(serviceDataList)",
                            "Value" to "Exception error with $error"
                        )
                        NewRelic.recordHandledException(error, handlingExceptionMap)
                    }
                }
            }
    }

    override fun getApiServiceData(serviceName: String): ApiServiceData? {
        var apiService = try {
            serviceDataList.find { it.serviceName == serviceName }
        } catch (error: ConcurrentModificationException) {
            val handlingExceptionMap = mapOf(
                "Key" to "DomainRepositoryImpl.getApiServiceData()",
                "Value" to "ConcurrentModificationException error with $error"
            )
            NewRelic.recordHandledException(error, handlingExceptionMap)
            null
        } catch (error: NullPointerException) {
            val handlingExceptionMap = mapOf(
                "Key" to "DomainRepositoryImpl.getApiServiceData()",
                "Value" to "NullPointerException error with $error"
            )
            NewRelic.recordHandledException(error, handlingExceptionMap)
            null
        } catch (error: Exception) {
            val handlingExceptionMap = mapOf(
                "Key" to "DomainRepositoryImpl.getApiServiceData()",
                "Value" to "Unexpected error with $error"
            )
            NewRelic.recordHandledException(error, handlingExceptionMap)
            null
        }
        if (apiService == null) {
            apiService = try {
                val serviceCache = domainCacheRepository.getCache()
                serviceCache?.find { it.serviceName == serviceName }
            } catch (error: NullPointerException) {
                val handlingExceptionMap = mapOf(
                    "Key" to "DomainRepositoryImpl.getApiServiceData() from domainCacheRepository",
                    "Value" to "NullPointerException error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
                null
            }
        }
        return apiService
    }
}
