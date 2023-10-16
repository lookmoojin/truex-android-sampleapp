package com.truedigital.component.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.truedigital.component.R;

public class MIRatioImageView extends AppCompatImageView {

    public static final float RATIO_3_4 = 3f / 4f;
    public static final float RATIO_9_16 = 9f / 16f;
    public static final float RATIO_SQUARE = 1f;

    private double ratio = 3f / 4f;

    public MIRatioImageView(Context context) {
        super(context);
    }

    public MIRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRatio(context, attrs);
    }


    public MIRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initRatio(context, attrs);
    }

    private void initRatio(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MIRatioImageView);

        if (typedArray.hasValue(R.styleable.MIRatioImageView_ratio)) {
            float ratioType = typedArray.getInt(R.styleable.MIRatioImageView_ratio, 0);
            if (ratioType == 0) {
                this.ratio = RATIO_3_4;
            } else if (ratioType == 1) {
                this.ratio = RATIO_9_16;
            } else if (ratioType == 2) {
                this.ratio = RATIO_SQUARE;
            }
        }
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = (int) (width * ratio);
        setMeasuredDimension(width, height);
    }

}
