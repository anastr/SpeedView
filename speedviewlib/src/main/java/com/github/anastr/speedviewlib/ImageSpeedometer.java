package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
    protected void defaultSpeedometerValues() {
        setBackgroundCircleColor(0);
    }

    @Override
    protected void defaultGaugeValues() {
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageSpeedometer, 0, 0);

        imageSpeedometer = a.getDrawable(R.styleable.ImageSpeedometer_sv_image);
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

        drawSpeedUnitText(canvas);
        drawIndicator(canvas);
        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();

        if (imageSpeedometer != null) {
            imageSpeedometer.setBounds((int)getViewLeft() + getPadding(),(int)getViewTop() +  getPadding()
                    , (int)getViewRight() - getPadding(), (int)getViewBottom() - getPadding());
            imageSpeedometer.draw(c);
        }
        drawTicks(c);
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
     * set background speedometer image, Preferably be square.
     * @param bitmapImage image bitmap.
     * @see #setImageSpeedometer(int)
     */
    public void setImageSpeedometer (Bitmap bitmapImage) {
        setImageSpeedometer(new BitmapDrawable(getContext().getResources(), bitmapImage));
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getLowSpeedColor() {
        return 0;
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
        return 0;
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
        return 0;
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
     * @param typeface nothing.
     */
    @Deprecated
    @Override
    public void setTextTypeface(Typeface typeface) {}

    /**
     * this Speedometer doesn't use this method.
     * @return {@code 0} always.
     */
    @Deprecated
    @Override
    public float getTextSize() {
        return 0;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param textSize nothing.
     */
    @Deprecated
    @Override
    public void setTextSize(float textSize) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code 0} always.
     */
    @Deprecated
    @Override
    public int getTextColor() {
        return 0;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param textColor nothing.
     */
    @Deprecated
    @Override
    public void setTextColor(int textColor) {
    }
}
