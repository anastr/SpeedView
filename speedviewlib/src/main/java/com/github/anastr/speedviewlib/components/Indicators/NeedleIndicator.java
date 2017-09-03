package com.github.anastr.speedviewlib.components.Indicators;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */

public class NeedleIndicator extends Indicator {

    private Path indicatorPath = new Path();

    public NeedleIndicator(Context context) {
        super(context);
        updateIndicator();
    }

    @Override
    protected float getDefaultIndicatorWidth() {
        return dpTOpx(12f);
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
        indicatorPath.moveTo(getCenterX(), getPadding());
        float y = (float) (getIndicatorWidth() * Math.sin(265)) + getViewSize()*.5f + getPadding();
        float xLeft = (float) (getIndicatorWidth() * Math.cos(265)) + getViewSize()*.5f + getPadding();
        indicatorPath.lineTo(xLeft, y);
        RectF rectF = new RectF(getCenterX() - getIndicatorWidth(), getCenterX() - getIndicatorWidth()
                , getCenterX() + getIndicatorWidth(), getCenterX() + getIndicatorWidth());
        indicatorPath.arcTo(rectF, 265, -10f);
        indicatorPath.addCircle(getCenterX(), getCenterY(), getIndicatorWidth(), Path.Direction.CW);

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
