package com.github.anastr.speedviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.github.anastr.speedviewlib.util.OnSectionChangeListener;
import com.github.anastr.speedviewlib.util.OnSpeedChangeListener;

import java.util.Locale;
import java.util.Random;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unused")
public abstract class Gauge extends View {

    private Paint speedUnitTextBitmapPaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
    protected TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint speedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG),
            unitTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    /** the text after speedText */
    private String unit = "Km/h";
    /** automatically increase and decrease speed value around the {@link #speed} */
    private boolean withTremble = true;

    /** the max range in speedometer, {@code default = 100} */
    private float maxSpeed = 100f;
    /** the min range in speedometer, {@code default = 0} */
    private float minSpeed = 0f;
    /**
     * the last speed which you set by {@link #speedTo(float)}
     * or {@link #speedTo(float, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     */
    private float speed = minSpeed;
    /** what is speed now in <b>int</b> */
    private int currentIntSpeed = 0;
    /** what is speed now in <b>float</b> */
    private float currentSpeed = minSpeed;
    private boolean isSpeedIncrease = false;
    /** a degree to increases and decreases speed value around {@link #speed} */
    private float trembleDegree = 4f;
    private int trembleDuration = 1000;

    private ValueAnimator speedAnimator, trembleAnimator, realSpeedAnimator;
    private boolean canceled = false;
    private OnSpeedChangeListener onSpeedChangeListener;
    private OnSectionChangeListener onSectionChangeListener;
    /** this animatorListener to call {@link #tremble()} method when animator done */
    private Animator.AnimatorListener animatorListener;

    /** to contain all drawing that doesn't change */
    protected Bitmap backgroundBitmap;
    private Paint backgroundBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int padding = 0;
    /** view width without padding */
    private int widthPa = 0;
    /** View height without padding */
    private int heightPa = 0;

    /** low speed area */
    private int lowSpeedPercent = 60;
    /** medium speed area */
    private int mediumSpeedPercent = 87;

    public static final byte LOW_SECTION = 1;
    public static final byte MEDIUM_SECTION = 2;
    public static final byte HIGH_SECTION = 3;
    private byte section = LOW_SECTION;

    private boolean speedometerTextRightToLeft = false;

    private boolean attachedToWindow = false;

    protected float translatedDx = 0;
    protected float translatedDy = 0;

    /** object to set text digits locale */
    private Locale locale = Locale.getDefault();

    /** Number expresses the Acceleration, between (0, 1] */
    private float accelerate = .1f;
    /** Number expresses the Deceleration, between (0, 1] */
    private float decelerate = .1f;

    private Position speedTextPosition = Position.BOTTOM_CENTER;
    /** space between unitText and speedText */
    private float unitSpeedInterval = dpTOpx(1);
    private float speedTextPadding = dpTOpx(20f);
    private boolean unitUnderSpeedText = false;
    private Bitmap speedUnitTextBitmap;
    private Canvas speedUnitTextCanvas;

    /** draw speed text as <b>integer</b> .*/
    public static final byte INTEGER_FORMAT = 0;
    /** draw speed text as <b>.1 float</b>. */
    public static final byte FLOAT_FORMAT = 1;
    /** number of decimal places */
    private int speedTextFormat = FLOAT_FORMAT;
    /** number of decimal places */
    private int tickTextFormat = INTEGER_FORMAT;

    public Gauge(Context context) {
        this(context, null);
    }

    public Gauge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Gauge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
        initAttributeValue();
    }

    private void init() {
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(dpTOpx(10f));
        textPaint.setTextAlign(Paint.Align.CENTER);
        speedTextPaint.setColor(0xFF000000);
        speedTextPaint.setTextSize(dpTOpx(18f));
        unitTextPaint.setColor(0xFF000000);
        unitTextPaint.setTextSize(dpTOpx(15f));

        if (Build.VERSION.SDK_INT >= 11) {
            speedAnimator = ValueAnimator.ofFloat(0f, 1f);
            trembleAnimator = ValueAnimator.ofFloat(0f, 1f);
            realSpeedAnimator = ValueAnimator.ofFloat(0f, 1f);
            animatorListener = new Animator.AnimatorListener() {
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
        }
        defaultGaugeValues();
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Gauge, 0, 0);

        maxSpeed = a.getFloat(R.styleable.Gauge_sv_maxSpeed, maxSpeed);
        minSpeed = a.getFloat(R.styleable.Gauge_sv_minSpeed, minSpeed);
        speed = minSpeed;
        currentSpeed = minSpeed;
        withTremble = a.getBoolean(R.styleable.Gauge_sv_withTremble, withTremble);
        textPaint.setColor(a.getColor(R.styleable.Gauge_sv_textColor, textPaint.getColor()));
        textPaint.setTextSize(a.getDimension(R.styleable.Gauge_sv_textSize, textPaint.getTextSize()));
        speedTextPaint.setColor(a.getColor(R.styleable.Gauge_sv_speedTextColor, speedTextPaint.getColor()));
        speedTextPaint.setTextSize(a.getDimension(R.styleable.Gauge_sv_speedTextSize, speedTextPaint.getTextSize()));
        unitTextPaint.setColor(a.getColor(R.styleable.Gauge_sv_unitTextColor, unitTextPaint.getColor()));
        unitTextPaint.setTextSize(a.getDimension(R.styleable.Gauge_sv_unitTextSize, unitTextPaint.getTextSize()));
        String unit = a.getString(R.styleable.Gauge_sv_unit);
        this.unit =  (unit != null) ? unit : this.unit;
        trembleDegree = a.getFloat(R.styleable.Gauge_sv_trembleDegree, trembleDegree);
        trembleDuration = a.getInt(R.styleable.Gauge_sv_trembleDuration, trembleDuration);
        lowSpeedPercent = a.getInt(R.styleable.Gauge_sv_lowSpeedPercent, lowSpeedPercent);
        mediumSpeedPercent = a.getInt(R.styleable.Gauge_sv_mediumSpeedPercent, mediumSpeedPercent);
        speedometerTextRightToLeft = a.getBoolean(R.styleable.Gauge_sv_textRightToLeft, speedometerTextRightToLeft);
        accelerate = a.getFloat(R.styleable.Gauge_sv_accelerate, accelerate);
        decelerate = a.getFloat(R.styleable.Gauge_sv_decelerate, decelerate);
        unitUnderSpeedText = a.getBoolean(R.styleable.Gauge_sv_unitUnderSpeedText, unitUnderSpeedText);
        unitSpeedInterval = a.getDimension(R.styleable.Gauge_sv_unitSpeedInterval, unitSpeedInterval);
        speedTextPadding = a.getDimension(R.styleable.Gauge_sv_speedTextPadding, speedTextPadding);
        String speedTypefacePath = a.getString(R.styleable.Gauge_sv_speedTextTypeface);
        if (speedTypefacePath != null)
            setSpeedTextTypeface(Typeface.createFromAsset(getContext().getAssets(), speedTypefacePath));
        String typefacePath = a.getString(R.styleable.Gauge_sv_textTypeface);
        if (typefacePath != null)
            setTextTypeface(Typeface.createFromAsset(getContext().getAssets(), typefacePath));
        int position = a.getInt(R.styleable.Gauge_sv_speedTextPosition, -1);
        if (position != -1)
            setSpeedTextPosition(Position.values()[position]);
        int speedFormat = a.getInt(R.styleable.Gauge_sv_speedTextFormat, -1);
        if (speedFormat != -1)
            setSpeedTextFormat(speedFormat);
        int tickFormat = a.getInt(R.styleable.Gauge_sv_tickTextFormat, -1);
        if (tickFormat != -1)
            setTickTextFormat(tickFormat);
        a.recycle();
        checkSpeedometerPercent();
        checkAccelerate();
        checkDecelerate();
        checkTrembleData();
    }

    private void initAttributeValue() {
        if (unitUnderSpeedText) {
            speedTextPaint.setTextAlign(Paint.Align.CENTER);
            unitTextPaint.setTextAlign(Paint.Align.CENTER);
        }
        else {
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            unitTextPaint.setTextAlign(Paint.Align.LEFT);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        if (widthPa > 0 && heightPa > 0)
            speedUnitTextBitmap = Bitmap.createBitmap(widthPa
                    , heightPa, Bitmap.Config.ARGB_8888);
        speedUnitTextCanvas = new Canvas(speedUnitTextBitmap);
    }

    private void checkSpeedometerPercent() {
        if (lowSpeedPercent > mediumSpeedPercent)
            throw new IllegalArgumentException("lowSpeedPercent must be smaller than mediumSpeedPercent");
        if (lowSpeedPercent > 100 || lowSpeedPercent < 0)
            throw new IllegalArgumentException("lowSpeedPercent must be between [0, 100]");
        if (mediumSpeedPercent > 100 || mediumSpeedPercent < 0)
            throw new IllegalArgumentException("mediumSpeedPercent must be between [0, 100]");
    }

    private void checkAccelerate() {
        if (accelerate > 1f || accelerate <= 0)
            throw new IllegalArgumentException("accelerate must be between (0, 1]");
    }

    private void checkDecelerate() {
        if (decelerate > 1f || decelerate <= 0)
            throw new IllegalArgumentException("decelerate must be between (0, 1]");
    }

    private void checkTrembleData() {
        if (trembleDegree < 0)
            throw new IllegalArgumentException("trembleDegree  can't be Negative");
        if (trembleDuration < 0)
            throw new IllegalArgumentException("trembleDuration  can't be Negative");
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

    /**
     * add default values for Gauge inside this method,
     * call super setting method to set default value,
     * Ex :
     * <pre>
     *     super.setBackgroundCircleColor(Color.TRANSPARENT);
     * </pre>
     */
    abstract protected void defaultGaugeValues();
    /**
     * notice that {@link #backgroundBitmap} must recreate.
     */
    abstract protected void updateBackgroundBitmap();

    /**
     * notice that padding or size have changed.
     */
    private void updatePadding(int left, int top, int right, int bottom) {
        padding = Math.max(Math.max(left, right), Math.max(top, bottom));
        widthPa  = getWidth() - padding*2;
        heightPa = getHeight() - padding*2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(translatedDx, translatedDy);

        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundBitmapPaint);

        // check onSpeedChangeEvent.
        int newSpeed = (int) currentSpeed;
        if (newSpeed != currentIntSpeed && onSpeedChangeListener != null) {
            boolean byTremble = Build.VERSION.SDK_INT >= 11 && trembleAnimator.isRunning();
            boolean isSpeedUp = newSpeed > currentIntSpeed;
            int update = isSpeedUp ? 1 : -1;
            // this loop to pass on all speed values,
            // to safe handle by call gauge.getCorrectIntSpeed().
            while (currentIntSpeed != newSpeed) {
                currentIntSpeed += update;
                onSpeedChangeListener.onSpeedChange(this, isSpeedUp, byTremble);
            }
        }
        currentIntSpeed = newSpeed;

        // check onSectionChangeEvent.
        byte newSection = getSection();
        if (section != newSection)
            onSectionChangeEvent(section, newSection);
        section = newSection;
    }

    /**
     * draw speed and unit text at {@link #speedTextPosition},
     * this method must call in subSpeedometer's {@code onDraw} method.
     * @param canvas view canvas to draw.
     */
    protected void drawSpeedUnitText(Canvas canvas) {
        RectF r = getSpeedUnitTextBounds();
        updateSpeedUnitTextBitmap(getSpeedText());
        canvas.drawBitmap(speedUnitTextBitmap, r.left - speedUnitTextBitmap.getWidth() *.5f + r.width() *.5f
                , r.top - speedUnitTextBitmap.getHeight() *.5f + r.height() *.5f, speedUnitTextBitmapPaint);
    }

    /**
     * clear {@link #speedUnitTextBitmap} and draw speed and unit Text
     * taking into consideration {@link #speedometerTextRightToLeft} and {@link #unitUnderSpeedText}.
     */
    private void updateSpeedUnitTextBitmap(String speedText) {
        speedUnitTextBitmap.eraseColor(0);

        if (unitUnderSpeedText) {
            speedUnitTextCanvas.drawText(speedText, speedUnitTextBitmap.getWidth() *.5f
                    , speedUnitTextBitmap.getHeight() *.5f  - unitSpeedInterval *.5f, speedTextPaint);
            speedUnitTextCanvas.drawText(unit, speedUnitTextBitmap.getWidth() *.5f
                    , speedUnitTextBitmap.getHeight() *.5f + unitTextPaint.getTextSize() + unitSpeedInterval *.5f, unitTextPaint);
        }
        else {
            float speedX;
            float unitX;
            if (isSpeedometerTextRightToLeft()) {
                unitX = speedUnitTextBitmap.getWidth() *.5f - getSpeedUnitTextWidth() *.5f;
                speedX = unitX + unitTextPaint.measureText(unit) + unitSpeedInterval;
            }
            else {
                speedX = speedUnitTextBitmap.getWidth() *.5f - getSpeedUnitTextWidth() *.5f;
                unitX = speedX + speedTextPaint.measureText(speedText) + unitSpeedInterval;
            }
            float h = speedUnitTextBitmap.getHeight() *.5f + getSpeedUnitTextHeight() *.5f;
            speedUnitTextCanvas.drawText(speedText, speedX, h, speedTextPaint);
            speedUnitTextCanvas.drawText(unit, unitX, h, unitTextPaint);
        }
    }

    /**
     * speed-unit text position and size.
     * @return speed-unit's rect.
     */
    protected RectF getSpeedUnitTextBounds() {
        float left = getWidthPa()*speedTextPosition.x -translatedDx + padding
                - getSpeedUnitTextWidth()*speedTextPosition.width
                + speedTextPadding*speedTextPosition.paddingH;
        float top = getHeightPa()*speedTextPosition.y -translatedDy + padding
                - getSpeedUnitTextHeight()*speedTextPosition.height
                + speedTextPadding*speedTextPosition.paddingV;
        return new RectF(left, top, left + getSpeedUnitTextWidth(), top + getSpeedUnitTextHeight());
    }

    /**
     * @return the width of speed & unit text at runtime.
     */
    private float getSpeedUnitTextWidth() {
        return unitUnderSpeedText ?
                Math.max(speedTextPaint.measureText(getSpeedText()), unitTextPaint.measureText(getUnit()))
                : speedTextPaint.measureText(getSpeedText()) + unitTextPaint.measureText(getUnit()) + unitSpeedInterval;
    }

    /**
     * @return the height of speed & unit text at runtime.
     */
    private float getSpeedUnitTextHeight() {
        return unitUnderSpeedText ?
                speedTextPaint.getTextSize() + unitTextPaint.getTextSize() + unitSpeedInterval
                : Math.max(speedTextPaint.getTextSize(), unitTextPaint.getTextSize());
    }

    /**
     * create canvas to draw {@link #backgroundBitmap}.
     * @return {@link #backgroundBitmap}'s canvas.
     */
    protected Canvas createBackgroundBitmapCanvas() {
        if (getWidth() == 0 || getHeight() == 0)
            return new Canvas();
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        return new Canvas(backgroundBitmap);
    }

    /**
     * Implement this method to handle section change event.
     * @param oldSection where speed value came from.
     * @param newSection where speed value move to.
     */
    protected void onSectionChangeEvent(byte oldSection, byte newSection) {
        if (onSectionChangeListener != null)
            onSectionChangeListener.onSectionChangeListener(oldSection, newSection);
    }

    /**
     * stop speedometer and run tremble if {@link #withTremble} is true.
     * use this method just when you wont to stop {@code speedTo and realSpeedTo}.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void stop() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        if (!speedAnimator.isRunning() && !realSpeedAnimator.isRunning())
            return;
        speed = currentSpeed;
        cancelSpeedAnimator();
        tremble();
    }

    /**
     * cancel all animators without call {@link #tremble()}.
     */
    protected void cancelSpeedAnimator() {
        cancelSpeedMove();
        cancelTremble();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void cancelTremble() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        canceled = true;
        trembleAnimator.cancel();
        canceled = false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void cancelSpeedMove() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        canceled = true;
        speedAnimator.cancel();
        realSpeedAnimator.cancel();
        canceled = false;
    }

    /**
     * move speed value to new speed without animation.
     * @param speed current speed to move.
     */
    public void setSpeedAt(float speed) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        isSpeedIncrease = speed > currentSpeed;
        this.speed = speed;
        this.currentSpeed = speed;
        cancelSpeedAnimator();
        invalidate();
        tremble();
    }

    /**
     * move speed to percent value.
     * @param percent percent value to move, must be between [0,100].
     *
     * @see #speedTo(float)
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int, long)
     * @see #realSpeedTo(float)
     */
    public void speedPercentTo(int percent) {
        speedPercentTo(percent, 2000);
    }

    /**
     * move speed to percent value.
     * @param percent percent value to move, must be between [0,100].
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     *
     * @see #speedTo(float)
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(float)
     */
    public void speedPercentTo(int percent, long moveDuration) {
        speedTo(getSpeedValue(percent), moveDuration);
    }

    /**
     * move speed to current value smoothly,
     * it should be between [{@link #minSpeed}, {@link #maxSpeed}].<br>
     * <br>
     * if {@code speed > maxSpeed} speed value will move to {@link #maxSpeed},<br>
     * if {@code speed < minSpeed} speed value will move to {@link #minSpeed}.<br>
     *
     * it is the same {@link #speedTo(float, long)}
     * with default {@code moveDuration = 2000}.
     *
     * @param speed current speed to move.
     *
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(float)
     */
    public void speedTo(float speed) {
        speedTo(speed, 2000);
    }

    /**
     * move speed to current value smoothly with animation duration,
     * it should be between [{@link #minSpeed}, {@link #maxSpeed}].<br>
     * <br>
     * if {@code speed > maxSpeed} speed value will move to {@link #maxSpeed},<br>
     * if {@code speed < minSpeed} speed value will move to {@link #minSpeed}.
     *
     * @param speed current speed to move.
     * @param moveDuration The length of animation, in milliseconds.
     *                     This value cannot be negative.
     *
     * @see #speedTo(float)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(float)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void speedTo(float speed, long moveDuration) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        if (speed == this.speed)
            return;
        this.speed = speed;

        if (Build.VERSION.SDK_INT < 11){
            setSpeedAt(speed);
            return;
        }

        isSpeedIncrease = speed > currentSpeed;

        cancelSpeedAnimator();
        speedAnimator = ValueAnimator.ofFloat(currentSpeed, speed);
        speedAnimator.setInterpolator(new DecelerateInterpolator());
        speedAnimator.setDuration(moveDuration);
        speedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentSpeed = (float) speedAnimator.getAnimatedValue();
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
     * @see #realSpeedTo(float)
     * @see #slowDown()
     */
    public void speedUp() {
        realSpeedTo(getMaxSpeed());
    }

    /**
     * this method use {@code #realSpeedTo()} to slow down
     * the speedometer to {@link #minSpeed}.
     *
     * @see #realSpeedTo(float)
     * @see #speedUp()
     */
    public void slowDown() {
        realSpeedTo(0);
    }

    /**
     * move speed to percent value by using {@link #realSpeedTo(float)} method.
     * @param percent percent value to move, must be between [0,100].
     */
    public void realSpeedPercentTo(float percent) {
        realSpeedTo(getSpeedValue(percent));
    }

    /**
     * to make speedometer some real.
     * <br>
     * when <b>speed up</b> : speed value will increase <i>slowly</i> by {@link #accelerate}.
     * <br>
     * when <b>slow down</b> : speed value will decrease <i>rapidly</i> by {@link #decelerate}.
     * @param speed current speed to move.
     *
     * @see #speedTo(float)
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int)
     * @see #speedUp()
     * @see #slowDown()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void realSpeedTo(float speed) {
        boolean oldIsSpeedUp = this.speed > currentSpeed;
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        if (speed == this.speed)
            return;
        this.speed = speed;

        if (Build.VERSION.SDK_INT < 11) {
            setSpeedAt(speed);
            return;
        }
        isSpeedIncrease = speed > currentSpeed;
        if (realSpeedAnimator.isRunning() && oldIsSpeedUp == isSpeedIncrease)
            return;

        cancelSpeedAnimator();
        realSpeedAnimator = ValueAnimator.ofInt((int) currentSpeed, (int)speed);
        realSpeedAnimator.setRepeatCount(ValueAnimator.INFINITE);
        realSpeedAnimator.setInterpolator(new LinearInterpolator());
        realSpeedAnimator.setDuration(Math.abs((long) ((speed - currentSpeed) * 10) ));
        final float finalSpeed = speed;
        realSpeedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isSpeedIncrease) {
                    float per = 100.005f-getPercentSpeed();
                    currentSpeed += (accelerate * 10f) * per *.01f;
                    if (currentSpeed > finalSpeed)
                        currentSpeed = finalSpeed;
                }
                else {
                    float per = getPercentSpeed()+.005f;
                    currentSpeed -= (decelerate * 10f) * per *.01f +.1f;
                    if (currentSpeed < finalSpeed)
                        currentSpeed = finalSpeed;
                }
                postInvalidate();
                if (finalSpeed == currentSpeed)
                    stop();
            }
        });
        realSpeedAnimator.addListener(animatorListener);
        realSpeedAnimator.start();
    }

    /**
     * check if {@link #withTremble} true, and run tremble.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void tremble() {
        cancelTremble();
        if (!isWithTremble() || Build.VERSION.SDK_INT < 11)
            return;
        Random random = new Random();
        float mad = trembleDegree * random.nextFloat() * ((random.nextBoolean()) ? -1 :1);
        mad = (speed + mad > maxSpeed) ? maxSpeed - speed
                : (speed + mad < minSpeed) ? minSpeed - speed : mad;
        trembleAnimator = ValueAnimator.ofFloat(currentSpeed, speed + mad);
        trembleAnimator.setInterpolator(new DecelerateInterpolator());
        trembleAnimator.setDuration(trembleDuration);
        trembleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                isSpeedIncrease = (float) trembleAnimator.getAnimatedValue() > currentSpeed;
                currentSpeed = (float) trembleAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        trembleAnimator.addListener(animatorListener);
        trembleAnimator.start();
    }

    /**
     * @param percentSpeed between [0, 100].
     * @return speed value at current percentSpeed.
     */
    private float getSpeedValue(float percentSpeed) {
        percentSpeed = (percentSpeed > 100) ? 100 : (percentSpeed < 0) ? 0 : percentSpeed;
        return percentSpeed * (maxSpeed - minSpeed) *.01f + minSpeed;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
        updateBackgroundBitmap();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelSpeedAnimator();
        attachedToWindow = false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putFloat("speed", speed);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        speed = bundle.getFloat("speed");
        state = bundle.getParcelable("superState");
        super.onRestoreInstanceState(state);
        setSpeedAt(speed);
    }


    /**
     * default : 4 speed value.
     * @param trembleDegree a speed value to increases and decreases current speed around {@link #speed}.
     * @throws IllegalArgumentException If trembleDegree is Negative.
     */
    public void setTrembleDegree (float trembleDegree) {
        setTrembleData(trembleDegree, trembleDuration);
    }

    /**
     * default : 1000 millisecond.
     * @param trembleDuration tremble Animation duration in millisecond.
     * @throws IllegalArgumentException If trembleDuration is Negative.
     */
    public void setTrembleDuration (int trembleDuration) {
        setTrembleData(trembleDegree, trembleDuration);
    }

    /**
     * tremble control.
     * @param trembleDegree a speed value to increases and decreases current around {@link #speed}.
     * @param trembleDuration tremble Animation duration in millisecond.
     *
     * @see #setTrembleDegree(float)
     * @see #setTrembleDuration(int)
     * @throws IllegalArgumentException If trembleDegree OR trembleDuration is Negative.
     */
    public void setTrembleData (float trembleDegree, int trembleDuration) {
        this.trembleDegree = trembleDegree;
        this.trembleDuration = trembleDuration;
        checkTrembleData();
    }

    /**
     * @return speed text's format, [{@link #INTEGER_FORMAT} or {@link #FLOAT_FORMAT}].
     */
    public int getSpeedTextFormat() {
        return speedTextFormat;
    }

    /**
     * change speed text's format [{@link #INTEGER_FORMAT} or {@link #FLOAT_FORMAT}]
     * or number of decimal places you want.
     * @param speedTextFormat new format.
     */
    public void setSpeedTextFormat(int speedTextFormat) {
        this.speedTextFormat = speedTextFormat;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return tick text's format, [{@link #INTEGER_FORMAT} or {@link #FLOAT_FORMAT}].
     */
    public int getTickTextFormat() {
        return tickTextFormat;
    }

    /**
     * change tick text's format [{@link #INTEGER_FORMAT} or {@link #FLOAT_FORMAT}]
     * or number of decimal places you want.
     * @param tickTextFormat new format.
     */
    public void setTickTextFormat(int tickTextFormat) {
        this.tickTextFormat = tickTextFormat;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * get current speed as string to <b>Draw</b>.
     * @return current speed to draw.
     */
    protected String getSpeedText() {
        return String.format(locale, "%."+speedTextFormat+"f", currentSpeed);
    }

    /**
     * get Max speed as string to <b>Draw</b>.
     * @return Max speed to draw.
     */
    protected String getMaxSpeedText() {
        return String.format(locale, "%."+tickTextFormat+"f", maxSpeed);
    }

    /**
     * get Min speed as string to <b>Draw</b>.
     * @return Min speed to draw.
     */
    protected String getMinSpeedText() {
        return String.format(locale, "%."+tickTextFormat+"f", minSpeed);
    }

    /**
     * <b>if true</b> : the speed value automatically will be increases and decreases
     * by {@link #trembleDegree} around last speed you set,
     * used to add some reality to speedometer.<br>
     * <b>if false</b> : nothing will done.
     * @param withTremble to play tremble Animation
     *
     * @see #setTrembleData(float, int)
     */
    public void setWithTremble(boolean withTremble) {
        this.withTremble = withTremble;
        tremble();
    }

    /**
     * @return whether speed value could increase and decrease automatically
     * around last speed by {@link #trembleDegree}.
     */
    public boolean isWithTremble() {
        return withTremble;
    }

    /**
     * @return the last speed which you set by {@link #speedTo(float)}
     * or {@link #speedTo(float, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     *
     * @see #getCurrentSpeed()
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * what is current speed now.
     * <p>It will give different results if {@link #withTremble} is running.</p>
     *
     * @return current speed now.
     * @see #setWithTremble(boolean)
     * @see #getSpeed()
     */
    public float getCurrentSpeed() {
        return currentSpeed;
    }

    /**
     * given a state of the speed change if it's increase or decrease.
     * @return is speed increase in the last change or not.
     */
    public boolean isSpeedIncrease() {
        return isSpeedIncrease;
    }

    /**
     * what is speed now in <b>integer</b>.
     * <p>
     *     safe method to handle all speed values in {@link #onSpeedChangeListener}.
     * </p>
     * @return current speed in Integer
     * @see #getCurrentSpeed()
     */
    public int getCurrentIntSpeed() {
        return currentIntSpeed;
    }

    /**
     * get max speed in speedometer, default max speed is 100.
     * @return max speed.
     *
     * @see #getMinSpeed()
     * @see #setMaxSpeed(float)
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * change max speed.<br>
     * this method will move {@link #currentSpeed} to its new position
     * immediately without animation.
     *
     * @param maxSpeed new MAX Speed.
     *
     * @throws IllegalArgumentException if {@code minSpeed >= maxSpeed}
     */
    public void setMaxSpeed(float maxSpeed) {
        setMinMaxSpeed(minSpeed, maxSpeed);
    }

    /**
     * get min speed in speedometer, default min speed is 0.
     * @return min speed.
     *
     * @see #getMaxSpeed()
     * @see #setMinSpeed(float)
     */
    public float getMinSpeed() {
        return minSpeed;
    }

    /**
     * change min speed.<br>
     * this method will move {@link #currentSpeed} to its new position
     * immediately without animation.
     *
     * @param minSpeed new MIN Speed.
     *
     * @throws IllegalArgumentException if {@code minSpeed >= maxSpeed}
     */
    public void setMinSpeed(float minSpeed) {
        setMinMaxSpeed(minSpeed, maxSpeed);
    }

    /**
     * change Min and Max speed.<br>
     * this method will move {@link #currentSpeed} to its new position
     * immediately without animation.
     *
     * @param minSpeed new MIN Speed.
     * @param maxSpeed new MAX Speed.
     *
     * @throws IllegalArgumentException if {@code minSpeed >= maxSpeed}
     */
    public void setMinMaxSpeed(float minSpeed, float maxSpeed) {
        if (minSpeed >= maxSpeed)
            throw new IllegalArgumentException("minSpeed must be smaller than maxSpeed !!");
        cancelSpeedAnimator();
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        setSpeedAt(speed);
    }

    /**
     * get current speed as <b>percent</b>.
     * @return percent speed, between [0,100].
     */
    public float getPercentSpeed() {
        return (currentSpeed - minSpeed) * 100f / (maxSpeed - minSpeed);
    }

    /**
     * @return offset speed, between [0,1].
     */
    public float getOffsetSpeed() {
        return (currentSpeed - minSpeed) / (maxSpeed - minSpeed);
    }

    /**
     * @return all text color without <b>speed, unit text</b>.
     */
    public int getTextColor() {
        return textPaint.getColor();
    }

    /**
     * change all text color without <b>speed, unit text</b>.
     * @param textColor new color.
     *
     * @see #setSpeedTextColor(int)
     * @see #setUnitTextColor(int)
     */
    public void setTextColor(int textColor) {
        textPaint.setColor(textColor);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return just speed text color.
     */
    public int getSpeedTextColor() {
        return speedTextPaint.getColor();
    }

    /**
     * change just speed text color.
     * @param speedTextColor new color.
     *
     * @see #setUnitTextColor(int)
     * @see #setTextColor(int)
     */
    public void setSpeedTextColor(int speedTextColor) {
        speedTextPaint.setColor(speedTextColor);
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * @return just unit text color.
     */
    public int getUnitTextColor() {
        return unitTextPaint.getColor();
    }

    /**
     * change just unit text color.
     * @param unitTextColor new color.
     *
     * @see #setSpeedTextColor(int)
     * @see #setTextColor(int)
     */
    public void setUnitTextColor(int unitTextColor) {
        unitTextPaint.setColor(unitTextColor);
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * @return all text size without <b>speed and unit text</b>.
     */
    public float getTextSize() {
        return textPaint.getTextSize();
    }

    /**
     * change all text size without <b>speed and unit text</b>.
     * @param textSize new size in pixel.
     *
     * @see #dpTOpx(float)
     * @see #setSpeedTextSize(float)
     * @see #setUnitTextSize(float)
     */
    public void setTextSize(float textSize) {
        textPaint.setTextSize(textSize);
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * @return just speed text size.
     */
    public float getSpeedTextSize() {
        return speedTextPaint.getTextSize();
    }

    /**
     * change just speed text size.
     * @param speedTextSize new size in pixel.
     *
     * @see #dpTOpx(float)
     * @see #setTextSize(float)
     * @see #setUnitTextSize(float)
     */
    public void setSpeedTextSize(float speedTextSize) {
        speedTextPaint.setTextSize(speedTextSize);
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * @return just unit text size.
     */
    public float getUnitTextSize() {
        return unitTextPaint.getTextSize();
    }

    /**
     * change just unit text size.
     * @param unitTextSize new size in pixel.
     *
     * @see #dpTOpx(float)
     * @see #setSpeedTextSize(float)
     * @see #setTextSize(float)
     */
    public void setUnitTextSize(float unitTextSize) {
        unitTextPaint.setTextSize(unitTextSize);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return unit text.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * unit text, the text after speed text.
     * @param unit unit text.
     */
    public void setUnit(String unit) {
        this.unit = unit;
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * Register a callback to be invoked when speed value changed (in integer).
     * @param onSpeedChangeListener maybe null, The callback that will run.
     */
    public void setOnSpeedChangeListener(OnSpeedChangeListener onSpeedChangeListener) {
        this.onSpeedChangeListener = onSpeedChangeListener;
    }

    /**
     * Register a callback to be invoked when
     * <a href="https://github.com/anastr/SpeedView/wiki/Usage#control-division-of-the-speedometer">section</a> changed.
     * @param onSectionChangeListener maybe null, The callback that will run.
     */
    public void setOnSectionChangeListener(OnSectionChangeListener onSectionChangeListener) {
        this.onSectionChangeListener = onSectionChangeListener;
    }

    /**
     * @return the long of low speed area (low section) as percent.
     */
    public int getLowSpeedPercent() {
        return lowSpeedPercent;
    }

    /**
     * @return the long of low speed area (low section) as Offset [0, 1].
     */
    public float getLowSpeedOffset() {
        return lowSpeedPercent *.01f;
    }

    /**
     * to change low speed area (low section).
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

    /**
     * @return the long of Medium speed area (Medium section) as percent.
     */
    public int getMediumSpeedPercent() {
        return mediumSpeedPercent;
    }

    /**
     * @return the long of Medium speed area (Medium section) as Offset [0, 1].
     */
    public float getMediumSpeedOffset() {
        return mediumSpeedPercent *.01f;
    }

    /**
     * to change medium speed area (medium section).
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

    /**
     * @return whether drawing unit text to left of speed text.
     */
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
        return widthPa;
    }

    /**
     * @return View height without padding.
     */
    public int getHeightPa() {
        return heightPa;
    }

    public int getViewSize() {
        return Math.max(getWidth(), getHeight());
    }

    public int getViewSizePa() {
        return Math.max(widthPa, heightPa);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        updatePadding(left, top, right, bottom);
        super.setPadding(padding, padding, padding, padding);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        updatePadding(start, top, end, bottom);
        super.setPaddingRelative(padding, padding, padding, padding);
    }

    /**
     * @return digit's Locale.
     */
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
     * check if current speed in <b>Low Speed Section</b>.
     * @return true if current speed in Low Section.
     *
     * @see #setLowSpeedPercent(int)
     */
    public boolean isInLowSection() {
        return (maxSpeed - minSpeed)*getLowSpeedOffset() + minSpeed >= currentSpeed;
    }

    /**
     * check if current speed in <b>Medium Speed Section</b>.
     * @return true if current speed in Medium Section
     * , and it is not in Low Section.
     *
     * @see #setMediumSpeedPercent(int)
     */
    public boolean isInMediumSection() {
        return (maxSpeed - minSpeed)*getMediumSpeedOffset() + minSpeed >= currentSpeed && !isInLowSection();
    }

    /**
     * check if current speed in <b>High Speed Section</b>.
     * @return true if current speed in High Section
     * , and it is not in Low Section or Medium Section.
     */
    public boolean isInHighSection() {
        return currentSpeed > (maxSpeed - minSpeed)*getMediumSpeedOffset() + minSpeed;
    }

    /**
     * @return current section,
     * used in condition : {@code if (speedometer.getSection() == speedometer.LOW_SECTION)}.
     */
    public byte getSection() {
        if (isInLowSection())
            return LOW_SECTION;
        else if (isInMediumSection())
            return MEDIUM_SECTION;
        else
            return HIGH_SECTION;
    }

    public int getPadding() {
        return padding;
    }

    /**
     * @return whether this view attached to Layout or not.
     */
    public boolean isAttachedToWindow() {
        return attachedToWindow;
    }

    /**
     * @return typeface for <b>speed and unit</b> text.
     */
    public Typeface getSpeedTextTypeface() {
        return speedTextPaint.getTypeface();
    }

    /**
     * change typeface for <b>speed and unit</b> text.
     * @param typeface Maybe null. The typeface to be installed.
     */
    public void setSpeedTextTypeface(Typeface typeface) {
        speedTextPaint.setTypeface(typeface);
        unitTextPaint.setTypeface(typeface);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return typeface for all texts without speed and unit text.
     */
    public Typeface getTextTypeface() {
        return textPaint.getTypeface();
    }

    /**
     * change typeface for all texts without speed and unit text.
     * @param typeface Maybe null. The typeface to be installed.
     */
    public void setTextTypeface(Typeface typeface) {
        textPaint.setTypeface(typeface);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return {@link #accelerate} used in {@link #realSpeedTo(float)}.
     * @see #setAccelerate(float)
     */
    public float getAccelerate() {
        return accelerate;
    }

    /**
     * change accelerate, used by {@link #realSpeedTo(float)} {@link #speedUp()}
     * and {@link #slowDown()} methods.<br>
     * must be between {@code (0, 1]}, default value 0.1f.
     * @param accelerate new accelerate.
     * @throws IllegalArgumentException if {@code accelerate} out of range.
     */
    public void setAccelerate(float accelerate) {
        this.accelerate = accelerate;
        checkAccelerate();
    }

    /**
     * @return {@link #decelerate} used in {@link #realSpeedTo(float)}.
     * @see #setDecelerate(float)
     */
    public float getDecelerate() {
        return decelerate;
    }

    /**
     * change decelerate, used by {@link #realSpeedTo(float)} {@link #speedUp()}
     * and {@link #slowDown()} methods.<br>
     * must be between {@code (0, 1]}, default value 0.1f.
     * @param decelerate new decelerate.
     * @throws IllegalArgumentException if {@code decelerate} out of range.
     */
    public void setDecelerate(float decelerate) {
        this.decelerate = decelerate;
    }

    /**
     * @return canvas translate dx.
     */
    protected final float getTranslatedDx() {
        return translatedDx;
    }

    /**
     * @return canvas translate dy.
     */
    protected final float getTranslatedDy() {
        return translatedDy;
    }

    /**
     * @return the space between Speed Text and Unit Text.
     */
    public float getUnitSpeedInterval() {
        return unitSpeedInterval;
    }

    /**
     * change space between speedText and UnitText.
     * @param unitSpeedInterval new space in pixel.
     */
    public void setUnitSpeedInterval(float unitSpeedInterval) {
        this.unitSpeedInterval = unitSpeedInterval;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return Speed-Unit Text padding.
     */
    public float getSpeedTextPadding() {
        return speedTextPadding;
    }

    /**
     * change the Speed-Unit Text padding,
     * this value will ignore if {@code {@link #speedTextPosition} == Position.CENTER}.
     * @param speedTextPadding padding in pixel.
     */
    public void setSpeedTextPadding(float speedTextPadding) {
        this.speedTextPadding = speedTextPadding;
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * @return whether Unit Text under Speed Text format.
     */
    public boolean isUnitUnderSpeedText() {
        return unitUnderSpeedText;
    }

    /**
     * to make Unit Text under Speed Text.
     * @param unitUnderSpeedText if true: drawing unit text <b>under</b> speed text.
     *                           false: drawing unit text and speed text <b>side by side</b>.
     */
    public void setUnitUnderSpeedText(boolean unitUnderSpeedText) {
        this.unitUnderSpeedText = unitUnderSpeedText;
        if (unitUnderSpeedText) {
            speedTextPaint.setTextAlign(Paint.Align.CENTER);
            unitTextPaint.setTextAlign(Paint.Align.CENTER);
        }
        else {
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            unitTextPaint.setTextAlign(Paint.Align.LEFT);
        }
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * change position of speed and Unit Text.
     * @param position new Position (enum value).
     */
    public void setSpeedTextPosition (Position position) {
        this.speedTextPosition = position;
        if(!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * position of Speed-Unit Text.
     */
    public enum Position {
        TOP_LEFT        (0f  , 0f  , 0f  , 0f  , 1  , 1 )
        , TOP_CENTER    (.5f , 0f  , .5f , 0f  , 0  , 1 )
        , TOP_RIGHT     (1f  , 0f  , 1f  , 0f  , -1 , 1 )
        , LEFT          (0f  , .5f , 0f  , .5f , 1  , 0 )
        , CENTER        (.5f , .5f , .5f , .5f , 0  , 0 )
        , RIGHT         (1f  , .5f , 1f  , .5f , -1 , 0 )
        , BOTTOM_LEFT   (0f  , 1f  , 0f  , 1f  , 1  , -1)
        , BOTTOM_CENTER (.5f , 1f  , .5f , 1f  , 0  , -1)
        , BOTTOM_RIGHT  (1f  , 1f  , 1f  , 1f  , -1 , -1);

        final float x;
        final float y;
        final float width;
        final float height;
        final int paddingH; // horizontal padding
        final int paddingV; // vertical padding

        Position(float x, float y, float width, float height, int paddingH, int paddingV) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.paddingH = paddingH;
            this.paddingV = paddingV;
        }
    }
}
