package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.Indicators.NormalIndicator

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class SpeedView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val markPath = Path()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    /**
     * change the color of the center circle (if exist),
     * **this option is not available for all Speedometers**.
     */
    var centerCircleColor: Int
        get() = circlePaint.color
        set(centerCircleColor) {
            circlePaint.color = centerCircleColor
            if (!isAttachedToWindow)
                return
            invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {}

    override fun defaultSpeedometerValues() {
        super.setIndicator(NormalIndicator(context))
        super.backgroundCircleColor = 0
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        markPaint.style = Paint.Style.STROKE
        circlePaint.color = -0xbbbbbc
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SpeedView, 0, 0)

        circlePaint.color = a.getColor(R.styleable.SpeedView_sv_centerCircleColor, circlePaint.color)
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateBackgroundBitmap()
    }

    private fun initDraw() {
        speedometerPaint.strokeWidth = getSpeedometerWidth()
        markPaint.color = markColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSpeedUnitText(canvas)

        drawIndicator(canvas)
        canvas.drawCircle(size * .5f, size * .5f, widthPa / 12f, circlePaint)

        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        initDraw()

        val markH = viewSizePa / 28f
        markPath.reset()
        markPath.moveTo(size * .5f, padding.toFloat())
        markPath.lineTo(size * .5f, markH + padding)
        markPaint.strokeWidth = markH / 3f

        val risk = getSpeedometerWidth() * .5f + padding
        speedometerRect.set(risk, risk, size - risk, size - risk)

        speedometerPaint.color = getHighSpeedColor()
        c.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()).toFloat(), false, speedometerPaint)
        speedometerPaint.color = getMediumSpeedColor()
        c.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()) * getMediumSpeedOffset(), false, speedometerPaint)
        speedometerPaint.color = getLowSpeedColor()
        c.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()) * getLowSpeedOffset(), false, speedometerPaint)

        c.save()
        c.rotate(90f + getStartDegree(), size * .5f, size * .5f)
        val everyDegree = (getEndDegree() - getStartDegree()) * .111f
        var i = getStartDegree().toFloat()
        while (i < getEndDegree() - 2f * everyDegree) {
            c.rotate(everyDegree, size * .5f, size * .5f)
            c.drawPath(markPath, markPaint)
            i += everyDegree
        }
        c.restore()

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }
}
