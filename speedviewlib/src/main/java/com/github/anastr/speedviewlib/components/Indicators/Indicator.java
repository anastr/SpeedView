package com.github.anastr.speedviewlib.components.Indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.anastr.speedviewlib.Speedometer;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unchecked,unused,WeakerAccess")
public abstract class Indicator<I extends Indicator> {

    protected Paint indicatorPaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
    private float density;
    private float indicatorWidth;
    private float viewSize;
    private float speedometerWidth;
    private int indicatorColor = 0xff2196F3;
    private int padding;
    private boolean inEditMode;

    protected Indicator (Context context) {
        this.density = context.getResources().getDisplayMetrics().density;
        init();
    }

    private void init() {
        indicatorPaint.setColor(indicatorColor);
        indicatorWidth = getDefaultIndicatorWidth();
    }

    public abstract void draw(Canvas canvas, float degree);
    /** called when size change or color, width */
    protected abstract void updateIndicator();
    /** @param withEffects if indicator have effects like BlurMaskFilter */
    protected abstract void setWithEffects(boolean withEffects);
    protected abstract float getDefaultIndicatorWidth();

    /**  @return Y position of indicator */
    public float getTop(){
        return getPadding();
    }
    /** @return Bottom Y position of indicator */
    public float getBottom(){
        return getCenterY();
    }
    /** @return down point after center */
    public float getLightBottom() {
        return getCenterY() > getBottom() ? getBottom() : getCenterY();
    }

    /**
     * must call in {@code speedometer.onSizeChanged()}
     * @param speedometer target speedometer.
     */
    public void onSizeChange(Speedometer speedometer) {
        setTargetSpeedometer(speedometer);
    }

    /**
     * to change indicator's data,
     * this method called by the library.
     * @param speedometer target speedometer.
     */
    public void setTargetSpeedometer(Speedometer speedometer) {
        updateData(speedometer);
        updateIndicator();
    }

    private void updateData(Speedometer speedometer) {
        this.viewSize = speedometer.getSize();
        this.speedometerWidth = speedometer.getSpeedometerWidth();
        this.padding = speedometer.getPadding();
        this.inEditMode = speedometer.isInEditMode();
    }

    public float dpTOpx(float dp) {
        return dp * density;
    }

    public float getIndicatorWidth() {
        return indicatorWidth;
    }

    public I setIndicatorWidth(float indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
        return (I) this;
    }

    /**
     * @return size of Speedometer View without padding.
     */
    public float getViewSize() {
        return viewSize - (padding*2f);
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public I setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        return (I) this;
    }

    /**
     * @return x center of speedometer.
     */
    public float getCenterX() {
        return viewSize /2f;
    }

    /**
     * @return y center of speedometer.
     */
    public float getCenterY() {
        return viewSize /2f;
    }

    public int getPadding() {
        return padding;
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    public void noticeIndicatorWidthChange(float indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
        updateIndicator();
    }

    public void noticeIndicatorColorChange(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        updateIndicator();
    }

    public void noticeSpeedometerWidthChange(float speedometerWidth) {
        this.speedometerWidth = speedometerWidth;
        updateIndicator();
    }

    public void noticePaddingChange(int newPadding) {
        this.padding = newPadding;
        updateIndicator();
    }

    public void withEffects(boolean withEffects) {
        setWithEffects(withEffects);
        updateIndicator();
    }

    public boolean isInEditMode() {
        return inEditMode;
    }

    /** indicator's shape */
    public enum Indicators {
        NoIndicator, NormalIndicator, NormalSmallIndicator, TriangleIndicator
        , SpindleIndicator, LineIndicator, HalfLineIndicator, QuarterLineIndicator
        , KiteIndicator, NeedleIndicator
    }

    /**
     * create new {@link Indicator} with default values.
     * @param context required.
     * @param indicator new indicator (Enum value).
     * @return new indicator object.
     */
    public static Indicator createIndicator (Context context, Indicators indicator) {
        switch (indicator) {
            case NoIndicator :
                return new NoIndicator(context);
            case NormalIndicator :
                return new NormalIndicator(context);
            case NormalSmallIndicator :
                return new NormalSmallIndicator(context);
            case TriangleIndicator :
                return new TriangleIndicator(context);
            case SpindleIndicator :
                return new SpindleIndicator(context);
            case LineIndicator :
                return new LineIndicator(context, LineIndicator.LINE);
            case HalfLineIndicator :
                return new LineIndicator(context, LineIndicator.HALF_LINE);
            case QuarterLineIndicator :
                return new LineIndicator(context, LineIndicator.QUARTER_LINE);
            case KiteIndicator :
                return new KiteIndicator(context);
            case NeedleIndicator :
                return new NeedleIndicator(context);
            default :
                return new NormalIndicator(context);
        }
    }
}
