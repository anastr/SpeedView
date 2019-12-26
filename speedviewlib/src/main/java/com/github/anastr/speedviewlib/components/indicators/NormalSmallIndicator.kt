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

    override val defaultIndicatorWidth: Float
        get() = dpTOpx(12f)

    override fun getTop(): Float {
        return getViewSize() / 5f + speedometer!!.padding
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
        indicatorPath.moveTo(getCenterX(), getViewSize() / 5f + speedometer!!.padding)
        bottomY = getViewSize() * 3f / 5f + speedometer!!.padding
        indicatorPath.lineTo(getCenterX() - indicatorWidth, bottomY)
        indicatorPath.lineTo(getCenterX() + indicatorWidth, bottomY)
        val rectF = RectF(getCenterX() - indicatorWidth, bottomY - indicatorWidth, getCenterX() + indicatorWidth, bottomY + indicatorWidth)
        indicatorPath.addArc(rectF, 0f, 180f)

        indicatorPaint.color = indicatorColor
    }

    override fun setWithEffects(withEffects: Boolean) {
        if (withEffects && !speedometer!!.isInEditMode) {
            indicatorPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        } else {
            indicatorPaint.maskFilter = null
        }
    }
}
