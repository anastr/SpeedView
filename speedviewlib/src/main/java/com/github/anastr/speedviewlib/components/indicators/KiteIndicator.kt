package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Path

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */

class KiteIndicator(context: Context) : Indicator<KiteIndicator>(context) {

    private val indicatorPath = Path()
    private var bottomY: Float = 0.toFloat()

    init {
        width = dpTOpx(12f)
    }

    override fun getBottom(): Float {
        return bottomY
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(indicatorPath, indicatorPaint)
    }

    override fun updateIndicator() {
        indicatorPath.reset()
        indicatorPath.moveTo(getCenterX(), speedometer!!.padding.toFloat())
        bottomY = getViewSize() * .5f + speedometer!!.padding
        indicatorPath.lineTo(getCenterX() - width, bottomY)
        indicatorPath.lineTo(getCenterX(), bottomY + width)
        indicatorPath.lineTo(getCenterX() + width, bottomY)

        indicatorPaint.color = color
    }

    override fun setWithEffects(withEffects: Boolean) {
        if (withEffects && !speedometer!!.isInEditMode)
            indicatorPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        else
            indicatorPaint.maskFilter = null
    }
}
