package com.truedigital.common.share.componentv3.widget.searchanimation.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.truedigital.common.share.componentv3.widget.searchanimation.model.SearchAnimationData
import com.truedigital.common.share.componentv3.widget.searchanimation.usecase.CheckDateSearchAnimationUseCase
import com.truedigital.common.share.componentv3.widget.searchanimation.usecase.GetSearchAnimationUseCase
import com.truedigital.foundation.extension.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchAnimationViewModel @Inject constructor(
    private val getSearchAnimationUseCase: GetSearchAnimationUseCase,
    private val checkDateSearchAnimationUseCase: CheckDateSearchAnimationUseCase
) : ViewModel() {

    private val showSearchAnimationData = MutableLiveData<SearchAnimationData>()
    private val compositeDisposable = CompositeDisposable()

    fun onShowSearchAnimationData(): LiveData<SearchAnimationData> = showSearchAnimationData

    fun getSearchAnimation(pageKey: String) {
        getSearchAnimationUseCase.execute(pageKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data ->
                if (data.adsUrl.isNotEmpty() &&
                    checkDateSearchAnimationUseCase.execute(data.searchAnimationTime)
                ) {
                    showSearchAnimationData.value = data
                }
            }
            .addTo(compositeDisposable)
    }
}
