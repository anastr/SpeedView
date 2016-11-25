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

import java.util.Locale;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class DeluxeSpeedView extends Speedometer {

    private Path indicatorPath = new Path(),
            markPath = new Path(),
            smallMarkPath = new Path();
    private Paint centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            smallMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF(),
            speedBackgroundRect = new RectF();
    private int speedBackgroundColor = Color.WHITE;

    private boolean withEffects = true;

    public DeluxeSpeedView(Context context) {
        super(context);
        init();
    }

    public DeluxeSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public DeluxeSpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
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

        float risk = getSpeedometerWidth()/2f;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float indW = w/32f;

        indicatorPath.reset();
        indicatorPath.moveTo(w/2f, h/5f);
        indicatorPath.lineTo(w/2f -indW, h*3f/5f);
        indicatorPath.lineTo(w/2f +indW, h*3f/5f);
        RectF rectF = new RectF(w/2f -indW, h*3f/5f -indW, w/2f +indW, h*3f/5f +indW);
        indicatorPath.addArc(rectF, 0f, 180f);
        indicatorPath.moveTo(0f, 0f);

        float markH = h/28f;
        markPath.reset();
        markPath.moveTo(w/2f, 0f);
        markPath.lineTo(w/2f, markH);
        markPath.moveTo(0f, 0f);
        markPaint.setStrokeWidth(markH/3f);

        float smallMarkH = h/20f;
        smallMarkPath.reset();
        smallMarkPath.moveTo(w/2f, getSpeedometerWidth());
        smallMarkPath.lineTo(w/2f, getSpeedometerWidth() + smallMarkH);
        smallMarkPath.moveTo(0f, 0f);
        smallMarkPaint.setStrokeWidth(3);

        updateBackgroundBitmap();
    }

    private void initDraw() {
        indicatorPaint.setColor(getIndicatorColor());
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
        smallMarkPaint.setColor(getMarkColor());
        centerCirclePaint.setColor(getCenterCircleColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundBitmapPaint);

        canvas.save();
        canvas.rotate(90f +getDegree(), getWidth()/2f, getHeight()/2f);
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/12f, centerCirclePaint);

        String speed = String.format(Locale.getDefault(), "%.1f", getCorrectSpeed());
        float speedTextPadding = dpTOpx(1);
        float unitTextPadding = dpTOpx(1f);
        float unitW = unitTextPadding + unitTextPaint.measureText(getUnit()) + 5;
        speedBackgroundRect.top = getHeight()*.9f - Math.max(speedTextPaint.getTextSize(), unitTextPaint.getTextSize());
        speedBackgroundRect.bottom = getHeight()*.9f + 4f;
        if (isSpeedometerTextRightToLeft()) {
            unitTextPaint.setTextAlign(Paint.Align.RIGHT);
            speedBackgroundRect.left = getWidth()/2f - unitW;
            unitTextPadding *= -1;
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            speedBackgroundRect.right = getWidth() / 2f + speedTextPaint.measureText(speed) + 5f + speedTextPadding;
            speedTextPadding *= -1;
        }
        else {
            unitTextPaint.setTextAlign(Paint.Align.LEFT);
            speedBackgroundRect.right = getWidth()/2f + unitW;
            speedTextPaint.setTextAlign(Paint.Align.RIGHT);
            speedBackgroundRect.left = getWidth() / 2f - speedTextPaint.measureText(speed) - 5f - speedTextPadding;
        }
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint);

        canvas.drawText(speed
                , getWidth()/2f - speedTextPadding, getHeight()*.9f, speedTextPaint);
        canvas.drawText(getUnit()
                , getWidth()/2f + unitTextPadding, getHeight()*.9f, unitTextPaint);
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

        c.save();
        c.rotate(90f + getMIN_DEGREE(), getWidth()/2f, getHeight()/2f);
        for (float i= getMIN_DEGREE(); i < getMAX_DEGREE() - 10f; i+=10f) {
            c.rotate(10f, getWidth()/2f, getHeight()/2f);
            c.drawPath(smallMarkPath, smallMarkPaint);
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

        return backgroundBitmap;
    }

    public boolean isWithEffects() {
        return withEffects;
    }

    public void setWithEffects(boolean withEffects) {
        this.withEffects = withEffects;
        if (withEffects && !isInEditMode()) {
            indicatorPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
            markPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
            speedBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
            centerCirclePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        }
        else {
            indicatorPaint.setMaskFilter(null);
            markPaint.setMaskFilter(null);
            speedBackgroundPaint.setMaskFilter(null);
            centerCirclePaint.setMaskFilter(null);
        }
        updateBackgroundBitmap();
        invalidate();
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
