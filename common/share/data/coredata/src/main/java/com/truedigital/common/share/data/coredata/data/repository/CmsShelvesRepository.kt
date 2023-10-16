package com.truedigital.common.share.data.coredata.data.repository

import com.truedigital.common.share.data.coredata.data.api.CmsShelvesApiInterface
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CmsShelvesRepository {
    fun getCmsShelfList(
        shelfId: String,
        country: String,
        fields: String,
        lang: String? = null,
        page: String? = null,
        limit: String? = null
    ): Flow<List<Shelf>>

    fun getCmsShelfListData(
        shelfId: String,
        country: String,
        fields: String,
        lang: String? = null,
        page: String? = null,
        limit: String? = null
    ): Flow<Data>

    fun getCmsShelfListDataWithSlug(
        shelfId: String,
        country: String,
        fields: String,
        lang: String? = null,
        page: String? = null,
        limit: String? = null,
        shelfSlug: String? = null
    ): Flow<Data>

    fun getCmsPublicContentShelfListData(
        shelfId: String,
        country: String,
        fields: String,
        lang: String? = null,
        page: String? = null,
        limit: String? = null
    ): Flow<Data>

    fun getCmsProgressiveShelfData(
        shelfId: String,
        country: String,
        fields: String,
        lang: String? = null,
        limit: String? = null,
        merchantType: String? = null,
        articleCategory: String? = null
    ): Flow<Data>

    fun getCmsProgressiveShelfData(
        shelfId: String,
        country: String,
        expand: String,
        fields: String,
        lang: String? = null,
        limit: String? = null
    ): Flow<Data>

    fun getCmsShelfContentsList(
        shelfId: String? = null,
        country: String,
        fields: String? = null,
        lang: String? = null,
        page: String? = null,
        limit: String? = null,
        articleCategory: String? = null,
        contentType: String? = null,
        movieType: String? = null,
        epMaster: String? = null,
        isVodLayer: String? = null
    ): Flow<List<Data>>
}

class CmsShelvesRepositoryImpl @Inject constructor(
    private val api: CmsShelvesApiInterface
) :
    CmsShelvesRepository {
    companion object {
        private const val PROGRESSIVE_SHELF_SUCCESS_CODE = 10001
        private const val ERROR_LOAD_SHELF = "Retrieving shelf is fail or data not found"
    }

    override fun getCmsShelfList(
        shelfId: String,
        country: String,
        fields: String,
        lang: String?,
        page: String?,
        limit: String?
    ): Flow<List<Shelf>> {
        return flow {
            val response = api.getCmsShelfList(
                shelfId = shelfId,
                country = country,
                fields = fields,
                lang = lang,
                page = page,
                limit = limit,
            )

            if (response.isSuccessful && response.body()?.data?.shelfList != null) {
                emit(response.body()?.data?.shelfList ?: listOf())
            } else {
                error(ERROR_LOAD_SHELF)
            }
        }
    }

    override fun getCmsShelfListData(
        shelfId: String,
        country: String,
        fields: String,
        lang: String?,
        page: String?,
        limit: String?
    ): Flow<Data> {
        return flow {
            val response = api.getCmsShelfList(
                shelfId = shelfId,
                country = country,
                fields = fields,
                lang = lang,
                page = page,
                limit = limit,
            )
            if (response.isSuccessful && response.body()?.data != null) {
                emit(response.body()?.data ?: Data())
            } else {
                error(ERROR_LOAD_SHELF)
            }
        }
    }

    override fun getCmsShelfListDataWithSlug(
        shelfId: String,
        country: String,
        fields: String,
        lang: String?,
        page: String?,
        limit: String?,
        shelfSlug: String?
    ): Flow<Data> {
        return flow {
            val response = api.getCmsShelfListWithSlug(
                shelfId = shelfId,
                country = country,
                fields = fields,
                lang = lang,
                page = page,
                limit = limit,
                shelfSlug = shelfSlug
            )
            val result = if (response.isSuccessful && response.body()?.data != null) {
                response.body()?.data ?: Data()
            } else {
                error(ERROR_LOAD_SHELF)
            }
            emit(result)
        }
    }

    override fun getCmsShelfContentsList(
        shelfId: String?,
        country: String,
        fields: String?,
        lang: String?,
        page: String?,
        limit: String?,
        articleCategory: String?,
        contentType: String?,
        movieType: String?,
        epMaster: String?,
        isVodLayer: String?
    ): Flow<List<Data>> {
        return flow {
            val response = api.getCmsShelfContentsList(
                shelfId = shelfId,
                country = country,
                fields = fields,
                lang = lang,
                page = page,
                limit = limit,
                articleCategory = articleCategory,
                contentType = contentType,
                movieType = movieType,
                epMaster = epMaster,
                isVodLayer = isVodLayer
            )
            val result = if (response.isSuccessful && response.body()?.data != null) {
                response.body()?.data ?: listOf()
            } else {
                error(ERROR_LOAD_SHELF)
            }
            emit(result)
        }
    }

    override fun getCmsPublicContentShelfListData(
        shelfId: String,
        country: String,
        fields: String,
        lang: String?,
        page: String?,
        limit: String?
    ): Flow<Data> {
        return flow {
            val response = api.getCmsPublicContentShelfList(
                shelfId = shelfId,
                country = country,
                fields = fields,
                lang = lang,
                page = page,
                limit = limit,
            )
            val result = if (response.isSuccessful && response.body()?.data != null) {
                response.body()?.data ?: Data()
            } else {
                error(ERROR_LOAD_SHELF)
            }
            emit(result)
        }
    }

    override fun getCmsProgressiveShelfData(
        shelfId: String,
        country: String,
        fields: String,
        lang: String?,
        limit: String?,
        merchantType: String?,
        articleCategory: String?
    ): Flow<Data> {
        return flow {
            val response = api.getCmsProgressiveShelfList(
                shelfId = shelfId,
                country = country,
                fields = fields,
                lang = lang,
                limit = limit,
                merchantType = merchantType,
                articleCategory = articleCategory
            )
            val result = if (response.isSuccessful &&
                response.body()?.code == PROGRESSIVE_SHELF_SUCCESS_CODE &&
                response.body()?.data != null
            ) {
                response.body()?.data ?: Data()
            } else {
                error(response.code())
            }
            emit(result)
        }
    }

    override fun getCmsProgressiveShelfData(
        shelfId: String,
        country: String,
        expand: String,
        fields: String,
        lang: String?,
        limit: String?,
    ): Flow<Data> {
        return flow {
            val response = api.getCmsProgressiveShelfListWithExpand(
                shelfId = shelfId,
                country = country,
                expand = expand,
                fields = fields,
                lang = lang,
                limit = limit,
            )
            val result = if (response.isSuccessful &&
                response.body()?.code == PROGRESSIVE_SHELF_SUCCESS_CODE &&
                response.body()?.data != null
            ) {
                response.body()?.data ?: Data()
            } else {
                error(ERROR_LOAD_SHELF)
            }
            emit(result)
        }
    }
}
