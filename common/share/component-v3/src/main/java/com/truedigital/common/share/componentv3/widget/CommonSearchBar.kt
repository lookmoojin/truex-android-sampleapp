package com.truedigital.common.share.componentv3.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.CommonSearchBarBinding
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.foundation.extension.clearKeyboardFocus
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CommonSearchBar : FrameLayout {

    interface OnSearchInteraction {
        fun onSearchAction(keyword: String)
        fun onSearchAutoSuggest(keyword: String)
        fun onEmptyTextSearchBar()
    }

    @Inject
    lateinit var analyticManager: AnalyticManager

    private var searchText = ""
    private var onSearchInteraction: OnSearchInteraction? = null
    private var searchDisposable: Disposable? = null
    private var intervalAutoComplete = 2000L

    var onClickAnimation: (() -> Unit)? = null
    var onClickSearch: (() -> Unit)? = null
    var onClickScan: (() -> Unit)? = null
    var onClickClear: (() -> Unit)? = null

    private val binding by lazy {
        CommonSearchBarBinding.inflate(LayoutInflater.from(context))
    }

    init {
        ComponentV3Component.getInstance().inject(this)
    }

    constructor(context: Context) : super(context) {
        initialView(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialView(attrs)
    }

    private fun initialView(attrs: AttributeSet?) {
        setupStyleable(attrs)
        setupView()
        setupListener()
    }

    private fun setupListener() {
        binding.animationView.onClick {
            onClickAnimation?.invoke()
        }
        binding.searchMaterialCardView.onClick {
            trackOnClickSearch()
            onClickSearch?.invoke()
        }
        binding.scanAppCompatImageView.onClick {
            onClickScan?.invoke()
        }
        binding.closeAppCompatImageView.onClick {
            onClickClear?.invoke()
        }
    }

    private fun setupStyleable(attrs: AttributeSet?) {
        attrs?.let { _attrs ->
            val typedArray =
                context?.obtainStyledAttributes(_attrs, R.styleable.CommonSearchBar)
            searchText = typedArray?.getString(
                R.styleable.CommonSearchBar_csb_title
            ).orEmpty()
            typedArray?.recycle()
        }
    }

    private fun setupView() {
        addView(binding.root)
        setHideClearButton()
        binding.searchTextView.text = searchText
        binding.editSearchTextView.setOnEditorActionListener { editTextView: TextView?, actionId: Int?, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                editTextView?.text?.let { keyword ->
                    if (keyword.isEmpty()) {
                        return@setOnEditorActionListener false
                    }
                    this.clearKeyboardFocus()
                    onSearchInteraction?.onSearchAction(keyword.toString())
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }
        binding.editSearchTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {

                val editableString = editable?.toString()
                if (editableString.isNullOrEmpty()
                ) {
                    onSearchInteraction?.onEmptyTextSearchBar()
                } else {
                    setShowClearTextButton()
                    searchAutoSuggest(editable.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
    }

    fun requestFocusAndShowKeyboard() {
        binding.editSearchTextView.requestFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.showSoftInput(
            binding.editSearchTextView,
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    fun setHideClearButton() {
        binding.closeAppCompatImageView.gone()
    }

    fun clearSearchText() {
        binding.editSearchTextView.setText("")
        binding.closeAppCompatImageView.invisible()
        searchDisposable?.dispose()
    }

    fun clearFocusAndHideKeyboard() {
        binding.editSearchTextView.clearKeyboardFocus()
    }

    fun getEditSearchTextView() = binding.editSearchTextView

    fun setSearchListener(listener: OnSearchInteraction) {
        onSearchInteraction = listener
    }

    fun setTitle(searchText: String) {
        this.searchText = searchText
        binding.searchTextView.text = searchText
    }

    fun setHintText(hintText: String) {
        binding.editSearchTextView.hint = hintText
        binding.editSearchTextView.visible()
        binding.searchTextView.gone()
    }

    fun setEditText(keyword: String) {
        binding.editSearchTextView.text = Editable.Factory.getInstance().newEditable(keyword)
        binding.editSearchTextView.visible()
        binding.searchTextView.gone()
    }

    fun setHideQRScan() {
        binding.scanAppCompatImageView.gone()
    }

    fun setShowClearTextButton() {
        binding.closeAppCompatImageView.visible()
    }

    fun setAnimationVisible() {
        binding.animationView.visible()
    }

    fun setSearchAnimationFile(adsUrl: String) {
        binding.animationView.setAnimationFromUrl(adsUrl)
    }

    fun setPlayAndHideAnimationAfterFinishedShowing() {
        val totalShowTime = 10000
        binding.animationView.playAnimation()
        binding.animationView.addAnimatorUpdateListener { valueAnimator ->
            val duration = (valueAnimator.duration).toInt()
            val repeatCount = totalShowTime / duration
            valueAnimator.repeatCount = repeatCount
            valueAnimator.doOnEnd { setHideAnimation() }
        }
    }

    fun searchAutoSuggest(txtKeyword: String) {
        val interval = intervalAutoComplete
        searchDisposable?.dispose()
        searchDisposable = Observable.interval(interval, TimeUnit.MILLISECONDS)
            .doOnNext {
                if (binding.editSearchTextView.text.toString().isNotEmpty()) {
                    onSearchInteraction?.onSearchAutoSuggest(txtKeyword)
                }
                searchDisposable?.dispose()
            }
            .subscribe()
    }

    fun setupEdittextAfterSearch() {
        binding.editSearchTextView.clearKeyboardFocus()
        binding.editSearchTextView.setSelection(binding.editSearchTextView.length())
        searchDisposable?.dispose()
    }

    fun setTimeInterval(time: Long) {
        this.intervalAutoComplete = time
    }

    fun getTextFromEditText(): String = binding.editSearchTextView.text.toString()

    private fun trackOnClickSearch() {
        analyticManager.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Music.Event.EVENT_SEARCH_BEGIN
            )
        )
    }

    private fun setHideAnimation() {
        binding.animationView.gone()
    }
}
