package com.truedigital.common.share.componentv3.widget.truepoint.presentation

import android.view.View
import androidx.annotation.VisibleForTesting
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointCardStyleCacheRepository
import com.truedigital.common.share.componentv3.widget.truepoint.data.model.TruePointCardStyle
import com.truedigital.common.share.componentv3.widget.truepoint.domain.GetTruePointTitleUseCase
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.convertToCurrencyFormat
import com.truedigital.core.extensions.launchSafe
import com.truedigital.foundation.extension.SingleLiveEvent
import com.truedigital.share.data.truepoint.domain.model.TrueCardType
import com.truedigital.share.data.truepoint.domain.model.TrueUserType
import com.truedigital.share.data.truepoint.manager.UserPointInfoManager
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class TruePointWidgetViewModel @Inject constructor(
    private val loginManagerInterface: LoginManagerInterface,
    private val getTruePointTitleUseCase: GetTruePointTitleUseCase,
    private val userPointInfoManager: UserPointInfoManager,
    private val truePointCardStyleCacheRepository: TruePointCardStyleCacheRepository
) : ScopedViewModel() {

    val viewVisibility = SingleLiveEvent<Int>()
    val showTitle = SingleLiveEvent<String>()
    val hideTitle = SingleLiveEvent<Unit>()
    val renderPoint = SingleLiveEvent<String>()
    val showCardIcon = SingleLiveEvent<Int>()
    val hideCardIcon = SingleLiveEvent<Unit>()
    val showBackgroundCard = SingleLiveEvent<Pair<Int, String>>()
    val showTruePointCardStyle = SingleLiveEvent<TruePointCardStyle>()

    fun loadData(hideTitle: Boolean) = launchSafe {
        if (loginManagerInterface.isLoggedIn()) {
            viewVisibility.value = View.VISIBLE
            getTitle(hideTitle)
            getUserPoint()
        } else {
            viewVisibility.value = View.GONE
        }
    }

    @VisibleForTesting
    fun getTitle(_hideTitle: Boolean) = launchSafe {
        if (_hideTitle) {
            hideTitle.value = Unit
        } else {
            getTruePointTitleUseCase.execute()
                ?.takeIf { it.isNotEmpty() }
                ?.also { title ->
                    showTitle.value = title
                }
                ?: run {
                    hideTitle.value = Unit
                }
        }
    }

    @VisibleForTesting
    fun getUserPoint() = launchSafe {
        userPointInfoManager.observeUserPointUpdated()
            .catch {
                viewVisibility.value = View.GONE
            }
            .collectSafe { userInfo ->
                when (userInfo.userType) {
                    TrueUserType.TRUE -> {
                        viewVisibility.value = View.VISIBLE
                        renderPoint.value = userInfo.point.toIntOrNull()?.convertToCurrencyFormat() ?: "0"

                        userInfo.trueCard?.type?.let { _type ->
                            getTypeIcon(_type)?.let { icon ->
                                showCardIcon.value = icon
                            } ?: run {
                                hideCardIcon.value = Unit
                            }

                            getTypeBackground(_type)?.let { icon ->
                                showBackgroundCard.value = Pair(icon, _type)
                            } ?: run {
                                hideCardIcon.value = Unit
                            }
                        } ?: run {
                            hideCardIcon.value = Unit
                        }
                        mapTruePointCardStyle(userInfo.trueCard?.type ?: TrueCardType.TYPE_NONE_CARD)
                    }
                    else -> {
                        viewVisibility.value = View.GONE
                    }
                }
            }
    }

    @VisibleForTesting
    fun getTypeIcon(trueCard: String): Int? {
        return when (trueCard) {
            TrueCardType.TYPE_BLACK_PRIVE -> R.drawable.ic_upn_black_prive_card
            TrueCardType.TYPE_BLACK -> R.drawable.ic_upn_black_card
            TrueCardType.TYPE_RED -> R.drawable.ic_card_red
            TrueCardType.TYPE_BLUE -> R.drawable.ic_upn_blue_card
            TrueCardType.TYPE_GREEN -> R.drawable.ic_upn_green_card
            TrueCardType.TYPE_WHITE -> R.drawable.ic_upn_white_card
            else -> null
        }
    }

    @VisibleForTesting
    fun getTypeBackground(trueCard: String): Int? {
        return when (trueCard) {
            TrueCardType.TYPE_BLACK_PRIVE -> R.drawable.bg_card_black_prive
            TrueCardType.TYPE_BLACK -> R.drawable.bg_card_black
            TrueCardType.TYPE_RED -> R.drawable.bg_card_red
            TrueCardType.TYPE_BLUE -> R.drawable.bg_card_blue
            TrueCardType.TYPE_GREEN -> R.drawable.bg_card_green
            TrueCardType.TYPE_WHITE -> R.drawable.bg_card_white
            else -> null
        }
    }

    @VisibleForTesting
    fun mapTruePointCardStyle(
        cardType: String,
    ) {
        val style = TruePointCardStyle()
        when (cardType) {
            TrueCardType.TYPE_GREEN, TrueCardType.TYPE_BLUE, TrueCardType.TYPE_WHITE, TrueCardType.TYPE_NONE_CARD -> {
                style.apply {
                    textColor = R.color.black
                    buttonSeeLessStyle = R.drawable.common_stroke_black_25dp
                }
            }
            else -> {
                style.apply {
                    textColor = R.color.white
                    buttonSeeLessStyle = R.drawable.common_stroke_white_25dp
                }
            }
        }
        showTruePointCardStyle.value = style
        truePointCardStyleCacheRepository.saveCacheCardStyle(style)
    }
}
