package com.github.anastr.speedviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.github.anastr.speedviewlib.components.Indicators.ImageIndicator;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.github.anastr.speedviewlib.components.Indicators.NoIndicator;
import com.github.anastr.speedviewlib.components.note.Note;
import com.github.anastr.speedviewlib.util.OnSectionChangeListener;
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
    private Paint circleBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedUnitTextBitmapPaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
    protected TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint speedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG),
            unitTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private float speedometerWidth = dpTOpx(30f);
    private float speedTextSize = dpTOpx(18f);
    private float textSize = dpTOpx(10f);
    private float unitTextSize = dpTOpx(15f);
    /** the text after speedText */
    private String unit = "Km/h";
    private boolean withTremble = true;

    private int centerCircleColor = Color.DKGRAY
            , markColor = Color.WHITE
            , lowSpeedColor = Color.GREEN
            , mediumSpeedColor = Color.YELLOW
            , highSpeedColor = Color.RED
            , textColor = Color.BLACK
            , backgroundCircleColor = Color.WHITE
            , speedTextColor = Color.BLACK;

    /** the max range in speedometer, {@code default = 100} */
    private int maxSpeed = 100;
    /** the min range in speedometer, {@code default = 0} */
    private int minSpeed = 0;
    /**
     * the last speed which you set by {@link #speedTo(float)}
     * or {@link #speedTo(float, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     */
    private float speed = minSpeed;
    /** what is speed now in <b>int</b> */
    private int correctIntSpeed = 0;
    /** what is speed now in <b>float</b> */
    private float correctSpeed = 0f;
    /** a degree to increases and decreases the indicator around correct speed */
    private float trembleDegree = 4f;
    private int trembleDuration = 1000;
    private int startDegree = 135, endDegree = 135+270;
    /** to rotate indicator */
    private float degree = startDegree;
    private ValueAnimator speedAnimator, trembleAnimator, realSpeedAnimator;
    private boolean canceled = false;
    private OnSpeedChangeListener onSpeedChangeListener;
    private OnSectionChangeListener onSectionChangeListener;
    /** this animatorListener to call {@link #tremble()} method when animator done */
    private Animator.AnimatorListener animatorListener;

    /** to contain all drawing that doesn't change */
    private Bitmap backgroundBitmap;
    private Paint backgroundBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int padding = 0;

    /** low speed area, started from {@link #startDegree} */
    private int lowSpeedPercent = 60;
    /** medium speed area, started from {@link #startDegree} */
    private int mediumSpeedPercent = 87;

    public static final byte LOW_SECTION = 1;
    public static final byte MEDIUM_SECTION = 2;
    public static final byte HIGH_SECTION = 3;
    private byte section = LOW_SECTION;

    private boolean speedometerTextRightToLeft = false;

    /** array to contain all notes that will be draw */
    private ArrayList<Note> notes = new ArrayList<>();
    private boolean attachedToWindow = false;

    /** object to set text digits locale */
    private Locale locale = Locale.getDefault();

    /** Number expresses the Acceleration, between (0, 1] */
    private float accelerate = .1f;
    /** Number expresses the Deceleration, between (0, 1] */
    private float decelerate = .3f;

    private Mode speedometerMode = Mode.NORMAL;

    private float translatedDx = 0;
    private float translatedDy = 0;

    private Position speedTextPosition = Position.BOTTOM_CENTER;
    /** space between unitText and speedText */
    private float unitSpeedInterval = dpTOpx(1);
    private boolean unitUnderSpeedText = false;
    private Bitmap speedUnitTextBitmap;

    /** draw speed text as <b>integer</b> .*/
    public static final byte INTEGER_FORMAT = 0;
    /** draw speed text as <b>float</b>. */
    public static final byte FLOAT_FORMAT = 1;
    private byte speedTextFormat = FLOAT_FORMAT;

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
        indicator = new NoIndicator(getContext());

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
        defaultValues();
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Speedometer, 0, 0);

        int mode = a.getInt(R.styleable.Speedometer_speedometerMode, -1);
        if (mode != -1 && mode != 0)
            setSpeedometerMode(Mode.values()[mode]);
        setIndicatorColor(a.getColor(R.styleable.Speedometer_indicatorColor, indicator.getIndicatorColor()));
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
        this.unit =  (unit != null) ? unit : this.unit;
        unitTextSize = a.getDimension(R.styleable.Speedometer_unitTextSize, unitTextSize);
        trembleDegree = a.getFloat(R.styleable.Speedometer_trembleDegree, trembleDegree);
        trembleDuration = a.getInt(R.styleable.Speedometer_trembleDuration, trembleDuration);
        startDegree = a.getInt(R.styleable.Speedometer_startDegree, startDegree);
        endDegree = a.getInt(R.styleable.Speedometer_endDegree, endDegree);
        lowSpeedPercent = a.getInt(R.styleable.Speedometer_lowSpeedPercent, lowSpeedPercent);
        mediumSpeedPercent = a.getInt(R.styleable.Speedometer_mediumSpeedPercent, mediumSpeedPercent);
        speedometerTextRightToLeft = a.getBoolean(R.styleable.Speedometer_speedometerTextRightToLeft, speedometerTextRightToLeft);
        setIndicatorWidth(a.getDimension(R.styleable.Speedometer_indicatorWidth, indicator.getIndicatorWidth()));
        accelerate = a.getFloat(R.styleable.Speedometer_accelerate, accelerate);
        decelerate = a.getFloat(R.styleable.Speedometer_decelerate, decelerate);
        unitUnderSpeedText = a.getBoolean(R.styleable.Speedometer_unitUnderSpeedText, unitUnderSpeedText);
        unitSpeedInterval = a.getDimension(R.styleable.Speedometer_unitSpeedInterval, unitSpeedInterval);
        String speedTypefacePath = a.getString(R.styleable.Speedometer_speedTextTypeface);
        if (speedTypefacePath != null)
            setSpeedTextTypeface(Typeface.createFromAsset(getContext().getAssets(), speedTypefacePath));
        String typefacePath = a.getString(R.styleable.Speedometer_textTypeface);
        if (typefacePath != null)
            setTextTypeface(Typeface.createFromAsset(getContext().getAssets(), typefacePath));
        int ind = a.getInt(R.styleable.Speedometer_indicator, -1);
        if (ind != -1)
            setIndicator(Indicator.Indicators.values()[ind]);
        int position = a.getInt(R.styleable.Speedometer_speedTextPosition, -1);
        if (position != -1)
            setSpeedTextPosition(Position.values()[position]);
        setIndicatorColor(a.getColor(R.styleable.Speedometer_indicatorColor, indicator.getIndicatorColor()));
        byte format = (byte) a.getInt(R.styleable.Speedometer_speedTextFormat, -1);
        if (format != -1)
            setSpeedTextFormat(format);
        setIndicatorColor(a.getColor(R.styleable.Speedometer_indicatorColor, indicator.getIndicatorColor()));
        degree = startDegree;
        a.recycle();
        checkStartAndEndDegree();
        checkSpeedometerPercent();
        checkAccelerate();
        checkDecelerate();
        checkTrembleData();
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
        if (unitUnderSpeedText) {
            speedTextPaint.setTextAlign(Paint.Align.CENTER);
            unitTextPaint.setTextAlign(Paint.Align.CENTER);
        }
        else {
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            unitTextPaint.setTextAlign(Paint.Align.LEFT);
        }
        recreateSpeedUnitTextBitmap();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = (width > height) ? height : width;
        setMeasuredDimension(size/speedometerMode.divWidth, size/speedometerMode.divHeight);
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
        if (startDegree < speedometerMode.minDegree)
            throw new IllegalArgumentException("StartDegree must be bigger than " + speedometerMode.minDegree
                    + " in " + speedometerMode + " Mode !");
        if (endDegree > speedometerMode.maxDegree)
            throw new IllegalArgumentException("EndDegree must be smaller than " + speedometerMode.maxDegree
                    + " in " + speedometerMode + " Mode !");
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
     *  add default values inside this method,
     * call super setting method to set default value,
     * Ex :
     * <pre>
     *     super.setBackgroundCircleColor(Color.TRANSPARENT);
     * </pre>
     */
    abstract protected void defaultValues();
    abstract protected void updateBackgroundBitmap();

    private void updatePadding() {
        padding = Math.max(Math.max(getPaddingLeft(), getPaddingRight()), Math.max(getPaddingTop(), getPaddingBottom()));
        super.setPadding(padding, padding, padding, padding);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            super.setPaddingRelative(padding, padding, padding, padding);
        if (indicator != null)
            indicator.noticePaddingChange(padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (speedometerMode == Mode.RIGHT || speedometerMode == Mode.TOP_RIGHT
                || speedometerMode == Mode.BOTTOM_RIGHT) {
            translatedDx = - getSize() / 2f;
            canvas.translate(translatedDx, 0);
        }
        else
            translatedDx = 0;
        if (speedometerMode == Mode.BOTTOM || speedometerMode == Mode.BOTTOM_LEFT
                || speedometerMode == Mode.BOTTOM_RIGHT) {
            translatedDy = - getSize() / 2f;
            canvas.translate(0, translatedDy);
        }
        else
            translatedDy = 0;

        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundBitmapPaint);

        correctSpeed = getSpeedAtDegree(degree);
        // check onSpeedChangeEvent.
        int newSpeed = (int) correctSpeed;
        if (newSpeed != correctIntSpeed) {
            boolean isSpeedUp = newSpeed > correctIntSpeed;
            correctIntSpeed = newSpeed;
            if (onSpeedChangeListener != null){
                boolean byTremble = false;
                if (Build.VERSION.SDK_INT >= 11)
                    byTremble = trembleAnimator.isRunning();
                onSpeedChangeListener.onSpeedChange(this, isSpeedUp, byTremble);
            }
        }
        // check onSectionChangeEvent.
        byte newSection = getSection();
        if (section != newSection) {
            onSectionChangeEvent(section, newSection);
            section = newSection;
        }
    }

    /**
     * draw speed and unit text at correct {@link #speedTextPosition},
     * this method must call in subSpeedometer's {@code onDraw} method.
     * @param canvas view canvas to draw.
     */
    protected void drawSpeedUnitText(Canvas canvas) {
        RectF r = getSpeedUnitTextBounds();
        canvas.drawBitmap(updateSpeedUnitTextBitmap(), r.left, r.top, speedUnitTextBitmapPaint);
    }

    /**
     * draw indicator at correct {@link #degree},
     * this method must call in subSpeedometer's {@code onDraw} method.
     * @param canvas view canvas to draw.
     */
    protected void drawIndicator(Canvas canvas) {
        indicator.draw(canvas, degree);
    }

    /**
     * draw Notes,
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
     * fixable method to create {@link #speedUnitTextBitmap}
     * to avoid create it every frame in {@code onDraw} method.
     */
    private void recreateSpeedUnitTextBitmap() {
        speedUnitTextBitmap = Bitmap.createBitmap((int) getMaxWidthForSpeedUnitText()
                , (int) getSpeedUnitTextHeight(), Bitmap.Config.ARGB_8888);
    }

    /**
     * clear {@link #speedUnitTextBitmap} and draw speed and unit Text
     * taking into consideration {@link #speedometerTextRightToLeft} and {@link #unitUnderSpeedText}.
     * @return {@link #speedUnitTextBitmap} after update.
     */
    private Bitmap updateSpeedUnitTextBitmap() {
        speedUnitTextBitmap.eraseColor(Color.TRANSPARENT);
        Canvas c = new Canvas(speedUnitTextBitmap);

        if (unitUnderSpeedText) {
            c.drawText(getSpeedText(), speedUnitTextBitmap.getWidth()/2f
                    , speedTextPaint.getTextSize(), speedTextPaint);
            c.drawText(getUnit(), speedUnitTextBitmap.getWidth()/2f
                    , speedTextPaint.getTextSize() + unitSpeedInterval + unitTextPaint.getTextSize(), unitTextPaint);
            return speedUnitTextBitmap;
        }
        else {
            float speedX = 0f;
            float unitX = speedTextPaint.measureText(getSpeedText()) + unitSpeedInterval;
            if (isSpeedometerTextRightToLeft()) {
                speedX = unitTextPaint.measureText(getUnit()) + unitSpeedInterval;
                unitX = 0f;
            }
            c.drawText(getSpeedText(), speedX, c.getHeight() - .1f, speedTextPaint);
            c.drawText(getUnit(), unitX, c.getHeight() - .1f, unitTextPaint);
            return speedUnitTextBitmap;
        }
    }

    /**
     * speed-unit text position and size.
     * @return correct speed-unit's rect.
     */
    protected RectF getSpeedUnitTextBounds() {
        float left = getWidthPa()*speedTextPosition.x -translatedDx + padding
                - speedUnitTextBitmap.getWidth()*speedTextPosition.width;
        float top = getHeightPa()*speedTextPosition.y -translatedDy + padding
                - speedUnitTextBitmap.getHeight()*speedTextPosition.height;
        return new RectF(left, top, left + getSpeedUnitTextWidth(), top + getSpeedUnitTextHeight());
    }

    private float getMaxWidthForSpeedUnitText() {
        String speedUnitText = speedTextFormat == FLOAT_FORMAT ? String.format(locale, "%.1f", (float)maxSpeed)
                : String.format(locale, "%d", maxSpeed);
        if (unitUnderSpeedText)
            return Math.max(speedTextPaint.measureText(speedUnitText)
                    , unitTextPaint.measureText(getUnit()));
        return speedTextPaint.measureText(speedUnitText)
                + unitTextPaint.measureText(getUnit()) + unitSpeedInterval;
    }

    private float getSpeedUnitTextWidth() {
        if (unitUnderSpeedText)
            return Math.max(speedTextPaint.measureText(getSpeedText()), unitTextPaint.measureText(getUnit()));
        return speedTextPaint.measureText(getSpeedText()) + unitTextPaint.measureText(getUnit()) + unitSpeedInterval;
    }

    private float getSpeedUnitTextHeight() {
        if (unitUnderSpeedText)
            return speedTextPaint.getTextSize() + unitTextPaint.getTextSize() + unitSpeedInterval;
        return Math.max(speedTextPaint.getTextSize(), unitTextPaint.getTextSize());
    }

    /**
     * create canvas to draw {@link #backgroundBitmap}.
     * @return {@link #backgroundBitmap}'s canvas.
     */
    protected final Canvas createBackgroundBitmapCanvas() {
        if (getWidth() == 0 || getHeight() == 0)
            return new Canvas();
        backgroundBitmap = Bitmap.createBitmap(getSize(), getSize(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBitmap);
        canvas.drawCircle(getSize()/2f, getSize()/2f, getSize()/2f - getPadding(), circleBackPaint);
        return canvas;
    }

    /**
     * Implement this method to handle section change event.
     * @param oldSection where indicator came from.
     * @param newSection where indicator move to.
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
        speed = correctSpeed;
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
     * rotate indicator to correct speed without animation.
     * @param speed correct speed to move.
     */
    public void setIndicatorAt(float speed) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        this.speed = speed;
        cancelSpeedAnimator();
        degree = getDegreeAtSpeed(speed);
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
        percent = (percent > 100) ? 100 : (percent < 0) ? 0 : percent;
        speedTo(percent * (maxSpeed - minSpeed) / 100 + minSpeed, moveDuration);
    }

    /**
     * move speed to correct {@code int},
     * it should be between [{@link #minSpeed}, {@link #maxSpeed}].<br>
     * <br>
     * if {@code speed > maxSpeed} speed will change to {@link #maxSpeed},<br>
     * if {@code speed < minSpeed} speed will change to {@link #minSpeed}.<br>
     *
     * it is the same {@link #speedTo(float, long)}
     * with default {@code moveDuration = 2000}.
     *
     * @param speed correct speed to move.
     *
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(float)
     */
    public void speedTo(float speed) {
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
     * @see #speedTo(float)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(float)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void speedTo(float speed, long moveDuration) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        this.speed = speed;

        float newDegree = getDegreeAtSpeed(speed);
        if (newDegree == degree)
            return;
        if (Build.VERSION.SDK_INT < 11){
            setIndicatorAt(speed);
            return;
        }

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
     * to make speedometer some real.
     * <br>
     * when <b>speed up</b> : speed value well increase <i>slowly</i> by {@link #accelerate}.
     * <br>
     * when <b>slow down</b> : speed value will decrease <i>rapidly</i> by {@link #decelerate}.
     * @param speed correct speed to move.
     *
     * @see #speedTo(float)
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int)
     * @see #speedUp()
     * @see #slowDown()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void realSpeedTo(float speed) {
        boolean oldIsSpeedUp = this.speed > correctSpeed;
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        this.speed = speed;

        float newDegree = getDegreeAtSpeed(speed);
        if (newDegree == degree)
            return;
        if (Build.VERSION.SDK_INT < 11) {
            setIndicatorAt(speed);
            return;
        }
        final boolean isSpeedUp = speed > correctSpeed;
        if (realSpeedAnimator.isRunning() && oldIsSpeedUp == isSpeedUp)
            return;

        cancelSpeedAnimator();
        realSpeedAnimator = ValueAnimator.ofInt((int)degree, (int)newDegree);
        realSpeedAnimator.setRepeatCount(ValueAnimator.INFINITE);
        realSpeedAnimator.setInterpolator(new LinearInterpolator());
        realSpeedAnimator.setDuration(Math.abs((long) ((newDegree - degree) * 10) ));
        final int finalSpeed = (int) speed;
        realSpeedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isSpeedUp) {
                    float per = 100.005f-getPercentSpeed();
                    degree += (accelerate * 10f) * per/100f;
                }
                else {
                    float per = getPercentSpeed()+.005f;
                    degree -= (decelerate * 10f) * per/100f +.2f;
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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void tremble() {
        cancelTremble();
        if (!isWithTremble() || Build.VERSION.SDK_INT < 11)
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

    /**
     * @return correct degree where indicator must be.
     */
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
     * @throws IllegalArgumentException If trembleDegree is Negative.
     */
    public void setTrembleDegree (float trembleDegree) {
        this.trembleDegree = trembleDegree;
        checkTrembleData();
    }

    /**
     * default : 1000 millisecond.
     * @param trembleDuration tremble Animation duration in millisecond.
     * @throws IllegalArgumentException If trembleDuration is Negative.
     */
    public void setTrembleDuration (int trembleDuration) {
        this.trembleDuration = trembleDuration;
        checkTrembleData();
    }

    /**
     * tremble control.
     * @param trembleDegree a degree to increases and decreases the indicator around correct speed.
     * @param trembleDuration tremble Animation duration in millisecond.
     *
     * @see #setTrembleDegree(float)
     * @see #setTrembleDuration(int)
     * @throws IllegalArgumentException If trembleDegree OR trembleDuration is Negative.
     */
    public void setTrembleData (float trembleDegree, int trembleDuration) {
        setTrembleDegree(trembleDegree);
        setTrembleDuration(trembleDuration);
        checkTrembleData();
    }

    public byte getSpeedTextFormat() {
        return speedTextFormat;
    }

    /**
     * change speed text's format [{@link #INTEGER_FORMAT} or {@link #FLOAT_FORMAT}].
     * @param speedTextFormat new format.
     */
    public void setSpeedTextFormat(byte speedTextFormat) {
        this.speedTextFormat = speedTextFormat;
        recreateSpeedUnitTextBitmap();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * get correct speed as string to <b>Draw</b>.
     * @return correct speed to draw.
     */
    protected String getSpeedText() {
        return speedTextFormat == FLOAT_FORMAT ? String.format(locale, "%.1f", correctSpeed)
                : String.format(locale, "%d", correctIntSpeed);
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
     * @return the last speed which you set by {@link #speedTo(float)}
     * or {@link #speedTo(float, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     *
     * @see #getCorrectSpeed()
     */
    public float getSpeed() {
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
     * what is speed now in <b>integer</b>.
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
     * this method well call {@link #speedTo(float)} method
     * to make the change smooth.<br>
     * if {@code maxSpeed <= minSpeed} will ignore.
     *
     * @param maxSpeed new MAX Speed.
     */
    public void setMaxSpeed(int maxSpeed) {
        if (maxSpeed <= minSpeed)
            return;
        this.maxSpeed = maxSpeed;
        recreateSpeedUnitTextBitmap();
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
     * this method well call {@link #speedTo(float)} method
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

    /**
     * @return offset speed, between [0,1].
     */
    public float getOffsetSpeed() {
        return (correctSpeed - minSpeed) / (float)(maxSpeed - minSpeed);
    }

    public int getIndicatorColor() {
        return indicator.getIndicatorColor();
    }

    public void setIndicatorColor(int indicatorColor) {
        indicator.noticeIndicatorColorChange(indicatorColor);
        if (!attachedToWindow)
            return;
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

    /**
     * change the color of Low Section.
     * @param lowSpeedColor new color.
     */
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

    /**
     * change the color of Medium Section.
     * @param mediumSpeedColor new color.
     */
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

    /**
     * change the color of High Section.
     * @param highSpeedColor new color.
     */
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
     * change all text color without <b>speed, unit text</b>.
     * @param textColor new color.
     *
     * @see #setSpeedTextColor(int)
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getSpeedTextColor() {
        return speedTextColor;
    }

    /**
     * change just speed text color.
     * @param speedTextColor new color.
     *
     * @see #setTextColor(int)
     */
    public void setSpeedTextColor(int speedTextColor) {
        this.speedTextColor = speedTextColor;
        speedTextPaint.setColor(speedTextColor);
        unitTextPaint.setColor(speedTextColor);
        if (!attachedToWindow)
            return;
        invalidate();
    }

    public int getBackgroundCircleColor() {
        return backgroundCircleColor;
    }

    /**
     * Circle Background Color,
     * you can set it {@code Color.TRANSPARENT}
     * to remove circle background.
     * @param backgroundCircleColor new Circle Background Color.
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
     * change all text size without <b>speed and unit text</b>.
     * @param textSize new size in pixel.
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
     * @param speedTextSize new size in pixel.
     *
     * @see #dpTOpx(float)
     * @see #setTextSize(float)
     * @see #setUnitTextSize(float)
     */
    public void setSpeedTextSize(float speedTextSize) {
        this.speedTextSize = speedTextSize;
        speedTextPaint.setTextSize(speedTextSize);
        recreateSpeedUnitTextBitmap();
        if (!attachedToWindow)
            return;
        invalidate();
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
        this.unitTextSize = unitTextSize;
        unitTextPaint.setTextSize(unitTextSize);
        recreateSpeedUnitTextBitmap();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public float getUnitTextSize() {
        return unitTextSize;
    }

    /**
     * @return unit text.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * the text after speed text.
     * @param unit unit text.
     */
    public void setUnit(String unit) {
        this.unit = unit;
        recreateSpeedUnitTextBitmap();
        if (!attachedToWindow)
            return;
        invalidate();
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    /**
     * change the width of speedometer's bar.
     * @param speedometerWidth new width in pixel.
     */
    public void setSpeedometerWidth(float speedometerWidth) {
        this.speedometerWidth = speedometerWidth;
        if (!attachedToWindow)
            return;
        indicator.noticeSpeedometerWidthChange(speedometerWidth);
        updateBackgroundBitmap();
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
        cancelSpeedAnimator();
        degree = getDegreeAtSpeed(speed);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        tremble();
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
        cancelSpeedAnimator();
        degree = getDegreeAtSpeed(speed);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        tremble();
        invalidate();
    }

    /**
     * change start and end of speedometer.
     * @param startDegree the start of speedometer.
     * @param endDegree the end of speedometer.
     * @throws IllegalArgumentException if {@code startDegree OR endDegree} negative.
     * @throws IllegalArgumentException if {@code startDegree >= endDegree}.
     * @throws IllegalArgumentException if the difference between {@code endDegree and startDegree} bigger than 360.
     */
    public void setStartEndDegree (int startDegree, int endDegree) {
        this.startDegree = startDegree;
        this.endDegree = endDegree;
        checkStartAndEndDegree();
        cancelSpeedAnimator();
        degree = getDegreeAtSpeed(speed);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        tremble();
        invalidate();
    }

    public int getLowSpeedPercent() {
        return lowSpeedPercent;
    }

    public float getLowSpeedOffset() {
        return lowSpeedPercent/100f;
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

    public int getMediumSpeedPercent() {
        return mediumSpeedPercent;
    }

    public float getMediumSpeedOffset() {
        return mediumSpeedPercent/100f;
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

    /**
     * @return size of speedometer.
     */
    public int getSize() {
        if (speedometerMode == Mode.NORMAL)
            return getWidth();
        if (speedometerMode.isHalf)
            return getWidth() > getHeight() ? getWidth() : getHeight();
        return getWidth()*2;
    }

    /**
     * @return size of speedometer without padding.
     */
    public int getSizePa() {
        return getSize() - (padding*2);
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
     * Display new <a href="https://github.com/anastr/SpeedView/wiki/Notes">Note</a>
     * for 3 seconds.
     * @param note to display.
     */
    public void addNote(Note note) {
        addNote(note, 3000);
    }

    /**
     * Display new <a href="https://github.com/anastr/SpeedView/wiki/Notes">Note</a>
     * for custom seconds.
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
     * remove All <a href="https://github.com/anastr/SpeedView/wiki/Notes">Notes</a>.
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
     * draw minSpeedText and maxSpeedText at default Position.
     * @param c canvas to draw.
     */
    protected void drawDefMinMaxSpeedPosition(Canvas c) {
        if (getStartDegree()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getStartDegree()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getStartDegree()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getStartDegree() + 90f, getSize()/2f, getSize()/2f);
        c.rotate(-(getStartDegree() + 90f)
                , getSizePa()/2f - textPaint.getTextSize() + padding, textPaint.getTextSize() + padding);
        c.drawText(getMinSpeedText(), getSizePa()/2f - textPaint.getTextSize() + padding
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
        c.rotate(getEndDegree() + 90f, getSize()/2f, getSize()/2f);
        c.rotate(-(getEndDegree() + 90f)
                , getSizePa()/2f + textPaint.getTextSize() + padding, textPaint.getTextSize() + padding);
        c.drawText(getMaxSpeedText(), getSizePa()/2f + textPaint.getTextSize() + padding
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

    /**
     * @return correct section,
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

    public Typeface getSpeedTextTypeface() {
        return speedTextPaint.getTypeface();
    }

    /**
     * change typeface for att texts without speed and unit text.
     * @param typeface Maybe null. The typeface to be installed.
     */
    public void setTextTypeface(Typeface typeface) {
        textPaint.setTypeface(typeface);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

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

    public float getDecelerate() {
        return decelerate;
    }

    /**
     * change decelerate, used by {@link #realSpeedTo(float)} {@link #speedUp()}
     * and {@link #slowDown()} methods.<br>
     * must be between {@code (0, 1]}, default value 0.3f.
     * @param decelerate new decelerate.
     * @throws IllegalArgumentException if {@code decelerate} out of range.
     */
    public void setDecelerate(float decelerate) {
        this.decelerate = decelerate;
    }

    public float getIndicatorWidth() {
        return indicator.getIndicatorWidth();
    }

    /**
     * change indicator width, this value have several meaning
     * between {@link Indicator.Indicators}, it will be ignore
     * if using {@link ImageIndicator}.
     * @param indicatorWidth new width in pixel.
     */
    public void setIndicatorWidth(float indicatorWidth) {
        indicator.noticeIndicatorWidthChange(indicatorWidth);
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * call this method to apply/remove blur effect for indicator.
     * @param withEffects effect.
     */
    protected void indicatorEffects(boolean withEffects) {
        indicator.withEffects(withEffects);
    }

    /**
     * change <a href="https://github.com/anastr/SpeedView/wiki/Indicators">indicator shape</a>.<br>
     * this method will get bach indicatorColor and indicatorWidth to default.
     * @param indicator new indicator (Enum value).
     */
    public void setIndicator (Indicator.Indicators indicator) {
        this.indicator = Indicator.createIndicator(getContext(), indicator);
        if(!attachedToWindow)
            return;
        this.indicator.setTargetSpeedometer(this);
        invalidate();
    }

    /**
     * add custom <a href="https://github.com/anastr/SpeedView/wiki/Indicators">indicator</a>.
     * @param indicator new indicator.
     */
    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
        if(!attachedToWindow)
            return;
        this.indicator.setTargetSpeedometer(this);
        invalidate();
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
     * @return correct position of center X to use in drawing.
     */
    protected final float getViewCenterX() {
        switch (speedometerMode) {
            case LEFT:
            case TOP_LEFT:
            case BOTTOM_LEFT:
                return getSize()/2f - (getWidth()/2f);
            case RIGHT:
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                return getSize()/2f + (getWidth()/2f);
            default:
                return getSize()/2f;
        }
    }

    /**
     * @return correct position of center Y to use in drawing.
     */
    protected final float getViewCenterY() {
        switch (speedometerMode) {
            case TOP:
            case TOP_LEFT:
            case TOP_RIGHT:
                return getSize()/2f - (getHeight()/2f);
            case BOTTOM:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                return getSize()/2f + (getHeight()/2f);
            default:
                return getSize()/2f;
        }
    }

    protected final float getViewLeft() {
        return getViewCenterX() - getWidth()/2f;
    }

    protected final float getViewTop() {
        return getViewCenterY() - getHeight()/2f;
    }

    protected final float getViewRight() {
        return getViewCenterX() + getWidth()/2f;
    }

    protected final float getViewBottom() {
        return getViewCenterY() + getHeight()/2f;
    }

    public float getUnitSpeedInterval() {
        return unitSpeedInterval;
    }

    /**
     * change space between speedText and UnitText.
     * @param unitSpeedInterval new space in pixel.
     */
    public void setUnitSpeedInterval(float unitSpeedInterval) {
        this.unitSpeedInterval = unitSpeedInterval;
        recreateSpeedUnitTextBitmap();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public boolean isUnitUnderSpeedText() {
        return unitUnderSpeedText;
    }

    /**
     * to make unit text under speed text.
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
        recreateSpeedUnitTextBitmap();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * change speedometer shape, style and indicator position.<br>
     * this option will return {@link #startDegree} to the <b>minimum</b> value,
     * and {@link #endDegree} to the <b>maximum</b> value
     * if the speedometerMode doesn't equal to {@code Mode.NORMAL}.
     * @param speedometerMode enum value.
     */
    public void setSpeedometerMode (Mode speedometerMode) {
        this.speedometerMode = speedometerMode;
        if (speedometerMode != Mode.NORMAL) {
            startDegree = speedometerMode.minDegree;
            endDegree = speedometerMode.maxDegree;
        }
        cancelSpeedAnimator();
        degree = getDegreeAtSpeed(speed);
        indicator.onSizeChange(this);
        if(!attachedToWindow)
            return;
        requestLayout();
        updateBackgroundBitmap();
        tremble();
        invalidate();
    }

    public enum Mode {
        NORMAL         (0 ,360*2, false, 1, 1)
        , LEFT         (90 , 270, true , 2, 1)
        , TOP          (180, 360, true , 1, 2)
        , RIGHT        (270, 450, true , 2, 1)
        , BOTTOM       (0  , 180, true , 1, 2)
        , TOP_LEFT     (180, 270, false, 1, 1)
        , TOP_RIGHT    (270, 360, false, 1, 1)
        , BOTTOM_RIGHT (0  , 90 , false, 1, 1)
        , BOTTOM_LEFT  (90 , 180, false, 1, 1);

        int minDegree;
        int maxDegree;
        boolean isHalf;
        int divWidth;
        int divHeight;
        Mode (int minDegree, int maxDegree, boolean isHalf, int divWidth, int divHeight) {
            this.minDegree = minDegree;
            this.maxDegree = maxDegree;
            this.isHalf = isHalf;
            this.divWidth = divWidth;
            this.divHeight = divHeight;
        }
    }

    /**
     * change position of speed and unit text.
     * @param position new Position (enum value).
     */
    public void setSpeedTextPosition (Position position) {
        this.speedTextPosition = position;
        if(!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public enum Position {
        TOP_LEFT        (.1f, .1f, 0f , 0f)
        , TOP_CENTER    (.5f, .1f, .5f, 0f)
        , TOP_RIGHT     (.9f, .1f, 1f , 0f)
        , LEFT          (.1f, .5f, 0f , .5f)
        , CENTER        (.5f, .5f, .5f, .5f)
        , RIGHT         (.9f, .5f, 1f , .5f)
        , BOTTOM_LEFT   (.1f, .9f, 0f , 1f)
        , BOTTOM_CENTER (.5f, .9f, .5f, 1f)
        , BOTTOM_RIGHT  (.9f, .9f, 1f , 1f);

        float x;
        float y;
        float width;
        float height;

        Position(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
