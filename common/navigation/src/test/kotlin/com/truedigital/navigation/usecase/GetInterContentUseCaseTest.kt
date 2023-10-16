package com.truedigital.navigation.usecase

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.core.extensions.collectSafe
import com.truedigital.navigation.TestData
import com.truedigital.navigation.data.repository.GetCacheInterContentRepository
import com.truedigital.navigation.data.repository.GetInterContentRepository
import com.truedigital.navigation.domain.model.PersonaDomainData
import com.truedigital.navigation.domain.usecase.GetPersonaConfigUseCase
import com.truedigital.navigation.domain.usecase.GetTodayPersonaSegmentEnableUseCase
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.HttpException
import kotlin.test.assertEquals

internal class GetInterContentUseCaseTest {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var useCase: GetInterContentUseCase
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val contentRepository: GetInterContentRepository = mock()
    private val contentCacheRepository: GetCacheInterContentRepository = mock()
    private val getPersonaConfigUseCase: GetPersonaConfigUseCase = mock()
    private val getTodayPersonaSegmentEnableUseCase: GetTodayPersonaSegmentEnableUseCase = mock()
    private val loginInterface: LoginManagerInterface = mock()

    @BeforeEach
    fun setUp() {
        useCase = GetInterContentUseCaseImpl(
            coroutineDispatcher,
            contentRepository,
            contentCacheRepository,
            getPersonaConfigUseCase,
            getTodayPersonaSegmentEnableUseCase,
            loginInterface
        )
    }

    @Test
    fun `fetch content from api successfully, expect return flow of list of shelf skeleton`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow { emit(getShelfSkeletonJsonArray()) }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            useCase.execute("").collectSafe { content ->
                assertNotNull(content)
                assertTrue(content.isNotEmpty())
            }

            verify(contentRepository, times(1)).getContentFeed(any())
            verify(contentCacheRepository, times(0)).getContentFeed(any())
        }
    }

    @Test
    fun `fetch content from api unsuccessfully then get content from cache successfully, expect return flow of list of shelf skeleton`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    throw TestData.Exception.http404
                }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            whenever(contentCacheRepository.getContentFeed(any())).thenReturn(
                getShelfSkeletonJsonArray()
            )

            val flow = useCase.execute("").collectSafe { content ->
                assertNotNull(content)
                assertTrue(content.isNotEmpty())
            }

            verify(contentRepository, times(1)).getContentFeed(any())
            verify(contentCacheRepository, times(1)).getContentFeed(any())
        }
    }

    @Test
    fun `fetch content from api unsuccessfully then get content from cache unsuccessfully, expect catching exception`() {
        val expectedException = TestData.Exception.http404
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    throw expectedException
                }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            whenever(contentCacheRepository.getContentFeed(any())).thenReturn(null)

            val flow = useCase.execute("")

            assertThrows<HttpException> { flow.single() }
            verify(contentRepository, times(1)).getContentFeed(any())
            verify(contentCacheRepository, times(1)).getContentFeed(any())
        }
    }

    @Test
    fun `fetch content from api successfully but json is invalid, expect catching exception`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    emit(
                        JsonArray().apply {
                            add(0)
                        }
                    )
                }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            val flow = useCase.execute("")
            assertThrows<IllegalStateException> { flow.single() }

            verify(contentRepository, times(1)).getContentFeed(any())
            verify(contentCacheRepository, times(1)).getContentFeed(any())
        }
    }

    @Test
    fun `fetch content from api successfully but shelf header not found, expect catching exception`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    emit(
                        JsonArray().apply {
                            add(JsonObject())
                        }
                    )
                }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            useCase.execute("").collectSafe { content ->
                assertTrue {
                    content.isEmpty()
                }
            }
            verify(contentRepository, times(1)).getContentFeed(any())
            verify(contentCacheRepository, times(0)).getContentFeed(any())
        }
    }

    @Test
    fun `fetch content from api successfully but shelf header is invalid, expect empty list`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    emit(
                        JsonArray().apply {
                            add(
                                JsonObject().apply {
                                    addProperty("header", "")
                                }
                            )
                        }
                    )
                }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            useCase.execute("").collectSafe { content ->
                assertTrue {
                    content.isEmpty()
                }
            }
            verify(contentRepository, times(1)).getContentFeed(any())
            verify(contentCacheRepository, times(0)).getContentFeed(any())
        }
    }

    @Test
    fun `fetch content from api successfully but shelf header is empty object, expect expect empty list`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    emit(
                        getShelfSkeletonJsonArray()
                    )
                }
            )
            useCase.execute("").collectSafe { content ->
                assertTrue {
                    content.first().json.has("theme")
                    content.first().json.has("items")
                    content.first().json.has("type_call_api")
                }
            }
            verify(contentRepository, times(1)).getContentFeed(any())
            verify(contentCacheRepository, times(0)).getContentFeed(any())
        }
    }

    @Test
    fun `fetch content from api successfully but shelf id not found, expect list`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    emit(
                        JsonArray().apply {
                            add(
                                JsonObject().apply {
                                    add(
                                        "header",
                                        JsonObject().apply {
                                            addProperty("navigate", "")
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            useCase.execute("").collectSafe { content ->
                assertTrue {
                    content.isEmpty()
                }
            }

            verify(contentRepository, times(1)).getContentFeed(any())
            verify(contentCacheRepository, times(0)).getContentFeed(any())
        }
    }

    @Test
    fun `test add new shelfIndex from success data`() = runTest {

        whenever(loginInterface.isLoggedIn()).thenReturn(false)
        whenever(contentRepository.getContentFeed(any())).thenReturn(
            flow { emit(getShelfSkeletonJsonArray()) }
        )
        whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)

        useCase.execute("").collectSafe {
            assertEquals(6, it.size)
            assertEquals(3, it[5].shelfIndex)
        }
    }

    @Test
    fun `test persona enable and user not login url today, expect return flow of list of shelf skeleton`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow { emit(getShelfSkeletonJsonArray()) }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(true)
            whenever(loginInterface.isLoggedIn()).thenReturn(false)

            useCase.execute("today").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(contentCacheRepository, times(0)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona enable and user not login url not today, expect return flow of list of shelf skeleton`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow { emit(getShelfSkeletonJsonArray()) }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(true)
            whenever(loginInterface.isLoggedIn()).thenReturn(false)

            useCase.execute("").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(contentCacheRepository, times(0)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona not enable and user not login url today, expect return flow of list of shelf skeleton`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow { emit(getShelfSkeletonJsonArray()) }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            whenever(loginInterface.isLoggedIn()).thenReturn(false)

            useCase.execute("today").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(contentCacheRepository, times(0)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona not enable and user not login url not today, expect return flow of list of shelf skeleton`() {
        runTest {
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow { emit(getShelfSkeletonJsonArray()) }
            )
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(false)
            whenever(loginInterface.isLoggedIn()).thenReturn(false)

            useCase.execute("watch").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(contentCacheRepository, times(0)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona enable and user login, expect return flow of list of shelf skeleton from persona`() {
        runTest {
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(true)
            whenever(loginInterface.isLoggedIn()).thenReturn(true)
            whenever(getPersonaConfigUseCase.execute()).thenReturn(
                flowOf(
                    PersonaDomainData(
                        url = "url",
                        schemaId = "schemaId"
                    )
                )
            )
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow { emit(getShelfSkeletonJsonArray()) }
            )

            useCase.execute("today").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(getPersonaConfigUseCase, times(1)).execute()
                verify(contentCacheRepository, times(0)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona enable and user login, but persona failed expect return flow of list of default shelf skeleton`() {
        runTest {
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(true)
            whenever(loginInterface.isLoggedIn()).thenReturn(true)
            whenever(getPersonaConfigUseCase.execute()).thenReturn(
                flow { error("error failed") }
            )
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow { emit(getShelfSkeletonJsonArray()) }
            )

            useCase.execute("today").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(getPersonaConfigUseCase, times(1)).execute()
                verify(contentRepository, times(1)).getContentFeed(any())
                verify(contentCacheRepository, times(0)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona enable and user login, but persona failed and default failed expect return cache`() {
        runTest {
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(true)
            whenever(loginInterface.isLoggedIn()).thenReturn(true)
            whenever(getPersonaConfigUseCase.execute()).thenReturn(
                flow { error("error failed") }
            )
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    throw TestData.Exception.http404
                }
            )
            whenever(contentCacheRepository.getContentFeed(any())).thenReturn(
                getShelfSkeletonJsonArray()
            )
            useCase.execute("today").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(getPersonaConfigUseCase, times(1)).execute()
                verify(contentRepository, times(1)).getContentFeed(any())
                verify(contentCacheRepository, times(1)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona enable and user login, persona url success but call api failed expect return cache`() {
        runTest {
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(true)
            whenever(loginInterface.isLoggedIn()).thenReturn(true)
            whenever(getPersonaConfigUseCase.execute()).thenReturn(
                flowOf(
                    PersonaDomainData(
                        url = "url",
                        schemaId = "schemaId"
                    )
                )
            )
            whenever(contentRepository.getContentFeed(any())).thenReturn(
                flow {
                    throw TestData.Exception.http404
                }
            )
            whenever(contentCacheRepository.getContentFeed(any())).thenReturn(
                getShelfSkeletonJsonArray()
            )
            useCase.execute("today").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(getPersonaConfigUseCase, times(1)).execute()
                verify(contentRepository, times(1)).getContentFeed(any())
                verify(contentCacheRepository, times(1)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona enable and user login, persona url success but call api failed expect return default shelf`() {
        runTest {
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(true)
            whenever(loginInterface.isLoggedIn()).thenReturn(true)
            whenever(getPersonaConfigUseCase.execute()).thenReturn(
                flowOf(
                    PersonaDomainData(
                        url = "url",
                        schemaId = "schemaId"
                    )
                )
            )
            whenever(contentRepository.getContentFeed("url")).thenReturn(
                flow {
                    throw TestData.Exception.http404
                }
            )
            whenever(contentRepository.getContentFeed("today")).thenReturn(
                flow { emit(getShelfSkeletonJsonArray()) }
            )
            whenever(contentCacheRepository.getContentFeed("url")).thenReturn(
                null
            )
            useCase.execute("today").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(getPersonaConfigUseCase, times(1)).execute()
                verify(contentRepository, times(2)).getContentFeed(any())
                verify(contentCacheRepository, times(1)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    @Test
    fun `test persona enable and user login, persona url success but call api failed expect return default cache`() {
        runTest {
            whenever(getTodayPersonaSegmentEnableUseCase.execute()).thenReturn(true)
            whenever(loginInterface.isLoggedIn()).thenReturn(true)
            whenever(getPersonaConfigUseCase.execute()).thenReturn(
                flowOf(
                    PersonaDomainData(
                        url = "url",
                        schemaId = "schemaId"
                    )
                )
            )
            whenever(contentRepository.getContentFeed("url")).thenReturn(
                flow {
                    throw TestData.Exception.http404
                }
            )
            whenever(contentRepository.getContentFeed("today")).thenReturn(
                flow {
                    throw TestData.Exception.http404
                }
            )
            whenever(contentCacheRepository.getContentFeed("today")).thenReturn(
                getShelfSkeletonJsonArray()
            )
            whenever(contentCacheRepository.getContentFeed("url")).thenReturn(
                null
            )
            useCase.execute("today").collectSafe { shelfSkeleton ->
                verify(getTodayPersonaSegmentEnableUseCase, times(1)).execute()
                verify(getPersonaConfigUseCase, times(1)).execute()
                verify(contentRepository, times(2)).getContentFeed(any())
                verify(contentCacheRepository, times(2)).getContentFeed(any())
                assertNotNull(shelfSkeleton)
                assertTrue(shelfSkeleton.isNotEmpty())
            }
        }
    }

    private fun getShelfSkeletonJsonArray(): JsonArray {

        val jsonArray = JsonArray()
        jsonArray.add(
            JsonObject().apply {
                add(
                    "header",
                    JsonObject().apply {
                        addProperty("vType", "header_horizontal")
                        addProperty("navigate", "")
                    }
                )
                addProperty("vType", "lib:today/v1/shelf_multiple_horizontal")
                addProperty("theme", "white")
                addProperty("shelfId", "0")
            }
        )
        jsonArray.add(
            JsonObject().apply {
                add(
                    "header",
                    JsonObject().apply {
                        addProperty("vType", "header_horizontal")
                        addProperty("navigate", "")
                    }
                )
                addProperty("vType", "lib:today/v1/shelf_multiple_horizontal")
                addProperty("theme", "white")
                addProperty("shelfId", "4")
            }
        )
        jsonArray.add(
            JsonObject().apply {
                addProperty("vType", "lib:today/v1/shelf_multiple_horizontal")
                addProperty("theme", "white")
                addProperty("shelfId", "1")
            }
        )

        jsonArray.add(
            JsonObject().apply {
                addProperty("vType", "lib:today/v1/shelf_component")
                addProperty("theme", "white")
                addProperty("shelfId", "2")
                add(
                    "items",
                    JsonArray().apply {
                        add(
                            JsonObject().apply {
                                addProperty("viewType", "item_component")
                                add(
                                    "data",
                                    JsonObject().apply {
                                        add(
                                            "setting",
                                            JsonObject().apply {
                                                addProperty("component_name", "truepoint")
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
        jsonArray.add(
            JsonObject().apply {
                addProperty("vType", "lib:today/v1/shelf_multiple_horizontal")
                addProperty("theme", "white")
                addProperty("shelfId", "2")
            }
        )

        return jsonArray
    }
}
