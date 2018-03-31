package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.github.anastr.speedviewlib.components.Indicators.TriangleIndicator;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class AwesomeSpeedometer extends Speedometer {

    private Path markPath = new Path(),
            trianglesPath = new Path();
    private Paint markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            trianglesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    private int speedometerColor = 0xff00e6e6;

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
    protected void defaultGaugeValues() {

        super.setTextColor(0xffffc260);
        super.setSpeedTextColor(0xFFFFFFFF);
        super.setUnitTextColor(0xFFFFFFFF);
        super.setTextTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        super.setSpeedTextPosition(Position.CENTER);
        super.setUnitUnderSpeedText(true);
    }

    @Override
    protected void defaultSpeedometerValues() {
        super.setIndicator(new TriangleIndicator(getContext())
                .setIndicatorWidth(dpTOpx(25f))
                .setIndicatorColor(0xff00e6e6));
        super.setStartEndDegree(135, 135 + 320);
        super.setSpeedometerWidth(dpTOpx(60f));
        super.setBackgroundCircleColor(0xff212121);
        super.setTickNumber(9);
        super.setTickPadding(0);
    }

    private void init() {
        markPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        ringPaint.setStyle(Paint.Style.STROKE);
        trianglesPaint.setColor(0xff3949ab);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AwesomeSpeedometer, 0, 0);

        speedometerColor = a.getColor(R.styleable.AwesomeSpeedometer_sv_speedometerColor, speedometerColor);
        trianglesPaint.setColor(a.getColor(R.styleable.AwesomeSpeedometer_sv_trianglesColor, trianglesPaint.getColor()));
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateGradient();
        updateBackgroundBitmap();
    }

    private void updateGradient() {
        float stop = (getSizePa() *.5f - getSpeedometerWidth()) / (getSizePa() *.5f);
        float stop2 = stop+((1f-stop)*.1f);
        float stop3 = stop+((1f-stop)*.36f);
        float stop4 = stop+((1f-stop)*.64f);
        float stop5 = stop+((1f-stop)*.9f);
        int []colors = new int[]{getBackgroundCircleColor(), speedometerColor, getBackgroundCircleColor()
                , getBackgroundCircleColor(), speedometerColor, speedometerColor};
        Shader radialGradient = new RadialGradient(getSize() *.5f, getSize() *.5f, getSizePa() *.5f
                , colors, new float[]{stop, stop2, stop3, stop4, stop5, 1f}, Shader.TileMode.CLAMP);
        ringPaint.setShader(radialGradient);
    }

    private void initDraw() {
        ringPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSpeedUnitText(canvas);
        drawIndicator(canvas);
        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        initDraw();

        float markH = getViewSizePa()/22f;
        markPath.reset();
        markPath.moveTo(getSize() *.5f, getPadding());
        markPath.lineTo(getSize() *.5f, markH + getPadding());
        markPaint.setStrokeWidth(markH/5f);

        float triangleHeight = getViewSizePa() / 20f;
        setInitTickPadding(triangleHeight);

        trianglesPath.reset();
        trianglesPath.moveTo(getSize() *.5f, getPadding() + getViewSizePa()/20f);
        float triangleWidth = getViewSize()/20f;
        trianglesPath.lineTo(getSize() *.5f - triangleWidth/2f, getPadding());
        trianglesPath.lineTo(getSize() *.5f + triangleWidth/2f, getPadding());

        float risk = getSpeedometerWidth() *.5f + getPadding();
        speedometerRect.set(risk, risk, getSize() -risk, getSize() -risk);
        c.drawArc(speedometerRect, 0f, 360f, false, ringPaint);

        drawMarks(c);
        drawTicks(c);
    }

    protected void drawMarks(Canvas c) {
        for (int i=0; i < getTickNumber(); i++) {
            float d = getDegreeAtSpeed(getTicks().get(i)) + 90f;
            c.save();
            c.rotate(d, getSize() *.5f, getSize() *.5f);

            c.drawPath(trianglesPath, trianglesPaint);
            if(i+1 != getTickNumber()) {
                c.save();
                float d2 = getDegreeAtSpeed(getTicks().get(i + 1)) + 90f;
                float eachDegree = d2 - d;
                for (int j = 1; j < 10; j++) {
                    c.rotate(eachDegree * .1f, getSize() * .5f, getSize() * .5f);
                    if (j == 5)
                        markPaint.setStrokeWidth(getSize() / 22f / 5);
                    else
                        markPaint.setStrokeWidth(getSize() / 22f / 9);
                    c.drawPath(markPath, markPaint);
                }
                c.restore();
            }
            c.restore();
        }
    }

    @Override
    public void setSpeedometerWidth(float speedometerWidth) {
        super.setSpeedometerWidth(speedometerWidth);
        float risk = speedometerWidth *.5f;
        speedometerRect.set(risk, risk, getSize() -risk, getSize() -risk);
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
        return trianglesPaint.getColor();
    }

    public void setTrianglesColor(int trianglesColor) {
        trianglesPaint.setColor(trianglesColor);
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getLowSpeedColor() {
        return 0;
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
        return 0;
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
        return 0;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param highSpeedColor nothing.
     */
    @Deprecated
    @Override
    public void setHighSpeedColor(int highSpeedColor) {
    }
}
