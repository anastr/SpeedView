package com.github.anastr.speedviewlib.components.Indicators;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unused,WeakerAccess")
public class ImageIndicator extends Indicator<ImageIndicator> {

    private Bitmap bitmapIndicator;
    private int width, height;
    private RectF bitmapRect = new RectF();

    /**
     * create indicator from resources, the indicator direction must be up.<br>
     * center indicator position will be center of speedometer.
     * @param context you can use {@code getApplicationContext()}.
     * @param resource the image id.
     */
    public ImageIndicator(Context context, int resource) {
        this(context, BitmapFactory.decodeResource(context.getResources(), resource));
    }

    /**
     * create indicator from resources, the indicator direction must be up.<br>
     * center indicator position will be center of speedometer.
     * @param context you can use {@code getApplicationContext()}.
     * @param resource the image id.
     * @param width the custom width of the indicator.
     * @param height the custom height of the indicator.
     * @throws IllegalArgumentException if {@code width <= 0 OR height <= 0}.
     */
    public ImageIndicator(Context context, int resource, int width, int height) {
        this(context, BitmapFactory.decodeResource(context.getResources(), resource)
                , width, height);
    }

    /**
     * create indicator from bitmap, the indicator direction must be up.<br>
     * center indicator position will be center of speedometer.
     * @param context you can use {@code getApplicationContext()}.
     * @param bitmapIndicator the indicator.
     */
    public ImageIndicator(Context context, Bitmap bitmapIndicator) {
        this(context, bitmapIndicator, bitmapIndicator.getWidth(), bitmapIndicator.getHeight());
    }

    /**
     * create indicator from bitmap, the indicator direction must be up.<br>
     * center indicator position will be center of speedometer.
     * @param context you can use {@code getApplicationContext()}.
     * @param bitmapIndicator the indicator.
     * @param width the custom width of the indicator.
     * @param height the custom height of the indicator.
     * @throws IllegalArgumentException if {@code width <= 0 OR height <= 0}.
     */
    public ImageIndicator(Context context, Bitmap bitmapIndicator, int width, int height) {
        super(context);
        this.bitmapIndicator = bitmapIndicator;
        this.width = width;
        this.height = height;
        if (width <= 0)
            throw new IllegalArgumentException("width must be bigger than 0");
        if (height <= 0)
            throw new IllegalArgumentException("height must be bigger than 0");
    }

    @Override
    protected float getDefaultIndicatorWidth() {
        return 0f;
    }

    @Override
    public void draw(Canvas canvas, float degree) {
        canvas.save();
        canvas.rotate(90f + degree, getCenterX(), getCenterY());
        bitmapRect.set(getCenterX() - (width/2f), getCenterY() - (height/2f)
                , getCenterX() + (width/2f), getCenterY() + (height/2f));
        canvas.drawBitmap(bitmapIndicator, null, bitmapRect, indicatorPaint);
        canvas.restore();
    }

    @Override
    protected void updateIndicator() {
    }

    @Override
    protected void setWithEffects(boolean withEffects) {
    }
}
