package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
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
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            rayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint speedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG),
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
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
        setMarkColor(Color.BLACK);
        setTextColor(Color.WHITE);
        setBackgroundCircleColor(Color.parseColor("#212121"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = (width > height) ? height : width;
        setMeasuredDimension(size, size);
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
    }

    private void init() {
        markPaint.setStyle(Paint.Style.STROKE);
        rayPaint.setStyle(Paint.Style.STROKE);
        rayPaint.setStrokeWidth(dpTOpx(1.8f));
        speedTextPaint.setTextAlign(Paint.Align.CENTER);

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
    }

    private void initDraw() {
        speedTextPaint.setColor(getSpeedTextColor());
        speedTextPaint.setTextSize(getSpeedTextSize());
        textPaint.setColor(getTextColor());
        textPaint.setTextSize(getTextSize());
        speedBackgroundPaint.setColor(speedBackgroundColor);
        circlePaint.setColor(getBackgroundCircleColor());
        markPaint.setStrokeWidth(markWidth);
        rayPaint.setColor(rayColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        if (isWithBackgroundCircle())
            canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f, circlePaint);

        canvas.save();
        for (int i=0; i<6; i++) {
            canvas.rotate(58f, getWidth()/2f, getHeight()/2f);
            if (i % 2 == 0)
                canvas.drawPath(ray1Path, rayPaint);
            else
                canvas.drawPath(ray2Path, rayPaint);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(135f+90f, getWidth()/2f, getHeight()/2f);
        for (int i=135; i < 405; i+=degreeBetweenMark) {
            if (getDegree() <= i) {
                markPaint.setColor(getMarkColor());
                canvas.drawPath(markPath, markPaint);
                canvas.rotate(degreeBetweenMark, getWidth()/2f, getHeight()/2f);
                continue;
            }
            if (i < 135+160)
                markPaint.setColor(getLowSpeedColor());
            else if (i < 135+160+75)
                markPaint.setColor(getMediumSpeedColor());
            else
                markPaint.setColor(getHighSpeedColor());
            canvas.drawPath(markPath, markPaint);
            canvas.rotate(degreeBetweenMark, getWidth()/2f, getHeight()/2f);
        }
        canvas.restore();

        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("00", getWidth()/5f, getHeight()*6/7f, textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format(Locale.getDefault(), "%d", getMaxSpeed()), getWidth()*4/5f, getHeight()*6/7f, textPaint);
        String sSpeedUnit = String.format(Locale.getDefault(), "%.1f"
                , getCorrectSpeed()) +getUnit();
        speedBackgroundRect.set(getWidth()/2f - (speedTextPaint.measureText(sSpeedUnit)/2f) -5
                , getHeight()/2f - (speedTextPaint.getTextSize()/2f)
                , getWidth()/2f + (speedTextPaint.measureText(sSpeedUnit)/2f) +5
                , getHeight()/2f + (speedTextPaint.getTextSize()/2f) + 4);
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint);
        canvas.drawText(sSpeedUnit, getWidth()/2f, getHeight()/2f + (speedTextPaint.getTextSize()/2f), speedTextPaint);
    }

    public boolean isWithEffects() {
        return withEffects;
    }

    public void setWithEffects(boolean withEffects) {
        this.withEffects = withEffects;
        if (withEffects) {
            rayPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.SOLID));
            markPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
            speedBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
        }
        else {
            rayPaint.setMaskFilter(null);
            markPaint.setMaskFilter(null);
            speedBackgroundPaint.setMaskFilter(null);
        }
        invalidate();
    }

    public int getSpeedBackgroundColor() {
        return speedBackgroundColor;
    }

    public void setSpeedBackgroundColor(int speedBackgroundColor) {
        this.speedBackgroundColor = speedBackgroundColor;
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
        invalidate();
    }

    public int getRayColor() {
        return rayColor;
    }

    public void setRayColor(int rayColor) {
        this.rayColor = rayColor;
    }

    @Deprecated
    @Override
    public int getIndicatorColor() {
        return super.getIndicatorColor();
    }

    @Deprecated
    @Override
    public void setIndicatorColor(int indicatorColor) {
    }

    @Deprecated
    @Override
    public int getCenterCircleColor() {
        return super.getCenterCircleColor();
    }

    @Deprecated
    @Override
    public void setCenterCircleColor(int centerCircleColor) {
    }
}
