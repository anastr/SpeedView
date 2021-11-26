package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */

class NeedleIndicator(context: Context) : Indicator<NeedleIndicator>(context) {

    private val indicatorPath = Path()
    private val circlePath = Path()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bottomY: Float = 0.toFloat()

    init {
        width = dpTOpx(12f)
        circlePaint.style = Paint.Style.STROKE
    }

    override fun getBottom(): Float {
        return bottomY
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(indicatorPath, indicatorPaint)
        canvas.drawPath(circlePath, circlePaint)
    }

    override fun updateIndicator() {
        indicatorPath.reset()
        circlePath.reset()
        indicatorPath.moveTo(getCenterX(), speedometer!!.padding.toFloat())
        bottomY = (width * sin(Math.toRadians(260.0))).toFloat() + getViewSize() * .5f + speedometer!!.padding.toFloat()
        val xLeft = (width * cos(Math.toRadians(260.0))).toFloat() + getViewSize() * .5f + speedometer!!.padding.toFloat()
        indicatorPath.lineTo(xLeft, bottomY)
        val rectF = RectF(getCenterX() - width, getCenterY() - width, getCenterX() + width, getCenterY() + width)
        indicatorPath.arcTo(rectF, 260f, 20f)

        val circleWidth = width * .25f
        circlePath.addCircle(getCenterX(), getCenterY(), width - circleWidth * .5f + .6f, Path.Direction.CW)

        indicatorPaint.color = color
        circlePaint.color = color
        circlePaint.strokeWidth = circleWidth
    }

    override fun setWithEffects(withEffects: Boolean) {
        if (withEffects && !speedometer!!.isInEditMode)
            indicatorPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        else
            indicatorPaint.maskFilter = null
    }
}
