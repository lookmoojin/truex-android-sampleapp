package com.truedigital.features.music.widget.favorite

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.common.share.componentv3.extension.showSnackBar
import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.foundation.extension.clickAsFlow
import com.truedigital.foundation.presentations.ViewModelFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class MusicFavoriteWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CompoundButton(context, attrs, defStyleAttr), LifecycleObserver {

    companion object {
        enum class ToastType {
            ADD,
            REMOVE,
            ERROR
        }
    }

    private var favId: Int = 0
    private var favType: FavoriteType = FavoriteType.UNKNOWN

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicFavoriteViewModel by lazy {
        viewModelFactory.create(MusicFavoriteViewModel::class.java)
    }

    init {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        inflateLayout()
    }

    fun updateState(id: Int, favoriteType: FavoriteType) {
        this.favId = id
        this.favType = favoriteType
        this.isChecked = false
        musicFavoriteViewModel.fetchFavorite(favId, favType)
    }

    private fun inflateLayout() {
        initialOnClick()
        observeViewModel()
    }

    fun updateFavoriteState(isFavorite: Boolean) {
        if (isFavorite != this@MusicFavoriteWidget.isChecked) {
            this@MusicFavoriteWidget.isChecked = isFavorite
        }
        this@MusicFavoriteWidget.isEnabled = true
    }

    private fun observeViewModel() {
        with(musicFavoriteViewModel) {
            onFavSong().observe(context as FragmentActivity) {
                updateFavoriteState(it)
            }
            onAddFavToast().observe(context as FragmentActivity) {
                setShowToast(ToastType.ADD)
            }
            onRemoveFavToast().observe(context as FragmentActivity) {
                setShowToast(ToastType.REMOVE)
            }
            onFavErrorToast().observe(context as FragmentActivity) {
                this@MusicFavoriteWidget.isEnabled = true
                setShowToast(ToastType.ERROR)
                this@MusicFavoriteWidget.isChecked = !this@MusicFavoriteWidget.isChecked
            }
        }
    }

    private fun initialOnClick() {
        this.clickAsFlow()
            .catch { Timber.e(it) }
            .debounce(com.truedigital.features.listens.share.constant.MusicConstant.DelayTime.DELAY_500)
            .map {
                Pair(favId, favType)
            }
            .filter { (id, type) ->
                id != 0 && type != FavoriteType.UNKNOWN
            }
            .onEach { (id, type) ->
                this.isEnabled = false
                musicFavoriteViewModel.toggleFavorite(id, type)
            }.launchIn(MainScope())
    }

    private fun setShowToast(toastType: ToastType) {
        val textResource = when (toastType) {
            ToastType.ADD -> R.string.added_to_favorite
            ToastType.REMOVE -> R.string.removed_to_favorite
            ToastType.ERROR -> if (this@MusicFavoriteWidget.isChecked) {
                R.string.error_added_to_favorite
            } else {
                R.string.error_removed_to_favorite
            }
        }
        val snackBarType = when (toastType) {
            ToastType.ADD,
            ToastType.REMOVE -> SnackBarType.SUCCESS
            ToastType.ERROR -> SnackBarType.ERROR
        }
        this.showSnackBar(textResource, snackBarType)
    }
}
