package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.HeaderSelectionModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TrueCloudV3GroupContactUseCaseTest {

    lateinit var trueCloudV3GroupContactUseCase: GetGroupContactUseCase

    @BeforeEach
    fun setup() {
        trueCloudV3GroupContactUseCase = GetGroupContactUseCaseImpl()
    }

    @Test
    fun `test execute GroupContact success`() = runTest {
        // arrange
        val contactDataAA = ContactTrueCloudModel(
            firstName = "aa",
            lastName = "lastName",
            email = "email"
        )
        val contactDataAB = ContactTrueCloudModel(
            firstName = "ab",
            lastName = "lastName",
            email = "email"
        )
        val contactDataCC = ContactTrueCloudModel(
            firstName = "cc",
            lastName = "lastName",
            email = "email"
        )
        val list = listOf(contactDataAA, contactDataCC, contactDataAB)
        // act
        val flow = trueCloudV3GroupContactUseCase.execute(list)

        // assert
        flow.collectSafe { response ->
            assertNotEquals(3, response.size)
        }
    }

    @Test
    fun `test execute GroupContact error`() = runTest {
        // arrange
        val headerSelectionModel = HeaderSelectionModel(
            key = "a",
            size = 2
        )
        val contactDataAB = ContactTrueCloudModel(
            firstName = "ab",
            lastName = "lastName",
            email = "email"
        )
        val contactDataCC = ContactTrueCloudModel(
            firstName = "cc",
            lastName = "lastName",
            email = "email"
        )
        val list = listOf(headerSelectionModel, contactDataCC, contactDataAB)
        // act
        val flow = trueCloudV3GroupContactUseCase.execute(list)

        // assert
        flow.collectSafe { response ->
            assertEquals(3, response.size)
        }
    }

    @Test
    fun `execute should return expected flow of grouped contacts`() = runTest {
        // Arrange
        val contacts = listOf(
            ContactTrueCloudModel(
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                tel = mutableListOf(
                    ContactPhoneNumberModel(
                        type = "mobile",
                        number = "123-456-7890"
                    )
                )
            ),
            ContactTrueCloudModel(
                firstName = "Zane",
                lastName = "Doe",
                email = "jane.doe@example.com",
                tel = mutableListOf(
                    ContactPhoneNumberModel(
                        type = "mobile",
                        number = "123-456-7891"
                    )
                )
            ),
            ContactTrueCloudModel(
                firstName = "1234",
                lastName = "Doe",
                email = "jane.doe@example.com",
                tel = mutableListOf(
                    ContactPhoneNumberModel(
                        type = "mobile",
                        number = "123-456-7891"
                    )
                )
            )
        )
        val expectedGroupedContacts = listOf(
            HeaderSelectionModel(key = "#", size = 2),
            ContactTrueCloudModel(
                firstName = "1234",
                lastName = "Doe",
                email = "jane.doe@example.com",
                tel = mutableListOf(
                    ContactPhoneNumberModel(
                        type = "mobile",
                        number = "123-456-7891"
                    )
                )
            ),
            HeaderSelectionModel(key = "J", size = 2),
            ContactTrueCloudModel(
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                tel = mutableListOf(
                    ContactPhoneNumberModel(
                        type = "mobile",
                        number = "123-456-7890"
                    )
                )
            ),
            HeaderSelectionModel(key = "Z", size = 2),
            ContactTrueCloudModel(
                firstName = "Zane",
                lastName = "Doe",
                email = "jane.doe@example.com",
                tel = mutableListOf(
                    ContactPhoneNumberModel(
                        type = "mobile",
                        number = "123-456-7891"
                    )
                )
            )

        )

        // Act
        val flow = trueCloudV3GroupContactUseCase.execute(contacts)

        // Assert
        flow.collectSafe { response ->
            val expectedGroup = expectedGroupedContacts.last() as ContactTrueCloudModel
            val resultGroup = response.last() as ContactTrueCloudModel
            assertEquals(expectedGroupedContacts.size, response.size)
            assertEquals(expectedGroup.firstName, resultGroup.firstName)
            assertEquals(expectedGroup.lastName, resultGroup.lastName)
            assertEquals(expectedGroup.email, resultGroup.email)
            assertEquals(expectedGroup.tel.size, resultGroup.tel.size)
            assertEquals(expectedGroup.tel.first().number, resultGroup.tel.first().number)
            assertEquals(expectedGroup.tel.first().type, resultGroup.tel.first().type)
        }
    }
}
