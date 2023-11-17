package com.truedigital.features.truecloudv3.widget.decoration

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class FilesDecorationTest {
    private lateinit var filesDecoration: FilesDecoration
    private val context = mockk<Context>()
    private val resource = mockk<Resources>()
    private val UPLOAD_GRID_ITEM_TYPE = 2
    private val FOLDER_GRID_ITEM_TYPE = 6
    private val FILE_GRID_ITEM_TYPE = 8

    @Test
    fun `testGetItemOffsets UPLOAD_GRID_ITEM_TYPE OddNumber`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 2,
            folderItemSize = 2
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5
        val doubleMargin = 20
        val ZERO = 0

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 1
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns UPLOAD_GRID_ITEM_TYPE

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(doubleMargin, margin, margin, ZERO) }
    }

    @Test
    fun `testGetItemOffsets UPLOAD_GRID_ITEM_TYPE EvenNumber`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 2,
            folderItemSize = 2
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5
        val doubleMargin = 20
        val ZERO = 0

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns UPLOAD_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 2

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(margin, margin, doubleMargin, ZERO) }
    }

    @Test
    fun `testGetItemOffsets FOLDER_GRID_ITEM_TYPE EvenNumber`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 3,
            folderItemSize = 2
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5
        val doubleMargin = 20
        val ZERO = 0

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FOLDER_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 2

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(doubleMargin, margin, margin, margin) }
    }

    @Test
    fun `testGetItemOffsets FOLDER_GRID_ITEM_TYPE OddNumber`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 2,
            folderItemSize = 2
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5
        val doubleMargin = 20

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FOLDER_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 3

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(doubleMargin, margin, margin, margin) }
    }

    @Test
    fun `testGetItemOffsets FILE_GRID_ITEM_TYPE OddNumber`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 2,
            folderItemSize = 2
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FILE_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 3

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(5, 5, 20, 5) }
    }

    @Test
    fun `testGetItemOffsets FILE_GRID_ITEM_TYPE itemsize 0`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 0,
            folderItemSize = 2
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5
        val doubleMargin = 20

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FILE_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 3

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(doubleMargin, margin, margin, margin) }
    }

    @Test
    fun `testGetItemOffsets FILE_GRID_ITEM_TYPE upload and folder is 0`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 0,
            folderItemSize = 0
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FILE_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 3

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(5, 5, 20, 5) }
    }

    @Test
    fun `testGetItemOffsets FOLDER_GRID_ITEM_TYPE getItemPosition is 2`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 2,
            folderItemSize = 0
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5
        val doubleMargin = 20

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FOLDER_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 2

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(margin, margin, margin, margin) }
    }

    @Test
    fun `testGetItemOffsets FOLDER_GRID_ITEM_TYPE getItemPosition is 1`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 1,
            folderItemSize = 2
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FOLDER_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 3

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(margin, margin, margin, margin) }
    }

    @Test
    fun `testGetItemOffsets FOLDER_GRID_ITEM_TYPE folderItemSize is 3`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 0,
            folderItemSize = 3
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockState = mockk<RecyclerView.State>()
        val mockView = mockk<View>()
        val mockRect = mockk<Rect>()
        val margin = 5
        val doubleMargin = 20

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FOLDER_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 3

        filesDecoration.getItemOffsets(mockRect, mockView, mockRecyclerView, mockState)

        verify(exactly = 1) { mockRect.set(margin, margin, margin, margin) }
    }

    @Test
    fun `testGetItemOffsets checkItemSize`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 3,
            folderItemSize = 5
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockRect = mockk<Rect>()
        val margin = 5
        val doubleMargin = 20

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FOLDER_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 3

        filesDecoration.checkItemSize(mockRect, 3, 3)

        verify(exactly = 1) { mockRect.set(margin, margin, doubleMargin, margin) }
    }

    @Test
    fun `testGetItemOffsets FOLDER_GRID_ITEM_TYPE ODD NUMBER`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 3,
            folderItemSize = 5
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockRect = mockk<Rect>()
        val mockView = mockk<View>()
        val mockRecyclerViewState = mockk<RecyclerView.State>()
        val margin = 5

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRecyclerView.rootView } returns mockView
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FOLDER_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 2
        filesDecoration.getItemOffsets(
            mockRect,
            mockRecyclerView.rootView,
            mockRecyclerView,
            mockRecyclerViewState
        )

        verify(exactly = 1) { mockRect.set(20, 5, 5, 5) }
    }
    @Test
    fun `testGetItemOffsets FOLDER_GRID_ITEM_TYPE EVEN NUMBER`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 3,
            folderItemSize = 5
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockRect = mockk<Rect>()
        val mockView = mockk<View>()
        val mockRecyclerViewState = mockk<RecyclerView.State>()
        val margin = 5

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRecyclerView.rootView } returns mockView
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FOLDER_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 2
        filesDecoration.getItemOffsets(
            mockRect,
            mockRecyclerView.rootView,
            mockRecyclerView,
            mockRecyclerViewState
        )

        verify(exactly = 1) { mockRect.set(20, 5, 5, 5) }
    }

    @Test
    fun `testGetItemOffsets FILE_GRID_ITEM_TYPE ODD NUMBER`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 3,
            folderItemSize = 5
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockRect = mockk<Rect>()
        val mockView = mockk<View>()
        val mockRecyclerViewState = mockk<RecyclerView.State>()
        val margin = 5

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRecyclerView.rootView } returns mockView
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FILE_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 2
        filesDecoration.getItemOffsets(
            mockRect,
            mockRecyclerView.rootView,
            mockRecyclerView,
            mockRecyclerViewState
        )

        verify(exactly = 1) { mockRect.set(20, 5, 5, 5) }
    }
    @Test
    fun `testGetItemOffsets FILE_GRID_ITEM_TYPE EVEN NUMBER`() {
        every { context.resources } returns resource
        every { resource.getDimensionPixelSize(any()) } returns 5
        val filesDecoration = FilesDecoration(
            context = context,
            resId = 1,
            uploadItemSize = 3,
            folderItemSize = 5
        )
        val mockRecyclerView = mockk<RecyclerView>()
        val mockRect = mockk<Rect>()
        val mockView = mockk<View>()
        val mockRecyclerViewState = mockk<RecyclerView.State>()
        val margin = 5

        every { resource.getDimensionPixelSize(any()) } returns margin
        every { mockRecyclerView.rootView } returns mockView
        every { mockRect.set(any(), any(), any(), any()) } returns Unit
        every { mockRecyclerView.adapter?.getItemViewType(any()) } returns FILE_GRID_ITEM_TYPE
        every { mockRecyclerView.getChildAdapterPosition(any()) } returns 2
        filesDecoration.getItemOffsets(
            mockRect,
            mockRecyclerView.rootView,
            mockRecyclerView,
            mockRecyclerViewState
        )

        verify(exactly = 1) { mockRect.set(20, 5, 5, 5) }
    }
}
