package com.truedigital.share.data.firestoreconfig.domainconfig.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.share.data.firestoreconfig.FirestoreTest
import com.truedigital.share.data.firestoreconfig.domainconfig.model.ApiServiceData
import io.mockk.every
import io.mockk.mockkObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class DomainRepositoryImplTest : FirestoreTest() {

    private lateinit var domainRepository: DomainRepository
    private val domainCacheRepository: DomainCacheRepository = mock()

    @BeforeEach
    fun setUp() {
        domainRepository = DomainRepositoryImpl(firestoreUtil, domainCacheRepository)
        setUpFirestoreTest()
    }

    @Test
    fun `test get firestore success`() {
        getDataSuccess()
        val childMapA = mapOf(
            "loginurl" to "loginurlA",
            "nonloginurl" to "nonloginurlA",
            "token" to "tokenA",
            "usejwt" to "yes",
            "ABC" to "abc"
        )
        val childMapB = mapOf(
            "loginurl" to "loginurlB",
            "nonloginurl" to "nonloginurlB",
            "token" to "tokenB",
            "usejwt" to "no"
        )
        val mapResponse = mapOf("serviceA" to childMapA, "serviceB" to childMapB)
        whenever(firestoreDocumentSnapshot.data).thenReturn(mapResponse)

        domainRepository.loadApiConfig(
            versionName = "3.0",
            countryCode = "TH"
        )

        verify(firestoreUtil, times(1)).getFirestore()
        verify(domainCacheRepository, times(1)).saveCache(any())

        val serviceA = domainRepository.getApiServiceData("serviceA")
        assertNotNull(serviceA)
        assertEquals(serviceA.nonLoginUrl, "nonloginurlA")

        verify(domainCacheRepository, times(0)).getCache()

        val serviceB = domainRepository.getApiServiceData("serviceB")
        assertNotNull(serviceB)
        assertEquals(serviceB.nonLoginUrl, "nonloginurlB")
        assertEquals(serviceB.useJwt, false)

        verify(domainCacheRepository, times(0)).getCache()

        val serviceC = domainRepository.getApiServiceData("serviceC")
        assertNull(serviceC)

        verify(domainCacheRepository, times(1)).getCache()
    }

    @Test
    fun `test get firestore success but data null`() {
        getDataSuccess()
        whenever(firestoreDocumentSnapshot.data).thenReturn(null)

        domainRepository.loadApiConfig(
            versionName = "3.0",
            countryCode = "TH"
        )

        val serviceA = domainRepository.getApiServiceData("serviceAB")
        assertNull(serviceA)

        verify(domainCacheRepository, times(1)).getCache()
    }

    @Test
    fun `test get firestore fail`() {
        getDataFail()

        domainRepository.loadApiConfig(
            versionName = "3.0",
            countryCode = "TH"
        )

        val serviceA = domainRepository.getApiServiceData("serviceA")
        assertNull(serviceA)

        verify(domainCacheRepository, times(1)).getCache()
    }

    @Test
    fun `test first get success but second get fail then get data from cache`() {
        getDataSuccess()
        val childMap = mapOf(
            "loginurl" to "loginurlA",
            "nonloginurl" to "nonloginurlA",
            "token" to "tokenA",
            "usejwt" to "yes"
        )
        val mapResponse = mapOf("serviceA" to childMap)
        whenever(firestoreDocumentSnapshot.data).thenReturn(mapResponse)

        val dataList = mutableListOf(
            ApiServiceData().apply {
                serviceName = "serviceA"
                loginUrl = "loginurlA"
                nonLoginUrl = "nonloginurlA"
                apiToken = "tokenA"
                useJwt = true
            }
        )
        whenever(domainCacheRepository.getCache()).thenReturn(dataList)

        domainRepository.loadApiConfig(
            versionName = "3.0",
            countryCode = "TH"
        )

        val serviceA = domainRepository.getApiServiceData("serviceA")
        assertNotNull(serviceA)
        assertEquals(serviceA.nonLoginUrl, "nonloginurlA")

        verify(domainCacheRepository, times(0)).getCache()

        getDataFail()

        domainRepository.loadApiConfig(
            versionName = "3.0",
            countryCode = "TH"
        )

        val serviceA2 = domainRepository.getApiServiceData("serviceA")
        assertNotNull(serviceA2)
        assertEquals(serviceA2.nonLoginUrl, "nonloginurlA")

        verify(domainCacheRepository, times(1)).getCache()
    }

    @Test
    fun `test getApiServiceData error Exception`() {
        getDataSuccess()
        val childMap = mapOf(
            "loginurl" to "loginurlA",
            "nonloginurl" to "nonloginurlA",
            "token" to "tokenA",
            "usejwt" to "yes"
        )
        val mapResponse = mapOf("serviceA" to childMap)
        whenever(firestoreDocumentSnapshot.data).thenReturn(mapResponse)

        mockkObject(DomainRepositoryImpl.serviceDataList)
        every { DomainRepositoryImpl.serviceDataList.find { any() } } throws ConcurrentModificationException()

        val dataList = mutableListOf(
            ApiServiceData().apply {
                serviceName = "serviceA"
                loginUrl = "loginurlA"
                nonLoginUrl = "nonloginurlA"
                apiToken = "tokenA"
                useJwt = true
            }
        )
        whenever(domainCacheRepository.getCache()).thenReturn(dataList)

        domainRepository.loadApiConfig(
            versionName = "3.0",
            countryCode = "TH"
        )

        val serviceA = domainRepository.getApiServiceData("serviceA")
        assertNotNull(serviceA)
        assertEquals(serviceA.nonLoginUrl, "nonloginurlA")

        verify(domainCacheRepository, times(1)).getCache()
    }

    @Test
    fun `test getEmptyDataFromCache`() {
        whenever(domainCacheRepository.getCache()).thenReturn(listOf())

        val serviceA = domainRepository.getApiServiceData("serviceA")
        assertNull(serviceA)
    }
}
