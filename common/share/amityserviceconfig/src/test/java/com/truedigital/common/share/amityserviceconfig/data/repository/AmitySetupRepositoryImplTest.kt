package com.truedigital.common.share.amityserviceconfig.data.repository

import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupRepository
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.reactivex.Completable
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AmitySetupRepositoryImplTest {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var amitySetupRepository: AmitySetupRepository
    private var amitySocialUISettings: AmitySocialUISettings = mockk()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @BeforeEach
    fun setUp() {
        amitySetupRepository = AmitySetupRepositoryImpl(
            coroutineDispatcher = coroutineDispatcher,
            amitySocialUISettings = amitySocialUISettings
        )
    }

    @Test
    fun `when app set up amity api key by country`() {
        mockkObject(AmityCoreClient)
        mockkObject(AmitySocialUISettings)
        every {
            AmityCoreClient.setup(any())
        } returns Completable.complete()
        every {
            AmitySocialUISettings.setup(any(), any())
        } returns Completable.complete()

        val result = runCatching {
            amitySetupRepository.amitySetupApiKey(
                "amity_apikey"
            )
        }
        assertTrue(result.isSuccess)
    }
}
