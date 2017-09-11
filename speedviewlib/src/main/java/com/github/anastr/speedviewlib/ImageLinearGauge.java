package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class ImageLinearGauge extends LinearGauge {

    private Drawable image;

    private int backColor = 0xffd6d7d7;

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
    protected void defaultGaugeValues() {
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
        float imageW = (float) image.getIntrinsicWidth();
        float imageH = (float) image.getIntrinsicHeight();
        float view_w_to_h = w / h;
        float image_w_to_h = imageW / imageH;

        if (image_w_to_h > view_w_to_h)
            setMeasuredDimension(w, (int) (w * imageH / imageW));
        else
            setMeasuredDimension((int)(h * imageW / imageH), h);
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
