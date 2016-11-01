package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextPaint;
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
    private TextPaint unitTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    private int speedometerColor = Color.parseColor("#eeeeee")
            , pointerColor = Color.WHITE;

    private float unitTextSize = dpTOpx(11);
    private Rect bounds = new Rect();

    public PointerSpeedometer(Context context) {
        super(context);
        init();
    }

    public PointerSpeedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public PointerSpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        setIndicatorColor(Color.WHITE);
        setMarkColor(Color.WHITE);
        setTextColor(Color.WHITE);
        setCenterCircleColor(Color.WHITE);
        setSpeedTextColor(Color.WHITE);
        setSpeedTextSize(dpTOpx(24));
        setSpeedometerWidth(dpTOpx(10));
        setBackgroundCircleColor(Color.parseColor("#48cce9"));
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
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PointerSpeedometer, 0, 0);

        speedometerColor = a.getColor(R.styleable.PointerSpeedometer_speedometerColor, speedometerColor);
        unitTextSize = a.getDimension(R.styleable.PointerSpeedometer_unitTextSize, unitTextSize);
        pointerColor = a.getColor(R.styleable.PointerSpeedometer_pointerColor, pointerColor);
        a.recycle();
        initAttributeValue();
    }

    private void initAttributeValue() {
        unitTextPaint.setTextSize(unitTextSize);
        pointerPaint.setColor(pointerColor);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f + dpTOpx(8);
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float indW = w/20f;
        indicatorPath.reset();
        indicatorPath.moveTo(w/2f, h/2f);
        indicatorPath.quadTo(w/2f -indW, h*.34f, w/2f, h*.18f);
        indicatorPath.quadTo(w/2f +indW, h*.34f, w/2f, h/2f);

        markPath.reset();
        markPath.moveTo(w/2f, getSpeedometerWidth() + dpTOpx(8) + dpTOpx(4));
        markPath.lineTo(w/2f, getSpeedometerWidth() + dpTOpx(8) + dpTOpx(4) + w/60);

        updateRadial();
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

        canvas.drawArc(speedometerRect, MIN_DEGREE, 270f, false, speedometerPaint);

        canvas.save();
        canvas.rotate(90f + MIN_DEGREE, getWidth()/2f, getHeight()/2f);
        for (int i=135; i <= 345; i+=30) {
            canvas.rotate(30f, getWidth()/2f, getHeight()/2f);
            canvas.drawPath(markPath, markPaint);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(90 + getDegree(), getWidth()/2f, getHeight()/2f);
        canvas.drawCircle(getWidth()/2f, getSpeedometerWidth()/2f + dpTOpx(8)
                , getSpeedometerWidth()/2f + dpTOpx(8), pointerBackPaint);
        canvas.drawCircle(getWidth()/2f, getSpeedometerWidth()/2f + dpTOpx(8)
                , getSpeedometerWidth()/2f + dpTOpx(1), pointerPaint);
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();

        int c = getCenterCircleColor();
        circlePaint.setColor(Color.argb(120, Color.red(c), Color.green(c), Color.blue(c)));
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/14f, circlePaint);
        circlePaint.setColor(c);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/22f, circlePaint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("00", getWidth()/5f, getHeight()*6/7f, textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format(Locale.getDefault(), "%d", getMaxSpeed()), getWidth()*4/5f, getHeight()*6/7f, textPaint);
        String speed = String.format(Locale.getDefault(), "%.1f", getCorrectSpeed());
        speedTextPaint.getTextBounds(speed, 0, speed.length(), bounds);
        canvas.drawText(speed
                , getWidth()/2f - (bounds.width()/2f), getHeight()*.9f, speedTextPaint);
        canvas.drawText(getUnit()
                , getWidth()/2f + dpTOpx(1), getHeight()*.9f, unitTextPaint);
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
        matrix.postRotate(MIN_DEGREE, getWidth()/2f, getHeight()/2f);
        sweepGradient.setLocalMatrix(matrix);
        return sweepGradient;
    }

    private void updateRadial() {
        int centerColor = Color.argb(160, Color.red(pointerColor), Color.green(pointerColor), Color.blue(pointerColor));
        int edgeColor = Color.argb(10, Color.red(pointerColor), Color.green(pointerColor), Color.blue(pointerColor));
        RadialGradient pointerGradient = new RadialGradient(getWidth() / 2f, getSpeedometerWidth() / 2f + dpTOpx(8)
                , getSpeedometerWidth() / 2f + dpTOpx(8), new int[]{centerColor, edgeColor}
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

    public float getUnitTextSize() {
        return unitTextSize;
    }

    public void setUnitTextSize(float unitTextSize) {
        this.unitTextSize = unitTextSize;
        unitTextPaint.setTextSize(unitTextSize);
        invalidate();
    }

    @Deprecated
    @Override
    public int getLowSpeedColor() {
        return super.getLowSpeedColor();
    }

    @Deprecated
    @Override
    public void setLowSpeedColor(int lowSpeedColor) {
    }

    @Deprecated
    @Override
    public int getMediumSpeedColor() {
        return super.getMediumSpeedColor();
    }

    @Deprecated
    @Override
    public void setMediumSpeedColor(int mediumSpeedColor) {
    }

    @Deprecated
    @Override
    public int getHighSpeedColor() {
        return super.getHighSpeedColor();
    }

    @Deprecated
    @Override
    public void setHighSpeedColor(int highSpeedColor) {
    }
}
