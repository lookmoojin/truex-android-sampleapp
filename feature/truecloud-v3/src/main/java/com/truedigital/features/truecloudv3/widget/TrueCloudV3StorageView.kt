package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.TrueCloudv3WidgetStorageBinding
import com.truedigital.core.extensions.toDateFromUTC
import com.truedigital.features.truecloudv3.common.FileStorageType
import com.truedigital.features.truecloudv3.common.MigrationStatus
import com.truedigital.features.truecloudv3.domain.model.DataMigrationModel
import com.truedigital.features.truecloudv3.domain.model.DataStorageModel
import com.truedigital.features.truecloudv3.extension.formatBinarySize
import com.truedigital.features.truecloudv3.util.CalculateStorage.TOTAL_PERCENTAGE
import com.truedigital.features.truecloudv3.util.CalculateStorage.getStoragePercentage
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import java.util.Calendar
import java.util.concurrent.TimeUnit

class TrueCloudV3StorageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val WEIGHT_PERCENTAGE = 0.01
        private const val ZERO_LONG = 0L
        private const val ZERO = 0
        private const val ONE = 1
        private const val PROGRESS_PRIORITY_FIRST = 0
        private const val PROGRESS_PRIORITY_SECOND = 1
        private const val PROGRESS_PRIORITY_THIRD = 2
        private const val PROGRESS_PRIORITY_FOURTH = 3
        private const val PROGRESS_PRIORITY_FIFTH = 4
    }

    private var migrationStatus = ""
    private val binding: TrueCloudv3WidgetStorageBinding = TrueCloudv3WidgetStorageBinding.inflate(
        LayoutInflater.from(context),
        this,
        false
    )

    init {
        addView(binding.root)
    }

    fun setOnClickAutoBackup(onClick: () -> Unit) {
        binding.trueCloudAutoBackupTextView.onClick {
            onClick.invoke()
        }
    }

    fun setActiveBackup() {
        showAutoBackup()
        binding.trueCloudAutoBackupTextView.gone()
        binding.iconAutoBackupImageView.visible()
        binding.headerAutoBackupTextView.text =
            context.getString(R.string.true_cloudv3_backing_up_files)
        binding.subtitleAutoBackupTextView.text = context.getString(
            R.string.true_cloudv3_auto_backup_time_remaining_hour
        )
    }

    fun setFiledBackup() {
        binding.trueCloudAutoBackupTextView.gone()
        binding.iconAutoBackupImageView.setImageDrawable(
            ContextCompat.getDrawable(context, R.drawable.icon_true_cloudv3_auto_backup_error)
        )
        binding.trueCloudAutoBackupTextView.visible()
        binding.trueCloudAutoBackupTextView.text =
            context.getString(R.string.true_cloudv3_auto_backup_button)
        binding.iconAutoBackupImageView.visible()
        binding.headerAutoBackupTextView.setTextColor(ContextCompat.getColor(context, R.color.red))
        binding.headerAutoBackupTextView.text =
            context.getString(R.string.true_cloudv3_backing_up_failed)
        binding.subtitleAutoBackupTextView.text = context.getString(
            R.string.true_cloudv3_backing_up_tyr_again
        )
    }

    fun hideAutoBackup() {
        binding.containerAutoBackup.gone()
    }

    fun showAutoBackup() {
        binding.containerAutoBackup.visible()
    }

    fun setOnClickMigrate(onMigrate: () -> Unit) {
        binding.trueCloudMigrateTextView.onClick {
            onMigrate.invoke()
        }
        binding.containerMigrateData.onClick {
            if (MigrationStatus.MIGRATING.key.equals(migrationStatus)) {
                onMigrate.invoke()
            }
        }
    }

    fun hideMigrationFail() {
        binding.containerMigrateData.gone()
        binding.trueCloudMigrateTextView.gone()
    }

    fun setStorageUsageMostSecond(dataUsage: Pair<String, Long?>?, totalStorageCal: Long) {
        val typeStorage = dataUsage?.first
        val storageViewType = FileStorageType.getStorageType(typeStorage)
        binding.progressBarStorageUsageMostSecond.apply {
            val usedStorage = dataUsage?.second ?: ZERO_LONG
            val usedStoragePercentage = getStoragePercentage(usedStorage, totalStorageCal)
            val weight = (usedStoragePercentage * WEIGHT_PERCENTAGE).toFloat()

            layoutParams = LinearLayoutCompat.LayoutParams(
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_s),
                weight
            )
            max = usedStoragePercentage
            progress = usedStoragePercentage
            progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    storageViewType.typeColorRes
                )
            )
        }
        binding.trueCloudNoTwoTextView.apply {
            text = context.resources.getString(storageViewType.textRes)
            setCompoundDrawablesWithIntrinsicBounds(
                storageViewType.storageDrawableRes,
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none)
            )
        }
    }

    fun setStorageUsageMostThird(dataUsage: Pair<String, Long?>?, totalStorageCal: Long) {
        val typeStorage = dataUsage?.first
        val storageViewType = FileStorageType.getStorageType(typeStorage)
        binding.progressBarStorageUsageMostThird.apply {
            val usedStorage = dataUsage?.second ?: ZERO_LONG
            val usedStoragePercentage = getStoragePercentage(usedStorage, totalStorageCal)
            val weight = (usedStoragePercentage * WEIGHT_PERCENTAGE).toFloat()

            layoutParams = LinearLayoutCompat.LayoutParams(
                0,
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_s),
                weight
            )
            max = usedStoragePercentage
            progress = usedStoragePercentage
            progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    storageViewType.typeColorRes
                )
            )
        }
        binding.trueCloudNoThreeTextView.apply {
            text = context.resources.getString(storageViewType.textRes)
            setCompoundDrawablesWithIntrinsicBounds(
                storageViewType.storageDrawableRes,
                0,
                0,
                0
            )
        }
    }

    fun setStorageUsageMostFourth(dataUsage: Pair<String, Long?>?, totalStorageCal: Long) {
        val typeStorage = dataUsage?.first
        val storageViewType = FileStorageType.getStorageType(typeStorage)
        binding.progressBarStorageUsageMostFourth.apply {
            val usedStorage = dataUsage?.second ?: ZERO_LONG
            val usedStoragePercentage = getStoragePercentage(usedStorage, totalStorageCal)
            val weight = (usedStoragePercentage * WEIGHT_PERCENTAGE).toFloat()

            layoutParams = LinearLayoutCompat.LayoutParams(
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_s),
                weight
            )
            max = usedStoragePercentage
            progress = usedStoragePercentage
            progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    storageViewType.typeColorRes
                )
            )
        }
        binding.trueCloudNoFourTextView.apply {
            text = context.resources.getString(storageViewType.textRes)
            setCompoundDrawablesWithIntrinsicBounds(
                storageViewType.storageDrawableRes,
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none)
            )
        }
    }

    fun setStorageUsageMostFifth(dataUsage: Pair<String, Long?>?, totalStorageCal: Long) {
        val typeStorage = dataUsage?.first
        val storageViewType = FileStorageType.getStorageType(typeStorage)
        binding.progressBarStorageUsageMostFifth.apply {
            val usedStorage = dataUsage?.second ?: ZERO_LONG
            val usedStoragePercentage = getStoragePercentage(usedStorage, totalStorageCal)
            val weight = (usedStoragePercentage * WEIGHT_PERCENTAGE).toFloat()

            layoutParams = LinearLayoutCompat.LayoutParams(
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_s),
                weight
            )
            max = usedStoragePercentage
            progress = usedStoragePercentage
            progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    storageViewType.typeColorRes
                )
            )
        }
        binding.trueCloudNoFiveTextView.apply {
            text = context.resources.getString(storageViewType.textRes)
            setCompoundDrawablesWithIntrinsicBounds(
                storageViewType.storageDrawableRes,
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none)
            )
        }
    }

    private fun setProgressStorageFree(
        dataUsageList: MutableList<Pair<String, Long?>>?,
        totalStorageCal: Long
    ) {
        binding.progressBarStorageFree.apply {
            var used = ZERO
            dataUsageList?.forEach {
                val usedStorage = it.second ?: ZERO_LONG
                used += getStoragePercentage(usedStorage, totalStorageCal)
            }
            val weight = ((TOTAL_PERCENTAGE - used) * WEIGHT_PERCENTAGE).toFloat()
            layoutParams = LinearLayoutCompat.LayoutParams(
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_s),
                weight
            )
        }
    }

    fun setTrueCloudStorage(dataStorage: DataStorageModel) {
        dataStorage.let {
            it.migration?.let {
                setMigrationView(it)
            }

            val totalUsedStorage =
                it.dataUsage?.sortedObj?.sumOf { it.second ?: ZERO_LONG } ?: ZERO_LONG
            val totalStorageCal = it.quota
            val totalStorage = it.quota.formatBinarySize(context)
            binding.trueCloudUsedStorageTextView.text =
                context.getString(
                    R.string.true_cloudv3_storage_used,
                    totalUsedStorage.formatBinarySize(context)
                )
            binding.trueCloudTotalStorageTextView.text =
                context.getString(R.string.true_cloudv3_storage_total, totalStorage)

            setStorageUsageMost(
                it.dataUsage?.sortedObj?.get(PROGRESS_PRIORITY_FIRST),
                totalStorageCal
            )
            setStorageUsageMostSecond(
                it.dataUsage?.sortedObj?.get(PROGRESS_PRIORITY_SECOND),
                totalStorageCal
            )
            setStorageUsageMostThird(
                it.dataUsage?.sortedObj?.get(PROGRESS_PRIORITY_THIRD),
                totalStorageCal
            )
            setStorageUsageMostFourth(
                it.dataUsage?.sortedObj?.get(PROGRESS_PRIORITY_FOURTH),
                totalStorageCal
            )
            setStorageUsageMostFifth(
                it.dataUsage?.sortedObj?.get(PROGRESS_PRIORITY_FIFTH),
                totalStorageCal
            )

            setProgressStorageFree(it.dataUsage?.sortedObj, totalStorageCal)
        }
    }

    private fun setStorageUsageMost(dataUsage: Pair<String, Long?>?, totalStorageCal: Long) {
        val typeStorage = dataUsage?.first
        val storageViewType = FileStorageType.getStorageType(typeStorage)
        binding.progressBarStorageUsageMost.apply {
            val usedStorage = dataUsage?.second ?: ZERO_LONG
            val usedStoragePercentage = getStoragePercentage(usedStorage, totalStorageCal)
            val weight = (usedStoragePercentage * WEIGHT_PERCENTAGE).toFloat()
            layoutParams = LinearLayoutCompat.LayoutParams(
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_s),
                weight
            )
            max = usedStoragePercentage
            progress = usedStoragePercentage
            progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    storageViewType.typeColorRes
                )
            )
        }
        binding.trueCloudNoOneTextView.apply {
            text = context.resources.getString(storageViewType.textRes)
            setCompoundDrawablesWithIntrinsicBounds(
                storageViewType.storageDrawableRes,
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none),
                context.resources.getDimensionPixelOffset(R.dimen.true_cloudv3_spacing_none)
            )
        }
    }

    private fun setMigrationView(migration: DataMigrationModel) {
        migrationStatus = migration.status
        when (migration.status) {
            MigrationStatus.MIGRATING.key -> {
                binding.containerMigrateData.visible()
                val timestamp = migration.estimatedTimeToComplete.toDateFromUTC().time
                val now = Calendar.getInstance().time.time
                val remaining = (timestamp - now)
                val hour = TimeUnit.MILLISECONDS.toHours(remaining).toInt()
                var remainingMsg = ""
                if (hour >= ONE) {
                    remainingMsg = resources.getQuantityString(
                        R.plurals.migrate_remaining_hour,
                        hour,
                        hour
                    )
                } else {
                    var minute = TimeUnit.MILLISECONDS.toMinutes(remaining).toInt()
                    if (minute == ZERO) {
                        minute = ONE
                    }
                    remainingMsg = resources.getQuantityString(
                        R.plurals.migrate_remaining_minute,
                        minute,
                        minute
                    )
                }
                if (migration.estimatedTimeToComplete == null) {
                    remainingMsg =
                        resources.getString(R.string.true_cloudv3_migration_time_remaining_na)
                }
                binding.headerMigrateTextView.text =
                    context.getString(R.string.true_cloudv3_migrating_data)
                binding.subtitleMigrateionTextView.text = remainingMsg
            }

            MigrationStatus.FAILED.key -> {
                binding.containerMigrateData.visible()
                binding.trueCloudMigrateTextView.visible()
                binding.headerMigrateTextView.text =
                    context.getString(R.string.true_cloudv3_migration_you_migration_failed)
                binding.subtitleMigrateionTextView.text =
                    context.getString(R.string.true_cloudv3_migration_please_try_migrate_again)
                binding.headerMigrateTextView.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.true_cloudv3_color_migration_failed
                    )
                )
                binding.iconMigrateImageView.setImageResource(R.drawable.ic_migrate_error)
            }

            else -> {
                binding.containerMigrateData.gone()
            }
        }
    }
}
