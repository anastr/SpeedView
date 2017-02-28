package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.github.anastr.speedviewlib.base.LinearGauge;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class ImageLinearGauge extends LinearGauge {

    private Drawable image;

    private int backColor = Color.parseColor("#d6d7d7");

    public ImageLinearGauge(Context context) {
        this(context, null);
    }

    public ImageLinearGauge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageLinearGauge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        super.setSpeedTextPosition(Position.CENTER);
        super.setUnitUnderSpeedText(true);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageLinearGauge, 0, 0);

        backColor = a.getColor(R.styleable.ImageLinearGauge_sv_speedometerBackColor, backColor);
        image = a.getDrawable(R.styleable.ImageLinearGauge_sv_image);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (image == null
                || image.getIntrinsicWidth() == -1 || image.getIntrinsicHeight() == -1)
            return;
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (getOrientation() == Orientation.HORIZONTAL) {
            float image_w_to_h = (float) image.getIntrinsicWidth() / (float) image.getIntrinsicHeight();
            if (image_w_to_h > 1f)
                setMeasuredDimension(w, (int) (w / image_w_to_h));
            else
                setMeasuredDimension(w, (int) (w * image_w_to_h));
        }
        else {
            float image_h_to_w = (float) image.getIntrinsicHeight() / (float) image.getIntrinsicWidth();
            if (image_h_to_w > 1f)
                setMeasuredDimension((int) (h / image_h_to_w), h);
            else
                setMeasuredDimension((int) (h * image_h_to_w), h);
        }
    }

    @Override
    protected void updateFrontAndBackBitmaps() {
        Canvas canvasBack = createBackgroundBitmapCanvas();
        Canvas canvasFront = createForegroundBitmapCanvas();

        if (image != null) {
            image.setBounds(getPadding(), getPadding(), getWidth() - getPadding(), getHeight() - getPadding());
            image.setColorFilter(backColor, PorterDuff.Mode.SRC_IN);
            image.draw(canvasBack);

            image.setColorFilter(null);
            image.draw(canvasFront);
        }
    }
}
