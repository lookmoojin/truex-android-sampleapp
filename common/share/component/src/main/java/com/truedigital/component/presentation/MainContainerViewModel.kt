package com.truedigital.component.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.foundation.extension.LiveEvent
import com.truedigital.foundation.extension.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainContainerViewModel @Inject constructor(val authManagerWrapper: AuthManagerWrapper) :
    ViewModel() {

    companion object {
        const val TAB_HOME_SLUG = "home"
    }

    val isShowBottomNavigation = MutableLiveData<Boolean>()
    val animateBottomNavigation = MutableLiveData<Float>()
    val deepLinkNavigation = MutableLiveData<String>()
    val switchBottomTab = MutableLiveData<String>()
    val isLoginSuccess = LiveEvent<Unit>()

    private var tabSlugActive: String? = null
    private val compositeDisposable = CompositeDisposable()

    fun showBottomNavigation(isShowBottomNavigation: Boolean) {
        this.isShowBottomNavigation.value = isShowBottomNavigation
    }

    fun animateBottomNavigation(progress: Float) {
        animateBottomNavigation.value = progress
    }

    fun deepLinkNavigation(urlDeepLink: String) {
        deepLinkNavigation.value = urlDeepLink
    }

    fun switchBottomTab(tapSlug: String) {
        switchBottomTab.value = tapSlug
    }

    fun isHomeActive(): Boolean {
        return tabSlugActive == TAB_HOME_SLUG
    }

    fun setTabSlugActive(slug: String) {
        tabSlugActive = slug
    }

    fun observeUserState() {
        authManagerWrapper.onFinishFlowLogin()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { isLogin ->
                    if (isLogin) {
                        isLoginSuccess.value = Unit
                    }
                },
                {}
            )
            .addTo(compositeDisposable)
    }
}
