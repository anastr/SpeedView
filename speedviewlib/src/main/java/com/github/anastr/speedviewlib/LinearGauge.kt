package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
abstract class LinearGauge @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Gauge(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /** to draw part of bitmap  */
    private val rect = Rect()
    private var foregroundBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    /**
     * horizontal or vertical direction .
     * change fill orientation,
     * this will change view width and height.
     */
    var orientation = Orientation.HORIZONTAL
        set(orientation) {
            field = orientation
            if (isAttachedToWindow) {
                requestLayout()
                invalidateGauge()
            }
        }

    init {
        initAttributeSet(context, attrs)
    }

    /**
     * update background and foreground bitmap,
     * this method called when change size, color, orientation...
     *
     *
     * must call [createBackgroundBitmapCanvas] and
     * [createForegroundBitmapCanvas] inside this method.
     *
     */
    protected abstract fun updateFrontAndBackBitmaps()

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LinearGauge, 0, 0)

        val orientation = a.getInt(R.styleable.LinearGauge_sv_orientation, -1)
        if (orientation != -1)
            this.orientation = Orientation.values()[orientation]
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateBackgroundBitmap()
    }

    override fun updateBackgroundBitmap() {
        updateFrontAndBackBitmaps()
    }

    protected fun createForegroundBitmapCanvas(): Canvas {
        if (widthPa == 0 || heightPa == 0)
            return Canvas()
        foregroundBitmap = Bitmap.createBitmap(widthPa, heightPa, Bitmap.Config.ARGB_8888)
        return Canvas(foregroundBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (this.orientation == Orientation.HORIZONTAL)
            rect.set(0, 0, (widthPa * getOffsetSpeed()).toInt(), heightPa)
        else
            rect.set(0, heightPa - (heightPa * getOffsetSpeed()).toInt(), widthPa, heightPa)

        canvas.translate(padding.toFloat(), padding.toFloat())
        canvas.drawBitmap(foregroundBitmap, rect, rect, paint)
        canvas.translate((-padding).toFloat(), (-padding).toFloat())

        drawSpeedUnitText(canvas)
    }

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }
}
