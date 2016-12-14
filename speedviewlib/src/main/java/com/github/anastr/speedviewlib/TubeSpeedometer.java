package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

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
    }

    private void init() {
        tubePaint.setStyle(Paint.Style.STROKE);
        tubeBacPaint.setStyle(Paint.Style.STROKE);

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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f + padding;
        speedometerRect.set(risk, risk, w -risk, h -risk);

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

    private void initDraw() {
        tubePaint.setStrokeWidth(getSpeedometerWidth());
        tubeBacPaint.setStrokeWidth(getSpeedometerWidth());
        if (isInLowSection())
            tubePaint.setColor(getLowSpeedColor());
        else if (isInMediumSection())
            tubePaint.setColor(getMediumSpeedColor());
        else
            tubePaint.setColor(getHighSpeedColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        float sweepAngle = (getEndDegree() - getStartDegree())*getPercentSpeed()/100f;
        canvas.drawArc(speedometerRect,  getStartDegree(), sweepAngle, false, tubePaint);

        float speedTextPadding = dpTOpx(1);
        if (isSpeedometerTextRightToLeft()) {
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            speedTextPadding *= -1;
        }
        else
            speedTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(getSpeedText()
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

        c.drawArc(speedometerRect, getStartDegree(), getEndDegree()- getStartDegree(), false, tubeBacPaint);

        drawDefaultMinAndMaxSpeedPosition(c);

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
