package com.github.anastr.speedviewlib.components.Indicators;

import android.graphics.Canvas;

import com.github.anastr.speedviewlib.Speedometer;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class NoIndicator extends Indicator {

    public NoIndicator(Speedometer speedometer) {
        super(speedometer);
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
