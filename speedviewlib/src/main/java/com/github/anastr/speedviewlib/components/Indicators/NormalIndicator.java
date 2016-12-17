package com.github.anastr.speedviewlib.components.Indicators;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.anastr.speedviewlib.Speedometer;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class NormalIndicator extends Indicator {

    private Path indicatorPath = new Path();

    public NormalIndicator(Speedometer speedometer) {
        super(speedometer);
        updateIndicator();
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
        float indicatorBottom = getViewHeight()*2f/3f + getPadding();
        indicatorPath.lineTo(getCenterX() - getIndicatorWidth(), indicatorBottom);
        indicatorPath.lineTo(getCenterX() + getIndicatorWidth(), indicatorBottom);
        RectF rectF = new RectF(getCenterX() - getIndicatorWidth(), indicatorBottom - getIndicatorWidth()
                , getCenterX() + getIndicatorWidth(), indicatorBottom + getIndicatorWidth());
        indicatorPath.addArc(rectF, 0f, 180f);

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
