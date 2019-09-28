package com.github.anastr.speedviewlib.components.note

import android.content.Context
import android.graphics.*

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class ImageNote
/**
 * @param context you can use `getApplicationContext()` method.
 * @param image to display.
 * @param width set custom width.
 * @param height set custom height.
 * @throws IllegalArgumentException if `width <= 0 OR height <= 0`.
 */
@JvmOverloads constructor(context: Context, private val image: Bitmap, private val width: Int = image.width, private val height: Int = image.height) : Note<ImageNote>(context) {
    private val imageRect = RectF()
    private val notePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * @param context you can use `getApplicationContext()` method.
     * @param resource the image id.
     */
    constructor(context: Context, resource: Int) : this(context, BitmapFactory.decodeResource(context.resources, resource))

    /**
     * @param context you can use `getApplicationContext()` method.
     * @param resource the image id.
     * @param width set custom width.
     * @param height set custom height.
     * @throws IllegalArgumentException if `width <= 0 OR height <= 0`.
     */
    constructor(context: Context, resource: Int, width: Int, height: Int) : this(context, BitmapFactory.decodeResource(context.resources, resource), width, height)

    init {
        require(width > 0) { "width must be bigger than 0" }
        require(height > 0) { "height must be bigger than 0" }
    }

    override fun build(viewWidth: Int) {
        noticeContainsSizeChange(this.width, this.height)
    }

    override fun drawContains(canvas: Canvas, leftX: Float, topY: Float) {
        imageRect.set(leftX, topY, leftX + width, topY + height)
        canvas.drawBitmap(image, null, imageRect, notePaint)
    }
}
