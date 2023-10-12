package com.truedigital.features.truecloudv3.domain.usecase

import android.os.Environment
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.util.Calendar
import kotlin.test.assertNotNull

class ExportContactUseCaseTest {
    private lateinit var exportContactUseCase: ExportContactUseCase
    private val contextDataProvider: ContextDataProvider = mockk()

    @BeforeEach
    fun setup() {
        exportContactUseCase = ExportContactUseCaseImpl()
    }

    @Test
    fun `test export success`() = runTest {
        // arrange
        val contactTrueCloudModels = mutableListOf<ContactTrueCloudModel>()
        val phoneNumbers = mutableListOf<ContactPhoneNumberModel>()
        phoneNumbers.add(
            ContactPhoneNumberModel(
                type = "testType",
                number = "number"
            )
        )
        contactTrueCloudModels.add(
            ContactTrueCloudModel(
                picture = "picture",
                firstName = "firstName",
                lastName = "lastName",
                email = "email",
                tel = phoneNumbers
            )
        )
        val calendar = mockk<Calendar>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns calendar
        every { calendar.getTimeInMillis() } returns 10000L
        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(any()).path } returns "/storage/emulated/0"
        every { mockFile.exists() } returns true
        every { mockFile.isFile } returns true

        // act
        val response = exportContactUseCase.execute(contactTrueCloudModels)

        // assert
        response.collectSafe {
            assertNotNull(response)
        }
    }
}
