package com.github.anastr.speedviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.github.anastr.speedviewlib.components.Indicators.HalfLineIndicator;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.github.anastr.speedviewlib.components.Indicators.LineIndicator;
import com.github.anastr.speedviewlib.components.Indicators.NoIndicator;
import com.github.anastr.speedviewlib.components.Indicators.NormalIndicator;
import com.github.anastr.speedviewlib.components.Indicators.NormalSmallIndicator;
import com.github.anastr.speedviewlib.components.Indicators.QuarterLineIndicator;
import com.github.anastr.speedviewlib.components.Indicators.SpindleIndicator;
import com.github.anastr.speedviewlib.components.Indicators.TriangleIndicator;
import com.github.anastr.speedviewlib.components.note.Note;
import com.github.anastr.speedviewlib.util.OnSpeedChangeListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unused")
abstract public class Speedometer extends View {

    private Indicator indicator;
    protected Paint circleBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected TextPaint speedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG),
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG),
            unitTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private float speedometerWidth = dpTOpx(30f);
    private float indicatorWidth = dpTOpx(12f);
    private float speedTextSize = dpTOpx(18f);
    private float textSize = dpTOpx(10f);
    private float unitTextSize = dpTOpx(15f);
    /** the text after speedText */
    private String unit = "Km/h";
    private boolean withTremble = true;
    /** the max range in speedometer, {@code default = 100} */
    private int maxSpeed = 100;
    /** the min range in speedometer, {@code default = 0} */
    private int minSpeed = 0;

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
    protected int startDegree = 135, endDegree = 135+270;
    /** to rotate indicator */
    private float degree = startDegree;
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

    /** to contain all drawing that doesn't change */
    protected Bitmap backgroundBitmap;
    protected Paint backgroundBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected int padding = 0;

    /** low speed area, started from {@link #startDegree} */
    private int lowSpeedPercent = 60;
    /** medium speed area, started from {@link #startDegree} */
    private int mediumSpeedPercent = 87;

    private boolean speedometerTextRightToLeft = false;

    private ArrayList<Note> notes = new ArrayList<>();
    private boolean attachedToWindow = false;

    /** object to set text digits locale */
    private Locale locale = Locale.getDefault();

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
        indicator = new NoIndicator(this);

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
        minSpeed = a.getInt(R.styleable.Speedometer_minSpeed, minSpeed);
        withTremble = a.getBoolean(R.styleable.Speedometer_withTremble, withTremble);
        speedTextSize = a.getDimension(R.styleable.Speedometer_speedTextSize, speedTextSize);
        textSize = a.getDimension(R.styleable.Speedometer_textSize, textSize);
        String unit = a.getString(R.styleable.Speedometer_unit);
        unitTextSize = a.getDimension(R.styleable.Speedometer_unitTextSize, unitTextSize);
        trembleDegree = a.getFloat(R.styleable.Speedometer_trembleDegree, trembleDegree);
        trembleDuration = a.getInt(R.styleable.Speedometer_trembleDuration, trembleDuration);
        startDegree = a.getInt(R.styleable.Speedometer_startDegree, startDegree);
        endDegree = a.getInt(R.styleable.Speedometer_endDegree, endDegree);
        lowSpeedPercent = a.getInt(R.styleable.Speedometer_lowSpeedPercent, lowSpeedPercent);
        mediumSpeedPercent = a.getInt(R.styleable.Speedometer_mediumSpeedPercent, mediumSpeedPercent);
        speedometerTextRightToLeft = a.getBoolean(R.styleable.Speedometer_speedometerTextRightToLeft, speedometerTextRightToLeft);
        indicatorWidth = a.getDimension(R.styleable.Speedometer_indicatorWidth, indicatorWidth);
        int ind = a.getInt(R.styleable.Speedometer_indicator, -1);
        if (ind != -1)
            setIndicator(Indicator.Indicators.values()[ind]);
        degree = startDegree;
        a.recycle();
        this.unit =  (unit != null) ? unit : this.unit;
        checkStartAndEndDegree();
        checkSpeedometerPercent();
        initAttributeValue();
    }

    private void initAttributeValue() {
        circleBackPaint.setColor(backgroundCircleColor);
        speedTextPaint.setColor(speedTextColor);
        speedTextPaint.setTextSize(speedTextSize);
        unitTextPaint.setColor(speedTextColor);
        unitTextPaint.setTextSize(unitTextSize);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updatePadding();
        indicator.onSizeChange(this);
    }

    private void checkStartAndEndDegree() {
        if (startDegree < 0)
            throw new IllegalArgumentException("StartDegree can\'t be Negative");
        if (endDegree < 0)
            throw new IllegalArgumentException("EndDegree can\'t be Negative");
        if (startDegree >= endDegree)
            throw new IllegalArgumentException("EndDegree must be bigger than StartDegree !");
        if (endDegree - startDegree > 360)
            throw new IllegalArgumentException("(EndDegree - StartDegree) must be smaller than 360 !");
    }

    private void checkSpeedometerPercent() {
        if (lowSpeedPercent > mediumSpeedPercent)
            throw new IllegalArgumentException("lowSpeedPercent must be smaller than mediumSpeedPercent");
        if (lowSpeedPercent > 100 || lowSpeedPercent < 0)
            throw new IllegalArgumentException("lowSpeedPercent must be between [0, 100]");
        if (mediumSpeedPercent > 100 || mediumSpeedPercent < 0)
            throw new IllegalArgumentException("mediumSpeedPercent must be between [0, 100]");
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
    abstract protected Bitmap updateBackgroundBitmap();

    private void updatePadding() {
        padding = Math.max(Math.max(getPaddingLeft(), getPaddingRight()), Math.max(getPaddingTop(), getPaddingBottom()));
        indicator.noticePaddingChange(padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundBitmapPaint);

        correctSpeed = getSpeedAtDegree(degree);
        int newSpeed = (int) correctSpeed;
        if (newSpeed != correctIntSpeed) {
            boolean isSpeedUp = newSpeed > correctIntSpeed;
            correctIntSpeed = newSpeed;
            if (onSpeedChangeListener != null){
                onSpeedChangeListener.onSpeedChange(this, isSpeedUp, trembleAnimator.isRunning());
            }
        }
    }

    protected void drawIndicator(Canvas canvas) {
        indicator.draw(canvas, degree);
    }

    /**
     * every Speedometer must call this method at End of it's {@code onDraw()} method.
     * @param canvas view canvas to draw notes.
     */
    protected void drawNotes(Canvas canvas) {
        for (Note note : notes) {
            if (note.getPosition() == Note.Position.CenterSpeedometer)
                note.draw(canvas, getWidth()/2f, getHeight()/2f);
            else {
                float y = 0f;
                if (note.getPosition() == Note.Position.CenterIndicator)
                    y = getHeightPa()/4f + padding;
                else if (note.getPosition() == Note.Position.TopIndicator)
                    y = padding;
                canvas.save();
                canvas.rotate(90f +getDegree(), getWidth()/2f, getHeight()/2f);
                canvas.rotate(-(90f +getDegree()), getWidth()/2f, y);
                note.draw(canvas, getWidth()/2f, y);
                canvas.restore();
            }
        }
    }

    /**
     * stop speedometer and run tremble if {@link #withTremble} is true.
     * use this method just when you wont to stop {@code speedTo and realSpeedTo}.
     */
    public void stop() {
        if (!speedAnimator.isRunning() && !realSpeedAnimator.isRunning())
            return;
        speed = (int) correctSpeed;
        cancelSpeedAnimator();
        tremble();
    }

    /**
     * cancel all animators without call {@link #tremble()}.
     */
    private void cancelSpeedAnimator() {
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
     * @param speed to know the degree at it.
     * @return correct Degree at that speed.
     */
    protected float getDegreeAtSpeed (float speed) {
        return (speed - minSpeed) * (endDegree - startDegree) /(maxSpeed - minSpeed) + startDegree;
    }

    /**
     * @param degree to know the speed at it.
     * @return correct speed at that degree.
     */
    protected float getSpeedAtDegree (float degree) {
        return (degree - startDegree) * (maxSpeed - minSpeed) /(endDegree - startDegree) + minSpeed;
    }

    /**
     * move speed to percent value.
     * @param percent percent value to move, must be between [0,100].
     *
     * @see #speedTo(int)
     * @see #speedTo(int, long)
     * @see #realSpeedTo(int)
     */
    public void speedPercentTo(int percent) {
        percent = (percent > 100) ? 100 : (percent < 0) ? 0 : percent;
        speedTo(percent * (maxSpeed - minSpeed) / 100 + minSpeed);
    }

    /**
     * move speed to correct {@code int},
     * it should be between [{@link #minSpeed}, {@link #maxSpeed}].<br>
     * <br>
     * if {@code speed > maxSpeed} speed will change to {@link #maxSpeed},<br>
     * if {@code speed < minSpeed} speed will change to {@link #minSpeed}.<br>
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
     * it should be between [{@link #minSpeed}, {@link #maxSpeed}].<br>
     * <br>
     * if {@code speed > maxSpeed} speed will change to {@link #maxSpeed},<br>
     * if {@code speed < minSpeed} speed will change to {@link #minSpeed}.
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
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        this.speed = speed;

        float newDegree = getDegreeAtSpeed(speed);
        if (newDegree == degree)
            return;

        cancelSpeedAnimator();
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
     * the speedometer to {@link #maxSpeed}.
     *
     * @see #realSpeedTo(int)
     * @see #slowDown()
     */
    public void speedUp() {
        realSpeedTo(getMaxSpeed());
    }

    /**
     * this method use {@code #realSpeedTo()} to slow down
     * the speedometer to {@link #minSpeed}.
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
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        this.speed = speed;

        float newDegree = getDegreeAtSpeed(speed);
        if (newDegree == degree)
            return;

        cancelSpeedAnimator();
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

    /**
     * check if {@link #withTremble} true, and run tremble.
     */
    private void tremble() {
        cancelTremble();
        if (!isWithTremble())
            return;
        Random random = new Random();
        float mad = trembleDegree * random.nextFloat() * ((random.nextBoolean()) ? -1 :1);
        float originalDegree = getDegreeAtSpeed(speed);
        mad = (originalDegree+mad > endDegree) ? endDegree - originalDegree
                : (originalDegree+mad < startDegree) ? startDegree - originalDegree : mad;
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelSpeedAnimator();
        attachedToWindow = false;
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
     * get correct speed as string to <b>Draw</b>.
     * @return correct speed to draw.
     */
    protected String getSpeedText() {
        return String.format(locale, "%.1f", correctSpeed);
    }

    /**
     * get Max speed as string to <b>Draw</b>.
     * @return Max speed to draw.
     */
    protected String getMaxSpeedText() {
        return String.format(locale, "%d", maxSpeed);
    }

    /**
     * get Min speed as string to <b>Draw</b>.
     * @return Min speed to draw.
     */
    protected String getMinSpeedText() {
        return String.format(locale, "%d", minSpeed);
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
     * @see #getMinSpeed()
     * @see #setMaxSpeed(int)
     */
    public int getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * change max speed.<br>
     * this method well call {@link #speedTo(int)} method
     * to make the change smooth.<br>
     * if {@code maxSpeed <= minSpeed} will ignore.
     *
     * @param maxSpeed new MAX Speed.
     */
    public void setMaxSpeed(int maxSpeed) {
        if (maxSpeed <= minSpeed)
            return;
        this.maxSpeed = maxSpeed;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        speedTo(speed);
    }

    /**
     * get min speed in speedometer, default min speed is 0.
     * @return min speed.
     *
     * @see #getMaxSpeed()
     * @see #setMinSpeed(int)
     */
    public int getMinSpeed() {
        return minSpeed;
    }

    /**
     * change min speed.<br>
     * this method well call {@link #speedTo(int)} method
     * to make the change smooth.<br>
     * if {@code minSpeed >= maxSpeed} will ignore.
     *
     * @param minSpeed new MAX Speed.
     */
    public void setMinSpeed(int minSpeed) {
        if (minSpeed >= maxSpeed)
            return;
        this.minSpeed = minSpeed;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        speedTo(speed);
    }

    /**
     * get correct speed as <b>percent</b>.
     * @return percent speed, between [0,100].
     */
    public float getPercentSpeed() {
        return (correctSpeed - minSpeed) * 100f / (float)(maxSpeed - minSpeed);
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        if (!attachedToWindow)
            return;
        indicator.setIndicatorColor(indicatorColor);
        invalidate();
    }

    public int getCenterCircleColor() {
        return centerCircleColor;
    }

    public void setCenterCircleColor(int centerCircleColor) {
        this.centerCircleColor = centerCircleColor;
        if (!attachedToWindow)
            return;
        invalidate();
    }

    public int getMarkColor() {
        return markColor;
    }

    public void setMarkColor(int markColor) {
        this.markColor = markColor;
        if (!attachedToWindow)
            return;
        invalidate();
    }

    public int getLowSpeedColor() {
        return lowSpeedColor;
    }

    public void setLowSpeedColor(int lowSpeedColor) {
        this.lowSpeedColor = lowSpeedColor;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getMediumSpeedColor() {
        return mediumSpeedColor;
    }

    public void setMediumSpeedColor(int mediumSpeedColor) {
        this.mediumSpeedColor = mediumSpeedColor;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getHighSpeedColor() {
        return highSpeedColor;
    }

    public void setHighSpeedColor(int highSpeedColor) {
        this.highSpeedColor = highSpeedColor;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
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
        if (!attachedToWindow)
            return;
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
        if (!attachedToWindow)
            return;
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
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
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
     * @see #setUnitTextSize(float)
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        if (!attachedToWindow)
            return;
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
     * @see #setUnitTextSize(float)
     */
    public void setSpeedTextSize(float speedTextSize) {
        this.speedTextSize = speedTextSize;
        speedTextPaint.setTextSize(speedTextSize);
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * change just unit text size.
     * @param unitTextSize new size in pixel
     *
     * @see #dpTOpx(float)
     * @see #setSpeedTextSize(float)
     * @see #setTextSize(float)
     */
    public void setUnitTextSize(float unitTextSize) {
        this.unitTextSize = unitTextSize;
        unitTextPaint.setTextSize(unitTextSize);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public float getUnitTextSize() {
        return unitTextSize;
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
        if (!attachedToWindow)
            return;
        invalidate();
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    public void setSpeedometerWidth(float speedometerWidth) {
        this.speedometerWidth = speedometerWidth;
        if (!attachedToWindow)
            return;
        indicator.setSpeedometerWidth(speedometerWidth);
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * this well call when (int) speed change.
     * @param onSpeedChangeListener The callback that will run.
     */
    public void setOnSpeedChangeListener(OnSpeedChangeListener onSpeedChangeListener) {
        this.onSpeedChangeListener = onSpeedChangeListener;
    }

    protected int getStartDegree() {
        return startDegree;
    }

    /**
     * change the start of speedometer (at {@link #minSpeed}).
     * @param startDegree the start of speedometer.
     * @throws IllegalArgumentException if {@code startDegree} negative.
     * @throws IllegalArgumentException if {@code startDegree >= endDegree}.
     * @throws IllegalArgumentException if the difference between {@code endDegree and startDegree} bigger than 360.
     */
    public void setStartDegree(int startDegree) {
        this.startDegree = startDegree;
        checkStartAndEndDegree();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    protected int getEndDegree() {
        return endDegree;
    }

    /**
     * change the end of speedometer (at {@link #maxSpeed}).
     * @param endDegree the end of speedometer.
     * @throws IllegalArgumentException if {@code endDegree} negative.
     * @throws IllegalArgumentException if {@code endDegree <= startDegree}.
     * @throws IllegalArgumentException if the difference between {@code endDegree and startDegree} bigger than 360.
     */
    public void setEndDegree(int endDegree) {
        this.endDegree = endDegree;
        checkStartAndEndDegree();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getLowSpeedPercent() {
        return lowSpeedPercent;
    }

    public float getLowSpeedOffset() {
        return lowSpeedPercent/100f;
    }

    /**
     * to change low speed area.
     * @param lowSpeedPercent the long of low speed area as percent,
     *                        must be between {@code [0,100]}.
     * @throws IllegalArgumentException if {@code lowSpeedPercent} out of range.
     * @throws IllegalArgumentException if {@code lowSpeedPercent > mediumSpeedPercent}.
     */
    public void setLowSpeedPercent(int lowSpeedPercent) {
        this.lowSpeedPercent = lowSpeedPercent;
        checkSpeedometerPercent();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getMediumSpeedPercent() {
        return mediumSpeedPercent;
    }

    public float getMediumSpeedOffset() {
        return mediumSpeedPercent/100f;
    }

    /**
     * to change medium speed area.
     * @param mediumSpeedPercent the long of medium speed area as percent,
     *                        must be between {@code [0,100]}.
     * @throws IllegalArgumentException if {@code mediumSpeedPercent} out of range.
     * @throws IllegalArgumentException if {@code mediumSpeedPercent < lowSpeedPercent}.
     */
    public void setMediumSpeedPercent(int mediumSpeedPercent) {
        this.mediumSpeedPercent = mediumSpeedPercent;
        checkSpeedometerPercent();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public boolean isSpeedometerTextRightToLeft() {
        return speedometerTextRightToLeft;
    }

    /**
     * to support Right To Left Text.
     * @param speedometerTextRightToLeft true to flip text right to left.
     */
    public void setSpeedometerTextRightToLeft(boolean speedometerTextRightToLeft) {
        this.speedometerTextRightToLeft = speedometerTextRightToLeft;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return View width without padding.
     */
    public int getWidthPa() {
        return getWidth() - (padding*2);
    }

    /**
     * @return View height without padding.
     */
    public int getHeightPa() {
        return getHeight() - (padding*2);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        updatePadding();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        updatePadding();
    }

    /**
     * Display new Note for 3 seconds.
     * @param note to display.
     */
    public void addNote(Note note) {
        addNote(note, 3000);
    }

    /**
     * Display new Note for custom seconds.
     * @param note to display.
     * @param showTimeMillisecond time to remove Note.
     */
    public void addNote(final Note note, long showTimeMillisecond) {
        note.build(getWidth());
        notes.add(note);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (attachedToWindow) {
                    notes.remove(note);
                    postInvalidate();
                }
            }
        }, showTimeMillisecond);
        invalidate();
    }

    /**
     * remove All Notes.
     */
    public void removeAllNotes() {
        notes.clear();
        invalidate();
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * set Locale to localizing digits to the given locale,
     * for speed Text and speedometer Text.
     * @param locale the locale to apply, {@code null} value means no localization.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * draw minSpeedText and maxSpeedText aat default Position.
     * @param c canvas to draw.
     */
    protected void drawDefaultMinAndMaxSpeedPosition(Canvas c) {
        if (getStartDegree()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getStartDegree()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getStartDegree()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getStartDegree() + 90f, getWidth()/2f, getHeight()/2f);
        c.rotate(-(getStartDegree() + 90f), getWidthPa()/2f - textPaint.getTextSize() + padding, textPaint.getTextSize() + padding);
        c.drawText(getMinSpeedText(), getWidthPa()/2f - textPaint.getTextSize() + padding
                , textPaint.getTextSize() + padding, textPaint);
        c.restore();
        if (getEndDegree()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getEndDegree()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getEndDegree()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getEndDegree() + 90f, getWidth()/2f, getHeight()/2f);
        c.rotate(-(getEndDegree() + 90f), getWidthPa()/2f + textPaint.getTextSize() + padding, textPaint.getTextSize() + padding);
        c.drawText(getMaxSpeedText(), getWidthPa()/2f + textPaint.getTextSize() + padding
                , textPaint.getTextSize() + padding, textPaint);
        c.restore();
    }

    /**
     * check if correct speed in <b>Low Speed Section</b>.
     * @return true if correct speed in Low Speed Section.
     *
     * @see #setLowSpeedPercent(int)
     */
    public boolean isInLowSection() {
        return (endDegree - startDegree)*getLowSpeedOffset() + startDegree >= degree;
    }

    /**
     * check if correct speed in <b>Medium Speed Section</b>.
     * @return true if correct speed in Medium Speed Section
     * , and it is not in Low Speed Section.
     *
     * @see #setMediumSpeedPercent(int)
     */
    public boolean isInMediumSection() {
        return (endDegree - startDegree)*getMediumSpeedOffset() + startDegree >= degree && !isInLowSection();
    }

    /**
     * check if correct speed in <b>High Speed Section</b>.
     * @return true if correct speed in High Speed Section
     * , and it is not in Low Speed Section or Medium Speed Section.
     */
    public boolean isInHighSection() {
        return degree > (endDegree - startDegree)*getMediumSpeedOffset() + startDegree;
    }

    public int getPadding() {
        return padding;
    }

    public float getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
        indicator.setIndicatorWidth(indicatorWidth);
    }

    protected void indicatorEffects(boolean withEffects) {
        indicator.withEffects(withEffects);
    }

    /**
     * change indicator shape.
     * @param indicator new indicator.
     */
    public void setIndicator (Indicator.Indicators indicator) {
        switch (indicator) {
            case NoIndicator:
                this.indicator = new NoIndicator(this);
                break;
            case NormalIndicator:
                this.indicator = new NormalIndicator(this);
                break;
            case NormalSmallIndicator:
                this.indicator = new NormalSmallIndicator(this);
                break;
            case TriangleIndicator:
                this.indicator = new TriangleIndicator(this);
                break;
            case SpindleIndicator:
                this.indicator = new SpindleIndicator(this);
                break;
            case LineIndicator:
                this.indicator = new LineIndicator(this);
                break;
            case HalfLineIndicator:
                this.indicator = new HalfLineIndicator(this);
                break;
            case QuarterLineIndicator:
                this.indicator = new QuarterLineIndicator(this);
                break;
        }
    }
}
