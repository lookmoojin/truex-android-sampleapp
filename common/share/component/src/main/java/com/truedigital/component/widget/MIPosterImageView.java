package com.truedigital.component.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class MIPosterImageView extends AppCompatImageView {

    public MIPosterImageView(Context context) {
        super(context);
    }

    public MIPosterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MIPosterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = (int) (width * 1.48);
        setMeasuredDimension(width, height);
    }

}
