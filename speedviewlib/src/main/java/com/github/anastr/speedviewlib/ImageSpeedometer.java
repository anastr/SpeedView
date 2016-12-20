package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class ImageSpeedometer extends Speedometer {

    private Drawable imageSpeedometer;

    public ImageSpeedometer(Context context) {
        this(context, null);
    }

    public ImageSpeedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageSpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        super.setBackgroundCircleColor(Color.TRANSPARENT);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageSpeedometer, 0, 0);

        imageSpeedometer = a.getDrawable(R.styleable.ImageSpeedometer_imageSpeedometer);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateBackgroundBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawIndicator(canvas);

        float speedTextPadding = dpTOpx(1);
        if (isSpeedometerTextRightToLeft()) {
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            speedTextPadding *= -1;
        }
        else
            speedTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(getSpeedText()
                , getWidth()/2f - speedTextPadding, getHeightPa()*.9f + getPadding(), speedTextPaint);

        drawNotes(canvas);
    }

    @Override
    protected Bitmap updateBackgroundBitmap() {
        if (getWidth() == 0 || getHeight() == 0)
            return null;
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(backgroundBitmap);
        c.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f - getPadding(), circleBackPaint);

        if (imageSpeedometer != null) {
            imageSpeedometer.setBounds(getPadding(), getPadding()
                    , getWidth() - getPadding(), getHeight() - getPadding());
            imageSpeedometer.draw(c);
        }

        float unitTextPadding = dpTOpx(1);
        if (isSpeedometerTextRightToLeft()) {
            unitTextPaint.setTextAlign(Paint.Align.RIGHT);
            unitTextPadding *= -1;
        }
        else
            unitTextPaint.setTextAlign(Paint.Align.LEFT);

        c.drawText(getUnit()
                , getWidth()/2f + unitTextPadding, getHeightPa()*.9f + getPadding(), unitTextPaint);

        return backgroundBitmap;
    }

    public Drawable getImageSpeedometer() {
        return imageSpeedometer;
    }

    /**
     * set background speedometer image, Preferably be square.
     * @param imageResource image id.
     * @see #setImageSpeedometer(Drawable)
     */
    public void setImageSpeedometer (int imageResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setImageSpeedometer(getContext().getDrawable(imageResource));
        else
            setImageSpeedometer(getContext().getResources().getDrawable(imageResource));
    }

    /**
     * set background speedometer image, Preferably be square.
     * @param imageSpeedometer image drawable.
     * @see #setImageSpeedometer(int)
     */
    public void setImageSpeedometer(Drawable imageSpeedometer) {
        this.imageSpeedometer = imageSpeedometer;
        updateBackgroundBitmap();
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getLowSpeedColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param lowSpeedColor nothing.
     */
    @Deprecated
    @Override
    public void setLowSpeedColor(int lowSpeedColor) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getMediumSpeedColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param mediumSpeedColor nothing.
     */
    @Deprecated
    @Override
    public void setMediumSpeedColor(int mediumSpeedColor) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getHighSpeedColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param highSpeedColor nothing.
     */
    @Deprecated
    @Override
    public void setHighSpeedColor(int highSpeedColor) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code 0} always.
     */
    @Deprecated
    @Override
    public int getLowSpeedPercent() {
        return 0;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param lowSpeedPercent nothing.
     */
    @Deprecated
    @Override
    public void setLowSpeedPercent(int lowSpeedPercent) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code 0} always.
     */
    @Deprecated
    @Override
    public int getMediumSpeedPercent() {
        return 0;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param mediumSpeedPercent nothing.
     */
    @Deprecated
    @Override
    public void setMediumSpeedPercent(int mediumSpeedPercent) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code 0} always.
     */
    @Override
    public float getTextSize() {
        return 0;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param textSize nothing.
     */
    @Override
    public void setTextSize(float textSize) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code 0} always.
     */
    @Override
    public int getTextColor() {
        return 0;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param textColor nothing.
     */
    @Override
    public void setTextColor(int textColor) {
    }
}
