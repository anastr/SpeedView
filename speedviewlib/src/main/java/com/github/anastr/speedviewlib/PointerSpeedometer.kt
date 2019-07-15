package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.Indicators.SpindleIndicator

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class PointerSpeedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val markPath = Path()
    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerBackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    private var speedometerColor = -0x111112
    private var pointerColor = -0x1

    private var withPointer = true

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
            if (!isAttachedToWindow)
                return
            invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        super.textColor = -0x1
        super.speedTextColor = -0x1
        super.unitTextColor = -0x1
        super.speedTextSize = dpTOpx(24f)
        super.unitTextSize = dpTOpx(11f)
        super.speedTextTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun defaultSpeedometerValues() {
        super.setIndicator(SpindleIndicator(context)
                .setIndicatorWidth(dpTOpx(16f))
                .setIndicatorColor(-0x1))
        super.backgroundCircleColor = -0xb73317
        super.setSpeedometerWidth(dpTOpx(10f))
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        speedometerPaint.strokeCap = Paint.Cap.ROUND
        markPaint.style = Paint.Style.STROKE
        markPaint.strokeCap = Paint.Cap.ROUND
        markPaint.strokeWidth = dpTOpx(2f)
        circlePaint.color = -0x1
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
        withPointer = a.getBoolean(R.styleable.PointerSpeedometer_sv_withPointer, withPointer)
        a.recycle()
        initAttributeValue()
    }

    private fun initAttributeValue() {
        pointerPaint.color = pointerColor
    }


    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        val risk = getSpeedometerWidth() * .5f + dpTOpx(8f) + padding.toFloat()
        speedometerRect.set(risk, risk, size - risk, size - risk)

        updateRadial()
        updateBackgroundBitmap()
    }

    private fun initDraw() {
        speedometerPaint.strokeWidth = getSpeedometerWidth()
        speedometerPaint.shader = updateSweep()
        markPaint.color = markColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initDraw()

        canvas.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()).toFloat(), false, speedometerPaint)

        if (withPointer) {
            canvas.save()
            canvas.rotate(90 + degree, size * .5f, size * .5f)
            canvas.drawCircle(size * .5f, getSpeedometerWidth() * .5f + dpTOpx(8f) + padding.toFloat(), getSpeedometerWidth() * .5f + dpTOpx(8f), pointerBackPaint)
            canvas.drawCircle(size * .5f, getSpeedometerWidth() * .5f + dpTOpx(8f) + padding.toFloat(), getSpeedometerWidth() * .5f + dpTOpx(1f), pointerPaint)
            canvas.restore()
        }

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)

        val c = centerCircleColor
        circlePaint.color = Color.argb((Color.alpha(c) * .5f).toInt(), Color.red(c), Color.green(c), Color.blue(c))
        canvas.drawCircle(size * .5f, size * .5f, widthPa / 14f, circlePaint)
        circlePaint.color = c
        canvas.drawCircle(size * .5f, size * .5f, widthPa / 22f, circlePaint)

        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        initDraw()

        markPath.reset()
        markPath.moveTo(size * .5f, getSpeedometerWidth() + dpTOpx(8f) + dpTOpx(4f) + padding.toFloat())
        markPath.lineTo(size * .5f, getSpeedometerWidth() + dpTOpx(8f) + dpTOpx(4f) + padding.toFloat() + (size / 60).toFloat())

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
        val pointerGradient = RadialGradient(size * .5f, getSpeedometerWidth() * .5f + dpTOpx(8f) + padding.toFloat(), getSpeedometerWidth() * .5f + dpTOpx(8f), intArrayOf(centerColor, edgeColor), floatArrayOf(.4f, 1f), Shader.TileMode.CLAMP)
        pointerBackPaint.shader = pointerGradient
    }

    fun getSpeedometerColor(): Int {
        return speedometerColor
    }

    fun setSpeedometerColor(speedometerColor: Int) {
        this.speedometerColor = speedometerColor
        invalidate()
    }

    fun getPointerColor(): Int {
        return pointerColor
    }

    fun setPointerColor(pointerColor: Int) {
        this.pointerColor = pointerColor
        pointerPaint.color = pointerColor
        updateRadial()
        invalidate()
    }

    /**
     * this Speedometer doesn't use this method.
     * @return `Color.TRANSPARENT` always.
     */
    @Deprecated("")
    override fun getLowSpeedColor(): Int {
        return 0
    }

    /**
     * this Speedometer doesn't use this method.
     * @param lowSpeedColor nothing.
     */
    @Deprecated("")
    override fun setLowSpeedColor(lowSpeedColor: Int) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return `Color.TRANSPARENT` always.
     */
    @Deprecated("")
    override fun getMediumSpeedColor(): Int {
        return 0
    }

    /**
     * this Speedometer doesn't use this method.
     * @param mediumSpeedColor nothing.
     */
    @Deprecated("")
    override fun setMediumSpeedColor(mediumSpeedColor: Int) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return `Color.TRANSPARENT` always.
     */
    @Deprecated("")
    override fun getHighSpeedColor(): Int {
        return 0
    }

    /**
     * this Speedometer doesn't use this method.
     * @param highSpeedColor nothing.
     */
    @Deprecated("")
    override fun setHighSpeedColor(highSpeedColor: Int) {
    }
}
