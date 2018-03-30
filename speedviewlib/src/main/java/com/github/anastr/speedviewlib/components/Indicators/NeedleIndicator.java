package com.github.anastr.speedviewlib.components.Indicators;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */

public class NeedleIndicator extends Indicator {

    private Path indicatorPath = new Path();
    private Path circlePath = new Path();
    private Paint circlePaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
    private float bottomY;

    public NeedleIndicator(Context context) {
        super(context);
        circlePaint.setStyle(Paint.Style.STROKE);
        updateIndicator();
    }

    @Override
    protected float getDefaultIndicatorWidth() {
        return dpTOpx(12f);
    }

    @Override
    public float getBottom() {
        return bottomY;
    }

    @Override
    public void draw(Canvas canvas, float degree) {
        canvas.save();
        canvas.rotate(90f + degree, getCenterX(), getCenterY());
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.drawPath(circlePath, circlePaint);
        canvas.restore();
    }

    @Override
    protected void updateIndicator() {
        indicatorPath.reset();
        circlePath.reset();
        indicatorPath.moveTo(getCenterX(), getPadding());
        bottomY = (float) (getIndicatorWidth() * Math.sin(Math.toRadians(260))) + getViewSize()*.5f + getPadding();
        float xLeft = (float) (getIndicatorWidth() * Math.cos(Math.toRadians(260))) + getViewSize()*.5f + getPadding();
        indicatorPath.lineTo(xLeft, bottomY);
        RectF rectF = new RectF(getCenterX() - getIndicatorWidth(), getCenterY() - getIndicatorWidth()
                , getCenterX() + getIndicatorWidth(), getCenterY() + getIndicatorWidth());
        indicatorPath.arcTo(rectF, 260, 20f);

        float circleWidth = getIndicatorWidth() *.25f;
        circlePath.addCircle(getCenterX(), getCenterY(), getIndicatorWidth() - circleWidth*.5f +.6f, Path.Direction.CW);

        indicatorPaint.setColor(getIndicatorColor());
        circlePaint.setColor(getIndicatorColor());
        circlePaint.setStrokeWidth(circleWidth);
    }

    @Override
    protected void setWithEffects(boolean withEffects) {
        if (withEffects && !isInEditMode())
            indicatorPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
        else
            indicatorPaint.setMaskFilter(null);
    }
}
