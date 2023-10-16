package com.truedigital.common.share.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.coredata.data.api.CmsShelvesApiInterface
import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepositoryImpl
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.CmsShelfResponse
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals

class CmsShelvesRepositoryTest {
    private val api: CmsShelvesApiInterface = mock()
    private val cmsShelvesRepository by lazy {
        CmsShelvesRepositoryImpl(api)
    }

    private val shelfId = "XPLrGrK8DjX5"
    private val country = "th"
    private val fields = "setting,thumb_list,trailer"
    private val lang = null
    private val page = null
    private val limit = null

    @Test
    fun `When getCmsShelfListData success Then should return data`() = runTest {
        val mockData = Data().apply {
            shelfList = getMockShelfList()
        }
        val mockResponseData = Response.success(
            CmsShelfResponse().apply {
                code = 200
                data = mockData
            }
        )

        whenever(api.getCmsShelfList(shelfId, country, fields, lang, page, limit)).thenReturn(
            mockResponseData
        )

        cmsShelvesRepository.getCmsShelfListData(shelfId, country, fields)
            .collect { data ->
                assertEquals(data, mockData)
            }
    }

    @Test
    fun `When getCmsShelfListData fail Then should throw error`() = runTest {
        val mockResponseData = Response.success<CmsShelfResponse>(null)

        whenever(api.getCmsShelfList(shelfId, country, fields, lang, page, limit)).thenReturn(
            mockResponseData
        )

        cmsShelvesRepository.getCmsShelfListData(shelfId, country, fields)
            .catch { exception ->
                assertEquals(exception.message, "Retrieving shelf is fail or data not found")
            }
            .collect()
    }

    @Test
    fun `When getCmsShelfList success Then should return shelf list`() = runTest {
        val mockShelfList = getMockShelfList()
        val mockResponseData = Response.success(
            CmsShelfResponse().apply {
                code = 200
                data = Data().apply {
                    shelfList = mockShelfList
                }
            }
        )

        whenever(api.getCmsShelfList(shelfId, country, fields, lang, page, limit)).thenReturn(
            mockResponseData
        )

        cmsShelvesRepository.getCmsShelfList(shelfId, country, fields)
            .collect { shelfList ->
                assertEquals(shelfList, mockShelfList)
            }
    }

    @Test
    fun `When getCmsShelfList fail Then should throw error`() = runTest {
        val mockResponseData = Response.success<CmsShelfResponse>(null)

        whenever(api.getCmsShelfList(shelfId, country, fields, lang, page, limit)).thenReturn(
            mockResponseData
        )

        cmsShelvesRepository.getCmsShelfList(shelfId, country, fields)
            .catch { exception ->
                assertEquals(exception.message, "Retrieving shelf is fail or data not found")
            }
            .collect()
    }

    private fun getMockShelfList(): List<Shelf> {
        val shelf = Shelf().apply {
            id = "XPLrGrK8DjX5"
            title = "(TrueID App) Watch Top Navigation : For you"
        }
        return listOf(shelf)
    }

    @Test
    fun `When getCmsPublicContentShelfListData success Then should return data`() =
        runTest {
            val mockData = Data().apply {
                shelfList = getMockShelfList()
            }
            val mockResponseData = Response.success(
                CmsShelfResponse().apply {
                    code = 200
                    data = mockData
                }
            )

            whenever(
                api.getCmsPublicContentShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    page,
                    limit
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsPublicContentShelfListData(shelfId, country, fields)
                .collect { data ->
                    assertEquals(data, mockData)
                }
        }

    @Test
    fun `When getCmsPublicContentShelfListData code 300 Then should throw error`() =
        runTest {
            val mockData = Data().apply {
                shelfList = getMockShelfList()
            }
            val mockResponseData = Response.success(
                CmsShelfResponse().apply {
                    code = 300
                    data = mockData
                }
            )

            whenever(
                api.getCmsPublicContentShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    page,
                    limit
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsPublicContentShelfListData(shelfId, country, fields)
                .catch { exception ->
                    assertEquals(exception.message, "Retrieving shelf is fail or data not found")
                }
                .collect()
        }

    @Test
    fun `When getCmsPublicContentShelfListData data null Then should throw error`() =
        runTest {
            val mockResponseData = Response.success(
                CmsShelfResponse().apply {
                    code = 200
                    data = null
                }
            )

            whenever(
                api.getCmsPublicContentShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    page,
                    limit
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsPublicContentShelfListData(shelfId, country, fields)
                .catch { exception ->
                    assertEquals(exception.message, "Retrieving shelf is fail or data not found")
                }
                .collect()
        }

    @Test
    fun `When getCmsPublicContentShelfListData body null Then should throw error`() =
        runTest {
            val cmsShelfResponse: CmsShelfResponse? = null
            val mockResponseData = Response.success(cmsShelfResponse)

            whenever(
                api.getCmsPublicContentShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    page,
                    limit
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsPublicContentShelfListData(shelfId, country, fields)
                .catch { exception ->
                    assertEquals(exception.message, "Retrieving shelf is fail or data not found")
                }
                .collect()
        }

    @Test
    fun `When getCmsPublicContentShelfListData fail Then should throw error`() = runTest {
        val mockResponseData = Response.success<CmsShelfResponse>(null)

        whenever(
            api.getCmsPublicContentShelfList(
                shelfId,
                country,
                fields,
                lang,
                page,
                limit
            )
        ).thenReturn(
            mockResponseData
        )

        cmsShelvesRepository.getCmsPublicContentShelfListData(shelfId, country, fields)
            .catch { exception ->
                assertEquals(exception.message, "Retrieving shelf is fail or data not found")
            }
            .collect()
    }

    @Test
    fun `When getCmsProgressiveShelf success Then should return data`() =
        runTest {
            val mockResponseData = Response.success(
                CmsShelfResponse().apply {
                    code = 10001
                    data = Data().apply {
                        shelfList = getMockShelfList()
                    }
                }
            )

            whenever(
                api.getCmsProgressiveShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    limit
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsProgressiveShelfData(shelfId, country, fields)
                .collect { data ->
                    assertEquals(data, mockResponseData.body()?.data)
                }
        }

    @Test
    fun `When getCmsProgressiveShelf success data empty`() =
        runTest {
            val mockResponseData = Response.success(
                CmsShelfResponse().apply {
                    code = 10001
                    data = Data()
                }
            )

            whenever(
                api.getCmsProgressiveShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    limit
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsProgressiveShelfData(shelfId, country, fields)
                .collect { data ->
                    assertEquals(data, mockResponseData.body()?.data)
                }
        }

    @Test
    fun `When getCmsProgressiveShelf code 10008 Then should throw error`() =
        runTest {
            val mockResponseData = Response.error<CmsShelfResponse>(400, "{}".toResponseBody())

            whenever(
                api.getCmsProgressiveShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    page
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsProgressiveShelfData(shelfId, country, fields)
                .catch { exception ->
                    assertEquals(exception.message, "400")
                }
                .collect()
        }

    @Test
    fun `When getCmsProgressiveShelf body null Then should throw error`() =
        runTest {
            val mockResponseData = Response.success<CmsShelfResponse>(null)

            whenever(
                api.getCmsProgressiveShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    page
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsProgressiveShelfData(shelfId, country, fields)
                .catch { exception ->
                    assertEquals(exception.message, "200")
                }
                .collect()
        }

    @Test
    fun `When getCmsProgressiveShelf fail Then should throw error`() = runTest {
        val mockResponseData = Response.error<CmsShelfResponse>(
            400,
            "{}".toResponseBody("application/json".toMediaTypeOrNull())
        )

        whenever(
            api.getCmsProgressiveShelfList(
                shelfId,
                country,
                fields,
                lang,
                limit
            )
        ).thenReturn(
            mockResponseData
        )

        cmsShelvesRepository.getCmsProgressiveShelfData(shelfId, country, fields)
            .catch { exception ->
                assertEquals(exception.message, "400")
            }
            .collect()
    }

    @Test
    fun `When getCmsProgressiveShelf code null`() =
        runTest {
            val mockData = Data().apply {
                shelfList = getMockShelfList()
            }
            val mockResponseData = Response.success(
                CmsShelfResponse().apply {
                    code = null
                    data = mockData
                }
            )

            whenever(
                api.getCmsProgressiveShelfList(
                    shelfId,
                    country,
                    fields,
                    lang,
                    page
                )
            ).thenReturn(
                mockResponseData
            )

            cmsShelvesRepository.getCmsProgressiveShelfData(shelfId, country, fields)
                .catch { exception ->
                    assertEquals(exception.message, "200")
                }
                .collect()
        }
}
