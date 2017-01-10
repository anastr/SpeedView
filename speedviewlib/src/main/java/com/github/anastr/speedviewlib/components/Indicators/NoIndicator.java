package com.github.anastr.speedviewlib.components.Indicators;

import android.content.Context;
import android.graphics.Canvas;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class NoIndicator extends Indicator<NoIndicator> {

    public NoIndicator(Context context) {
        super(context);
    }

    @Override
    protected float getDefaultIndicatorWidth() {
        return 0f;
    }

    @Override
    public void draw(Canvas canvas, float degree) {
    }

    @Override
    protected void updateIndicator() {
    }

    @Override
    protected void setWithEffects(boolean withEffects) {
    }
}
