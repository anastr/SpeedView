package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class TubeSpeedometer extends Speedometer {

    private Paint tubePaint = new Paint(Paint.ANTI_ALIAS_FLAG)
            ,tubeBacPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    private int speedometerColor = Color.parseColor("#757575");

    private boolean withEffects3D = true;

    public TubeSpeedometer(Context context) {
        this(context, null);
    }

    public TubeSpeedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TubeSpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        super.setLowSpeedColor(Color.parseColor("#00BCD4"));
        super.setMediumSpeedColor(Color.parseColor("#FFC107"));
        super.setHighSpeedColor(Color.parseColor("#F44336"));
        super.setSpeedometerWidth(dpTOpx(40f));
        super.setBackgroundCircleColor(Color.TRANSPARENT);
    }

    private void init() {
        tubePaint.setStyle(Paint.Style.STROKE);
        tubeBacPaint.setStyle(Paint.Style.STROKE);

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            initAttributeValue();
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AwesomeSpeedometer, 0, 0);

        speedometerColor = a.getColor(R.styleable.TubeSpeedometer_speedometerColor, speedometerColor);
        withEffects3D = a.getBoolean(R.styleable.TubeSpeedometer_withEffects3D, withEffects3D);
        a.recycle();
        initAttributeValue();
    }

    private void initAttributeValue() {
        tubeBacPaint.setColor(speedometerColor);
        tubePaint.setColor(getLowSpeedColor());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateEmboss();
        updateBackgroundBitmap();
    }

    private void updateEmboss() {
        if (isInEditMode())
            return;
        if (!withEffects3D) {
            tubePaint.setMaskFilter(null);
            tubeBacPaint.setMaskFilter(null);
            return;
        }
        EmbossMaskFilter embossMaskFilter = new EmbossMaskFilter(
                new float[] { .5f, 1f, 1f }, .6f, 3f, pxTOdp(getSpeedometerWidth())*.35f);
        tubePaint.setMaskFilter(embossMaskFilter);
        EmbossMaskFilter embossMaskFilterBac = new EmbossMaskFilter(
                new float[] { -.5f, -1f, 0f }, .6f, 1f, pxTOdp(getSpeedometerWidth())*.35f);
        tubeBacPaint.setMaskFilter(embossMaskFilterBac);
    }

    @Override
    protected void onSectionChangeEvent(byte oldSection, byte newSection) {
        super.onSectionChangeEvent(oldSection, newSection);
        if (newSection == LOW_SECTION)
            tubePaint.setColor(getLowSpeedColor());
        else if (newSection == MEDIUM_SECTION)
            tubePaint.setColor(getMediumSpeedColor());
        else
            tubePaint.setColor(getHighSpeedColor());
    }

    private void initDraw() {
        tubePaint.setStrokeWidth(getSpeedometerWidth());
        tubeBacPaint.setStrokeWidth(getSpeedometerWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        float sweepAngle = (getEndDegree() - getStartDegree())*getOffsetSpeed();
        canvas.drawArc(speedometerRect,  getStartDegree(), sweepAngle, false, tubePaint);

        drawSpeedUnitText(canvas);
        drawIndicator(canvas);
        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        initDraw();

        float risk = getSpeedometerWidth()/2f + getPadding();
        speedometerRect.set(risk, risk, getSize() -risk, getSize() -risk);

        c.drawArc(speedometerRect, getStartDegree(), getEndDegree()- getStartDegree(), false, tubeBacPaint);

        drawDefMinMaxSpeedPosition(c);
    }

    public int getSpeedometerColor() {
        return speedometerColor;
    }

    public void setSpeedometerColor(int speedometerColor) {
        this.speedometerColor = speedometerColor;
        updateBackgroundBitmap();
        invalidate();
    }

    public boolean isWithEffects3D() {
        return withEffects3D;
    }

    public void setWithEffects3D(boolean withEffects3D) {
        this.withEffects3D = withEffects3D;
        updateEmboss();
        updateBackgroundBitmap();
        invalidate();
    }
}
