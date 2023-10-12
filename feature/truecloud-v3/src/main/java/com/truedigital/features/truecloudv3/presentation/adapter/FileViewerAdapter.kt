package com.truedigital.features.truecloudv3.presentation.adapter

import android.annotation.SuppressLint
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.presentation.AudioItemViewerFragment
import com.truedigital.features.truecloudv3.presentation.FileItemViewerFragment
import com.truedigital.features.truecloudv3.presentation.ImageItemViewerFragment
import com.truedigital.features.truecloudv3.presentation.VideoItemViewerFragment

class FileViewerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    companion object {
        private const val UNKNOWN_ITEM_TYPE = -1
        private const val IMAGE_ITEM_TYPE = 1
        private const val VIDEO_ITEM_TYPE = 2
        private const val AUDIO_ITEM_TYPE = 3
    }

    private var items = mutableListOf<TrueCloudFilesModel.File>()

    override fun createFragment(position: Int): Fragment {
        val fragment = when (getItemViewType(position)) {
            IMAGE_ITEM_TYPE -> ImageItemViewerFragment()
            VIDEO_ITEM_TYPE -> VideoItemViewerFragment()
            AUDIO_ITEM_TYPE -> AudioItemViewerFragment()
            else -> FileItemViewerFragment()
        }
        fragment.apply {
            arguments = bundleOf(KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW to items[position])
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].fileMimeType) {
            FileMimeType.IMAGE -> IMAGE_ITEM_TYPE
            FileMimeType.VIDEO -> VIDEO_ITEM_TYPE
            FileMimeType.AUDIO -> AUDIO_ITEM_TYPE
            else -> UNKNOWN_ITEM_TYPE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFiles(filesList: List<TrueCloudFilesModel.File>) {
        items.addAll(filesList)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun removeFiles(filesList: TrueCloudFilesModel.File) {
        items.remove(filesList)
        notifyItemRemoved(items.indexOf(filesList))
    }
}
