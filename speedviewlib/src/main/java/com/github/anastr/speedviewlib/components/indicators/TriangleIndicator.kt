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

    override val defaultIndicatorWidth: Float
        get() = dpTOpx(25f)

    init {
        updateIndicator()
    }

    override fun getTop(): Float {
        return indicatorTop
    }

    override fun getBottom(): Float {
        return indicatorTop + getIndicatorWidth()
    }

    override fun draw(canvas: Canvas, degree: Float) {
        canvas.save()
        canvas.rotate(90f + degree, getCenterX(), getCenterY())
        canvas.drawPath(indicatorPath, indicatorPaint)
        canvas.restore()
    }

    override fun updateIndicator() {
        indicatorPath = Path()
        indicatorTop = padding.toFloat() + speedometerWidth + dpTOpx(5f)
        indicatorPath.moveTo(getCenterX(), indicatorTop)
        indicatorPath.lineTo(getCenterX() - getIndicatorWidth(), indicatorTop + getIndicatorWidth())
        indicatorPath.lineTo(getCenterX() + getIndicatorWidth(), indicatorTop + getIndicatorWidth())
        indicatorPath.moveTo(0f, 0f)

        val endColor = Color.argb(0, Color.red(getIndicatorColor()), Color.green(getIndicatorColor()), Color.blue(getIndicatorColor()))
        val linearGradient = LinearGradient(getCenterX(), indicatorTop, getCenterX(), indicatorTop + getIndicatorWidth(), getIndicatorColor(), endColor, Shader.TileMode.CLAMP)
        indicatorPaint.shader = linearGradient
    }

    override fun setWithEffects(withEffects: Boolean) {
        if (withEffects && !isInEditMode) {
            indicatorPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        } else {
            indicatorPaint.maskFilter = null
        }
    }
}
