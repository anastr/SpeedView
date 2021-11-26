package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.*

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class TriangleIndicator(context: Context) : Indicator<TriangleIndicator>(context) {

    private var indicatorPath = Path()
    private var indicatorTop = 0f

    init {
        width = dpTOpx(25f)
    }

    override fun getTop(): Float {
        return indicatorTop
    }

    override fun getBottom(): Float {
        return indicatorTop + width
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(indicatorPath, indicatorPaint)
    }

    override fun updateIndicator() {
        indicatorPath = Path()
        indicatorTop = speedometer!!.padding.toFloat() + speedometer!!.speedometerWidth + dpTOpx(5f)
        indicatorPath.moveTo(getCenterX(), indicatorTop)
        indicatorPath.lineTo(getCenterX() - width, indicatorTop + width)
        indicatorPath.lineTo(getCenterX() + width, indicatorTop + width)
        indicatorPath.moveTo(0f, 0f)

        val endColor = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color))
        val linearGradient = LinearGradient(getCenterX(), indicatorTop, getCenterX(), indicatorTop + width, color, endColor, Shader.TileMode.CLAMP)
        indicatorPaint.shader = linearGradient
    }

    override fun setWithEffects(withEffects: Boolean) {
        if (withEffects && !speedometer!!.isInEditMode) {
            indicatorPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        } else {
            indicatorPaint.maskFilter = null
        }
    }
}
