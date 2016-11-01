package com.github.anastr.speedviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.github.anastr.speedviewlib.util.OnSpeedChangeListener;

import java.util.Random;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unused")
abstract public class Speedometer extends View {

    private Paint circleBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected TextPaint speedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG),
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private float speedometerWidth = dpTOpx(30f);
    private float speedTextSize = dpTOpx(18f);
    private float textSize = dpTOpx(10f);
    private String unit = "Km/h";
    private boolean withTremble = true;
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

    /**
     * the last speed which you set by {@link #speedTo(int)}
     * or {@link #speedTo(int, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     */
    private int speed = 0;
    /** what is speed now in <b>int</b> */
    private int correctIntSpeed = 0;
    /** what is speed now in <b>float</b> */
    private float correctSpeed = 0f;
    /** a degree to increases and decreases the indicator around correct speed */
    private float trembleDegree = 4f;
    private int trembleDuration = 1000;
    protected int MIN_DEGREE = 135, MAX_DEGREE = 135+270;
    /** to rotate indicator */
    private float degree = MIN_DEGREE;
    private ValueAnimator speedAnimator, trembleAnimator, realSpeedAnimator;
    private boolean canceled = false;
    private OnSpeedChangeListener onSpeedChangeListener;
    /** this animatorListener to call {@link #tremble()} method when animator done */
    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
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
    };

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = (width > height) ? height : width;
        setMeasuredDimension(size, size);
    }

    private void init() {
        speedAnimator = ValueAnimator.ofFloat(0f, 1f);
        trembleAnimator = ValueAnimator.ofFloat(0f, 1f);
        realSpeedAnimator = ValueAnimator.ofFloat(0f, 1f);

        speedTextPaint.setTextAlign(Paint.Align.CENTER);

        defaultValues();
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
        speedTextSize = a.getDimension(R.styleable.Speedometer_speedTextSize, speedTextSize);
        textSize = a.getDimension(R.styleable.Speedometer_textSize, textSize);
        String unit = a.getString(R.styleable.Speedometer_unit);
        a.recycle();
        this.unit =  (unit != null) ? unit : this.unit;
        initAttributeValue();
    }

    private void initAttributeValue() {
        circleBackPaint.setColor(backgroundCircleColor);
        speedTextPaint.setColor(speedTextColor);
        speedTextPaint.setTextSize(speedTextSize);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    /**
     * convert dp to <b>pixel</b>.
     * @param dp to convert.
     * @return Dimension in pixel.
     */
    public float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    /**
     * convert pixel to <b>dp</b>.
     * @param px to convert.
     * @return Dimension in dp.
     */
    public float pxTOdp(float px) {
        return px / getContext().getResources().getDisplayMetrics().density;
    }

    abstract protected void defaultValues();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f, circleBackPaint);

        correctSpeed = (degree-MIN_DEGREE) * maxSpeed/(MAX_DEGREE-MIN_DEGREE);
        int newSpeed = (int) correctSpeed;
        if (newSpeed != correctIntSpeed) {
            if (onSpeedChangeListener != null){
                onSpeedChangeListener.onSpeedChange(this, newSpeed > correctIntSpeed, trembleAnimator.isRunning());
            }
            correctIntSpeed = newSpeed;
        }
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
     * move speed to percent value.
     * @param percent percent value to move, should be between [0,100].
     *
     * @see #speedTo(int)
     * @see #speedTo(int, long)
     * @see #realSpeedTo(int)
     */
    public void speedPercentTo(int percent) {
        percent = (percent > 100) ? 100 : (percent < 0) ? 0 : percent;
        speedTo(percent * maxSpeed / 100);
    }

    /**
     * move speed to correct {@code int},
     * it should be between [0, MAX Speed].<br>
     * <br>
     * if {@code speed > maxSpeed} speed will be maxSpeed,<br>
     * if {@code speed < 0} speed will be 0.<br>
     *
     * it is the same {@link #speedTo(int, long)}
     * with default {@code moveDuration = 2000}.
     *
     * @param speed correct speed to move.
     *
     * @see #speedTo(int, long)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(int)
     */
    public void speedTo(int speed) {
        speedTo(speed, 2000);
    }

    /**
     * move speed to correct {@code int},
     * it should be between [0, MAX Speed].<br>
     * <br>
     * if {@code speed > maxSpeed} speed will be maxSpeed,<br>
     * if {@code speed < 0} speed will be 0.
     *
     * @param speed correct speed to move.
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     *
     * @see #speedTo(int)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(int)
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
        speedAnimator.addListener(animatorListener);
        speedAnimator.start();
    }

    /**
     * this method use {@code realSpeedTo()} to speed up
     * the speedometer to <b>MAX speed</b>.
     *
     * @see #realSpeedTo(int)
     * @see #slowDown()
     */
    public void speedUp() {
        realSpeedTo(getMaxSpeed());
    }

    /**
     * this method use {@code #realSpeedTo()} to slow down
     * the speedometer to <b>0</b>.
     *
     * @see #realSpeedTo(int)
     * @see #speedUp()
     */
    public void slowDown() {
        realSpeedTo(0);
    }

    /**
     * to make speedometer some real.
     * <br>
     * when <b>speed up</b> : speed value well increase <i>slowly</i>.
     * <br>
     * when <b>slow down</b> : speed value will decrease <i>rapidly</i>.
     * @param speed correct speed to move.
     *
     * @see #speedTo(int)
     * @see #speedTo(int, long)
     * @see #speedPercentTo(int)
     * @see #speedUp()
     * @see #slowDown()
     */
    public void realSpeedTo(int speed) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < 0) ? 0 : speed;
        this.speed = speed;

        float newDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /maxSpeed +MIN_DEGREE;
        if (newDegree == degree)
            return;

        cancel();
        realSpeedAnimator = ValueAnimator.ofInt((int)degree, (int)newDegree);
        realSpeedAnimator.setRepeatCount(ValueAnimator.INFINITE);
        realSpeedAnimator.setInterpolator(new LinearInterpolator());
        realSpeedAnimator.setDuration(Math.abs((long) ((newDegree - degree) * 10) ));
        final boolean isSpeedUp = speed > correctSpeed;
        final int finalSpeed = speed;
        realSpeedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isSpeedUp) {
                    float per = 100.005f-getPercentSpeed();
                    degree += .8f*per/100;
                }
                else {
                    float per = getPercentSpeed()+.005f;
                    degree -= 2f*per/100 +.2f;
                }
                postInvalidate();
                if (finalSpeed == correctIntSpeed)
                    stop();
            }
        });
        realSpeedAnimator.addListener(animatorListener);
        realSpeedAnimator.start();
    }

    private void tremble() {
        cancelTremble();
        if (!isWithTremble())
            return;
        Random random = new Random();
        float mad = trembleDegree * random.nextFloat() * ((random.nextBoolean()) ? -1 :1);
        float originalDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /maxSpeed +MIN_DEGREE;
        mad = (originalDegree+mad > MAX_DEGREE) ? MAX_DEGREE - originalDegree
                : (originalDegree+mad < MIN_DEGREE) ? MIN_DEGREE - originalDegree : mad;
        trembleAnimator = ValueAnimator.ofFloat(degree, originalDegree +mad);
        trembleAnimator.setInterpolator(new DecelerateInterpolator());
        trembleAnimator.setDuration(trembleDuration);
        trembleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) trembleAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        trembleAnimator.addListener(animatorListener);
        trembleAnimator.start();
    }

    protected float getDegree() {
        return degree;
    }

    /**
     * default : 4 degree.
     * @param trembleDegree a degree to increases and decreases the indicator around correct speed.
     *                      should be between ]0, 10]
     * @throws IllegalArgumentException If trembleDegree is out of range.
     */
    public void setTrembleDegree (float trembleDegree) {
        if (trembleDegree <= 0 || trembleDegree > 10)
            throw new IllegalArgumentException("trembleDegree should be > 0 and <= 10");
        this.trembleDegree = trembleDegree;
    }

    /**
     * default : 1000 millisecond.
     * @param trembleDuration tremble Animation duration in millisecond,
     *                        should be {@code > 0 and <= 6000}, else well be ignore.
     */
    public void setTrembleDuration (int trembleDuration) {
        if (trembleDuration <= 0 || trembleDuration > 6000)
            return;
        this.trembleDuration = trembleDuration;
    }

    /**
     * tremble control.
     * @param trembleDegree a degree to increases and decreases the indicator around correct speed.
     *                      should be between ]0, 10]
     * @param trembleDuration tremble Animation duration in millisecond,
     *                        should be {@code > 0 and <= 6000}, else well be ignore.
     *
     * @see #setTrembleDegree(float)
     * @see #setTrembleDuration(int)
     * @throws IllegalArgumentException If trembleDegree is out of its range.
     */
    public void setTrembleData (float trembleDegree, int trembleDuration) {
        setTrembleDegree(trembleDegree);
        setTrembleDuration(trembleDuration);
    }

    /**
     * <b>if true</b> : the indicator automatically will be increases and decreases {@link #trembleDegree} degree
     * around last speed you set,
     * to add some reality to speedometer.<br>
     * <b>if false</b> : nothing will do.
     * @param withTremble to play tremble Animation
     *
     * @see #setTrembleData(float, int)
     */
    public void setWithTremble(boolean withTremble) {
        this.withTremble = withTremble;
        tremble();
    }

    public boolean isWithTremble() {
        return withTremble;
    }

    /**
     * @return the last speed which you set by {@link #speedTo(int)}
     * or {@link #speedTo(int, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     *
     * @see #getCorrectSpeed()
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * what is correct speed now.
     * <p>It will give different results if withTremble is running.</p>
     *
     * @return correct speed now.
     * @see #setWithTremble(boolean)
     * @see #getSpeed()
     */
    public float getCorrectSpeed() {
        return correctSpeed;
    }

    /**
     * what is speed now in <b>int</b>.
     * @return correct speed in Integer
     * @see #getCorrectSpeed()
     */
    public int getCorrectIntSpeed() {
        return correctIntSpeed;
    }

    /**
     * get max speed in speedometer, default max speed is 100.
     * @return max speed.
     *
     * @see #setMaxSpeed(int)
     */
    public int getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * change max speed.<br>
     * this method well call {@link #speedTo(int)} method
     * to make the change smooth.
     *
     * @param maxSpeed new MAX Speed.
     */
    public void setMaxSpeed(int maxSpeed) {
        if (maxSpeed <= 0)
            return;
        this.maxSpeed = maxSpeed;
        speedTo(speed);
        invalidate();
    }

    /**
     * get speed as <b>percent</b>.
     * @return percent speed, between [0,100].
     */
    public float getPercentSpeed() {
        return correctSpeed * 100f / maxSpeed;
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

    /**
     * change all text color without <b>speed text</b>.
     * @param textColor new color
     *
     * @see #setSpeedTextColor(int)
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        invalidate();
    }

    public int getSpeedTextColor() {
        return speedTextColor;
    }

    /**
     * change just speed text color.
     * @param speedTextColor new color
     *
     * @see #setTextColor(int)
     */
    public void setSpeedTextColor(int speedTextColor) {
        this.speedTextColor = speedTextColor;
        speedTextPaint.setColor(speedTextColor);
        invalidate();
    }

    public int getBackgroundCircleColor() {
        return backgroundCircleColor;
    }

    /**
     * Circle Background Color,
     * you can set at {@code Color.TRANSPARENT}
     * to remove circle background.
     * @param backgroundCircleColor new Circle Background Color
     */
    public void setBackgroundCircleColor(int backgroundCircleColor) {
        this.backgroundCircleColor = backgroundCircleColor;
        circleBackPaint.setColor(backgroundCircleColor);
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    /**
     * change all text size without <b>speed text</b>.
     * @param textSize new size in pixel
     *
     * @see #dpTOpx(float)
     * @see #setSpeedTextSize(float)
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        invalidate();
    }

    public float getSpeedTextSize() {
        return speedTextSize;
    }

    /**
     * change just speed text size.
     * @param speedTextSize new size in pixel
     *
     * @see #dpTOpx(float)
     * @see #setTextSize(float)
     */
    public void setSpeedTextSize(float speedTextSize) {
        this.speedTextSize = speedTextSize;
        speedTextPaint.setTextSize(speedTextSize);
        invalidate();
    }

    public String getUnit() {
        return unit;
    }

    /**
     * text after speed text.
     * @param unit speed unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
        invalidate();
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    public void setSpeedometerWidth(float speedometerWidth) {
        this.speedometerWidth = speedometerWidth;
        try {
            onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        }
        catch (Exception ignore){}
        invalidate();
    }

    /**
     * this well call when (int) speed change.
     * @param onSpeedChangeListener The callback that will run.
     */
    public void setOnSpeedChangeListener(OnSpeedChangeListener onSpeedChangeListener) {
        this.onSpeedChangeListener = onSpeedChangeListener;
    }
}
