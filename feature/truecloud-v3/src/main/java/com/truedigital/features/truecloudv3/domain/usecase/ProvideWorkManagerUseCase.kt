package com.truedigital.features.truecloudv3.domain.usecase

import android.content.Context
import androidx.work.WorkManager
import javax.inject.Inject

interface ProvideWorkManagerUseCase {
    fun execute(): WorkManager
}

class ProvideWorkManagerUseCaseImpl @Inject constructor(
    private val context: Context
) : ProvideWorkManagerUseCase {
    override fun execute(): WorkManager {
        return WorkManager.getInstance(context)
    }
}
