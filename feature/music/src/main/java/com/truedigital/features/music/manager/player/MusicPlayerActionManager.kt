package com.truedigital.features.music.manager.player

import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.startServiceDefault
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import javax.inject.Inject

interface MusicPlayerActionManager {
    fun actionPlay()
    fun actionReplay()
    fun actionPause()
    fun actionPrevious()
    fun actionNext()
    fun actionRepeat()
    fun actionShuffle()
    fun actionSeek(progress: Long)
    fun actionClose()
}

class MusicPlayerActionManagerImpl @Inject constructor(
    private val context: Context
) : MusicPlayerActionManager {

    override fun actionPlay() {
        context.startServiceDefault {
            action = MusicPlayerController.ACTION_PLAY
        }
    }

    override fun actionReplay() {
        context.startServiceDefault {
            action = MusicPlayerController.ACTION_REPLAY
        }
    }

    override fun actionPause() {
        context.startServiceDefault {
            action = MusicPlayerController.ACTION_PAUSE
        }
    }

    override fun actionPrevious() {
        context.startServiceDefault {
            action = MusicPlayerController.ACTION_SKIP_PREVIOUS
        }
    }

    override fun actionNext() {
        context.startServiceDefault {
            action = MusicPlayerController.ACTION_SKIP_NEXT
        }
    }

    override fun actionRepeat() {
        context.startServiceDefault {
            action = MusicPlayerController.ACTION_TOGGLE_REPEAT
        }
    }

    override fun actionShuffle() {
        context.startServiceDefault {
            action = MusicPlayerController.ACTION_TOGGLE_SHUFFLE
        }
    }

    override fun actionSeek(progress: Long) {
        context.startServiceDefault {
            action = MusicPlayerController.ACTION_SEEK
            putExtra(
                MusicPlayerController.ACTION_SEEK,
                progress
            )
        }
    }

    override fun actionClose() {
        context.bindServiceMusic(
            object : SimpleServiceConnection {
                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                    val musicPlayerService = (binder as? MusicPlayerService.PlayerBinder)?.service
                    musicPlayerService?.clearQueue()
                    context.unbindService(this)
                }
            }
        )
    }
}
