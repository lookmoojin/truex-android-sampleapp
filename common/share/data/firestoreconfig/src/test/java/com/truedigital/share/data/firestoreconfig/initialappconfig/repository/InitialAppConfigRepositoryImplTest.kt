package com.truedigital.share.data.firestoreconfig.initialappconfig.repository

import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.share.data.firestoreconfig.FirestoreTest
import com.truedigital.share.maintenance.domain.constant.MaintenanceConstant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class InitialAppConfigRepositoryImplTest : FirestoreTest() {

    private lateinit var initialAppConfigRepository: InitialAppConfigRepository

    @BeforeEach
    fun setUp() {
        initialAppConfigRepository = InitialAppConfigRepositoryImpl(firestoreUtil)
        setUpFirestoreTest()
    }

    @Test
    fun `test get firestore success`() {
        getDataSuccess()
        val mapResponse = mapOf(
            "enable" to "true",
            "lotadata_tracking_mode" to "MinimalPower"
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(mapResponse)

        initialAppConfigRepository.loadInitialAppConfig(
            countryCode = "TH"
        )

        val responseA = initialAppConfigRepository.getConfigByKey("lotadata_tracking_mode")
        assertNotNull(responseA)
        assertEquals(responseA, "MinimalPower")

        val responseB = initialAppConfigRepository.getConfigByKey("ABC")
        assertNull(responseB)
    }

    @Test
    fun `test get firebase fail`() {
        getDataFail()

        initialAppConfigRepository.loadInitialAppConfig(
            countryCode = "TH"
        )

        val responseA = initialAppConfigRepository.getConfigByKey("lotadata_tracking_mode")
        assertNull(responseA)
    }

    @Nested
    inner class Maintenance {

        @Test
        fun `Get config by key maintenance`() {
            getDataSuccess()
            val mapResponse = mapOf(
                "maintenance" to mapOf(
                    "main" to mapOf(
                        "android" to mapOf(
                            "aButtonEn" to "Got it",
                            "aButtonTh" to "เข้าใจแล้ว",
                            "bButtonEn" to "",
                            "bButtonTh" to "",
                            "descriptionEn" to "\t System maintenance announcement : During Sep 9, 2021 from 1:00-1:10 a.m. and 3:00-3:10 a.m., you may not be able to watch live tv channels. Sorry for any inconvenience.",
                            "descriptionTh" to "แจ้งปรับปรุงระบบ ในวันที่ 9 ก.ย. 64 เวลา 1.00-1.10 น. และ 3:00-3:10 น. ในช่วงเวลาดังกล่าว คุณจะไม่สามารถรับชมช่องทีวีได้ทุกช่อง ขออภัยในความไม่สะดวกด้วยค่ะ",
                            "hideBtnClose" to "no",
                            "imageEn" to "https://cms.dmpcdn.com/misc/2020/08/04/47766790-d619-11ea-8433-c5d4d14f3a3c_original.png",
                            "imageTh" to "https://cms.dmpcdn.com/misc/2020/08/04/47766790-d619-11ea-8433-c5d4d14f3a3c_original.png",
                            "isShow" to "yes",
                            "time" to mapOf(
                                "endDate" to "2021-10-25 17:30:00",
                                "startDate" to "2021-10-25 17:30:00"
                            ),
                            "titleEn" to "True ID App Notice",
                            "titleTh" to "แอป TrueID มีเรื่องจะบอก"
                        )
                    )
                )
            )

            whenever(firestoreDocumentSnapshot.data).thenReturn(mapResponse)

            initialAppConfigRepository.run {
                loadInitialAppConfig(
                    countryCode = "TH"
                )

                assertNotNull((getConfigByKey(MaintenanceConstant.Key.MAINTENANCE_CONFIG_KEY) as? Map<*, *>))
            }
        }
    }
}
