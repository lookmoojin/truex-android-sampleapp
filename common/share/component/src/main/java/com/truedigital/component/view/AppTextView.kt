package com.truedigital.component.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.truedigital.component.R
import com.truedigital.component.injections.TIDComponent
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.font.FontStyle
import com.truedigital.core.font.ZawgyiConverter
import java.util.Locale
import javax.inject.Inject

open class AppTextView : AppCompatTextView {

    @Inject
    lateinit var localizationRepository: LocalizationRepository

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        TIDComponent.getInstance().inject(this)

        var typeArray: TypedArray? = null
        val fontStyle = if (attrs != null) {
            typeArray = context.obtainStyledAttributes(attrs, R.styleable.AppTextView)
            FontStyle.values()[typeArray.getInt(R.styleable.AppTextView_textStyle, 0)]
        } else {
            FontStyle.Regular
        }
        try {
            typeface = Typeface.createFromAsset(context.assets, "fonts/${fontStyle.fontName}.ttf")
        } catch (exception: RuntimeException) {
        }
        typeArray?.recycle()

        if (localizationRepository.getAppLanguageCode().toLowerCase(Locale.ENGLISH)
            == LocalizationRepository.Localization.MY.languageCode
        ) {
            this.text = ZawgyiConverter.convert(this.text.toString())
        }
    }

    fun setFont(font: String) {
        try {
            typeface = Typeface.createFromAsset(context.assets, "fonts/$font.ttf")
        } catch (exception: RuntimeException) {
        }
    }
}
