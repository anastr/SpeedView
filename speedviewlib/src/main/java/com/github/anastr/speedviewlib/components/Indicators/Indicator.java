package com.github.anastr.speedviewlib.components.Indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private float viewWidth;
    private float viewHeight;
    private float speedometerWidth;
    private int indicatorColor = Color.parseColor("#2196F3");
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
    /** if indicator have effects like BlurMaskFilter */
    protected abstract void setWithEffects(boolean withEffects);
    protected abstract float getDefaultIndicatorWidth();

    /**
     * must call in {@code speedometer.onSizeChanged()}
     * @param speedometer target speedometer.
     */
    public void onSizeChange(Speedometer speedometer) {
        setTargetSpeedometer(speedometer);
    }

    public void setTargetSpeedometer(Speedometer speedometer) {
        updateData(speedometer);
        updateIndicator();
    }

    private void updateData(Speedometer speedometer) {
        this.viewWidth = speedometer.getWidthPa();
        this.viewHeight = speedometer.getHeightPa();
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
        updateIndicator();
        return (I) this;
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public I setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        updateIndicator();
        return (I) this;
    }

    public float getCenterX() {
        return (2*padding + viewWidth) /2f;
    }

    public float getCenterY() {
        return (2*padding + viewHeight) /2f;
    }

    public int getPadding() {
        return padding;
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
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

    public enum Indicators {
        NoIndicator, NormalIndicator, NormalSmallIndicator, TriangleIndicator
        , SpindleIndicator, LineIndicator, HalfLineIndicator, QuarterLineIndicator
    }

}
