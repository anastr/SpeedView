package com.github.anastr.speedviewlib.components.Indicators;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Path;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class SpindleIndicator extends Indicator<SpindleIndicator> {

    private Path indicatorPath = new Path();

    public SpindleIndicator(Context context) {
        super(context);
        updateIndicator();
    }

    @Override
    protected float getDefaultIndicatorWidth() {
        return dpTOpx(16f);
    }

    @Override
    public float getTop() {
        return getViewSize()*.18f + getPadding();
    }

    @Override
    public void draw(Canvas canvas, float degree) {
        canvas.save();
        canvas.rotate(90f + degree, getCenterX(), getCenterY());
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();
    }

    @Override
    protected void updateIndicator() {
        indicatorPath.reset();
        indicatorPath.moveTo(getCenterX(), getCenterY());
        indicatorPath.quadTo(getCenterX() - getIndicatorWidth(), getViewSize()*.34f + getPadding()
                , getCenterX(), getViewSize()*.18f + getPadding());
        indicatorPath.quadTo(getCenterX() + getIndicatorWidth(), getViewSize()*.34f + getPadding()
                , getCenterX(), getCenterY());

        indicatorPaint.setColor(getIndicatorColor());
    }

    @Override
    protected void setWithEffects(boolean withEffects) {
        if (withEffects && !isInEditMode()) {
            indicatorPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
        }
        else {
            indicatorPaint.setMaskFilter(null);
        }
    }
}
