package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
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
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            smallMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

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
    protected void defaultGaugeValues() {
        super.setTextColor(0xFFFFFFFF);
    }

    @Override
    protected void defaultSpeedometerValues() {
        super.setIndicator(new NormalSmallIndicator(getContext())
                .setIndicatorColor(0xff00ffec));
        super.setBackgroundCircleColor(0xff212121);
        super.setLowSpeedColor(0xff37872f);
        super.setMediumSpeedColor(0xffa38234);
        super.setHighSpeedColor(0xff9b2020);
    }

    private void init() {
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
        smallMarkPaint.setStyle(Paint.Style.STROKE);
        speedBackgroundPaint.setColor(0xFFFFFFFF);
        circlePaint.setColor(0xffe0e0e0);

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWithEffects(withEffects);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            initAttributeValue();
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DeluxeSpeedView, 0, 0);

        speedBackgroundPaint.setColor(a.getColor(R.styleable.DeluxeSpeedView_sv_speedBackgroundColor
                , speedBackgroundPaint.getColor()));
        withEffects = a.getBoolean(R.styleable.DeluxeSpeedView_sv_withEffects, withEffects);
        circlePaint.setColor(a.getColor(R.styleable.DeluxeSpeedView_sv_centerCircleColor, circlePaint.getColor()));
        a.recycle();
        setWithEffects(withEffects);
        initAttributeValue();
    }

    private void initAttributeValue() {
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updateBackgroundBitmap();
    }

    private void initDraw() {
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
        smallMarkPaint.setColor(getMarkColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF speedBackgroundRect = getSpeedUnitTextBounds();
        speedBackgroundRect.left -= 2;
        speedBackgroundRect.right += 2;
        speedBackgroundRect.bottom += 2;
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint);

        drawSpeedUnitText(canvas);
        drawIndicator(canvas);
        canvas.drawCircle(getSize() *.5f, getSize() *.5f, getWidthPa()/12f, circlePaint);
        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        initDraw();

        float smallMarkH = getViewSizePa()/20f;
        smallMarkPath.reset();
        smallMarkPath.moveTo(getSize() *.5f, getSpeedometerWidth() + getPadding());
        smallMarkPath.lineTo(getSize() *.5f, getSpeedometerWidth() + getPadding() + smallMarkH);
        smallMarkPaint.setStrokeWidth(3);

        float markH = getViewSizePa()/28f;
        markPath.reset();
        markPath.moveTo(getSize() *.5f, getPadding());
        markPath.lineTo(getSize() *.5f, markH + getPadding());
        markPaint.setStrokeWidth(markH/3f);

        float risk = getSpeedometerWidth() *.5f + getPadding();
        speedometerRect.set(risk, risk, getSize() -risk, getSize() -risk);

        speedometerPaint.setColor(getHighSpeedColor());
        c.drawArc(speedometerRect, getStartDegree(), getEndDegree()- getStartDegree(), false, speedometerPaint);
        speedometerPaint.setColor(getMediumSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree()- getStartDegree())*getMediumSpeedOffset(), false, speedometerPaint);
        speedometerPaint.setColor(getLowSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree()- getStartDegree())*getLowSpeedOffset(), false, speedometerPaint);

        c.save();
        c.rotate(90f + getStartDegree(), getSize() *.5f, getSize() *.5f);
        float everyDegree = (getEndDegree() - getStartDegree()) * .111f;
        for (float i = getStartDegree(); i < getEndDegree()-(2f*everyDegree); i+=everyDegree) {
            c.rotate(everyDegree, getSize() *.5f, getSize() *.5f);
            c.drawPath(markPath, markPaint);
        }
        c.restore();

        c.save();
        c.rotate(90f + getStartDegree(), getSize() *.5f, getSize() *.5f);
        for (float i = getStartDegree(); i < getEndDegree() - 10f; i+=10f) {
            c.rotate(10f, getSize() *.5f, getSize() *.5f);
            c.drawPath(smallMarkPath, smallMarkPaint);
        }
        c.restore();

        if (getTickNumber() > 0)
            drawTicks(c);
        else
            drawDefMinMaxSpeedPosition(c);
    }

    public boolean isWithEffects() {
        return withEffects;
    }

    public void setWithEffects(boolean withEffects) {
        this.withEffects = withEffects;
        if (isInEditMode())
            return;
        indicatorEffects(withEffects);
        if (withEffects) {
            markPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
            speedBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
            circlePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        }
        else {
            markPaint.setMaskFilter(null);
            speedBackgroundPaint.setMaskFilter(null);
            circlePaint.setMaskFilter(null);
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
        return speedBackgroundPaint.getColor();
    }

    public void setSpeedBackgroundColor(int speedBackgroundColor) {
        speedBackgroundPaint.setColor(speedBackgroundColor);
        updateBackgroundBitmap();
        invalidate();
    }

    public int getCenterCircleColor() {
        return circlePaint.getColor();
    }

    /**
     * change the color of the center circle (if exist),
     * <b>this option is not available for all Speedometers</b>.
     * @param centerCircleColor new color.
     */
    public void setCenterCircleColor(int centerCircleColor) {
        circlePaint.setColor(centerCircleColor);
        if (!isAttachedToWindow())
            return;
        invalidate();
    }
}
