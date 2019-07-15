package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.EmbossMaskFilter
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class TubeSpeedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val tubePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tubeBacPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    private var withEffects3D = true

    var speedometerBackColor: Int
        get() = tubeBacPaint.color
        set(speedometerBackColor) {
            tubeBacPaint.color = speedometerBackColor
            updateBackgroundBitmap()
            invalidate()
        }

    var isWithEffects3D: Boolean
        get() = withEffects3D
        set(withEffects3D) {
            this.withEffects3D = withEffects3D
            updateEmboss()
            updateBackgroundBitmap()
            invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {}

    override fun defaultSpeedometerValues() {
        super.backgroundCircleColor = 0
        super.setLowSpeedColor(-0xff432c)
        super.setMediumSpeedColor(-0x3ef9)
        super.setHighSpeedColor(-0xbbcca)
        super.setSpeedometerWidth(dpTOpx(40f))
    }

    private fun init() {
        tubePaint.style = Paint.Style.STROKE
        tubeBacPaint.style = Paint.Style.STROKE
        tubeBacPaint.color = -0x8a8a8b
        tubePaint.color = getLowSpeedColor()

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TubeSpeedometer, 0, 0)

        tubeBacPaint.color = a.getColor(R.styleable.TubeSpeedometer_sv_speedometerBackColor, tubeBacPaint.color)
        withEffects3D = a.getBoolean(R.styleable.TubeSpeedometer_sv_withEffects3D, withEffects3D)
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateEmboss()
        updateBackgroundBitmap()
    }

    private fun updateEmboss() {
        if (isInEditMode)
            return
        if (!withEffects3D) {
            tubePaint.maskFilter = null
            tubeBacPaint.maskFilter = null
            return
        }
        val embossMaskFilter = EmbossMaskFilter(
                floatArrayOf(.5f, 1f, 1f), .6f, 3f, pxTOdp(getSpeedometerWidth()) * .35f)
        tubePaint.maskFilter = embossMaskFilter
        val embossMaskFilterBac = EmbossMaskFilter(
                floatArrayOf(-.5f, -1f, 0f), .6f, 1f, pxTOdp(getSpeedometerWidth()) * .35f)
        tubeBacPaint.maskFilter = embossMaskFilterBac
    }

    private fun initDraw() {
        tubePaint.strokeWidth = getSpeedometerWidth()
        val section = getSection()
        when (section) {
            Gauge.LOW_SECTION -> tubePaint.color = getLowSpeedColor()
            Gauge.MEDIUM_SECTION -> tubePaint.color = getMediumSpeedColor()
            else -> tubePaint.color = getHighSpeedColor()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initDraw()

        val sweepAngle = (getEndDegree() - getStartDegree()) * getOffsetSpeed()
        canvas.drawArc(speedometerRect, getStartDegree().toFloat(), sweepAngle, false, tubePaint)

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)
        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        tubeBacPaint.strokeWidth = getSpeedometerWidth()

        val risk = getSpeedometerWidth() * .5f + padding
        speedometerRect.set(risk, risk, size - risk, size - risk)

        c.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()).toFloat(), false, tubeBacPaint)

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }

    override fun setLowSpeedColor(lowSpeedColor: Int) {
        super.setLowSpeedColor(lowSpeedColor)

    }
}
