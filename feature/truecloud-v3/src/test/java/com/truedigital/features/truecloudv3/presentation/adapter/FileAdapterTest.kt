package com.truedigital.features.truecloudv3.presentation.adapter

import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FileAdapterTest {
    lateinit var adapter: FilesAdapter
    private lateinit var item: MutableList<TrueCloudFilesModel>
    private lateinit var folderListData: MutableList<TrueCloudFilesModel.Folder>

    @BeforeEach
    fun setUp() {
        // Set up the adapter and test data
        val file1 = TrueCloudFilesModel.File(
            id = "1",
            name = "namefile1"
        )
        val file2 = TrueCloudFilesModel.File(
            id = "2"
        )
        val file3 = TrueCloudFilesModel.File(
            id = "3"
        )
        val folder1 = TrueCloudFilesModel.Folder(
            id = "11"
        )
        val folder2 = TrueCloudFilesModel.Folder(
            id = "22"
        )
        val folder3 = TrueCloudFilesModel.Folder(
            id = "33"
        )
        item = mutableListOf(file1, file2, file3)
        folderListData = mutableListOf(folder1, folder2, folder3)
        item.addAll(folderListData)
        adapter = FilesAdapter(object : FilesAdapter.OnActionClickListener {
            override fun onPauseClicked(model: TrueCloudFilesModel.Upload) {
            }

            override fun onRetryClicked(model: TrueCloudFilesModel.Upload) {
            }

            override fun onCancelClicked(model: TrueCloudFilesModel.Upload) {
            }

            override fun onFileClicked(model: TrueCloudFilesModel.File) {
            }

            override fun onMoreClicked(model: TrueCloudFilesModel.File) {
            }

            override fun onLongClicked(model: TrueCloudFilesModel.File) {
            }

            override fun onCancelAllClicked() {
            }

            override fun onUploadExpandClicked(status: Boolean) {
            }

            override fun onFolderClicked(model: TrueCloudFilesModel.Folder) {
            }

            override fun onFolderMoreClicked(model: TrueCloudFilesModel.Folder) {
            }

            override fun onAutoBackupExpandClicked(status: Boolean) {
            }

            override fun onCancelAllBackupClicked() {
            }

            override fun onPauseAllBackupClicked() {
            }

            override fun onResumeAllBackupClicked() {
            }
        })
        adapter.refreshItemList(item)
    }

    @Test
    fun testGetItemCount() {
        val response = adapter.getItemCount()
        adapter.addFiles(folderListData)
        // Verify that the adapter returns the correct number of items
        assertEquals(6, response)
    }
}
