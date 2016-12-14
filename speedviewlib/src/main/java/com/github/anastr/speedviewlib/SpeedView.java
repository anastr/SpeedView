package com.github.anastr.speedviewlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class SpeedView extends Speedometer {

    private Path indicatorPath = new Path(),
            markPath = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    public SpeedView(Context context) {
        this(context, null);
    }

    public SpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void defaultValues() {
        super.setBackgroundCircleColor(Color.TRANSPARENT);
    }

    private void init() {
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f + padding;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float indW = w/32f;

        indicatorPath.reset();
        indicatorPath.moveTo(w/2f, padding);
        float indicatorBottom = getHeightPa()*2f/3f + padding;
        indicatorPath.lineTo(w/2f -indW, indicatorBottom);
        indicatorPath.lineTo(w/2f +indW, indicatorBottom);
        RectF rectF = new RectF(w/2f -indW, indicatorBottom -indW, w/2f +indW, indicatorBottom +indW);
        indicatorPath.addArc(rectF, 0f, 180f);

        float markH = getHeightPa()/28f;
        markPath.reset();
        markPath.moveTo(w/2f, padding);
        markPath.lineTo(w/2f, markH + padding);
        markPaint.setStrokeWidth(markH/3f);

        updateBackgroundBitmap();
    }

    private void initDraw() {
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        paint.setColor(getIndicatorColor());
        canvas.save();
        canvas.rotate(90f +getDegree(), getWidth()/2f, getHeight()/2f);
        canvas.drawPath(indicatorPath, paint);
        canvas.restore();
        paint.setColor(getCenterCircleColor());
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidthPa()/12f, paint);

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

        speedometerPaint.setColor(getHighSpeedColor());
        c.drawArc(speedometerRect, getStartDegree(), getEndDegree()- getStartDegree(), false, speedometerPaint);
        speedometerPaint.setColor(getMediumSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree()- getStartDegree())*getMediumSpeedOffset(), false, speedometerPaint);
        speedometerPaint.setColor(getLowSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree()- getStartDegree())*getLowSpeedOffset(), false, speedometerPaint);

        c.save();
        c.rotate(90f + getStartDegree(), getWidth()/2f, getHeight()/2f);
        float everyDegree = (getEndDegree() - getStartDegree()) * .111f;
        for (float i = getStartDegree(); i < getEndDegree()-(2f*everyDegree); i+=everyDegree) {
            c.rotate(everyDegree, getWidth()/2f, getHeight()/2f);
            c.drawPath(markPath, markPaint);
        }
        c.restore();

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
}
