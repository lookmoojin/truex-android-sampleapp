package com.truedigital.share.data.firestoreconfig.domainconfig.usecase

import android.content.Context
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.secure.FireBaseSecureRepository
import javax.inject.Inject

interface GetFireBaseSecureUseCase {
    fun execute(context: Context)
}

class GetFireBaseSecureUseCaseImpl @Inject constructor(
    private val fireBaseSecureRepository: FireBaseSecureRepository
) : GetFireBaseSecureUseCase {
    override fun execute(context: Context) {
        fireBaseSecureRepository.getFireBaseSecure(context)
    }
}
