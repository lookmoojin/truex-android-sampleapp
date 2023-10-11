package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.HeaderSelectionModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3GroupAlphabetContactUseCaseTest {
    private lateinit var trueCloudV3GroupAlphabetContactUseCase: GetGroupAlphabetContactUseCase

    @BeforeEach
    fun setup() {
        trueCloudV3GroupAlphabetContactUseCase = GetGroupAlphabetContactUseCaseImpl()
    }

    @Test
    fun `test execute NotEmpty`() = runTest {
        // arrange
        val headerSelectionModelF = HeaderSelectionModel(
            key = "F",
            size = 2
        )
        val contactData = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email"
        )
        val contactData2 = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email"
        )
        val headerSelectionModelG = HeaderSelectionModel(
            key = "G",
            size = 2
        )
        val contactDataG = ContactTrueCloudModel(
            firstName = "gerle",
            lastName = "lastName",
            email = "email"
        )
        val contactDataG2 = ContactTrueCloudModel(
            firstName = "gelame",
            lastName = "lastName",
            email = "email"
        )

        val alphabetItemModelF = AlphabetItemModel(
            alphabet = headerSelectionModelF.key,
            index = headerSelectionModelF.size,
            position = 0,
            isActive = false,
            size = headerSelectionModelF.size
        )

        val alphabetItemModelG = AlphabetItemModel(
            alphabet = headerSelectionModelG.key,
            index = headerSelectionModelG.size,
            position = 0,
            isActive = false,
            size = headerSelectionModelG.size
        )

        // act
        val flow = trueCloudV3GroupAlphabetContactUseCase.execute(
            listOf(
                headerSelectionModelF,
                contactData,
                contactData2,
                headerSelectionModelG,
                contactDataG,
                contactDataG2
            )
        )

        // assert
        flow.collectSafe { response ->
            assertEquals(listOf(alphabetItemModelF, alphabetItemModelG).size, response.size)
        }
        assert(
            headerSelectionModelF.areContentsTheSame(
                HeaderSelectionModel(
                    key = "F",
                    size = 2
                )
            )
        )
        assert(
            headerSelectionModelF.areItemsTheSame(
                HeaderSelectionModel(
                    key = "F",
                    size = 2
                )
            )
        )
        assert(
            !headerSelectionModelF.areItemsTheSame(
                HeaderSelectionModel(
                    key = "G",
                    size = 3
                )
            )
        )
        assert(
            !headerSelectionModelF.areItemsTheSame(
                ContactTrueCloudModel()
            )
        )
    }

    @Test
    fun `test execute Empty`() = runTest {
        // arrange
        val headerSelectionModelF = HeaderSelectionModel(
            key = "F",
            size = 2
        )
        val contactData = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email"
        )
        val contactData2 = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email"
        )

        // act
        val flow = trueCloudV3GroupAlphabetContactUseCase.execute(
            listOf(
                headerSelectionModelF,
                contactData,
                contactData2,
            )
        )

        // assert
        flow.collectSafe { response ->
            assertEquals(listOf(), response)
        }
    }

    @Test
    fun `test execute not header`() = runTest {
        // arrange
        val contactData = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email"
        )
        val contactData1 = ContactTrueCloudModel(
            firstName = "Aaa",
            lastName = "lastName",
            email = "email"
        )
        val contactData2 = ContactTrueCloudModel(
            firstName = "bbb",
            lastName = "lastName",
            email = "email"
        )

        // act
        val flow = trueCloudV3GroupAlphabetContactUseCase.execute(
            listOf(
                contactData,
                contactData1,
                contactData2,
            )
        )

        // assert
        flow.collectSafe { response ->
            assertEquals(listOf(), response)
        }
    }
}
