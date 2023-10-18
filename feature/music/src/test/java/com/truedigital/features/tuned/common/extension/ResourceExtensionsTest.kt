package com.truedigital.features.tuned.common.extension

import android.content.res.Resources
import android.util.DisplayMetrics
import com.truedigital.features.tuned.common.extensions.dp
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ResourceExtensionsTest {

    @MockK
    private lateinit var resources: Resources

    @MockK
    private lateinit var displayMetrics: DisplayMetrics

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun dp_returnDpValue() {
        // Given
        every { resources.displayMetrics } returns displayMetrics

        // When
        val result = resources.dp(10.0f)

        // Then
        assertNotNull(result)
    }
}
