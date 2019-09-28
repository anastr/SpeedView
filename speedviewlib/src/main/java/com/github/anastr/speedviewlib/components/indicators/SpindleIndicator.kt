package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Path

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class SpindleIndicator(context: Context) : Indicator<SpindleIndicator>(context) {

    private val indicatorPath = Path()

    override val defaultIndicatorWidth: Float
        get() = dpTOpx(16f)

    init {
        updateIndicator()
    }

    override fun getTop(): Float {
        return getViewSize() * .18f + padding
    }

    override fun draw(canvas: Canvas, degree: Float) {
        canvas.save()
        canvas.rotate(90f + degree, getCenterX(), getCenterY())
        canvas.drawPath(indicatorPath, indicatorPaint)
        canvas.restore()
    }

    override fun updateIndicator() {
        indicatorPath.reset()
        indicatorPath.moveTo(getCenterX(), getCenterY())
        indicatorPath.quadTo(getCenterX() - getIndicatorWidth(), getViewSize() * .34f + padding, getCenterX(), getViewSize() * .18f + padding)
        indicatorPath.quadTo(getCenterX() + getIndicatorWidth(), getViewSize() * .34f + padding, getCenterX(), getCenterY())

        indicatorPaint.color = getIndicatorColor()
    }

    override fun setWithEffects(withEffects: Boolean) {
        if (withEffects && !isInEditMode) {
            indicatorPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        } else {
            indicatorPaint.maskFilter = null
        }
    }
}
