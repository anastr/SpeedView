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
public class RaySpeedometer extends Speedometer {

    private Path markPath = new Path(),
            ray1Path = new Path(),
            ray2Path = new Path();
    private Paint markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            rayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF(),
            speedBackgroundRect = new RectF();
    private int speedBackgroundColor = Color.WHITE;

    private boolean withEffects = true;

    private int degreeBetweenMark = 5;
    private int rayColor = Color.WHITE;
    private float markWidth = dpTOpx(3);

    public RaySpeedometer(Context context) {
        super(context);
        init();
    }

    public RaySpeedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public RaySpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        super.setMarkColor(Color.BLACK);
        super.setTextColor(Color.WHITE);
        super.setBackgroundCircleColor(Color.parseColor("#212121"));
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RaySpeedometer, 0, 0);

        rayColor = a.getColor(R.styleable.RaySpeedometer_rayColor, rayColor);
        int degreeBetweenMark = a.getInt(R.styleable.RaySpeedometer_degreeBetweenMark, this.degreeBetweenMark);
        markWidth = a.getDimension(R.styleable.RaySpeedometer_markWidth, markWidth);
        speedBackgroundColor = a.getColor(R.styleable.RaySpeedometer_speedBackgroundColor, speedBackgroundColor);
        withEffects = a.getBoolean(R.styleable.RaySpeedometer_withEffects, withEffects);
        a.recycle();
        setWithEffects(withEffects);
        if (degreeBetweenMark > 0 && degreeBetweenMark <= 20)
            this.degreeBetweenMark = degreeBetweenMark;
        initAttributeValue();
    }

    private void initAttributeValue() {
        speedBackgroundPaint.setColor(speedBackgroundColor);
        markPaint.setStrokeWidth(markWidth);
        rayPaint.setColor(rayColor);
    }

    private void init() {
        markPaint.setStyle(Paint.Style.STROKE);
        rayPaint.setStyle(Paint.Style.STROKE);
        rayPaint.setStrokeWidth(dpTOpx(1.8f));

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWithEffects(withEffects);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        markPath.reset();
        markPath.moveTo(w/2f, 0f);
        markPath.lineTo(w/2f, getSpeedometerWidth());
        markPath.moveTo(0f, 0f);

        ray1Path.reset();
        ray1Path.moveTo(w/2f, h/2f);
        ray1Path.lineTo(w/2f, h/3.2f); ray1Path.moveTo(w/2f, h/3.2f);
        ray1Path.lineTo(w/2.2f, h/3f); ray1Path.moveTo(w/2.2f, h/3f);
        ray1Path.lineTo(w/2.1f, h/4.5f);

        ray2Path.reset();
        ray2Path.moveTo(w/2f, h/2f);
        ray2Path.lineTo(w/2f, h/3.2f); ray2Path.moveTo(w/2f, h/3.2f);
        ray2Path.lineTo(w/2.2f, h/3.8f); ray2Path.moveTo(w/2f, h/3.2f);
        ray2Path.lineTo(w/1.8f, h/3.8f);

        updateBackgroundBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundBitmapPaint);

        canvas.save();
        canvas.rotate(getStartDegree()+90f, getWidth()/2f, getHeight()/2f);
        for (int i = getStartDegree(); i < getEndDegree(); i+=degreeBetweenMark) {
            if (getDegree() <= i) {
                markPaint.setColor(getMarkColor());
                canvas.drawPath(markPath, markPaint);
                canvas.rotate(degreeBetweenMark, getWidth()/2f, getHeight()/2f);
                continue;
            }
            if (i > (getEndDegree()- getStartDegree())*getMediumSpeedOffset() + getStartDegree())
                markPaint.setColor(getHighSpeedColor());
            else if (i > (getEndDegree()- getStartDegree())*getLowSpeedOffset() + getStartDegree())
                markPaint.setColor(getMediumSpeedColor());
            else
                markPaint.setColor(getLowSpeedColor());
            canvas.drawPath(markPath, markPaint);
            canvas.rotate(degreeBetweenMark, getWidth()/2f, getHeight()/2f);
        }
        canvas.restore();

        String speed = String.format(Locale.getDefault(), "%.1f", getCorrectSpeed());
        float speedTextPadding = dpTOpx(1);
        float unitTextPadding = dpTOpx(1f);
        float unitW = unitTextPadding + unitTextPaint.measureText(getUnit()) + 5;
        speedBackgroundRect.top = getHeight()/2f - (Math.max(speedTextPaint.getTextSize(), unitTextPaint.getTextSize())/2f);
        speedBackgroundRect.bottom = getHeight()/2f +(Math.max(speedTextPaint.getTextSize(), unitTextPaint.getTextSize())/2f)+4f;
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

        canvas.drawText(speed, getWidth()/2f - speedTextPadding
                , getHeight()/2f + (Math.max(speedTextPaint.getTextSize(), unitTextPaint.getTextSize())/2f), speedTextPaint);
        canvas.drawText(getUnit(), getWidth()/2f + unitTextPadding
                , getHeight()/2f + (Math.max(speedTextPaint.getTextSize(), unitTextPaint.getTextSize())/2f), unitTextPaint);
    }

    @Override
    protected Bitmap updateBackgroundBitmap() {
        if (getWidth() == 0 || getHeight() == 0)
            return null;
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(backgroundBitmap);

        c.save();
        for (int i=0; i<6; i++) {
            c.rotate(58f, getWidth()/2f, getHeight()/2f);
            if (i % 2 == 0)
                c.drawPath(ray1Path, rayPaint);
            else
                c.drawPath(ray2Path, rayPaint);
        }
        c.restore();

        if (getStartDegree()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getStartDegree()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getStartDegree()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getStartDegree() + 90f, getWidth()/2f, getHeight()/2f);
        c.rotate(-(getStartDegree() + 90f), getWidth()/2f - textPaint.getTextSize(), textPaint.getTextSize());
        c.drawText("00", getWidth()/2f - textPaint.getTextSize(), textPaint.getTextSize(), textPaint);
        c.restore();
        if (getEndDegree()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getEndDegree()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getEndDegree()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getEndDegree() + 90f, getWidth()/2f, getHeight()/2f);
        c.rotate(-(getEndDegree() + 90f), getWidth()/2f + textPaint.getTextSize(), textPaint.getTextSize());
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
            rayPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.SOLID));
            markPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
            speedBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
        }
        else {
            rayPaint.setMaskFilter(null);
            markPaint.setMaskFilter(null);
            speedBackgroundPaint.setMaskFilter(null);
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

    public int getDegreeBetweenMark() {
        return degreeBetweenMark;
    }

    /**
     * The spacing between the marks
     * <p>
     *     it should be between (0-20] ,else well be ignore.
     * </p>
     * @param degreeBetweenMark degree between two marks.
     */
    public void setDegreeBetweenMark(int degreeBetweenMark) {
        if (degreeBetweenMark <= 0 || degreeBetweenMark > 20)
            return;
        this.degreeBetweenMark = degreeBetweenMark;
        invalidate();
    }

    public float getMarkWidth() {
        return markWidth;
    }

    public void setMarkWidth(float markWidth) {
        this.markWidth = markWidth;
        markPaint.setStrokeWidth(markWidth);
        invalidate();
    }

    public int getRayColor() {
        return rayColor;
    }

    public void setRayColor(int rayColor) {
        this.rayColor = rayColor;
        rayPaint.setColor(rayColor);
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getIndicatorColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param indicatorColor nothing.
     */
    @Deprecated
    @Override
    public void setIndicatorColor(int indicatorColor) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getCenterCircleColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param centerCircleColor nothing.
     */
    @Deprecated
    @Override
    public void setCenterCircleColor(int centerCircleColor) {
    }
}
