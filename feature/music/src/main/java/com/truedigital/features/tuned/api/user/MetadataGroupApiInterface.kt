package com.truedigital.features.tuned.api.user

import com.truedigital.features.tuned.data.user.model.ContentLanguage
import io.reactivex.Single
import retrofit2.http.GET

interface MetadataGroupApiInterface {
    @GET("groups/contentlanguages")
    fun getContentLanguages(): Single<List<ContentLanguage>>
}
