package com.truedigital.share.data.firestoreconfig.domainconfig.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetDomainServiceApiDataUseCaseImplTest {

    private var deviceRepository: DeviceRepository = mock()
    private var domainRepository: DomainRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    private lateinit var getDomainServiceApiDataUseCase: GetDomainServiceApiDataUseCase

    @BeforeEach
    fun setUp() {
        getDomainServiceApiDataUseCase = GetDomainServiceApiDataUseCaseImpl(
            deviceRepository = deviceRepository,
            domainRepository = domainRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun `check method was called with 3 digit appVersion`() {
        val appVersion = "3.0.0"
        doReturn(appVersion).whenever(deviceRepository).getAppVersion()

        getDomainServiceApiDataUseCase.execute()

        verify(deviceRepository, times(1)).getAppVersion()
        verify(domainRepository, times(1)).loadApiConfig(
            versionName = "3.0",
            countryCode = localizationRepository.getAppCountryCode()
        )
    }

    @Test
    fun `check method was called with 2 digit appVersion`() {
        val appVersion = "3.0"
        doReturn(appVersion).whenever(deviceRepository).getAppVersion()

        getDomainServiceApiDataUseCase.execute()

        verify(deviceRepository, times(1)).getAppVersion()
        verify(domainRepository, times(1)).loadApiConfig(
            versionName = "3.0",
            countryCode = localizationRepository.getAppCountryCode()
        )
    }

    @Test
    fun `check method was called with 1 digit appVersion`() {
        val appVersion = "3"
        doReturn(appVersion).whenever(deviceRepository).getAppVersion()

        getDomainServiceApiDataUseCase.execute()

        verify(deviceRepository, times(1)).getAppVersion()
        verify(domainRepository, times(1)).loadApiConfig(
            versionName = "",
            countryCode = localizationRepository.getAppCountryCode()
        )
    }
}
