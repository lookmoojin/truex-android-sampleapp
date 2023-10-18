package com.truedigital.navigation.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.navigation.data.PersonaData
import com.truedigital.navigation.data.repository.GetInterContentRepository
import com.truedigital.navigation.domain.model.PersonaDomainData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetPersonaConfigUseCase {
    fun execute(): Flow<PersonaDomainData>
}

class GetPersonaConfigUseCaseImpl @Inject constructor(
    private val getInterContentRepository: GetInterContentRepository,
    private val localizationRepository: LocalizationRepository
) : GetPersonaConfigUseCase {
    override fun execute(): Flow<PersonaDomainData> {
        return getInterContentRepository.getPersonaData(
            country = localizationRepository.getAppCountryCode(),
            lang = localizationRepository.getAppLanguageCode()
        ).map(::convertToDomainModel)
    }

    private fun convertToDomainModel(personaData: PersonaData): PersonaDomainData {
        return PersonaDomainData(
            url = personaData.url.orEmpty(),
            schemaId = personaData.schemaId.orEmpty()
        )
    }
}
