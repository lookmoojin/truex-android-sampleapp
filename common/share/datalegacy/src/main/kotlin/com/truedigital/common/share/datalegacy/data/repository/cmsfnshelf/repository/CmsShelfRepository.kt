package com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.repository

import com.truedigital.common.share.datalegacy.data.api.cmsfn.CmsFnApiInterface
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.CmsShelfResponse
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject

interface CmsShelfRepository {
    fun getCmsShelfData(shelfId: String, fields: String): Observable<Data>
}

class CmsShelfRepositoryImpl @Inject constructor(
    private val cmsFnDmpApi: CmsFnApiInterface
) : CmsShelfRepository {

    companion object {
        const val ERROR_LOAD_SHELF = "Retrieving shelf is fail or data not found"
    }

    override fun getCmsShelfData(shelfId: String, fields: String): Observable<Data> {
        return cmsFnDmpApi.getCmsShelfList(shelfId, fields)
            .map(::validateDataResponse)
    }

    private fun validateDataResponse(response: Response<CmsShelfResponse>): Data {
        return if ((!response.isSuccessful) || response.body()?.data == null) {
            throw Throwable(ERROR_LOAD_SHELF)
        } else {
            val cmsShelfResponse = response.body()
            cmsShelfResponse?.data ?: Data()
        }
    }
}
