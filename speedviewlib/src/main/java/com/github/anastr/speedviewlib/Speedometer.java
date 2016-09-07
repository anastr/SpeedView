package com.github.anastr.speedviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.Random;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
abstract public class Speedometer extends View {

    private float speedometerWidth = dpTOpx(30f);
    private float speedTextSize = dpTOpx(18f);
    private float textSize = dpTOpx(10f);
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
            , backgroundCircleColor = Color.WHITE
            , speedTextColor = Color.BLACK;

    private int speed = 0;
    private float correctSpeed = 0f;
    protected int MIN_DEGREE = 135, MAX_DEGREE = 135+270;
    /** to rotate indicator */
    private float degree = MIN_DEGREE;
    private ValueAnimator speedAnimator, trembleAnimator, realSpeedAnimator;
    private boolean canceled = false;

    public Speedometer(Context context) {
        super(context);
        init();
    }

    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public Speedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    private void init() {
        defaultValues();

        speedAnimator = ValueAnimator.ofFloat(0f, 1f);
        trembleAnimator = ValueAnimator.ofFloat(0f, 1f);
        realSpeedAnimator = ValueAnimator.ofFloat(0f, 1f);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Speedometer, 0, 0);

        indicatorColor = a.getColor(R.styleable.Speedometer_indicatorColor, indicatorColor);
        centerCircleColor = a.getColor(R.styleable.Speedometer_centerCircleColor, centerCircleColor);
        markColor = a.getColor(R.styleable.Speedometer_markColor, markColor);
        lowSpeedColor = a.getColor(R.styleable.Speedometer_lowSpeedColor, lowSpeedColor);
        mediumSpeedColor = a.getColor(R.styleable.Speedometer_mediumSpeedColor, mediumSpeedColor);
        highSpeedColor = a.getColor(R.styleable.Speedometer_highSpeedColor, highSpeedColor);
        textColor = a.getColor(R.styleable.Speedometer_textColor, textColor);
        backgroundCircleColor = a.getColor(R.styleable.Speedometer_backgroundCircleColor, backgroundCircleColor);
        speedTextColor = a.getColor(R.styleable.Speedometer_speedTextColor, speedTextColor);
        speedometerWidth = a.getDimension(R.styleable.Speedometer_speedometerWidth, speedometerWidth);
        maxSpeed = a.getInt(R.styleable.Speedometer_maxSpeed, maxSpeed);
        withTremble = a.getBoolean(R.styleable.Speedometer_withTremble, withTremble);
        withBackgroundCircle = a.getBoolean(R.styleable.Speedometer_withBackgroundCircle, withBackgroundCircle);
        speedTextSize = a.getDimension(R.styleable.Speedometer_speedTextSize, speedTextSize);
        textSize = a.getDimension(R.styleable.Speedometer_textSize, textSize);
        String unit = a.getString(R.styleable.Speedometer_unit);
        a.recycle();
        this.unit =  (unit != null) ? unit : this.unit;
    }

    protected float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    protected float pxTOdp(float px) {
        return px / getContext().getResources().getDisplayMetrics().density;
    }

    abstract protected void defaultValues();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        correctSpeed = (degree-MIN_DEGREE) * maxSpeed/(MAX_DEGREE-MIN_DEGREE);
    }

    public void stop() {
        speed = (int) correctSpeed;
        cancel();
        tremble();
    }

    private void cancel() {
        cancelSpeedMove();
        cancelTremble();
    }

    private void cancelTremble() {
        canceled = true;
        trembleAnimator.cancel();
        canceled = false;
    }

    private void cancelSpeedMove() {
        canceled = true;
        speedAnimator.cancel();
        realSpeedAnimator.cancel();
        canceled = false;
    }

    /**
     * change speed to percent value.
     * @param percent percent value to change, should be between [0,100].
     */
    public void speedPercentTo(int percent) {
        percent = (percent > 100) ? 100 : (percent < 0) ? 0 : percent;
        speedTo(percent * maxSpeed / 100);
    }

    /**
     * <p>change speed to correct {@code int},</p>
     * <p>if {@code speed > maxSpeed} speed will be maxSpeed,</p>
     * if {@code speed < 0} speed will be 0.
     * @param speed correct speed to move.
     */
    public void speedTo(int speed) {
        speedTo(speed, 2000);
    }

    /**
     * <p>change speed to correct {@code int},</p>
     * <p>if {@code speed > maxSpeed} speed will be maxSpeed,</p>
     * if {@code speed < 0} speed will be 0.
     * @param speed correct speed to move.
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     */
    public void speedTo(int speed, long moveDuration) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < 0) ? 0 : speed;
        this.speed = speed;

        float newDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /maxSpeed +MIN_DEGREE;
        if (newDegree == degree)
            return;

        cancel();
        speedAnimator = ValueAnimator.ofFloat(degree, newDegree);
        speedAnimator.setInterpolator(new DecelerateInterpolator());
        speedAnimator.setDuration(moveDuration);
        speedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) speedAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        speedAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled)
                    tremble();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        speedAnimator.start();
    }

    public void realSpeedTo(int speed) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < 0) ? 0 : speed;
        this.speed = speed;

        float newDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /maxSpeed +MIN_DEGREE;
        if (newDegree == degree)
            return;

        cancel();
        realSpeedAnimator = ValueAnimator.ofFloat(degree, newDegree);
        if (speed > correctSpeed) {
            realSpeedAnimator.setInterpolator(new LinearInterpolator());
            realSpeedAnimator.setDuration((long) ((newDegree - degree) * 40));
        }
        else {
            realSpeedAnimator.setInterpolator(new DecelerateInterpolator());
            realSpeedAnimator.setDuration((long) ((degree - newDegree) * 10));
        }
        realSpeedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) realSpeedAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        realSpeedAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled)
                    tremble();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        realSpeedAnimator.start();
    }

    private void tremble() {
        cancelTremble();
        if (!isWithTremble())
            return;
        Random random = new Random();
        float mad = 4*random.nextFloat() * ((random.nextBoolean()) ? -1 :1);
        float originalDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /maxSpeed +MIN_DEGREE;
        mad = (originalDegree+mad > MAX_DEGREE) ? MAX_DEGREE - originalDegree
                : (originalDegree+mad < MIN_DEGREE) ? MIN_DEGREE - originalDegree : mad;
        trembleAnimator = ValueAnimator.ofFloat(degree, originalDegree +mad);
        trembleAnimator.setInterpolator(new DecelerateInterpolator());
        trembleAnimator.setDuration(1000);
        trembleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) trembleAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        trembleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled)
                    tremble();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        trembleAnimator.start();
    }

    protected float getDegree() {
        return degree;
    }

    /**
     * @return the last speed which you set by {@link #speedTo(int)}
     * or {@link #speedTo(int, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * what is correct speed is now.
     * <p>It will give different results if withTremble is running.</p>
     * @see #setWithTremble(boolean)
     */
    public float getCorrectSpeed() {
        return correctSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        if (maxSpeed <= 0)
            return;
        this.maxSpeed = maxSpeed;
        speedTo(speed);
        invalidate();
    }

    public float getPercentSpeed() {
        return degree * 100f / maxSpeed;
    }

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

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
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
        tremble();
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    public void setSpeedometerWidth(float speedometerWidth) {
        this.speedometerWidth = speedometerWidth;
        invalidate();
    }

    public boolean isWithBackgroundCircle() {
        return withBackgroundCircle;
    }

    public void setWithBackgroundCircle(boolean withBackgroundCircle) {
        this.withBackgroundCircle = withBackgroundCircle;
        invalidate();
    }

    public int getSpeedTextColor() {
        return speedTextColor;
    }

    public void setSpeedTextColor(int speedTextColor) {
        this.speedTextColor = speedTextColor;
        invalidate();
    }
}
