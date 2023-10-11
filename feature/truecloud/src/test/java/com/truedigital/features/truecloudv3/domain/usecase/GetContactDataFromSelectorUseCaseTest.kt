package com.truedigital.features.truecloudv3.domain.usecase

import android.content.Intent
import com.truedigital.core.extensions.collectSafe
import com.wafflecopter.multicontactpicker.ContactResult
import com.wafflecopter.multicontactpicker.MultiContactPicker
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetContactDataFromSelectorUseCaseTest {
    private lateinit var getContactDataFromSelectorUseCase: GetContactDataFromSelectorUseCase

    @BeforeEach
    fun setup() {
        getContactDataFromSelectorUseCase = GetContactDataFromSelectorUseCaseImpl()
    }

    @Test
    fun `test execute success empty email`() = runTest {
        // arrange
        val mockIntent = mockk<Intent>()
        val mockContactResult = mockk<ContactResult>()
        val mockPhoneNumber = mockk<PhoneNumber>()
        val mockListContactResult = arrayListOf<ContactResult>()
        mockListContactResult.add(mockContactResult)
        mockkStatic(MultiContactPicker::class)
        every { MultiContactPicker.obtainResult(any()) } returns mockListContactResult
        every { mockContactResult.displayName } returns "panya n"
        every { mockContactResult.emails } returns listOf()
        every { mockContactResult.phoneNumbers } returns listOf(mockPhoneNumber)
        every { mockPhoneNumber.number } returns "090909090"
        every { mockPhoneNumber.typeLabel } returns "Mobile"

        // act
        val response = getContactDataFromSelectorUseCase.execute(mockIntent)

        // assert
        response.collectSafe {
            val contactModel = it.first()
            assertEquals("panya n", contactModel.firstName)
            assertEquals("", contactModel.email)
        }
    }
    @Test
    fun `test execute success`() = runTest {
        // arrange
        val mockIntent = mockk<Intent>()
        val mockContactResult = mockk<ContactResult>()
        val mockPhoneNumber = mockk<PhoneNumber>()
        val mockListContactResult = arrayListOf<ContactResult>()
        mockListContactResult.add(mockContactResult)
        mockkStatic(MultiContactPicker::class)
        every { MultiContactPicker.obtainResult(any()) } returns mockListContactResult
        every { mockContactResult.displayName } returns "panya n"
        every { mockContactResult.emails } returns listOf("panya.n@gmail.com")
        every { mockContactResult.phoneNumbers } returns listOf(mockPhoneNumber)
        every { mockPhoneNumber.number } returns "090909090"
        every { mockPhoneNumber.typeLabel } returns "Mobile"

        // act
        val response = getContactDataFromSelectorUseCase.execute(mockIntent)

        // assert
        response.collectSafe {
            val contactModel = it.first()
            assertEquals("panya n", contactModel.firstName)
            assertEquals("panya.n@gmail.com", contactModel.email)
        }
    }

    @Test
    fun `test execute empty`() = runTest {
        // arrange
        val mockIntent = mockk<Intent>()
        mockkStatic(MultiContactPicker::class)
        every { MultiContactPicker.obtainResult(any()) } returns null

        // act
        val response = getContactDataFromSelectorUseCase.execute(mockIntent)

        // assert
        response.collectSafe {
            assertEquals(true, it.isEmpty())
        }
    }
}
