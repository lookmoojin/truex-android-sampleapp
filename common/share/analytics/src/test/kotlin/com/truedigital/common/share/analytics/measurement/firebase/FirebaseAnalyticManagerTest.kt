package com.truedigital.common.share.analytics.measurement.firebase

import android.content.Context
import android.os.Bundle
import androidx.startup.AppInitializer
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.analytics.FirebaseAnalytics
import com.truedigital.common.share.analytics.AnalyticsInitializer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach

class FirebaseAnalyticManagerTest {

    private val firebaseAnalytics: FirebaseAnalytics = mockk()

    lateinit var context: Context
    lateinit var firebaseAnalyticManager: FirebaseAnalyticManager

    @BeforeEach
    fun setup() {

        context = InstrumentationRegistry.getInstrumentation().targetContext

        AppInitializer.getInstance(context).initializeComponent(AnalyticsInitializer::class.java)

        firebaseAnalyticManager = FirebaseAnalyticManager(firebaseAnalytics)
    }

    @Ignore("Not ready yet")
    fun `logEvent withValid EventKey And Bundle calls FirebaseAnalyticsLogEvent`() {
        val eventKey = "test_event"
        val bundle = Bundle()
        every { firebaseAnalytics.logEvent(eventKey, bundle) } just Runs
        firebaseAnalyticManager.logEvent(eventKey, bundle)
        verify(exactly = 1) { firebaseAnalytics.logEvent(eventKey, bundle) }
    }

    @Ignore("Not ready yet")
    fun `setUserId withValidUserId calls FirebaseAnalytics SetUserId`() {
        val userId = "user123"
        every { firebaseAnalytics.setUserId(userId) } just Runs
        firebaseAnalyticManager.setUserId(userId)
        verify(exactly = 1) { firebaseAnalytics.setUserId(userId) }
    }

    @Ignore("Not ready yet")
    fun `setUserProperty withValid KeyAndValue calls FirebaseAnalytics SetUserProperty`() {
        val key = "test_key"
        val value = "test_value"
        every { firebaseAnalytics.setUserProperty(key, value) } just Runs
        firebaseAnalyticManager.setUserProperty(key, value)
        verify(exactly = 1) { firebaseAnalytics.setUserProperty(key, value) }
    }
}
