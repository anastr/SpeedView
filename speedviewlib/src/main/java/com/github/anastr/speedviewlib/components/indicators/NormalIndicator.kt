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
class NormalIndicator(context: Context) : Indicator<NormalIndicator>(context) {

    private val indicatorPath = Path()
    private var bottomY: Float = 0.toFloat()

    override val defaultIndicatorWidth: Float
        get() = dpTOpx(12f)

    init {
        updateIndicator()
    }

    override fun getBottom(): Float {
        return bottomY
    }

    override fun draw(canvas: Canvas, degree: Float) {
        canvas.save()
        canvas.rotate(90f + degree, getCenterX(), getCenterY())
        canvas.drawPath(indicatorPath, indicatorPaint)
        canvas.restore()
    }

    override fun updateIndicator() {
        indicatorPath.reset()
        indicatorPath.moveTo(getCenterX(), padding.toFloat())
        bottomY = getViewSize() * 2f / 3f + padding
        indicatorPath.lineTo(getCenterX() - getIndicatorWidth(), bottomY)
        indicatorPath.lineTo(getCenterX() + getIndicatorWidth(), bottomY)
        val rectF = RectF(getCenterX() - getIndicatorWidth(), bottomY - getIndicatorWidth(), getCenterX() + getIndicatorWidth(), bottomY + getIndicatorWidth())
        indicatorPath.addArc(rectF, 0f, 180f)

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
