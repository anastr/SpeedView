package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class ProgressiveGauge extends LinearGauge {

    /** the shape */
    private Path path = new Path();

    private Paint frontPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
            , backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ProgressiveGauge(Context context) {
        this(context, null);
    }

    public ProgressiveGauge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressiveGauge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultGaugeValues() {
        super.setSpeedTextPosition(Position.CENTER);
        super.setUnitUnderSpeedText(true);
    }

    private void init() {
        frontPaint.setColor(0xFF00FFFF);
        backPaint.setColor(0xffd6d7d7);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LinearGauge, 0, 0);

        frontPaint.setColor(a.getColor(R.styleable.LinearGauge_sv_speedometerColor, frontPaint.getColor()));
        backPaint.setColor(a.getColor(R.styleable.LinearGauge_sv_speedometerBackColor, backPaint.getColor()));
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (getOrientation() == Orientation.HORIZONTAL) {
            if (h > w/2)
                setMeasuredDimension(w, w/2);
            else
                setMeasuredDimension(h*2, h);
        }
        else {
            if (w > h/2)
                setMeasuredDimension(h/2, h);
            else
                setMeasuredDimension(w, w*2);
        }
    }

    @Override
    protected void updateFrontAndBackBitmaps() {
        updateOrientation();
        Canvas canvasBack = createBackgroundBitmapCanvas();
        Canvas canvasFront = createForegroundBitmapCanvas();

        canvasBack.translate(getPadding(), getPadding());
        canvasBack.drawPath(path, backPaint);

        canvasFront.drawPath(path, frontPaint);
    }

    private void updateOrientation() {
        if (getOrientation() == Orientation.HORIZONTAL)
            updateHorizontalPath();
        else
            updateVerticalPath();
    }

    /**
     * to reset {@link #path} for horizontal shape.
     */
    protected void updateHorizontalPath() {
        path.reset();
        path.moveTo(0f, getHeightPa());
        path.lineTo(0f, getHeightPa() - getHeightPa() *.1f);
        path.quadTo(getWidthPa() *.75f, getHeightPa() *.75f
                , getWidthPa(), 0f);
        path.lineTo(getWidthPa(), getHeightPa());
        path.lineTo(0f, getHeightPa());
    }

    /**
     * to reset {@link #path} for vertical shape.
     */
    protected void updateVerticalPath() {
        path.reset();
        path.moveTo(0f, getHeightPa());
        path.lineTo(getWidthPa() *.1f, getHeightPa());
        path.quadTo(getWidthPa() *.25f, getHeightPa() *.25f
                , getWidthPa(), 0f);
        path.lineTo(0f, 0f);
        path.lineTo(0f, getHeightPa());
    }

    /**
     * @return foreground progress color.
     */
    public int getSpeedometerColor() {
        return frontPaint.getColor();
    }

    /**
     * change the color of progress.
     * @param speedometerColor new color.
     */
    public void setSpeedometerColor(int speedometerColor) {
        frontPaint.setColor(speedometerColor);
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return background progress color.
     */
    public int getSpeedometerBackColor() {
        return backPaint.getColor();
    }

    /**
     * change the color of background progress.
     * @param speedometerBackColor new color.
     */
    public void setSpeedometerBackColor(int speedometerBackColor) {
        backPaint.setColor(speedometerBackColor);
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }
}
