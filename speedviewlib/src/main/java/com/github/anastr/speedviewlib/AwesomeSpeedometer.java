package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;

import java.util.Locale;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class AwesomeSpeedometer extends Speedometer {

    private Path indicatorPath = new Path(),
            markPath = new Path(),
            trianglesPath = new Path();
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            trianglesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint speedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG),
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    private int speedometerColor = Color.parseColor("#00e6e6")
            , trianglesColor = Color.parseColor("#3949ab");
    private float indicatorWidth = dpTOpx(25f);

    public AwesomeSpeedometer(Context context) {
        super(context);
        init();
    }

    public AwesomeSpeedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public AwesomeSpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        MIN_DEGREE = 135;
        MAX_DEGREE = 135+320;

        super.setSpeedometerWidth(dpTOpx(60));
        setBackgroundCircleColor(Color.parseColor("#212121"));
        setIndicatorColor(Color.parseColor("#00e6e6"));
        setTextColor(Color.parseColor("#ffc260"));
        setSpeedTextColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = (width > height) ? height : width;
        setMeasuredDimension(size, size);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AwesomeSpeedometer, 0, 0);

        speedometerColor = a.getColor(R.styleable.AwesomeSpeedometer_speedometerColor, speedometerColor);
        trianglesColor = a.getColor(R.styleable.AwesomeSpeedometer_trianglesColor, trianglesColor);
        indicatorWidth = a.getDimension(R.styleable.AwesomeSpeedometer_indicatorWidth, indicatorWidth);
        a.recycle();
    }

    private void init() {
        markPaint.setStyle(Paint.Style.STROKE);
        speedTextPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextAlign(Paint.Align.CENTER);
        ringPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(dpTOpx(10));
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float markH = h/22f;
        markPath.reset();
        markPath.moveTo(w/2f, 0f);
        markPath.lineTo(w/2f, markH);
        markPath.moveTo(0f, 0f);
        markPaint.setStrokeWidth(markH/5f);

        trianglesPath.reset();
        trianglesPath.moveTo(w/2f, h/20f);
        trianglesPath.lineTo(w/2f -(w/40f), 0f);
        trianglesPath.lineTo(w/2f +(w/40f), 0f);
        trianglesPath.moveTo(0f, 0f);

        updateGradient();
    }

    private void updateGradient() {
        float w = getWidth();
        float stop = (getWidth()/2f - getSpeedometerWidth()) / (getWidth()/2f);
        float stop2 = stop+((1f-stop)*.1f);
        float stop3 = stop+((1f-stop)*.36f);
        float stop4 = stop+((1f-stop)*.64f);
        float stop5 = stop+((1f-stop)*.9f);
        int []colors = new int[]{getBackgroundCircleColor(), speedometerColor, getBackgroundCircleColor()
                , getBackgroundCircleColor(), speedometerColor, speedometerColor};
        Shader radialGradient = new RadialGradient(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f
                , colors, new float[]{stop, stop2, stop3, stop4, stop5, 1f}, Shader.TileMode.CLAMP);
        ringPaint.setShader(radialGradient);

        indicatorPath = new Path();
        indicatorPath.moveTo(w/2f, getSpeedometerWidth() + dpTOpx(5));
        indicatorPath.lineTo(w/2f -indicatorWidth, getSpeedometerWidth() +indicatorWidth);
        indicatorPath.lineTo(w/2f +indicatorWidth, getSpeedometerWidth() +indicatorWidth);
        indicatorPath.moveTo(0f, 0f);

        Shader linearGradient = new LinearGradient(w/2f, getSpeedometerWidth() + dpTOpx(5), w/2f, getSpeedometerWidth() +indicatorWidth
                , getIndicatorColor(), getBackgroundCircleColor(), Shader.TileMode.CLAMP);
        indicatorPaint.setShader(linearGradient);
    }

    private void initDraw() {
        indicatorPaint.setColor(getIndicatorColor());
        ringPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
        speedTextPaint.setColor(getSpeedTextColor());
        speedTextPaint.setTextSize(getSpeedTextSize());
        textPaint.setColor(getTextColor());
        textPaint.setTextSize(getTextSize());
        circlePaint.setColor(getBackgroundCircleColor());
        trianglesPaint.setColor(trianglesColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        if (isWithBackgroundCircle())
            canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f, circlePaint);

        canvas.drawArc(speedometerRect, 0f, 360f, false, ringPaint);

        canvas.save();
        canvas.rotate(135f+90f-5f, getWidth()/2f, getHeight()/2f);
        for (int i=0; i <= MAX_DEGREE - MIN_DEGREE; i+=4) {
            canvas.rotate(4f, getWidth()/2f, getHeight()/2f);
            if (i % 40 == 0) {
                canvas.drawPath(trianglesPath, trianglesPaint);
                canvas.drawText(i*getMaxSpeed()/(MAX_DEGREE -MIN_DEGREE) +""
                        , getWidth()/2f, getHeight()/20f +textPaint.getTextSize(), textPaint);
            }
            else {
                if (i % 20 == 0)
                    markPaint.setStrokeWidth(getHeight()/22f/5);
                else
                    markPaint.setStrokeWidth(getHeight()/22f/9);
                canvas.drawPath(markPath, markPaint);
            }
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(90f +getDegree(), getWidth()/2f, getHeight()/2f);
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();

        canvas.drawText(String.format(Locale.getDefault(), "%.1f", getCorrectSpeed())
                , getWidth()/2f, getHeight()/2f, speedTextPaint);
        canvas.drawText(getUnit()
                , getWidth()/2f, getHeight()/2f +speedTextPaint.getTextSize(), speedTextPaint);
    }

    @Override
    public void setSpeedometerWidth(float speedometerWidth) {
        super.setSpeedometerWidth(speedometerWidth);
        float risk = speedometerWidth/2f;
        speedometerRect.set(risk, risk, getWidth() -risk, getHeight() -risk);
        updateGradient();
        invalidate();
    }

    public int getSpeedometerColor() {
        return speedometerColor;
    }

    public void setSpeedometerColor(int speedometerColor) {
        this.speedometerColor = speedometerColor;
        invalidate();
    }

    public int getTrianglesColor() {
        return trianglesColor;
    }

    public void setTrianglesColor(int trianglesColor) {
        this.trianglesColor = trianglesColor;
        invalidate();
    }

    public float getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
        updateGradient();
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
