package com.truedigital.component.widget.like.presentation

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.component.R
import com.truedigital.component.injections.TIDComponent
import com.truedigital.component.widget.like.data.model.response.isLiked
import com.truedigital.component.widget.like.domain.LikeUseCase
import com.truedigital.foundation.extension.addTo
import com.truedigital.foundation.extension.onClick
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LikeWidget : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    class LikeAnalyticEvent {
        var category: String = ""
        var action: String = ""
        var label: String = ""
        var value: Long = 0
    }

    @Inject
    lateinit var likeUseCase: LikeUseCase
    @Inject
    lateinit var authManagerWrapper: AuthManagerWrapper

    private var analyticScreenName: String? = null
    private var cmsId: String? = null
    private var isAutoLike: Boolean = false
    private var likeDisposable: Disposable? = null
    var analyticEvent: LikeAnalyticEvent? = null

    private val disposeBag by lazy { CompositeDisposable() }

    init {
        TIDComponent.getInstance().inject(this)
    }

    fun start(cmsId: String?) {
        if (this.cmsId == cmsId) {
            return
        }
        this.cmsId = cmsId
        init()
        getStateLike()
    }

    private fun init() {
        initView()
        if (!authManagerWrapper.isLoggedIn()) {
            observeAfterFinishFlowLogin()
        }
        trackAnalyticsScreenName()
    }

    private fun initView() {
        initProperty()

        onClick {
            if (!authManagerWrapper.isLoggedIn()) {
                openLoginScreen()
            } else {
                isSelected = !isSelected

                callLikeApi(isSelected)
            }
        }
    }

    private fun initProperty() {
        foreground = ContextCompat.getDrawable(context, R.drawable.selectable_background_ripper_circle)
        setImageResource(R.drawable.ic_like_selector)
        isSelected = false
    }

    private fun trackAnalyticsScreenName() {
        // TODO: Missing FA here!!!!
    }

    private fun trackAnalyticEvent(likeType: String) {
        // TODO: Missing FA here!!!!
    }

    private fun showLike(isLike: Boolean) {
        isSelected = isLike
    }

    private fun getStateLike() {
        if (!authManagerWrapper.isLoggedIn()) {
            showLike(isLike = false)
            return
        }

        cmsId?.let {
            likeUseCase.getStateLike(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        showLike(isLike = response?.isLiked() == true)
                    },
                    {
                        showLike(isLike = false)
                    }
                )
                .addTo(disposeBag)
        }
    }

    private fun openLoginScreen() {
        isAutoLike = true
        authManagerWrapper.login(
            object : AuthLoginListener() {
                override fun onLoginCancel() {
                    isAutoLike = false
                }
            }
        )
    }

    private fun callLikeApi(isLike: Boolean) {
        cmsId?.let { cmsId ->
            likeDisposable?.dispose()
            likeDisposable = Observable.interval(3, TimeUnit.SECONDS)
                .doOnNext {
                    val isLiked = if (isLike) LikeUseCase.LIKE_ACTION else LikeUseCase.UNLIKE_ACTION
                    val likeType = if (isLike) "like" else "unlike"

                    likeUseCase.postLike(cmsId, isLiked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .doFinally {
                            trackAnalyticEvent(likeType)
                            likeDisposable?.dispose()
                        }
                        .subscribe()
                }
                .subscribe()
        }
    }

    private fun observeAfterFinishFlowLogin() {
        authManagerWrapper.onFinishFlowLogin()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { isLoginFinish ->
                    if (isLoginFinish) {
                        if (isAutoLike) {
                            isSelected = true
                            callLikeApi(isSelected)
                        } else {
                            getStateLike()
                        }
                    }
                    isAutoLike = false
                },
                {
                    isAutoLike = false
                }
            )
            .addTo(disposeBag)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposeBag.clear()
    }
}
