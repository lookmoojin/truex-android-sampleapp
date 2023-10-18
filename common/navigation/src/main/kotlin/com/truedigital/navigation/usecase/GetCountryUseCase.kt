package com.truedigital.navigation.usecase

import com.truedigital.navigation.data.repository.CountryRepository
import com.truedigital.navigations.share.data.model.CountryResponseItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetCountryUseCase {
    fun execute(): Flow<List<CountryResponseItem>>
}

class GetCountryUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository
) : GetCountryUseCase {
    override fun execute(): Flow<List<CountryResponseItem>> {
        return countryRepository.getCountryList()
    }
}
