package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.components.indicators.SpindleIndicator
import com.github.anastr.speedviewlib.util.getRoundAngle

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
open class PointerSpeedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerBackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    private var speedometerColor = 0xFFEEEEEE.toInt()
    private var pointerColor = 0xFFFFFFFF.toInt()

    private var withPointer = true

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
    var centerCircleRadius = dpTOpx(12f)
        set(centerCircleRadius) {
            field = centerCircleRadius
            if (isAttachedToWindow)
                invalidate()
        }

    /**
     * enable to draw circle pointer on speedometer arc.
     *
     * this will not make any change for the Indicator.
     *
     * true: draw the pointer,
     * false: don't draw the pointer.
     */
    var isWithPointer: Boolean
        get() = withPointer
        set(withPointer) {
            this.withPointer = withPointer
            if (isAttachedToWindow)
                 invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        super.speedometerWidth = dpTOpx(10f)
        super.textColor = 0xFFFFFFFF.toInt()
        super.speedTextColor = 0xFFFFFFFF.toInt()
        super.unitTextColor = 0xFFFFFFFF.toInt()
        super.speedTextSize = dpTOpx(24f)
        super.unitTextSize = dpTOpx(11f)
        super.speedTextTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun defaultSpeedometerValues() {
        super.marksNumber = 8
        super.marksPadding = speedometerWidth + dpTOpx(12f)
        super.markStyle = Style.ROUND
        super.markHeight = dpTOpx(5f)
        super.markWidth = dpTOpx(2f)
        indicator = SpindleIndicator(context)
        indicator.apply {
            width = dpTOpx(16f)
            color = 0xFFFFFFFF.toInt()
        }
        super.backgroundCircleColor = 0xff48cce9.toInt()
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        speedometerPaint.strokeCap = Paint.Cap.ROUND
        circlePaint.color = 0xFFFFFFFF.toInt()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            initAttributeValue()
            return
        }
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.PointerSpeedometer, 0, 0)

        speedometerColor = a.getColor(R.styleable.PointerSpeedometer_sv_speedometerColor, speedometerColor)
        pointerColor = a.getColor(R.styleable.PointerSpeedometer_sv_pointerColor, pointerColor)
        circlePaint.color = a.getColor(R.styleable.PointerSpeedometer_sv_centerCircleColor, circlePaint.color)
        centerCircleRadius = a.getDimension(R.styleable.SpeedView_sv_centerCircleRadius, centerCircleRadius)
        withPointer = a.getBoolean(R.styleable.PointerSpeedometer_sv_withPointer, withPointer)
        a.recycle()
        initAttributeValue()
    }

    private fun initAttributeValue() {
        pointerPaint.color = pointerColor
    }


    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        val risk = speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat()
        speedometerRect.set(risk, risk, size - risk, size - risk)

        updateRadial()
        updateBackgroundBitmap()
    }

    private fun initDraw() {
        speedometerPaint.strokeWidth = speedometerWidth
        speedometerPaint.shader = updateSweep()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initDraw()

        val roundAngle = getRoundAngle(speedometerWidth, speedometerRect.width())
        canvas.drawArc(speedometerRect, getStartDegree() + roundAngle
                , (getEndDegree() - getStartDegree()) - roundAngle * 2f, false, speedometerPaint)

        if (withPointer) {
            canvas.save()
            canvas.rotate(90 + degree, size * .5f, size * .5f)
            canvas.drawCircle(size * .5f, speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat(), speedometerWidth * .5f + dpTOpx(8f), pointerBackPaint)
            canvas.drawCircle(size * .5f, speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat(), speedometerWidth * .5f + dpTOpx(1f), pointerPaint)
            canvas.restore()
        }

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)

        val c = centerCircleColor
        circlePaint.color = Color.argb((Color.alpha(c) * .5f).toInt(), Color.red(c), Color.green(c), Color.blue(c))
        canvas.drawCircle(size * .5f, size * .5f, centerCircleRadius + dpTOpx(6f), circlePaint)
        circlePaint.color = c
        canvas.drawCircle(size * .5f, size * .5f, centerCircleRadius, circlePaint)

        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        initDraw()

        drawMarks(c)

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }

    private fun updateSweep(): SweepGradient {
        val startColor = Color.argb(150, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor))
        val color2 = Color.argb(220, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor))
        val color3 = Color.argb(70, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor))
        val endColor = Color.argb(15, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor))
        val position = getOffsetSpeed() * (getEndDegree() - getStartDegree()) / 360f
        val sweepGradient = SweepGradient(size * .5f, size * .5f, intArrayOf(startColor, color2, speedometerColor, color3, endColor, startColor), floatArrayOf(0f, position * .5f, position, position, .99f, 1f))
        val matrix = Matrix()
        matrix.postRotate(getStartDegree().toFloat(), size * .5f, size * .5f)
        sweepGradient.setLocalMatrix(matrix)
        return sweepGradient
    }

    private fun updateRadial() {
        val centerColor = Color.argb(160, Color.red(pointerColor), Color.green(pointerColor), Color.blue(pointerColor))
        val edgeColor = Color.argb(10, Color.red(pointerColor), Color.green(pointerColor), Color.blue(pointerColor))
        val pointerGradient = RadialGradient(size * .5f, speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat(), speedometerWidth * .5f + dpTOpx(8f), intArrayOf(centerColor, edgeColor), floatArrayOf(.4f, 1f), Shader.TileMode.CLAMP)
        pointerBackPaint.shader = pointerGradient
    }

    fun getSpeedometerColor(): Int {
        return speedometerColor
    }

    fun setSpeedometerColor(speedometerColor: Int) {
        this.speedometerColor = speedometerColor
        if (isAttachedToWindow)
            invalidate()
    }

    fun getPointerColor(): Int {
        return pointerColor
    }

    fun setPointerColor(pointerColor: Int) {
        this.pointerColor = pointerColor
        pointerPaint.color = pointerColor
        updateRadial()
        if (isAttachedToWindow)
            invalidate()
    }
}
