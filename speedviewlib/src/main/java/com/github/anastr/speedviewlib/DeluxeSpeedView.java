package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.github.anastr.speedviewlib.components.Indicators.NormalSmallIndicator;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class DeluxeSpeedView extends Speedometer {

    private Path markPath = new Path(),
            smallMarkPath = new Path();
    private Paint centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            smallMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF(),
            speedBackgroundRect = new RectF();
    private int speedBackgroundColor = Color.WHITE;

    private boolean withEffects = true;

    public DeluxeSpeedView(Context context) {
        this(context, null);
    }

    public DeluxeSpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeluxeSpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        super.setIndicator(new NormalSmallIndicator(getContext()));
        super.setIndicatorColor(Color.parseColor("#00ffec"));
        super.setCenterCircleColor(Color.parseColor("#e0e0e0"));
        super.setLowSpeedColor(Color.parseColor("#37872f"));
        super.setMediumSpeedColor(Color.parseColor("#a38234"));
        super.setHighSpeedColor(Color.parseColor("#9b2020"));
        super.setTextColor(Color.WHITE);
        super.setBackgroundCircleColor(Color.parseColor("#212121"));
    }

    private void init() {
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
        smallMarkPaint.setStyle(Paint.Style.STROKE);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWithEffects(withEffects);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            initAttributeValue();
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DeluxeSpeedView, 0, 0);

        speedBackgroundColor = a.getColor(R.styleable.DeluxeSpeedView_speedBackgroundColor, speedBackgroundColor);
        withEffects = a.getBoolean(R.styleable.DeluxeSpeedView_withEffects, withEffects);
        a.recycle();
        setWithEffects(withEffects);
        initAttributeValue();
    }

    private void initAttributeValue() {
        speedBackgroundPaint.setColor(speedBackgroundColor);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f + getPadding();
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float markH = getHeightPa()/28f;
        markPath.reset();
        markPath.moveTo(w/2f, getPadding());
        markPath.lineTo(w/2f, markH + getPadding());
        markPaint.setStrokeWidth(markH/3f);

        float smallMarkH = getHeightPa()/20f;
        smallMarkPath.reset();
        smallMarkPath.moveTo(w/2f, getSpeedometerWidth() + getPadding());
        smallMarkPath.lineTo(w/2f, getSpeedometerWidth() + getPadding() + smallMarkH);
        smallMarkPaint.setStrokeWidth(3);

        updateBackgroundBitmap();
    }

    private void initDraw() {
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
        smallMarkPaint.setColor(getMarkColor());
        centerCirclePaint.setColor(getCenterCircleColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        drawIndicator(canvas);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidthPa()/12f, centerCirclePaint);

        String speedText = getSpeedText();
        float speedTextPadding = dpTOpx(1);
        float unitTextPadding = dpTOpx(1f);
        float unitW = unitTextPadding + unitTextPaint.measureText(getUnit()) + 5;
        speedBackgroundRect.top = getHeightPa()*.9f + getPadding() - Math.max(speedTextPaint.getTextSize(), unitTextPaint.getTextSize());
        speedBackgroundRect.bottom = getHeightPa()*.9f + getPadding() + 4f;
        if (isSpeedometerTextRightToLeft()) {
            unitTextPaint.setTextAlign(Paint.Align.RIGHT);
            speedBackgroundRect.left = getWidth()/2f - unitW;
            unitTextPadding *= -1;
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            speedBackgroundRect.right = getWidth() / 2f + speedTextPaint.measureText(speedText) + 5f + speedTextPadding;
            speedTextPadding *= -1;
        }
        else {
            unitTextPaint.setTextAlign(Paint.Align.LEFT);
            speedBackgroundRect.right = getWidth()/2f + unitW;
            speedTextPaint.setTextAlign(Paint.Align.RIGHT);
            speedBackgroundRect.left = getWidth() / 2f - speedTextPaint.measureText(speedText) - 5f - speedTextPadding;
        }
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint);

        canvas.drawText(speedText
                , getWidth()/2f - speedTextPadding, getHeightPa()*.9f + getPadding(), speedTextPaint);
        canvas.drawText(getUnit()
                , getWidth()/2f + unitTextPadding, getHeightPa()*.9f + getPadding(), unitTextPaint);

        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        if (getWidth() == 0 || getHeight() == 0)
            return ;
        initDraw();
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(backgroundBitmap);
        c.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f - getPadding(), circleBackPaint);

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

        c.save();
        c.rotate(90f + getStartDegree(), getWidth()/2f, getHeight()/2f);
        for (float i = getStartDegree(); i < getEndDegree() - 10f; i+=10f) {
            c.rotate(10f, getWidth()/2f, getHeight()/2f);
            c.drawPath(smallMarkPath, smallMarkPaint);
        }
        c.restore();

        drawDefaultMinAndMaxSpeedPosition(c);
    }

    public boolean isWithEffects() {
        return withEffects;
    }

    public void setWithEffects(boolean withEffects) {
        this.withEffects = withEffects;
        indicatorEffects(withEffects);
        if (withEffects && !isInEditMode()) {
            markPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
            speedBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
            centerCirclePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        }
        else {
            markPaint.setMaskFilter(null);
            speedBackgroundPaint.setMaskFilter(null);
            centerCirclePaint.setMaskFilter(null);
        }
        updateBackgroundBitmap();
        invalidate();
    }

    @Override
    public void setIndicator(Indicator.Indicators indicator) {
        super.setIndicator(indicator);
        indicatorEffects(withEffects);
    }

    public int getSpeedBackgroundColor() {
        return speedBackgroundColor;
    }

    public void setSpeedBackgroundColor(int speedBackgroundColor) {
        this.speedBackgroundColor = speedBackgroundColor;
        speedBackgroundPaint.setColor(speedBackgroundColor);
        updateBackgroundBitmap();
        invalidate();
    }
}
