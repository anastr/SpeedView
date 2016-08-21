package com.github.anastr.speedviewlib;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

abstract public class Speed extends View {

    private float speedometerWidth = dpTOpx(30f);
    private float speedTextSize = dpTOpx(18f);
    private String unit = "Km/h";
    private boolean withTremble = true, withBackgroundCircle = true;
    private int maxSpeed = 100;

    private int indicatorColor = Color.RED
            , centerCircleColor = Color.DKGRAY
            , markColor = Color.WHITE
            , lowSpeedColor = Color.GREEN
            , mediumSpeedColor = Color.YELLOW
            , highSpeedColor = Color.RED
            , textColor = Color.BLACK
            , backgroundCircleColor = Color.WHITE;

    public Speed(Context context) {
        super(context);
    }

    public Speed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Speed(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    abstract public void speedToDef();

    /**
     * <p>change speed to correct {@code int},</p>
     * <p>if {@code speed > maxSpeed} speed will be maxSpeed,</p>
     * if {@code speed < 0} speed will be 0.
     * @param speed correct speed to move.
     */
    abstract public void speedTo(int speed);

    /**
     * <p>change speed to correct {@code int},</p>
     * <p>if {@code speed > maxSpeed} speed will be maxSpeed,</p>
     * if {@code speed < 0} speed will be 0.
     * @param speed correct speed to move.
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     */
    abstract public void speedTo(int speed, long moveDuration);

    /**
     * change speed to percent value.
     * @param percent percent value to change, should be between [0,100].
     */
    abstract public void speedPercentTo(int percent);

    /**
     * what speed is now
     * @return the last speed which you set by {@link #speedTo(int)}
     * or {@link #speedTo(int, long)} or {@link #speedPercentTo(int)}.
     */
    abstract public int getSpeed();

    abstract public int getPercentSpeed();



    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public int getCenterCircleColor() {
        return centerCircleColor;
    }

    public void setCenterCircleColor(int centerCircleColor) {
        this.centerCircleColor = centerCircleColor;
        invalidate();
    }

    public int getMarkColor() {
        return markColor;
    }

    public void setMarkColor(int markColor) {
        this.markColor = markColor;
        invalidate();
    }

    public int getLowSpeedColor() {
        return lowSpeedColor;
    }

    public void setLowSpeedColor(int lowSpeedColor) {
        this.lowSpeedColor = lowSpeedColor;
        invalidate();
    }

    public int getMediumSpeedColor() {
        return mediumSpeedColor;
    }

    public void setMediumSpeedColor(int mediumSpeedColor) {
        this.mediumSpeedColor = mediumSpeedColor;
        invalidate();
    }

    public int getHighSpeedColor() {
        return highSpeedColor;
    }

    public void setHighSpeedColor(int highSpeedColor) {
        this.highSpeedColor = highSpeedColor;
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public int getBackgroundCircleColor() {
        return backgroundCircleColor;
    }

    public void setBackgroundCircleColor(int backgroundCircleColor) {
        this.backgroundCircleColor = backgroundCircleColor;
        invalidate();
    }

    public float getSpeedTextSize() {
        return speedTextSize;
    }

    public void setSpeedTextSize(float speedTextSize) {
        this.speedTextSize = speedTextSize;
        invalidate();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        invalidate();
    }

    public boolean isWithTremble() {
        return withTremble;
    }

    public void setWithTremble(boolean withTremble) {
        this.withTremble = withTremble;
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    public void setSpeedometerWidth(float speedometerWidth) {
        this.speedometerWidth = speedometerWidth;
        invalidate();
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        if (maxSpeed <= 0)
            return;
        this.maxSpeed = maxSpeed;
        speedToDef();
        invalidate();
    }

    public boolean isWithBackgroundCircle() {
        return withBackgroundCircle;
    }

    public void setWithBackgroundCircle(boolean withBackgroundCircle) {
        this.withBackgroundCircle = withBackgroundCircle;
        invalidate();
    }
}
