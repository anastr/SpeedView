package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class NormalSmallIndicator(context: Context) : Indicator<NormalSmallIndicator>(context) {

    private val indicatorPath = Path()
    private var bottomY: Float = 0.toFloat()

    init {
        width = dpTOpx(12f)
    }

    override fun getTop(): Float {
        return getViewSize() / 5f + speedometer!!.padding
    }

    override fun getBottom(): Float {
        return bottomY
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(indicatorPath, indicatorPaint)
    }

    override fun updateIndicator() {
        indicatorPath.reset()
        indicatorPath.moveTo(getCenterX(), getViewSize() / 5f + speedometer!!.padding)
        bottomY = getViewSize() * 3f / 5f + speedometer!!.padding
        indicatorPath.lineTo(getCenterX() - width, bottomY)
        indicatorPath.lineTo(getCenterX() + width, bottomY)
        val rectF = RectF(getCenterX() - width, bottomY - width, getCenterX() + width, bottomY + width)
        indicatorPath.addArc(rectF, 0f, 180f)

        indicatorPaint.color = color
    }

    override fun setWithEffects(withEffects: Boolean) {
        if (withEffects && !speedometer!!.isInEditMode) {
            indicatorPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        } else {
            indicatorPaint.maskFilter = null
        }
    }
}
