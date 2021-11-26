package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class ImageIndicator
/**
 * create indicator from bitmap, the indicator direction must be up.
 *
 * center indicator position will be center of speedometer.
 * @param context you can use `applicationContext`.
 * @param bitmapIndicator the indicator.
 */
constructor(context: Context, private val bitmapIndicator: Drawable) : Indicator<ImageIndicator>(context) {

    override fun draw(canvas: Canvas) {
        bitmapIndicator.draw(canvas)
    }

    override fun updateIndicator() {
        bitmapIndicator.setBounds(0, 0, getViewSize().toInt(), getViewSize().toInt())
    }

    override fun setWithEffects(withEffects: Boolean) {}
}
