package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.components.indicators.NormalIndicator
import com.github.anastr.speedviewlib.util.getRoundAngle

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
open class SpeedView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    /**
     * change the color of the center circle.
     */
    var centerCircleColor: Int
        get() = circlePaint.color
        set(centerCircleColor) {
            circlePaint.color = centerCircleColor
            if (isAttachedToWindow)
                invalidate()
        }

    /**
     * change the width of the center circle.
     */
    var centerCircleRadius = dpTOpx(20f)
        set(centerCircleRadius) {
            field = centerCircleRadius
            if (isAttachedToWindow)
                invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {}

    override fun defaultSpeedometerValues() {
        indicator = NormalIndicator(context)
        super.backgroundCircleColor = 0
        super.marksNumber = 8
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        circlePaint.color = 0xFF444444.toInt()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SpeedView, 0, 0)

        circlePaint.color = a.getColor(R.styleable.SpeedView_sv_centerCircleColor, circlePaint.color)
        centerCircleRadius = a.getDimension(R.styleable.SpeedView_sv_centerCircleRadius, centerCircleRadius)
        val styleIndex = a.getInt(R.styleable.SpeedView_sv_sectionStyle, -1)
        if (styleIndex != -1)
            sections.forEach { it.style = Style.values()[styleIndex] }
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateBackgroundBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSpeedUnitText(canvas)

        drawIndicator(canvas)
        canvas.drawCircle(size * .5f, size * .5f, centerCircleRadius, circlePaint)

        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()

        sections.forEach {
            val risk = it.width * .5f + padding + it.padding
            speedometerRect.set(risk, risk, size - risk, size - risk)
            speedometerPaint.strokeWidth = it.width
            speedometerPaint.color = it.color
            val startAngle = (getEndDegree() - getStartDegree()) * it.startOffset + getStartDegree()
            val sweepAngle = (getEndDegree() - getStartDegree()) * it.endOffset - (startAngle - getStartDegree())
            if (it.style == Style.ROUND) {
                val roundAngle = getRoundAngle(it.width, speedometerRect.width())
                speedometerPaint.strokeCap = Paint.Cap.ROUND
                c.drawArc(speedometerRect, startAngle + roundAngle, sweepAngle - roundAngle * 2f, false, speedometerPaint)
            }
            else {
                speedometerPaint.strokeCap = Paint.Cap.BUTT
                c.drawArc(speedometerRect, startAngle, sweepAngle, false, speedometerPaint)
            }
        }

        drawMarks(c)

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }
}
