package com.github.anastr.speedviewlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.Locale;

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
        super(context);
        init();
    }

    public SpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
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

        float risk = getSpeedometerWidth()/2f;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float indW = w/32f;

        indicatorPath.reset();
        indicatorPath.moveTo(w/2f, 0f);
        indicatorPath.lineTo(w/2f -indW, h*2f/3f);
        indicatorPath.lineTo(w/2f +indW, h*2f/3f);
        RectF rectF = new RectF(w/2f -indW, h*2f/3f -indW, w/2f +indW, h*2f/3f +indW);
        indicatorPath.addArc(rectF, 0f, 180f);
        indicatorPath.moveTo(0f, 0f);

        float markH = h/28f;
        markPath.reset();
        markPath.moveTo(w/2f, 0f);
        markPath.lineTo(w/2f, markH);
        markPath.moveTo(0f, 0f);
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

        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundBitmapPaint);

        paint.setColor(getIndicatorColor());
        canvas.save();
        canvas.rotate(90f +getDegree(), getWidth()/2f, getHeight()/2f);
        canvas.drawPath(indicatorPath, paint);
        canvas.restore();
        paint.setColor(getCenterCircleColor());
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/12f, paint);

        String speed = String.format(Locale.getDefault(), "%.1f", getCorrectSpeed());
        float speedTextPadding = dpTOpx(1);
        if (isSpeedometerTextRightToLeft()) {
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            speedTextPadding *= -1;
        }
        else
            speedTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(speed
                , getWidth()/2f - speedTextPadding, getHeight()*.9f, speedTextPaint);
    }

    @Override
    protected Bitmap updateBackgroundBitmap() {
        if (getWidth() == 0 || getHeight() == 0)
            return null;
        initDraw();
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(backgroundBitmap);

        speedometerPaint.setColor(getHighSpeedColor());
        c.drawArc(speedometerRect, getMIN_DEGREE(), getMAX_DEGREE()-getMIN_DEGREE(), false, speedometerPaint);
        speedometerPaint.setColor(getMediumSpeedColor());
        c.drawArc(speedometerRect, getMIN_DEGREE()
                , (getMAX_DEGREE()-getMIN_DEGREE())*getMediumSpeedOffset(), false, speedometerPaint);
        speedometerPaint.setColor(getLowSpeedColor());
        c.drawArc(speedometerRect, getMIN_DEGREE()
                , (getMAX_DEGREE()-getMIN_DEGREE())*getLowSpeedOffset(), false, speedometerPaint);

        c.save();
        c.rotate(90f + getMIN_DEGREE(), getWidth()/2f, getHeight()/2f);
        float everyDegree = (getMAX_DEGREE() - getMIN_DEGREE()) * .111f;
        for (float i=getMIN_DEGREE(); i < getMAX_DEGREE()-(2f*everyDegree); i+=everyDegree) {
            c.rotate(everyDegree, getWidth()/2f, getHeight()/2f);
            c.drawPath(markPath, markPaint);
        }
        c.restore();

        if (getMIN_DEGREE()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getMIN_DEGREE()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getMIN_DEGREE()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getMIN_DEGREE() + 90f, getWidth()/2f, getHeight()/2f);
        c.rotate(-(getMIN_DEGREE() + 90f), getWidth()/2f - textPaint.getTextSize(), textPaint.getTextSize());
        c.drawText("00", getWidth()/2f - textPaint.getTextSize(), textPaint.getTextSize(), textPaint);
        c.restore();
        if (getMAX_DEGREE()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getMAX_DEGREE()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getMAX_DEGREE()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getMAX_DEGREE() + 90f, getWidth()/2f, getHeight()/2f);
        c.rotate(-(getMAX_DEGREE() + 90f), getWidth()/2f + textPaint.getTextSize(), textPaint.getTextSize());
        String maxSpeed = String.format(Locale.getDefault(), "%d", getMaxSpeed());
        c.drawText(maxSpeed, getWidth()/2f + textPaint.getTextSize(), textPaint.getTextSize(), textPaint);
        c.restore();

        float unitTextPadding = dpTOpx(1);
        if (isSpeedometerTextRightToLeft()) {
            unitTextPaint.setTextAlign(Paint.Align.RIGHT);
            unitTextPadding *= -1;
        }
        else
            unitTextPaint.setTextAlign(Paint.Align.LEFT);

        c.drawText(getUnit()
                , getWidth()/2f + unitTextPadding, getHeight()*.9f, unitTextPaint);

        return backgroundBitmap;
    }
}
