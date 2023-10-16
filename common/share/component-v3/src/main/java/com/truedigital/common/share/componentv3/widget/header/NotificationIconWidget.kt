package com.truedigital.common.share.componentv3.widget.header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Event.EVENT_CLICK
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_EVENT_NAME
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_LINK_DESC
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_LINK_TYPE
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Notification.LinkDesc.LINK_DESC_HEADER_NOTIFICATION
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Notification.LinkType.LINK_TYPE_HEADER_NOTIFICATION
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ViewNotificationIconBinding
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.common.share.componentv3.widget.badge.presentation.CountInboxViewModel
import com.truedigital.common.share.consent.ConsentManager
import com.truedigital.common.share.consent.constant.ConsentConstant
import com.truedigital.common.share.consent.constant.listener.ConsentListener
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class NotificationIconWidget : FrameLayout, LifecycleObserver {

    private val binding: ViewNotificationIconBinding by lazy {
        ViewNotificationIconBinding.inflate(LayoutInflater.from(context), this, false)
    }

    @Inject
    lateinit var analyticManager: AnalyticManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val countInboxViewModel: CountInboxViewModel by lazy {
        viewModelFactory.create(CountInboxViewModel::class.java)
    }

    @Inject
    lateinit var authManagerWrapper: AuthManagerWrapper

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        ComponentV3Component.getInstance().inject(this)

        addView(binding.root)
        setTheme(false)
        observeViewModel()
    }

    fun setTheme(isDark: Boolean) {
        binding.notificationShapeableImageView.setImageResource(if (isDark) R.drawable.common_ic_notification_dark else R.drawable.common_ic_notification_light)
    }

    fun setOnClick(block: () -> Unit) {
        binding.notificationShapeableImageView.onClick {
            sentAnalyticsClickEvent()
            if (authManagerWrapper.isLoggedIn()) {
                checkConsentUserInbox(block)
            } else {
                authManagerWrapper.login(
                    object : AuthLoginListener() {
                        override fun onLoginSuccess() {
                            checkConsentUserInbox(block)
                        }
                    },
                    false
                )
            }
        }
    }

    private fun checkConsentUserInbox(block: () -> Unit) {
        (context as? LifecycleOwner)?.let { lifecycleOwner ->
            ConsentManager.getFragmentManager(context = context)?.let { fragmentManager ->
                ConsentManager.checkConsentsFunction(
                    enableWarningConsents = false,
                    fragmentManager = fragmentManager,
                    functionKeys = ConsentConstant.Function.FUNCTION_USER_INBOX,
                    consentListener = object : ConsentListener {
                        override fun onSuccess(
                            status: Int,
                            functionKeys: String,
                            checkAccepted: Boolean
                        ) {
                            block.invoke()
                        }

                        override fun onFailed(status: Int, functionKeys: String) {
                            block.invoke()
                        }

                        override fun onCancel(functionKeys: String) {
                            // do nothing
                        }
                    },
                    lifecycleOwner = lifecycleOwner
                )
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            countInboxViewModel.checkShowInboxMessage()
            countInboxViewModel.checkShowInboxMessageNotCallService()
        }
    }

    private fun observeViewModel() {
        with(countInboxViewModel) {
            showInboxMessage().observe(
                context as AppCompatActivity
            ) { isShowInboxMessage ->
                if (isShowInboxMessage) {
                    binding.notificationCountInboxTextView.visible()
                } else {
                    binding.notificationCountInboxTextView.gone()
                }
            }
            showInboxMessageNumber().observe(
                context as AppCompatActivity
            ) { count ->
                binding.notificationCountInboxTextView.text = count
                binding.notificationCountInboxTextView.setPaddingByDigit()
            }

            countInboxViewModel.triggerCountInbox().observe(
                context as AppCompatActivity
            ) {
                countInboxViewModel.checkShowInboxMessageNotCallService()
            }
        }
    }

    private fun sentAnalyticsClickEvent() {
        analyticManager.trackEvent(
            hashMapOf(
                KEY_EVENT_NAME to EVENT_CLICK,
                KEY_LINK_TYPE to LINK_TYPE_HEADER_NOTIFICATION,
                KEY_LINK_DESC to LINK_DESC_HEADER_NOTIFICATION
            )
        )
    }
}
