package com.truedigital.common.share.componentv3.widget.truepoint.presentation

import android.view.View
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.spy
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointCardStyleCacheRepositoryImpl
import com.truedigital.common.share.componentv3.widget.truepoint.domain.GetTruePointTitleUseCase
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.share.data.truepoint.domain.model.TrueCardModel
import com.truedigital.share.data.truepoint.domain.model.TrueCardType
import com.truedigital.share.data.truepoint.domain.model.TrueUserType
import com.truedigital.share.data.truepoint.domain.model.UserPointModel
import com.truedigital.share.data.truepoint.manager.UserPointInfoManager
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.reactivex.RxImmediateSchedulerExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.IOException

interface TruePointWidgetViewModelTestCase {
    fun `Given non login When loadData Then viewVisibility View Gone`()
    fun `Given is login hide title When loadData Then observe hideTitle`()
    fun `Given hideTitle false getTruePointTitleUseCase is null When getTitle Then observe hideTitle`()
    fun `Given hideTitle false getTruePointTitleUseCase isEmpty When getTitle Then observe hideTitle`()
    fun `Given hideTitle false getTruePointTitleUseCase isNotEmpty When getTitle Then observe showTitle`()
    fun `Given userPointInfoManager observeUserPointUpdated error When getUserPoint Then observe viewVisibility View Gone`()
    fun `Given userPointInfoManager observeUserPointUpdated is true user When getUserPoint Then observe viewVisibility renderPoint showCardIcon showBackgroundCard`()
    fun `Given userPointInfoManager observeUserPointUpdated is true user True card null point null When getUserPoint Then observe viewVisibility renderPoint hideCardIcon`()
    fun `Given userPointInfoManager observeUserPointUpdated is true user True card not match type When getUserPoint Then observe viewVisibility renderPoint hideCardIcon`()
    fun `Given userPointInfoManager observeUserPointUpdated non true user When getUserPoint Then observe viewVisibility View Gone`()
    fun `Given all typeCard When getTypeIcon Then all type is match`()
    fun `Given all typeCard When getTypeBackground Then all type is match`()
}

@ExtendWith(
    RxImmediateSchedulerExtension::class,
    InstantTaskExecutorExtension::class,
    TestCoroutinesExtension::class
)
class TruePointWidgetViewModelTest : TruePointWidgetViewModelTestCase {

    private val loginManagerInterface = mockk<LoginManagerInterface>()
    private val getTruePointTitleUseCase = mockk<GetTruePointTitleUseCase>()
    private val userPointInfoManager = mockk<UserPointInfoManager>()
    private val truePointCardStyleCacheRepository = spy(TruePointCardStyleCacheRepositoryImpl())
    private lateinit var viewModel: TruePointWidgetViewModel

    @BeforeEach
    fun setup() {
        viewModel = TruePointWidgetViewModel(
            loginManagerInterface = loginManagerInterface,
            getTruePointTitleUseCase = getTruePointTitleUseCase,
            userPointInfoManager = userPointInfoManager,
            truePointCardStyleCacheRepository = truePointCardStyleCacheRepository
        )
    }

    @Test
    override fun `Given non login When loadData Then viewVisibility View Gone`() {
        every { loginManagerInterface.isLoggedIn() } returns false

        viewModel.loadData(false)

        viewModel.viewVisibility.test()
            .assertValue(View.GONE)
    }

    @Test
    override fun `Given is login hide title When loadData Then observe hideTitle`() {
        every { loginManagerInterface.isLoggedIn() } returns true

        viewModel.loadData(true)

        viewModel.hideTitle.test()
            .assertHasValue()
    }

    @Test
    override fun `Given hideTitle false getTruePointTitleUseCase is null When getTitle Then observe hideTitle`() {
        coEvery { getTruePointTitleUseCase.execute() } returns null

        viewModel.getTitle(false)

        viewModel.hideTitle.test()
            .assertHasValue()
    }

    @Test
    override fun `Given hideTitle false getTruePointTitleUseCase isEmpty When getTitle Then observe hideTitle`() {
        coEvery { getTruePointTitleUseCase.execute() } returns ""

        viewModel.getTitle(false)

        viewModel.hideTitle.test()
            .assertHasValue()
    }

    @Test
    override fun `Given hideTitle false getTruePointTitleUseCase isNotEmpty When getTitle Then observe showTitle`() {
        coEvery { getTruePointTitleUseCase.execute() } returns "title"

        viewModel.getTitle(false)

        viewModel.showTitle.test()
            .assertValue("title")
    }

    @Test
    override fun `Given userPointInfoManager observeUserPointUpdated error When getUserPoint Then observe viewVisibility View Gone`() {
        every { userPointInfoManager.observeUserPointUpdated() } returns flow { throw IOException("Test") }

        viewModel.getUserPoint()

        viewModel.viewVisibility.test()
            .assertValue(View.GONE)
    }

    @Test
    override fun `Given userPointInfoManager observeUserPointUpdated is true user When getUserPoint Then observe viewVisibility renderPoint showCardIcon showBackgroundCard`() {
        val result = createUserPointModel()
        every { userPointInfoManager.observeUserPointUpdated() } returns flowOf(result)

        viewModel.getUserPoint()

        viewModel.viewVisibility.test()
            .assertValue(View.VISIBLE)
        viewModel.renderPoint.test()
            .assertValue(result.point)
        viewModel.showCardIcon.test()
            .assertValue(R.drawable.ic_upn_green_card)
        viewModel.showBackgroundCard.test()
            .assertValue(Pair(R.drawable.bg_card_green, result.trueCard?.type!!))
        viewModel.showTruePointCardStyle.test()
            .assertHasValue()
    }

    @Test
    override fun `Given userPointInfoManager observeUserPointUpdated is true user True card null point null When getUserPoint Then observe viewVisibility renderPoint hideCardIcon`() {
        val result = createUserPointModel().copy(
            point = "a",
            trueCard = createTrueCardModel()
        )
        every { userPointInfoManager.observeUserPointUpdated() } returns flowOf(result)

        viewModel.getUserPoint()

        viewModel.viewVisibility.test()
            .assertValue(View.VISIBLE)
        viewModel.renderPoint.test()
            .assertValue("0")
        viewModel.hideCardIcon.test()
            .assertHasValue()
    }

    @Test
    override fun `Given userPointInfoManager observeUserPointUpdated is true user True card not match type When getUserPoint Then observe viewVisibility renderPoint hideCardIcon`() {
        val result =
            createUserPointModel().copy(trueCard = createTrueCardModel().copy(type = "something"))
        every { userPointInfoManager.observeUserPointUpdated() } returns flowOf(result)

        viewModel.getUserPoint()

        viewModel.viewVisibility.test()
            .assertValue(View.VISIBLE)
        viewModel.renderPoint.test()
            .assertValue(result.point)
        viewModel.hideCardIcon.test()
            .assertHasValue()
    }

    @Test
    override fun `Given userPointInfoManager observeUserPointUpdated non true user When getUserPoint Then observe viewVisibility View Gone`() {
        val result = createUserPointModel().copy(userType = TrueUserType.NON_TRUE)
        every { userPointInfoManager.observeUserPointUpdated() } returns flowOf(result)

        viewModel.getUserPoint()

        viewModel.viewVisibility.test()
            .assertValue(View.GONE)
    }

    @Test
    override fun `Given all typeCard When getTypeIcon Then all type is match`() {
        val results = listOf(
            R.drawable.ic_upn_black_prive_card,
            R.drawable.ic_upn_black_card,
            R.drawable.ic_card_red,
            R.drawable.ic_upn_blue_card,
            R.drawable.ic_upn_green_card,
            R.drawable.ic_upn_white_card,
            null
        )
        val cardTypes = createTrueCardList()

        cardTypes.forEach { card ->
            results.contains(viewModel.getTypeIcon(card))
        }
    }

    @Test
    override fun `Given all typeCard When getTypeBackground Then all type is match`() {
        val results = listOf(
            R.drawable.bg_card_black_prive,
            R.drawable.bg_card_black,
            R.drawable.bg_card_red,
            R.drawable.bg_card_blue,
            R.drawable.bg_card_green,
            R.drawable.bg_card_white,
            null
        )

        val cardTypes = createTrueCardList()

        cardTypes.forEach { card ->
            results.contains(viewModel.getTypeBackground(card))
        }
    }

    private fun createUserPointModel() = UserPointModel(
        userType = TrueUserType.TRUE,
        point = "1",
        displayName = null,
        displayLastName = null,
        trueCard = TrueCardModel(
            status = null,
            type = TrueCardType.TYPE_GREEN,
            cardNo = null,
            mastercardNo = null,
            expirationDate = null
        )
    )

    private fun createTrueCardModel() = TrueCardModel(
        status = null,
        type = null,
        cardNo = null,
        mastercardNo = null,
        expirationDate = null
    )

    private fun createTrueCardList() = listOf(
        TrueCardType.TYPE_BLACK_PRIVE,
        TrueCardType.TYPE_BLACK,
        TrueCardType.TYPE_RED,
        TrueCardType.TYPE_BLUE,
        TrueCardType.TYPE_GREEN,
        TrueCardType.TYPE_WHITE,
        TrueCardType.TYPE_NONE_CARD
    )
}
