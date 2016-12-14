package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class AwesomeSpeedometer extends Speedometer {

    private Path indicatorPath = new Path(),
            markPath = new Path(),
            trianglesPath = new Path();
    private Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            trianglesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    private int speedometerColor = Color.parseColor("#00e6e6")
            , trianglesColor = Color.parseColor("#3949ab");
    private float indicatorWidth = dpTOpx(25f);

    public AwesomeSpeedometer(Context context) {
        this(context, null);
    }

    public AwesomeSpeedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AwesomeSpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        super.setStartDegree(135);
        super.setEndDegree(135+320);

        super.setSpeedometerWidth(dpTOpx(60));
        super.setBackgroundCircleColor(Color.parseColor("#212121"));
        super.setIndicatorColor(Color.parseColor("#00e6e6"));
        super.setTextColor(Color.parseColor("#ffc260"));
        super.setSpeedTextColor(Color.WHITE);
    }

    private void init() {
        markPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        ringPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(dpTOpx(10));
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            initAttributeValue();
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AwesomeSpeedometer, 0, 0);

        speedometerColor = a.getColor(R.styleable.AwesomeSpeedometer_speedometerColor, speedometerColor);
        trianglesColor = a.getColor(R.styleable.AwesomeSpeedometer_trianglesColor, trianglesColor);
        indicatorWidth = a.getDimension(R.styleable.AwesomeSpeedometer_indicatorWidth, indicatorWidth);
        a.recycle();
        initAttributeValue();
    }

    private void initAttributeValue() {
        trianglesPaint.setColor(trianglesColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f + padding;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float markH = getHeightPa()/22f;
        markPath.reset();
        markPath.moveTo(w/2f, padding);
        markPath.lineTo(w/2f, markH + padding);
        markPaint.setStrokeWidth(markH/5f);

        trianglesPath.reset();
        trianglesPath.moveTo(w/2f, padding + getHeightPa()/20f);
        trianglesPath.lineTo(w/2f -(w/40f), padding);
        trianglesPath.lineTo(w/2f +(w/40f), padding);

        updateGradient();
        updateBackgroundBitmap();
    }

    private void updateGradient() {
        float w = getWidth();
        float stop = (getWidthPa()/2f - getSpeedometerWidth()) / (getWidthPa()/2f);
        float stop2 = stop+((1f-stop)*.1f);
        float stop3 = stop+((1f-stop)*.36f);
        float stop4 = stop+((1f-stop)*.64f);
        float stop5 = stop+((1f-stop)*.9f);
        int []colors = new int[]{getBackgroundCircleColor(), speedometerColor, getBackgroundCircleColor()
                , getBackgroundCircleColor(), speedometerColor, speedometerColor};
        Shader radialGradient = new RadialGradient(getWidth() / 2f, getHeight() / 2f, getWidthPa() / 2f
                , colors, new float[]{stop, stop2, stop3, stop4, stop5, 1f}, Shader.TileMode.CLAMP);
        ringPaint.setShader(radialGradient);

        indicatorPath = new Path();
        float indicatorH = padding + getSpeedometerWidth() + dpTOpx(5);
        indicatorPath.moveTo(w/2f, indicatorH);
        indicatorPath.lineTo(w/2f -indicatorWidth, indicatorH + indicatorWidth);
        indicatorPath.lineTo(w/2f +indicatorWidth, indicatorH + indicatorWidth);
        indicatorPath.moveTo(0f, 0f);

        int endColor = Color.argb(0, Color.red(getIndicatorColor()), Color.green(getIndicatorColor()), Color.blue(getIndicatorColor()));
        Shader linearGradient = new LinearGradient(w/2f, indicatorH, w/2f, indicatorH + indicatorWidth
                , getIndicatorColor(), endColor, Shader.TileMode.CLAMP);
        indicatorPaint.setShader(linearGradient);
    }

    private void initDraw() {
        ringPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
        speedTextPaint.setColor(getSpeedTextColor());
        speedTextPaint.setTextSize(getSpeedTextSize());
        unitTextPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(getTextColor());
        textPaint.setTextSize(getTextSize());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        canvas.save();
        canvas.rotate(90f +getDegree(), getWidth()/2f, getHeight()/2f);
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();

        canvas.drawText(getSpeedText()
                , getWidth()/2f, getHeight()/2f, speedTextPaint);
        canvas.drawText(getUnit()
                , getWidth()/2f, getHeight()/2f +unitTextPaint.getTextSize(), unitTextPaint);

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

        c.drawArc(speedometerRect, 0f, 360f, false, ringPaint);

        c.save();
        c.rotate(getStartDegree()+90f, getWidth()/2f, getHeight()/2f);
        for (float i = 0; i <= getEndDegree() - getStartDegree(); i+=4f) {
            c.rotate(4f, getWidth()/2f, getHeight()/2f);
            if (i % 40 == 0) {
                c.drawPath(trianglesPath, trianglesPaint);
                c.drawText(String.format(getLocale(), "%d", (int)getSpeedAtDegree(i + getStartDegree()))
                        , getWidth()/2f, getHeightPa()/20f +textPaint.getTextSize() + padding, textPaint);
            }
            else {
                if (i % 20 == 0)
                    markPaint.setStrokeWidth(getHeight()/22f/5);
                else
                    markPaint.setStrokeWidth(getHeight()/22f/9);
                c.drawPath(markPath, markPaint);
            }
        }
        c.restore();


        return backgroundBitmap;
    }

    @Override
    public void setSpeedometerWidth(float speedometerWidth) {
        super.setSpeedometerWidth(speedometerWidth);
        float risk = speedometerWidth/2f;
        speedometerRect.set(risk, risk, getWidth() -risk, getHeight() -risk);
        updateGradient();
        updateBackgroundBitmap();
        invalidate();
    }

    public int getSpeedometerColor() {
        return speedometerColor;
    }

    public void setSpeedometerColor(int speedometerColor) {
        this.speedometerColor = speedometerColor;
        updateGradient();
        updateBackgroundBitmap();
        invalidate();
    }

    public int getTrianglesColor() {
        return trianglesColor;
    }

    public void setTrianglesColor(int trianglesColor) {
        this.trianglesColor = trianglesColor;
        trianglesPaint.setColor(trianglesColor);
        updateBackgroundBitmap();
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
