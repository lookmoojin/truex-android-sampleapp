package com.truedigital.features.tuned.presentation.station.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.google.android.material.appbar.AppBarLayout
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.extensions.withAlpha
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.Constants.ALPHA_255F
import com.truedigital.features.tuned.common.Constants.FLOAT_16F
import com.truedigital.features.tuned.common.Constants.FLOAT_24F
import com.truedigital.features.tuned.common.Constants.FLOAT_8F
import com.truedigital.features.tuned.common.extensions.actionBarHeight
import com.truedigital.features.tuned.common.extensions.alert
import com.truedigital.features.tuned.common.extensions.dp
import com.truedigital.features.tuned.common.extensions.put
import com.truedigital.features.tuned.common.extensions.share
import com.truedigital.features.tuned.common.extensions.statusBarHeight
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.databinding.ActivityStationBinding
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerCollectionStatus
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.ViewPagerAdapter
import com.truedigital.features.tuned.presentation.components.LifecycleComponentActivity
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.components.ViewPagerComponent
import com.truedigital.features.tuned.presentation.popups.view.FullScreenImageDialog
import com.truedigital.features.tuned.presentation.popups.view.LoadingDialog
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.features.tuned.presentation.station.presenter.StationOverviewPresenter
import com.truedigital.features.tuned.presentation.station.presenter.StationPresenter
import com.truedigital.features.tuned.presentation.station.presenter.StationPresenter.Companion.STATION_ID_KEY
import com.truedigital.features.tuned.presentation.station.presenter.StationPresenter.Companion.STATION_KEY
import com.truedigital.features.tuned.presentation.station.presenter.TuningPresenter
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import javax.inject.Inject
import kotlin.math.abs

class StationActivity :
    LifecycleComponentActivity(),
    StationPresenter.ViewSurface,
    StationPresenter.RouterSurface {

    @Inject
    lateinit var presenter: StationPresenter

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var config: Configuration

    private val binding: ActivityStationBinding by viewBinding(ActivityStationBinding::inflate)

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private var fadeDistance: Int = 0
    private var scrimColor = Color.TRANSPARENT

    private var warningDialog: AlertDialog? = null

    private var isFavourited: Boolean? = null
    private var stationBottomSheet: BottomSheetProductPicker? = null

    private var favMenuItem: MenuItem? = null
    private var homeMenuItem: MenuItem? = null
    private var shouldShowFavMenu: Boolean = false
    private var shouldShowHomeMenu: Boolean = false

    // region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.colorFilter =
            PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

        scrimColor = ContextCompat.getColor(this, R.color.primary)
        fadeDistance = resources.getDimensionPixelSize(R.dimen.header_scrim_fade_distance)

        val artContainerLp = binding.stationArtContainer.layoutParams as FrameLayout.LayoutParams
        artContainerLp.topMargin = actionBarHeight + statusBarHeight
        artContainerLp.bottomMargin = resources.dp(FLOAT_24F)

        var previousOffset = 0
        binding.stationAppBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (verticalOffset != previousOffset) {
                    var ratio =
                        (abs(verticalOffset) - fadeDistance) /
                            (appBarLayout.totalScrollRange - fadeDistance).toFloat()
                    ratio = minOf(ratio, 1f)
                    ratio = maxOf(ratio, 0f)
                    val color = if (Math.abs(verticalOffset) > fadeDistance) {
                        Color.argb(
                            (ratio * ALPHA_255F).toInt(),
                            Color.red(scrimColor),
                            Color.green(scrimColor),
                            Color.blue(scrimColor)
                        )
                    } else {
                        Color.TRANSPARENT
                    }

                    val drawable = ColorDrawable(color)
                    binding.stationArt.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    binding.stationCollapsingToolbar.background = drawable

                    binding.viewOverlay.alpha = 1f - ratio
                    binding.stationArtOverlay.alpha = 1f - ratio
                    if (config.enableShareAndFavIcon) {
                        binding.ivShare.alpha = 1 - ratio
                        binding.ivStar.alpha = 1 - ratio
                    }
                    binding.layoutInfo.alpha = 1 - ratio
                    binding.toolbar.setTitleTextColor(Color.WHITE.withAlpha((ratio * ALPHA_255F).toInt()))
                }
                previousOffset = verticalOffset
            }
        )

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewpager.adapter = viewPagerAdapter
        binding.stationTabs.setupWithViewPager(binding.viewpager)
        viewPagerAdapter.onPageLoadedListener = { view ->
            if (view is StationOverviewView) {
                view.binding.buttonMore.onClick { presenter.onShowMoreOptions() }
            }
        }

        binding.tabUnderline.apply {
            if (config.enableTabUnderline) {
                this.visible()
            } else {
                this.gone()
            }
        }

        presenter.onInject(this, this)

        binding.ivStar.onClick { presenter.onToggleFavourite() }
        binding.stationArt.onClick { presenter.onImageSelected() }

        lifecycleComponents.add(PresenterComponent(presenter))
        lifecycleComponents.add(ViewPagerComponent(viewPagerAdapter, binding.stationTabs))
    }

    override fun onPause() {
        super.onPause()
        LoadingDialog.dismiss()
        warningDialog?.dismiss()
        stationBottomSheet?.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fav_home, menu)
        favMenuItem = menu.findItem(R.id.action_fav)
        if (shouldShowFavMenu) favMenuItem?.isVisible = true
        if (shouldShowHomeMenu) homeMenuItem?.isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ActivityCompat.finishAfterTransition(this)
            R.id.action_fav -> presenter.onToggleFavourite()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun finish() {
        if (isFavourited == false) {
            val station = intent.getParcelableExtra<Station>(STATION_KEY)
            val stationId = station?.id ?: intent.getIntExtra(STATION_ID_KEY, -1)
            val intent = Intent()
            intent.putExtra(ProductListActivity.UPDATED_PRODUCT_ID_KEY, stationId)
            setResult(Activity.RESULT_OK, intent)
        }
        super.finish()
    }

    // endregion

    // region ViewSurface

    override fun initStation(station: Station) {
        LoadingDialog.dismiss()

        supportActionBar?.title = station.name.valueForSystemLanguage(this)
        binding.textViewStationName.text = station.name.valueForSystemLanguage(this)
        binding.toolbar.setTitleTextColor(Color.TRANSPARENT)

        when (station.type) {
            Station.StationType.PRESET -> {
                binding.stationArtOverlay.foreground = null
                binding.tvStationType.text = getString(R.string.station_description)
            }

            Station.StationType.SINGLE_ARTIST -> {
                binding.stationArtOverlay.foreground =
                    ContextCompat.getDrawable(this, R.drawable.overlay_artist_shuffle)
                binding.tvStationType.text = getString(R.string.station_artist_shuffle_description)
            }

            Station.StationType.ARTIST -> {
                binding.stationArtOverlay.foreground =
                    ContextCompat.getDrawable(this, R.drawable.overlay_artist_and_similar)
                binding.tvStationType.text = getString(R.string.station_artist_description)
            }

            Station.StationType.USER -> {
                binding.stationArtOverlay.foreground = null
                binding.tvStationType.text = getString(R.string.station_user_description)
            }

            else -> {
                binding.stationArtOverlay.foreground = null
                binding.tvStationType.text = getString(R.string.station_description)
            }
        }

        station.coverImage.valueForSystemLanguage(this)?.let {
            val backgroundSize =
                resources.getDimensionPixelSize(R.dimen.header_background_image_size)
            imageManager.init(this)
                .load(it)
                .options(backgroundSize, filters = arrayOf("blur(50, 10)", "saturation(1.2)"))
                .intoBitmap {
                    scrimColor = Palette.Builder(it).generate().getDarkMutedColor(scrimColor)
                    binding.stationAppBar.background = BitmapDrawable(resources, it)
                }

            val stationArtSize = resources.getDimensionPixelSize(R.dimen.header_art_size)
            imageManager.init(this)
                .load(it)
                .options(stationArtSize)
                .intoRoundedCorner(binding.stationArt)
        }
    }

    override fun showOnlineStation(station: Station, trackHash: String?, autoPlay: Boolean) {
        if (!config.enableFavourites) {
            binding.stationTabs.gone()
            val lp = binding.stationArtContainer.layoutParams as FrameLayout.LayoutParams
            lp.bottomMargin =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) resources.dp(-FLOAT_8F) else resources.dp(
                    FLOAT_16F
                )
            binding.stationArtContainer.layoutParams = lp
            val toolbarLp = binding.toolbar.layoutParams as FrameLayout.LayoutParams
            toolbarLp.bottomMargin = 0
            binding.toolbar.layoutParams = toolbarLp

            val overviewBundle = Bundle().put(StationOverviewPresenter.STATION_KEY to station)
            viewPagerAdapter.items = listOf(
                ViewPagerAdapter.Page(
                    getString(R.string.overview_title),
                    StationOverviewView::class,
                    overviewBundle
                )
            )
        } else {
            val overviewBundle = Bundle().put(
                StationOverviewPresenter.STATION_KEY to station,
                StationOverviewPresenter.TRACK_HASH_KEY to trackHash,
                StationOverviewPresenter.AUTO_PLAY_KEY to autoPlay
            )
            val tuningBundle = Bundle().put(TuningPresenter.STATION_KEY to station)
            viewPagerAdapter.items =
                if (config.enableFavourites)
                    listOf(
                        ViewPagerAdapter.Page(
                            getString(R.string.overview_title),
                            StationOverviewView::class,
                            overviewBundle
                        ),
                        ViewPagerAdapter.Page(
                            getString(R.string.tuning_title),
                            TuningView::class,
                            tuningBundle
                        )
                    )
                else
                    listOf(
                        ViewPagerAdapter.Page(
                            getString(R.string.overview_title),
                            StationOverviewView::class,
                            overviewBundle
                        )
                    )
        }

        if (config.enableShareAndFavIcon && config.enableShare) binding.ivShare.visible()
        if (config.enableShareAndFavIcon && config.enableFavourites) binding.ivStar.visible()

        // showOnline could be called before onCreateOptions Menu, set the flag to make sure it will be visible
        shouldShowFavMenu = !config.enableShareAndFavIcon && config.enableFavourites
        shouldShowHomeMenu = isNestedActivity()
        if (shouldShowFavMenu) favMenuItem?.isVisible = true
        if (shouldShowHomeMenu) homeMenuItem?.isVisible = true
    }

    override fun showLoading() {
        LoadingDialog.show(this)
    }

    override fun showLoadStationError() {
        LoadingDialog.dismiss()
        warningDialog = alert {
            setMessage(R.string.load_station_error)
            setPositiveButton(R.string.dialog_ok) { _, _ ->
                ActivityCompat.finishAfterTransition(
                    this@StationActivity
                )
            }
            setCancelable(false)
        }
    }

    override fun showFavourited(favourited: Boolean) {
        isFavourited = favourited
        stationBottomSheet?.updateCollectionStatus(getCollectionStatus())
        binding.ivStar.setImageResource(
            if (favourited) {
                R.drawable.music_ic_star_ticked
            } else {
                R.drawable.music_ic_star_empty
            }
        )
        favMenuItem?.setIcon(
            if (favourited) {
                R.drawable.music_ic_star_ticked
            } else {
                R.drawable.music_ic_star_empty
            }
        )
    }

    override fun showFavouritedError() {
        toast(R.string.collection_error_message)
    }

    override fun showFavouritedToast() {
        toast(R.string.starred_station_added_message)
    }

    override fun showMoreOptions(station: Station) {
        stationBottomSheet = BottomSheetProductPicker(this) {
            itemType = ProductPickerType.MIX
            product = station
            isInCollectionStatus = getCollectionStatus()
            onOptionSelected = { this@StationActivity.presenter.onMoreOptionSelected(it) }
        }
        stationBottomSheet?.show()
    }

    override fun showVotesCleared() {
        toast(R.string.clear_votes_success)

        val tuningView = viewPagerAdapter.getViews().first { it is TuningView } as TuningView
        tuningView.onClearVotes()
    }

    override fun showClearVotesError() {
        toast(R.string.clear_votes_error)
    }

    override fun showClearVotesEmpty() {
        toast(R.string.clear_votes_empty)
    }

    override fun showEnlargedImage(images: List<LocalisedString>?) {
        images?.valueForSystemLanguage(this)?.let {
            FullScreenImageDialog(this, it).show()
        }
    }

    // region RouterSurface

    override fun shareStation(station: Station, link: String) {
        LoadingDialog.dismiss()
        share(
            getString(R.string.share_station_title, station.name.valueForSystemLanguage(this)),
            link
        )
    }

    // call back from stationOverviewView

    private fun getCollectionStatus(): ProductPickerCollectionStatus =
        when (isFavourited) {
            true -> ProductPickerCollectionStatus.IN_COLLECTION
            false -> ProductPickerCollectionStatus.NOT_IN_COLLECTION
            else -> ProductPickerCollectionStatus.PENDING_UPDATE
        }
}
