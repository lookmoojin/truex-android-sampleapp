package com.truedigital.features.tuned.common.extensions

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import com.newrelic.agent.android.NewRelic
import com.truedigital.core.extensions.windowManager
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.Constants.FLOAT_24F
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.album.view.AlbumActivity
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.presentation.playlist.presenter.PlaylistPresenter
import com.truedigital.features.tuned.presentation.playlist.view.PlaylistActivity
import com.truedigital.features.tuned.presentation.station.presenter.StationPresenter
import com.truedigital.features.tuned.presentation.station.view.StationActivity
import com.truedigital.features.tuned.presentation.tag.presenter.TagPresenter
import com.truedigital.features.tuned.presentation.tag.view.TagActivity
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.features.tuned.service.music.MusicPlayerServiceImpl
import timber.log.Timber
import java.net.URLDecoder
import kotlin.reflect.KClass

val Context.windowWidth: Int
    get() {
        val displayMetrics = DisplayMetrics()
        windowManager().defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

val Context.windowHeight: Int
    get() {
        val displayMetrics = DisplayMetrics()
        windowManager().defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

val Context.actionBarHeight: Int
    get() {
        val tv = TypedValue()
        return if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        } else {
            0
        }
    }

val Context.statusBarHeight: Int
    get() {
        var height = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId)
        } else if (this is Activity) {
            val rectangle = Rect()
            window.decorView.getWindowVisibleDisplayFrame(rectangle)
            height = rectangle.top
        }

        if (height == 0) {
            height = resources.dp(FLOAT_24F)
        }

        return height
    }

fun Context.launchIntent(): Intent? = packageManager.getLaunchIntentForPackage(packageName)

fun Context.pendingIntentForAction(action: String): PendingIntent {
    val intent = Intent(this, MusicPlayerServiceImpl::class.java)
    val flags = 0 or FLAG_IMMUTABLE
    return PendingIntent.getService(this, 0, intent.setAction(action), flags)
}

fun Context.launchPendingIntent(): PendingIntent {
    val flags = 0 or FLAG_IMMUTABLE
    return PendingIntent.getActivity(
        this,
        0,
        launchIntent()?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        flags
    )
}

fun Context.startActivity(
    target: KClass<out Activity>,
    init: Intent.() -> Unit = {}
) {
    startActivityInternal(target.java, init)
}

fun Context.startActivity(
    target: Class<out Activity>,
    init: Intent.() -> Unit = {}
) {
    startActivityInternal(target, init)
}

fun Activity.startActivityForResult(
    target: KClass<out Activity>,
    requestCode: Int,
    init: Intent.() -> Unit = {}
) {
    val intent = Intent(this, target.java)
    intent.init()
    startActivityForResult(intent, requestCode)
}

fun Context.startServiceDefault(init: Intent.() -> Unit = {}) {
    val intent = Intent(this, MusicPlayerServiceImpl::class.java)
    intent.init()

    try {
        // Workaround to bind service first and then unbind instantly
        // Will be removed if still have a crash log of MusicPlayerServiceImpl
        bindService(
            intent,
            object : SimpleServiceConnection {
                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                    if (binder is MusicPlayerService.PlayerBinder) {
                        binder.service.startMusicForegroundService(intent)
                    }
                    unbindService(this)
                }
            },
            Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
        )
    } catch (e: RuntimeException) {
        Timber.e(e)
        val customEventMap = mapOf(
            "Name" to "can't connect bindServiceMusic"
        )
        NewRelic.recordCustomEvent("Music", customEventMap)
    }
}

fun Context.bindServiceMusic(serviceConnection: ServiceConnection) {
    try {
        bindService(
            Intent(this, MusicPlayerServiceImpl::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
        )
    } catch (e: RuntimeException) {
        Timber.e(e)
        val customEventMap = mapOf(
            "Name" to "can't connect bindServiceMusic"
        )
        NewRelic.recordCustomEvent("Music", customEventMap)
    }
}

fun Context.getMusicServiceIntent() = Intent(this, MusicPlayerServiceImpl::class.java)

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.alert(initialize: AlertDialog.Builder.() -> Unit = {}): AlertDialog? {
    val builder = AlertDialog.Builder(this)
    builder.initialize()
    val dialog = builder.show()
    val font = ResourcesCompat.getFont(this, R.font.sukhumvit_tadmai)
    dialog.window?.findViewById<TextView>(R.id.alertTitle)?.typeface = font
    dialog.window?.findViewById<TextView>(android.R.id.message)?.typeface = font
    dialog.window?.findViewById<Button>(android.R.id.button1)?.typeface = font
    dialog.window?.findViewById<Button>(android.R.id.button2)?.typeface = font
    return dialog
}

fun Context.share(subject: String, message: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtras(Intent.EXTRA_SUBJECT to subject, Intent.EXTRA_TEXT to message)
    startActivity(Intent.createChooser(intent, getString(R.string.share)))
}

fun Context.browse(uri: Uri, sceneTransitionEnabled: Boolean = true): Boolean {
    var sanitised = uri
    val routeScheme = getString(R.string.route_scheme)

    if (sanitised.scheme != null) {
        if (sanitised.scheme?.startsWith("webview") == true) {
            sanitised = Uri.parse(
                sanitised.toString()
                    .replace("webview", "https://${getString(R.string.route_host)}/webview")
            )
        } else if (sanitised.scheme == routeScheme) {
            sanitised = Uri.parse(
                sanitised.toString()
                    .replace("$routeScheme://", "https://${getString(R.string.route_host)}/")
            )
        }
    }

    val intent =
        if (sanitised.host.isNullOrEmpty() || sanitised.host == getString(R.string.route_host)) {
            when (sanitised.pathSegments.firstOrNull()) {
                "artists" -> {
                    when (sanitised.pathSegments.size) {
                        2 -> Intent(
                            this,
                            ArtistActivity::class.java
                        ).putExtras(ArtistPresenter.ARTIST_ID_KEY to sanitised.pathSegments[1].toInt())

                        3 -> {
                            val artistId = sanitised.pathSegments[1].toInt()
                            when (sanitised.pathSegments[2]) {
                                "play" -> {
                                    val i = Intent(this, ArtistActivity::class.java)
                                    i.putExtras(
                                        ArtistPresenter.ARTIST_ID_KEY to artistId,
                                        ArtistPresenter.AUTO_PLAY_KEY to true
                                    )
                                    i
                                }

                                else -> null
                            }
                        }

                        else -> null
                    }
                }

                "stations" -> {
                    when (sanitised.pathSegments.size) {
                        2 -> {
                            val stationId = sanitised.pathSegments[1].toInt()
                            val i = Intent(this, StationActivity::class.java)
                            i.putExtras(StationPresenter.STATION_ID_KEY to stationId)
                            i
                        }

                        3 -> {
                            val stationId = sanitised.pathSegments[1].toInt()
                            when (sanitised.pathSegments[2]) {
                                "play" -> {
                                    val i = Intent(this, StationActivity::class.java)
                                    i.putExtras(
                                        StationPresenter.STATION_ID_KEY to stationId,
                                        StationPresenter.AUTO_PLAY_KEY to true
                                    )
                                    i
                                }

                                else -> {
                                    val trackHash = sanitised.pathSegments[2]
                                    val i = Intent(this, StationActivity::class.java)
                                    i.putExtras(
                                        StationPresenter.STATION_ID_KEY to stationId,
                                        StationPresenter.TRACK_HASH_KEY to trackHash,
                                        StationPresenter.AUTO_PLAY_KEY to true
                                    )
                                    i
                                }
                            }
                        }

                        4 -> {
                            val stationId = sanitised.pathSegments[1].toInt()
                            when (sanitised.pathSegments[2]) {
                                "play" -> {
                                    val trackHash = sanitised.pathSegments[3]
                                    val i = Intent(this, StationActivity::class.java)
                                    i.putExtras(
                                        StationPresenter.STATION_ID_KEY to stationId,
                                        StationPresenter.TRACK_HASH_KEY to trackHash,
                                        StationPresenter.AUTO_PLAY_KEY to true
                                    )
                                    i
                                }

                                else -> null
                            }
                        }

                        else -> null
                    }
                }

                "albums" -> {
                    when (sanitised.pathSegments.size) {
                        2 -> {
                            val albumId = sanitised.pathSegments[1].toInt()
                            val i = Intent(this, AlbumActivity::class.java)
                            i.putExtras(AlbumPresenter.ALBUM_ID_KEY to albumId)
                            i
                        }

                        3 -> {
                            val albumId = sanitised.pathSegments[1].toInt()
                            when (sanitised.pathSegments[2]) {
                                "play" -> {
                                    val i = Intent(this, AlbumActivity::class.java)
                                    i.putExtras(
                                        AlbumPresenter.ALBUM_ID_KEY to albumId,
                                        AlbumPresenter.AUTO_PLAY_KEY to true
                                    )
                                    i
                                }

                                else -> {
                                    try {
                                        val i = Intent(this, AlbumActivity::class.java)
                                        val songId = sanitised.pathSegments[2].toInt()
                                        i.putExtras(
                                            AlbumPresenter.ALBUM_ID_KEY to albumId,
                                            AlbumPresenter.SONG_ID_KEY to songId
                                        )
                                        i
                                    } catch (e: NumberFormatException) {
                                        null
                                    }
                                }
                            }
                        }

                        else -> null
                    }
                }

                "songs" -> {
                    when (sanitised.pathSegments.size) {
                        2 -> {
                            val songId = sanitised.pathSegments[1].toInt()
                            val i = Intent(this, AlbumActivity::class.java)
                            i.putExtras(AlbumPresenter.SONG_ID_KEY to songId)
                            i
                        }

                        3 -> {
                            val songId = sanitised.pathSegments[1].toInt()
                            when (sanitised.pathSegments[2]) {
                                "play" -> {
                                    val i = Intent(this, AlbumActivity::class.java)
                                    i.putExtras(
                                        AlbumPresenter.SONG_ID_KEY to songId,
                                        AlbumPresenter.AUTO_PLAY_KEY to true
                                    )
                                    i
                                }

                                else -> null
                            }
                        }

                        else -> null
                    }
                }

                "videos" -> {
                    val config =
                        MusicComponent.getInstance().getApplicationComponent().getConfiguration()
                    if (config.enableVideo) {
                        when (sanitised.pathSegments.size) {
                            2 -> {
                                val videoId = sanitised.pathSegments[1].toInt()
                                val musicServiceConnection = object : SimpleServiceConnection {
                                    override fun onServiceConnected(
                                        name: ComponentName,
                                        binder: IBinder
                                    ) {
                                        if (binder is MusicPlayerService.PlayerBinder) {
                                            binder.service.apply {
                                                startMusicForegroundService(getMusicServiceIntent())
                                                playVideo(videoId)
                                            }
                                        }
                                        unbindService(this)
                                    }
                                }
                                bindServiceMusic(musicServiceConnection)
                                null
                            }

                            else -> null
                        }
                    } else {
                        null
                    }
                }

                "playlists" -> {
                    val config =
                        MusicComponent.getInstance().getApplicationComponent().getConfiguration()
                    if (config.enablePlaylist) {
                        when (sanitised.pathSegments.size) {
                            2 -> {
                                val playlistId = sanitised.pathSegments[1].toInt()
                                val i = Intent(this, PlaylistActivity::class.java)
                                i.putExtras(PlaylistPresenter.PLAYLIST_ID_KEY to playlistId)
                                i
                            }

                            3 -> {
                                val playlistId = sanitised.pathSegments[1].toInt()
                                when (sanitised.pathSegments[2]) {
                                    "play" -> {
                                        val i = Intent(this, PlaylistActivity::class.java)
                                        i.putExtras(
                                            PlaylistPresenter.PLAYLIST_ID_KEY to playlistId,
                                            PlaylistPresenter.AUTO_PLAY_KEY to true
                                        )
                                        i
                                    }

                                    else -> null
                                }
                            }

                            else -> null
                        }
                    } else {
                        null
                    }
                }

                "stakkars" -> {
                    val stakkarId = sanitised.pathSegments[1].toInt()
                    val musicServiceConnection = object : SimpleServiceConnection {
                        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                            if (binder is MusicPlayerService.PlayerBinder) {
                                binder.service.apply {
                                    startMusicForegroundService(getMusicServiceIntent())
                                    playStakkar(stakkarId)
                                }
                            }
                            unbindService(this)
                        }
                    }
                    bindServiceMusic(musicServiceConnection)
                    null
                }

                "toast" -> {
                    toast(URLDecoder.decode(sanitised.pathSegments[1], "UTF-8"))
                    null
                }

                "webview" -> {
                    val actualUri = Uri.parse(sanitised.path?.removePrefix("/webview/"))
                    if (browse(actualUri)) {
                        return true
                    } else {
                        Intent(Intent.ACTION_VIEW, actualUri)
                    }
                }

                "tags" -> {
                    if (sanitised.pathSegments.size == 2) {
                        val tagName = sanitised.pathSegments[1]
                        val i = Intent(this, TagActivity::class.java)
                        i.putExtras(TagPresenter.TAG_NAME_KEY to tagName)
                        i
                    } else {
                        null
                    }
                }

                else -> null
            }
        } else {
            Intent(Intent.ACTION_VIEW, sanitised)
        }

    if (sceneTransitionEnabled)
        intent?.let { startActivity(it, sceneTransitionOptions()) }
    else
        intent?.let { startActivity(it) }

    return intent != null
}

private fun Context.sceneTransitionOptions(): Bundle? {
    var options: Bundle? = null
    if (this is AppCompatActivity) {
        val pairs: MutableList<Pair<View, String>> = mutableListOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val statusBar = find<View>(android.R.id.statusBarBackground)
            val navigationBar = find<View>(android.R.id.navigationBarBackground)
            statusBar?.let { pairs.add(Pair(it, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)) }
            navigationBar?.let {
                pairs.add(
                    Pair(
                        it,
                        Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME
                    )
                )
            }
        }
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs.toTypedArray())
            .toBundle()
    }
    return options
}

private fun Context.startActivityInternal(
    target: Class<out Activity>,
    init: Intent.() -> Unit
) {
    val intent = Intent(this, target)
    intent.init()
    startActivity(intent)
}
