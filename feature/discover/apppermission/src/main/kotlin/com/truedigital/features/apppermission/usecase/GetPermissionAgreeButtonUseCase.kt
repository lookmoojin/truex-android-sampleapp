package com.truedigital.features.apppermission.usecase

import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepository
import javax.inject.Inject

interface GetPermissionAgreeButtonUseCase {
    fun execute(): String
}

class GetPermissionAgreeButtonUseCaseImpl @Inject constructor(
    private var appPermissionGetConfigRepository: AppPermissionGetConfigRepository
) : GetPermissionAgreeButtonUseCase {
    override fun execute(): String {
        return appPermissionGetConfigRepository.getAppPermissionData().button
    }
}
