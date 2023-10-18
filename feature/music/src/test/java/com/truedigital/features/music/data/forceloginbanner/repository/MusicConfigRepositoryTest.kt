package com.truedigital.features.music.data.forceloginbanner.repository

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.forceloginbanner.model.AdsBannerPlayerModel
import com.truedigital.features.music.data.forceloginbanner.model.LoginBannerItemConfigModel
import com.truedigital.features.music.data.forceloginbanner.model.MusicConfigModel
import com.truedigital.features.music.data.forceloginbanner.model.MusicConfigValue
import com.truedigital.features.music.data.forceloginbanner.model.MusicEnableModel
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.stubbing.Answer
import java.util.concurrent.Executor
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicConfigRepositoryTest {

    private lateinit var musicConfigRepository: MusicConfigRepository
    private val localizationRepository: LocalizationRepository = mock()
    private val firestoreUtil: FirestoreUtil = mock()
    private val firestore: FirebaseFirestore = mock()
    private val firestoreCollectionReference: CollectionReference = mock()
    private val firestoreDocumentReference: DocumentReference = mock()
    private val firestoreDocumentSnapshot: DocumentSnapshot = mock()

    private val mockLoginBannerItemConfigModel = LoginBannerItemConfigModel(
        imageTH = "imageTH",
        imageEN = "imageEN"
    )
    private val mockMusicRootShelf = MusicConfigValue(
        android = "BjP4b8qrp8Bj"
    )
    private val mockAdsBannerPlayerModel = AdsBannerPlayerModel(
        enable = MusicEnableModel(),
        urlAds = "urlAds"
    )
    private val mockMusicConfigModel = MusicConfigModel(
        loginBannerItemConfigModel = mockLoginBannerItemConfigModel,
        rootShelf = mockMusicRootShelf,
        adsBannerPlayer = mockAdsBannerPlayerModel
    )

    @BeforeEach
    fun setup() {
        musicConfigRepository = MusicConfigRepositoryImpl(firestoreUtil, localizationRepository)
        whenever(firestoreUtil.getFirestore()).thenReturn(firestore)
        whenever(firestore.collection(any())).thenReturn(firestoreCollectionReference)
        whenever(firestoreCollectionReference.document(any())).thenReturn(firestoreDocumentReference)
        whenever(firestoreDocumentReference.collection(any())).thenReturn(
            firestoreCollectionReference
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("TH")
    }

    @Test
    fun getAdsBannerPlayerConfig_noCache_musicConfigModelIsNotNull_returnDataFromFireStore() =
        runTest {
            // Given
            getDataSuccess()
            val enableMap = mapOf(
                "android" to true
            )
            val childMap = mapOf(
                "enable" to enableMap,
                "url_ads" to "url"
            )
            val configMap = mapOf(
                "ads_banner_player" to childMap
            )
            musicConfigRepository.clearMusicConfigModel()
            whenever(firestoreDocumentSnapshot.data).thenReturn(configMap)

            // When
            val result = musicConfigRepository.getAdsBannerPlayerConfig()

            // Then
            result.collect {
                assertNotNull(it)
            }
        }

    @Test
    fun getAdsBannerPlayerConfig_noCache_musicConfigModelIsNull_returnNull() = runTest {
        // Given
        getDataSuccess()
        musicConfigRepository.clearMusicConfigModel()
        whenever(firestoreDocumentSnapshot.data).thenReturn(null)

        // When
        val result = musicConfigRepository.getAdsBannerPlayerConfig()

        // Then
        result.collect {
            assertNull(it)
        }
    }

    @Test
    fun getAdsBannerPlayerConfig_haveCache_returnCacheData() = runTest {
        // Given
        getDataSuccess()
        musicConfigRepository.setMusicConfigModel(mockMusicConfigModel)

        // When
        val result = musicConfigRepository.getAdsBannerPlayerConfig()

        // Then
        result.collect {
            assertEquals(it?.enable?.android, false)
            assertEquals(it?.urlAds, "urlAds")
        }
    }

    @Test
    fun getLoginBannerConfig_noCache_musicConfigModelIsNotNull_returnDataFromFireStore() = runTest {
        // Given
        getDataSuccess()
        val childMap = mapOf(
            "image_en" to "imageEn",
            "image_th" to "imageTh"
        )
        val configMap = mapOf(
            "login_banner" to childMap
        )
        musicConfigRepository.clearMusicConfigModel()
        whenever(firestoreDocumentSnapshot.data).thenReturn(configMap)

        // When
        val result = musicConfigRepository.getLoginBannerConfig()

        // Then
        result.collect {
            assertEquals(it?.imageEN, "imageEn")
            assertEquals(it?.imageTH, "imageTh")
        }
    }

    @Test
    fun getLoginBannerConfig_noCache_musicConfigModelIsNull_returnNull() = runTest {
        // Given
        getDataSuccess()
        musicConfigRepository.clearMusicConfigModel()
        whenever(firestoreDocumentSnapshot.data).thenReturn(null)

        // When
        val result = musicConfigRepository.getLoginBannerConfig()

        // Then
        result.collect {
            assertNull(it)
        }
    }

    @Test
    fun getLoginBannerConfig_haveCache_returnCacheData() = runTest {
        // Given
        getDataSuccess()
        musicConfigRepository.setMusicConfigModel(mockMusicConfigModel)

        // When
        val result = musicConfigRepository.getLoginBannerConfig()

        // Then
        result.collect {
            assertEquals(it?.imageEN, mockLoginBannerItemConfigModel.imageEN)
            assertEquals(it?.imageTH, mockLoginBannerItemConfigModel.imageTH)
        }
    }

    @Test
    fun getRootShelfConfig_noCache_musicConfigModelIsNotNull_returnDataFromFireStore() = runTest {
        // Given
        getDataSuccess()
        val childMap = mapOf(
            "android" to "BjP4b8qrp8Bj"
        )
        val configMap = mapOf(
            "root_shelf" to childMap
        )
        musicConfigRepository.clearMusicConfigModel()
        whenever(firestoreDocumentSnapshot.data).thenReturn(configMap)

        // When
        val result = musicConfigRepository.getRootShelfConfig()

        // Then
        result.collect {
            assertEquals(it, "BjP4b8qrp8Bj")
        }
    }

    @Test
    fun getRootShelfConfig_noCache_musicConfigModelIsNull_returnEmptyData() = runTest {
        // Given
        getDataSuccess()
        musicConfigRepository.clearMusicConfigModel()
        whenever(firestoreDocumentSnapshot.data).thenReturn(null)

        // When
        val result = musicConfigRepository.getRootShelfConfig()

        // Then
        result.collect {
            assertTrue(it.isEmpty())
        }
    }

    @Test
    fun getRootShelfConfig_haveCache_returnCacheData() = runTest {
        // Given
        getDataSuccess()
        musicConfigRepository.setMusicConfigModel(mockMusicConfigModel)

        // When
        val result = musicConfigRepository.getRootShelfConfig()

        // Then
        result.collect {
            assertEquals(it, mockMusicRootShelf.android)
        }
    }

    private fun getDataSuccess() {
        val answer = Answer {
            return@Answer object : Task<DocumentSnapshot>() {
                override fun isComplete(): Boolean {
                    return true
                }

                override fun isSuccessful(): Boolean {
                    return true
                }

                override fun isCanceled(): Boolean {
                    return false
                }

                override fun getResult(): DocumentSnapshot {
                    return firestoreDocumentSnapshot
                }

                override fun <X : Throwable?> getResult(p0: Class<X>): DocumentSnapshot {
                    return firestoreDocumentSnapshot
                }

                override fun getException(): Exception? {
                    return null
                }

                override fun addOnSuccessListener(p0: OnSuccessListener<in DocumentSnapshot>): Task<DocumentSnapshot> {
                    p0.onSuccess(firestoreDocumentSnapshot)
                    return this
                }

                override fun addOnSuccessListener(
                    p0: Executor,
                    p1: OnSuccessListener<in DocumentSnapshot>
                ): Task<DocumentSnapshot> {
                    p1.onSuccess(firestoreDocumentSnapshot)
                    return this
                }

                override fun addOnSuccessListener(
                    p0: Activity,
                    p1: OnSuccessListener<in DocumentSnapshot>
                ): Task<DocumentSnapshot> {
                    p1.onSuccess(firestoreDocumentSnapshot)
                    return this
                }

                override fun addOnFailureListener(p0: OnFailureListener): Task<DocumentSnapshot> {
                    return this
                }

                override fun addOnFailureListener(
                    p0: Executor,
                    p1: OnFailureListener
                ): Task<DocumentSnapshot> {
                    return this
                }

                override fun addOnFailureListener(
                    p0: Activity,
                    p1: OnFailureListener
                ): Task<DocumentSnapshot> {
                    return this
                }
            }
        }
        whenever(firestoreDocumentReference.get()).thenAnswer(answer)
    }
}
