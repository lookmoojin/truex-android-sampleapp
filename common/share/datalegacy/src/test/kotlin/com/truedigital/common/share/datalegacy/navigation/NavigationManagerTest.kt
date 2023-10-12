package com.truedigital.common.share.datalegacy.navigation

import android.content.Intent
import androidx.lifecycle.Observer
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
internal class NavigationManagerTest {

    private lateinit var navigationManager: NavigationManager

    @BeforeEach
    fun setUp() {
        navigationManager = NavigationManager
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getActionRequest returns SingleLiveEvent`() {
        val observer = mockk<Observer<NavigationRequest>>()

        every {
            observer.onChanged(any())
        } just runs

        val singleLiveEvent = navigationManager.getActionRequest()
        singleLiveEvent.observeForever(observer)

        val request = NavigationRequest()
        navigationManager.setActionRequest(request)

        verify { observer.onChanged(request) }
    }

    @Test
    fun `getActionIntentRequest returns SingleLiveEvent`() {
        val observer = mockk<Observer<Intent>>()
        val intent = mockk<Intent>()

        every {
            observer.onChanged(intent)
        } just runs

        val singleLiveEvent = navigationManager.getActionIntentRequest()
        singleLiveEvent.observeForever(observer)

        navigationManager.setActionIntentRequest(intent)

        verify { observer.onChanged(intent) }
    }

    @Test
    fun `getActionReplaceFragment returns SingleLiveEvent`() {
        val observer = mockk<Observer<Unit>>()

        every {
            observer.onChanged(Unit)
        } just runs

        val singleLiveEvent = navigationManager.getActionReplaceFragment()
        singleLiveEvent.observeForever(observer)

        navigationManager.setActionReplaceFragment()

        verify { observer.onChanged(Unit) }
    }

    @Test
    fun `getActionPopFragment returns SingleLiveEvent`() {
        val observer = mockk<Observer<Unit>>()

        every {
            observer.onChanged(Unit)
        } just runs

        val singleLiveEvent = navigationManager.getActionPopFragment()
        singleLiveEvent.observeForever(observer)

        navigationManager.setActionPopFragment()

        verify { observer.onChanged(Unit) }
    }
}
