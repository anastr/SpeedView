package com.github.anastr.speedviewlib.components.Indicators;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.github.anastr.speedviewlib.Speedometer;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unused,WeakerAccess")
public class ImageIndicator extends Indicator {

    private Bitmap bitmapIndicator;
    private int width, height;

    /**
     * create indicator from resources, the indicator direction must be up.<br>
     * center indicator position will be center of speedometer.
     * @param speedometer target speedometer.
     * @param resource the image id.
     */
    public ImageIndicator(Speedometer speedometer, int resource) {
        this(speedometer, BitmapFactory.decodeResource(speedometer.getContext().getResources(), resource));
    }

    /**
     * create indicator from resources, the indicator direction must be up.<br>
     * center indicator position will be center of speedometer.
     * @param speedometer target speedometer.
     * @param resource the image id.
     * @param width the custom width of the indicator.
     * @param height the custom height of the indicator.
     * @throws IllegalArgumentException if {@code width <= 0 OR height <= 0}.
     */
    public ImageIndicator(Speedometer speedometer, int resource, int width, int height) {
        this(speedometer, BitmapFactory.decodeResource(speedometer.getContext().getResources(), resource)
                , width, height);
    }

    /**
     * create indicator from bitmap, the indicator direction must be up.<br>
     * center indicator position will be center of speedometer.
     * @param speedometer target speedometer.
     * @param bitmapIndicator the indicator.
     */
    public ImageIndicator(Speedometer speedometer, Bitmap bitmapIndicator) {
        this(speedometer, bitmapIndicator, bitmapIndicator.getWidth(), bitmapIndicator.getHeight());
    }

    /**
     * create indicator from bitmap, the indicator direction must be up.<br>
     * center indicator position will be center of speedometer.
     * @param speedometer target speedometer.
     * @param bitmapIndicator the indicator.
     * @param width the custom width of the indicator.
     * @param height the custom height of the indicator.
     * @throws IllegalArgumentException if {@code width <= 0 OR height <= 0}.
     */
    public ImageIndicator(Speedometer speedometer, Bitmap bitmapIndicator, int width, int height) {
        super(speedometer);
        this.bitmapIndicator = bitmapIndicator;
        this.width = width;
        this.height = height;
        if (width <= 0)
            throw new IllegalArgumentException("width must be bigger than 0");
        if (height <= 0)
            throw new IllegalArgumentException("height must be bigger than 0");
    }

    @Override
    public void draw(Canvas canvas, float degree) {
        canvas.save();
        canvas.rotate(90f + degree, getCenterX(), getCenterY());
        canvas.drawBitmap(bitmapIndicator, getCenterX() - (width/2f), getCenterY() - (height /2f), indicatorPaint);
        canvas.restore();
    }

    @Override
    protected void updateIndicator() {
    }

    @Override
    protected void setWithEffects(boolean withEffects) {
    }
}
