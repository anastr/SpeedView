package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.Indicators.TriangleIndicator

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class AwesomeSpeedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val markPath = Path()
    private val trianglesPath = Path()
    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val trianglesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    private var speedometerColor = -0xff191a

    var trianglesColor: Int
        get() = trianglesPaint.color
        set(trianglesColor) {
            trianglesPaint.color = trianglesColor
            updateBackgroundBitmap()
            invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {

        super.textColor = -0x3da0
        super.speedTextColor = -0x1
        super.unitTextColor = -0x1
        super.textTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        super.setSpeedTextPosition(Gauge.Position.CENTER)
        super.unitUnderSpeedText = true
    }

    override fun defaultSpeedometerValues() {
        super.setIndicator(TriangleIndicator(context)
                .setIndicatorWidth(dpTOpx(25f))
                .setIndicatorColor(-0xff191a))
        super.setStartEndDegree(135, 135 + 320)
        super.setSpeedometerWidth(dpTOpx(60f))
        super.backgroundCircleColor = -0xdededf
        super.tickNumber = 9
        super.setTickPadding(0)
    }

    private fun init() {
        markPaint.style = Paint.Style.STROKE
        textPaint.textAlign = Paint.Align.CENTER
        ringPaint.style = Paint.Style.STROKE
        trianglesPaint.color = -0xc6b655
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.AwesomeSpeedometer, 0, 0)

        speedometerColor = a.getColor(R.styleable.AwesomeSpeedometer_sv_speedometerColor, speedometerColor)
        trianglesPaint.color = a.getColor(R.styleable.AwesomeSpeedometer_sv_trianglesColor, trianglesPaint.color)
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateGradient()
        updateBackgroundBitmap()
    }

    private fun updateGradient() {
        val stop = (sizePa * .5f - getSpeedometerWidth()) / (sizePa * .5f)
        val stop2 = stop + (1f - stop) * .1f
        val stop3 = stop + (1f - stop) * .36f
        val stop4 = stop + (1f - stop) * .64f
        val stop5 = stop + (1f - stop) * .9f
        val colors = intArrayOf(backgroundCircleColor, speedometerColor, backgroundCircleColor, backgroundCircleColor, speedometerColor, speedometerColor)
        val radialGradient = RadialGradient(size * .5f, size * .5f, sizePa * .5f, colors, floatArrayOf(stop, stop2, stop3, stop4, stop5, 1f), Shader.TileMode.CLAMP)
        ringPaint.shader = radialGradient
    }

    private fun initDraw() {
        ringPaint.strokeWidth = getSpeedometerWidth()
        markPaint.color = markColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)
        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        initDraw()

        val markH = viewSizePa / 22f
        markPath.reset()
        markPath.moveTo(size * .5f, padding.toFloat())
        markPath.lineTo(size * .5f, markH + padding)
        markPaint.strokeWidth = markH / 5f

        val triangleHeight = viewSizePa / 20f
        initTickPadding = triangleHeight

        trianglesPath.reset()
        trianglesPath.moveTo(size * .5f, padding + viewSizePa / 20f)
        val triangleWidth = viewSize / 20f
        trianglesPath.lineTo(size * .5f - triangleWidth / 2f, padding.toFloat())
        trianglesPath.lineTo(size * .5f + triangleWidth / 2f, padding.toFloat())

        val risk = getSpeedometerWidth() * .5f + padding
        speedometerRect.set(risk, risk, size - risk, size - risk)
        c.drawArc(speedometerRect, 0f, 360f, false, ringPaint)

        drawMarks(c)
        drawTicks(c)
    }

    protected fun drawMarks(c: Canvas) {
        for (i in 0 until tickNumber) {
            val d = getDegreeAtSpeed(getTicks()[i]) + 90f
            c.save()
            c.rotate(d, size * .5f, size * .5f)

            c.drawPath(trianglesPath, trianglesPaint)
            if (i + 1 != tickNumber) {
                c.save()
                val d2 = getDegreeAtSpeed(getTicks()[i + 1]) + 90f
                val eachDegree = d2 - d
                for (j in 1..9) {
                    c.rotate(eachDegree * .1f, size * .5f, size * .5f)
                    if (j == 5)
                        markPaint.strokeWidth = size.toFloat() / 22f / 5f
                    else
                        markPaint.strokeWidth = size.toFloat() / 22f / 9f
                    c.drawPath(markPath, markPaint)
                }
                c.restore()
            }
            c.restore()
        }
    }

    override fun setSpeedometerWidth(speedometerWidth: Float) {
        super.setSpeedometerWidth(speedometerWidth)
        val risk = speedometerWidth * .5f
        speedometerRect.set(risk, risk, size - risk, size - risk)
        updateGradient()
        updateBackgroundBitmap()
        invalidate()
    }

    fun getSpeedometerColor(): Int {
        return speedometerColor
    }

    fun setSpeedometerColor(speedometerColor: Int) {
        this.speedometerColor = speedometerColor
        updateGradient()
        updateBackgroundBitmap()
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
