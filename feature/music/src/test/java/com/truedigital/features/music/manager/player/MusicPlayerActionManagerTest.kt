package com.truedigital.features.music.manager.player

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.features.tuned.common.extensions.startServiceDefault
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MusicPlayerActionManagerTest {

    private val context: Context = mock()
    private lateinit var musicPlayerActionManager: MusicPlayerActionManager

    @BeforeEach
    fun setUp() {
        musicPlayerActionManager = MusicPlayerActionManagerImpl(context)
    }

    @Test
    fun testActionPlay_verifyActionPlay() {
        musicPlayerActionManager.actionPlay()

        verify(context).startServiceDefault {
            action = MusicPlayerController.ACTION_PLAY
        }
    }

    @Test
    fun testActionRePlay_verifyActionRePlay() {
        musicPlayerActionManager.actionReplay()

        verify(context).startServiceDefault {
            action = MusicPlayerController.ACTION_REPLAY
        }
    }

    @Test
    fun testActionPause_verifyActionPause() {
        musicPlayerActionManager.actionPause()

        verify(context).startServiceDefault {
            action = MusicPlayerController.ACTION_PAUSE
        }
    }

    @Test
    fun testActionPrevious_verifyActionPrevious() {
        musicPlayerActionManager.actionPrevious()

        verify(context).startServiceDefault {
            action = MusicPlayerController.ACTION_SKIP_PREVIOUS
        }
    }

    @Test
    fun testActionNext_verifyActionNext() {
        musicPlayerActionManager.actionNext()

        verify(context).startServiceDefault {
            action = MusicPlayerController.ACTION_SKIP_NEXT
        }
    }

    @Test
    fun testActionRepeat_verifyActionRepeat() {
        musicPlayerActionManager.actionRepeat()

        verify(context).startServiceDefault {
            action = MusicPlayerController.ACTION_TOGGLE_REPEAT
        }
    }

    @Test
    fun testActionShuffle_verifyActionShuffle() {
        musicPlayerActionManager.actionShuffle()

        verify(context).startServiceDefault {
            action = MusicPlayerController.ACTION_TOGGLE_SHUFFLE
        }
    }

    @Test
    fun testActionSeek_verifyActionSeek() {
        musicPlayerActionManager.actionSeek(100L)

        verify(context).startServiceDefault {
            action = MusicPlayerController.ACTION_SEEK
            putExtra(
                MusicPlayerController.ACTION_SEEK,
                100L
            )
        }
    }
}
