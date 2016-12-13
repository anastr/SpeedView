package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;

import java.util.Locale;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class PointerSpeedometer extends Speedometer {

    private Path indicatorPath = new Path(),
            markPath = new Path();
    private Paint speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            pointerBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    private int speedometerColor = Color.parseColor("#eeeeee")
            , pointerColor = Color.WHITE;

    public PointerSpeedometer(Context context) {
        this(context, null);
    }

    public PointerSpeedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointerSpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        super.setIndicatorColor(Color.WHITE);
        super.setMarkColor(Color.WHITE);
        super.setTextColor(Color.WHITE);
        super.setCenterCircleColor(Color.WHITE);
        super.setSpeedTextColor(Color.WHITE);
        super.setSpeedTextSize(dpTOpx(24f));
        super.setSpeedometerWidth(dpTOpx(10f));
        super.setBackgroundCircleColor(Color.parseColor("#48cce9"));
        super.setUnitTextSize(dpTOpx(11f));
    }

    private void init() {
        speedometerPaint.setStyle(Paint.Style.STROKE);
        speedometerPaint.setStrokeCap(Paint.Cap.ROUND);
        markPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStrokeCap(Paint.Cap.ROUND);
        markPaint.setStrokeWidth(dpTOpx(2));
        speedTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        unitTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            initAttributeValue();
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PointerSpeedometer, 0, 0);

        speedometerColor = a.getColor(R.styleable.PointerSpeedometer_speedometerColor, speedometerColor);
        pointerColor = a.getColor(R.styleable.PointerSpeedometer_pointerColor, pointerColor);
        a.recycle();
        initAttributeValue();
    }

    private void initAttributeValue() {
        pointerPaint.setColor(pointerColor);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f + dpTOpx(8) + padding;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float indW = w/20f;
        indicatorPath.reset();
        indicatorPath.moveTo(w/2f, h/2f);
        indicatorPath.quadTo(w/2f -indW, getHeightPa()*.34f + padding, w/2f, getHeightPa()*.18f + padding);
        indicatorPath.quadTo(w/2f +indW, getHeightPa()*.34f + padding, w/2f, h/2f);

        markPath.reset();
        markPath.moveTo(w/2f, getSpeedometerWidth() + dpTOpx(8) + dpTOpx(4) + padding);
        markPath.lineTo(w/2f, getSpeedometerWidth() + dpTOpx(8) + dpTOpx(4) + padding + w/60);

        updateRadial();
        updateBackgroundBitmap();
    }

    private void initDraw() {
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        speedometerPaint.setShader(updateSweep());
        markPaint.setColor(getMarkColor());
        indicatorPaint.setColor(getIndicatorColor());
        circlePaint.setColor(getCenterCircleColor());
        unitTextPaint.setColor(getSpeedTextColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        canvas.drawArc(speedometerRect, getStartDegree(), 270f, false, speedometerPaint);

        canvas.save();
        canvas.rotate(90 + getDegree(), getWidth()/2f, getHeight()/2f);
        canvas.drawCircle(getWidth()/2f, getSpeedometerWidth()/2f + dpTOpx(8) + padding
                , getSpeedometerWidth()/2f + dpTOpx(8), pointerBackPaint);
        canvas.drawCircle(getWidth()/2f, getSpeedometerWidth()/2f + dpTOpx(8) + padding
                , getSpeedometerWidth()/2f + dpTOpx(1), pointerPaint);
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();

        int c = getCenterCircleColor();
        circlePaint.setColor(Color.argb(120, Color.red(c), Color.green(c), Color.blue(c)));
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidthPa()/14f, circlePaint);
        circlePaint.setColor(c);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidthPa()/22f, circlePaint);


        String speed = String.format(Locale.getDefault(), "%.1f", getCorrectSpeed());
        float speedTextPadding = dpTOpx(1);
        if (isSpeedometerTextRightToLeft()) {
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            speedTextPadding *= -1;
        }
        else
            speedTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(speed
                , getWidth()/2f - speedTextPadding, getHeightPa()*.9f + padding, speedTextPaint);

        drawNotes(canvas);
    }

    @Override
    protected Bitmap updateBackgroundBitmap() {
        if (getWidth() == 0 || getHeight() == 0)
            return null;
        initDraw();
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(backgroundBitmap);
        c.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f - padding, circleBackPaint);

        c.save();
        c.rotate(90f + getStartDegree(), getWidth()/2f, getHeight()/2f);
        float everyDegree = (getEndDegree() - getStartDegree()) * .111f;
        for (float i = getStartDegree(); i < getEndDegree()-(2f*everyDegree); i+=everyDegree) {
            c.rotate(everyDegree, getWidth()/2f, getHeight()/2f);
            c.drawPath(markPath, markPaint);
        }
        c.restore();

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
        String minSpeed = String.format(Locale.getDefault(), "%d", getMinSpeed());
        c.drawText(minSpeed, getWidthPa()/2f - textPaint.getTextSize() + padding, textPaint.getTextSize() + padding, textPaint);
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
        String maxSpeed = String.format(Locale.getDefault(), "%d", getMaxSpeed());
        c.drawText(maxSpeed, getWidthPa()/2f + textPaint.getTextSize() + padding, textPaint.getTextSize() + padding, textPaint);
        c.restore();

        float unitTextPadding = dpTOpx(1);
        if (isSpeedometerTextRightToLeft()) {
            unitTextPaint.setTextAlign(Paint.Align.RIGHT);
            unitTextPadding *= -1;
        }
        else
            unitTextPaint.setTextAlign(Paint.Align.LEFT);

        c.drawText(getUnit()
                , getWidth()/2f + unitTextPadding, getHeightPa()*.9f + padding, unitTextPaint);

        return backgroundBitmap;
    }

    private SweepGradient updateSweep() {
        int startColor = Color.argb(150, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor));
        int color2 = Color.argb(220, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor));
        int color3 = Color.argb(70, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor));
        int endColor = Color.argb(15, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor));
        float position = getPercentSpeed()*.75f/100f;
        SweepGradient sweepGradient = new SweepGradient(getWidth()/2f, getHeight()/2f
                , new int[]{startColor, color2, speedometerColor, color3, endColor, startColor}
                , new float[]{0f, position/2f, position, position, .9f, 1f});
        Matrix matrix = new Matrix();
        matrix.postRotate(getStartDegree(), getWidth()/2f, getHeight()/2f);
        sweepGradient.setLocalMatrix(matrix);
        return sweepGradient;
    }

    private void updateRadial() {
        int centerColor = Color.argb(160, Color.red(pointerColor), Color.green(pointerColor), Color.blue(pointerColor));
        int edgeColor = Color.argb(10, Color.red(pointerColor), Color.green(pointerColor), Color.blue(pointerColor));
        RadialGradient pointerGradient = new RadialGradient(getWidth()/2f, getSpeedometerWidth()/2f + dpTOpx(8) + padding
                , getSpeedometerWidth()/2f + dpTOpx(8), new int[]{centerColor, edgeColor}
                , new float[]{.4f, 1f}, Shader.TileMode.CLAMP);
        pointerBackPaint.setShader(pointerGradient);
    }

    public int getSpeedometerColor() {
        return speedometerColor;
    }

    public void setSpeedometerColor(int speedometerColor) {
        this.speedometerColor = speedometerColor;
        invalidate();
    }

    public int getPointerColor() {
        return pointerColor;
    }

    public void setPointerColor(int pointerColor) {
        this.pointerColor = pointerColor;
        pointerPaint.setColor(pointerColor);
        updateRadial();
        invalidate();
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
}
