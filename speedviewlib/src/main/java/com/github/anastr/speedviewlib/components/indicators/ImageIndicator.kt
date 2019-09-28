package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class ImageIndicator
/**
 * create indicator from bitmap, the indicator direction must be up.<br></br>
 * center indicator position will be center of speedometer.
 * @param context you can use `getApplicationContext()`.
 * @param bitmapIndicator the indicator.
 * @param width the custom width of the indicator.
 * @param height the custom height of the indicator.
 * @throws IllegalArgumentException if `width <= 0 OR height <= 0`.
 */
@JvmOverloads constructor(context: Context, private val bitmapIndicator: Bitmap, private val width: Int = bitmapIndicator.width, private val height: Int = bitmapIndicator.height) : Indicator<ImageIndicator>(context) {
    private val bitmapRect = RectF()

    override val defaultIndicatorWidth: Float
        get() = 0f

    /**
     * create indicator from resources, the indicator direction must be up.<br></br>
     * center indicator position will be center of speedometer.
     * @param context you can use `getApplicationContext()`.
     * @param resource the image id.
     */
    constructor(context: Context, resource: Int) : this(context, BitmapFactory.decodeResource(context.resources, resource))

    /**
     * create indicator from resources, the indicator direction must be up.<br></br>
     * center indicator position will be center of speedometer.
     * @param context you can use `getApplicationContext()`.
     * @param resource the image id.
     * @param width the custom width of the indicator.
     * @param height the custom height of the indicator.
     * @throws IllegalArgumentException if `width <= 0 OR height <= 0`.
     */
    constructor(context: Context, resource: Int, width: Int, height: Int) : this(context, BitmapFactory.decodeResource(context.resources, resource), width, height)

    init {
        require(width > 0) { "width must be bigger than 0" }
        require(height > 0) { "height must be bigger than 0" }
    }

    override fun draw(canvas: Canvas, degree: Float) {
        canvas.save()
        canvas.rotate(90f + degree, getCenterX(), getCenterY())
        bitmapRect.set(getCenterX() - width / 2f, getCenterY() - height / 2f, getCenterX() + width / 2f, getCenterY() + height / 2f)
        canvas.drawBitmap(bitmapIndicator, null, bitmapRect, indicatorPaint)
        canvas.restore()
    }

    override fun updateIndicator() {}

    override fun setWithEffects(withEffects: Boolean) {}
}
