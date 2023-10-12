package com.truedigital.common.share.analytics.measurement.usecase

import android.content.Context
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.foundation.FoundationApplication
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.test.assertEquals

internal class TrackDeviceResolutionUseCaseImplTest {

    private lateinit var trackDeviceResolutionUseCase: TrackDeviceResolutionUseCase
    private lateinit var trackDeviceResolutionUseCaseImpl: TrackDeviceResolutionUseCaseImpl
    private var mockTrackDeviceResolutionUseCaseImpl: TrackDeviceResolutionUseCaseImpl = mock()

    private val analyticManager: AnalyticManager = mock()

    private val mockFragmentActivity: FragmentActivity = mock()
    private val mockContext: Context = mock()

    private val mockWindowManager: WindowManager = mock()
    private val mockDisplay: Display = mock()

    @BeforeEach
    fun setUp() {
        trackDeviceResolutionUseCase = TrackDeviceResolutionUseCaseImpl(analyticManager)
        trackDeviceResolutionUseCaseImpl = TrackDeviceResolutionUseCaseImpl(analyticManager)

        FoundationApplication.appContext = mockContext
    }

    @Test
    fun execute() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 29)
        whenever(
            mockTrackDeviceResolutionUseCaseImpl.getActuallyScreenResolution(
                mockFragmentActivity
            )
        ).thenReturn(
            "1080 x 1920"
        )

        whenever(mockFragmentActivity.windowManager).thenReturn(mockWindowManager)
        whenever(mockFragmentActivity.windowManager.defaultDisplay).thenReturn(mockDisplay)
    }

    @Test
    fun `Get actually screen resolution when sdk version below R(30)`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 29)

        whenever(
            mockTrackDeviceResolutionUseCaseImpl.getActuallyScreenResolution(
                mockFragmentActivity
            )
        ).thenReturn(
            "1080 x 1920"
        )

        whenever(mockFragmentActivity.windowManager).thenReturn(mockWindowManager)
        whenever(mockFragmentActivity.windowManager.defaultDisplay).thenReturn(mockDisplay)

        trackDeviceResolutionUseCaseImpl.getActuallyScreenResolution(mockFragmentActivity)
            .let { screen ->
                assertEquals("0 x 0", screen)
            }
    }

    @Test
    fun `Get actually screen resolution when sdk version upper R(30)`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 32)
        assertEquals(32, Build.VERSION.SDK_INT)
    }

    @Throws(Exception::class)
    private fun setFinalStatic(field: Field, newValue: Any?) {
        field.isAccessible = true
        val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(null, newValue)
    }
}
