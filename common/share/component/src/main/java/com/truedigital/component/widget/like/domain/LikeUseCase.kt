package com.truedigital.component.widget.like.domain

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.component.widget.like.data.api.CmsFnLikeApiInterface
import com.truedigital.component.widget.like.data.model.request.LikeDataRequest
import com.truedigital.component.widget.like.data.model.response.LikeResponse
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

interface LikeUseCase {

    companion object {
        const val LIKE_ACTION = "1"
        const val UNLIKE_ACTION = "0"
    }

    fun getStateLike(cmsId: String): Observable<LikeResponse>
    fun postLike(cmsId: String, emoCode: String): Completable
}

class LikeUseCaseImpl @Inject constructor(
    private val cmsFnLikeApi: CmsFnLikeApiInterface,
    private val userRepository: UserRepository
) : LikeUseCase {

    override fun getStateLike(cmsId: String): Observable<LikeResponse> {
        return cmsFnLikeApi.getStateLike(cmsId, userRepository.getSsoId())
    }

    override fun postLike(cmsId: String, emoCode: String): Completable {

        val likeDataRequest = LikeDataRequest().apply {
            id = cmsId
            emocode = emoCode
            accessToken = userRepository.getAccessToken()
        }

        return cmsFnLikeApi.postLike(likeDataRequest)
    }
}
