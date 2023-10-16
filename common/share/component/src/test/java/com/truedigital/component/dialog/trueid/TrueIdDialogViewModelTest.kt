package com.truedigital.component.dialog.trueid

import androidx.fragment.app.DialogFragment
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
internal class TrueIdDialogViewModelTest {

    private lateinit var trueIdDialogView: TrueIdDialogViewModel

    private val onDialogDisMissListener = object : DialogInterface.OnDismissListener {
        override fun onDismiss(dialog: DialogFragment) {
            dialog.dismiss()
        }
    }
    private val onDialogCancelListener = object : DialogInterface.OnCancelListener {
        override fun onCancel(dialog: DialogFragment) {
            dialog.dismiss()
        }
    }

    private val onClickDialogListener = object : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogFragment) {
            dialog.dismiss()
        }
    }

    @BeforeEach
    fun setUp() {
        trueIdDialogView = TrueIdDialogViewModel()
    }

    @Test
    fun `get onPrimaryClick should be null`() {
        assertNull(trueIdDialogView.onPrimaryClick)
    }

    @Test
    fun `set onPrimaryClick`() {
        trueIdDialogView.onPrimaryClick = onClickDialogListener

        assertNotNull(trueIdDialogView.onPrimaryClick)
    }

    @Test
    fun `get onSecondaryClick should be null`() {
        assertNull(trueIdDialogView.onSecondaryClick)
    }

    @Test
    fun `set onSecondaryClick`() {
        trueIdDialogView.onSecondaryClick = onClickDialogListener

        assertNotNull(trueIdDialogView.onSecondaryClick)
    }

    @Test
    fun `get onTertiaryClick should be null`() {
        assertNull(trueIdDialogView.onTertiaryClick)
    }

    @Test
    fun `set onTertiaryClick`() {
        trueIdDialogView.onSecondaryClick = onClickDialogListener
        assertNotNull(trueIdDialogView.onSecondaryClick)
    }

    @Test
    fun `get onBackButtonClick should be null`() {
        assertNull(trueIdDialogView.onBackButtonClick)
    }

    @Test
    fun `set onBackButtonClick`() {
        trueIdDialogView.onBackButtonClick = onDialogDisMissListener
        assertNotNull(trueIdDialogView.onBackButtonClick)
    }

    @Test
    fun `get onCloseButtonClick should be null`() {
        assertNull(trueIdDialogView.onCloseButtonClick)
    }

    @Test
    fun `set onCloseButtonClick`() {
        trueIdDialogView.onCloseButtonClick = onDialogDisMissListener
        assertNotNull(trueIdDialogView.onCloseButtonClick)
    }

    @Test
    fun `get onDismissListener should be null`() {
        assertNull(trueIdDialogView.onDismissListener)
    }

    @Test
    fun `set OnDismissListener`() {
        trueIdDialogView.onDismissListener = onDialogDisMissListener
        assertNotNull(trueIdDialogView.onDismissListener)
    }

    @Test
    fun `get onCancelListener should be null`() {
        assertNull(trueIdDialogView.onCancelListener)
    }

    @Test
    fun `set onCancelListener`() {
        trueIdDialogView.onCancelListener = onDialogCancelListener
        assertNotNull(trueIdDialogView.onCancelListener)
    }
}
