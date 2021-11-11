package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.indicators.TriangleIndicator

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
open class AwesomeSpeedometer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : Speedometer(context, attrs, defStyleAttr) {

    private val markPath = Path()
    private val trianglesPath = Path()
    private val customMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val trianglesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    private var speedometerColor = 0xff00e6e6.toInt()

    var trianglesColor: Int
        get() = trianglesPaint.color
        set(trianglesColor) {
            trianglesPaint.color = trianglesColor
            invalidateGauge()
        }

    override var speedometerWidth
        get() = super.speedometerWidth
        set(speedometerWidth) {
            super.speedometerWidth = speedometerWidth
            // in case AwesomeSpeedometer not initialized
            if (speedometerRect != null) {
                val risk = speedometerWidth * .5f
                speedometerRect.set(risk, risk, size - risk, size - risk)
                updateGradient()
                invalidateGauge()
            }
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {

        super.speedometerWidth = dpTOpx(60f)
        super.textColor = 0xffffc260.toInt()
        super.speedTextColor = 0xFFFFFFFF.toInt()
        super.unitTextColor = 0xFFFFFFFF.toInt()
        super.textTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        super.speedTextPosition = Position.CENTER
        super.unitUnderSpeedText = true
    }

    override fun defaultSpeedometerValues() {
        indicator = TriangleIndicator(context)
        indicator.apply {
            width = dpTOpx(25f)
            color = 0xff00e6e6.toInt()
        }
        super.setStartEndDegree(135, 135 + 320)
        super.backgroundCircleColor = 0xff212121.toInt()
        super.tickNumber = 9
        super.tickPadding = 0f
    }

    private fun init() {
        customMarkPaint.style = Paint.Style.STROKE
        textPaint.textAlign = Paint.Align.CENTER
        ringPaint.style = Paint.Style.STROKE
        trianglesPaint.color = 0xff3949ab.toInt()
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
        val stop = (sizePa * .5f - speedometerWidth) / (sizePa * .5f)
        val stop2 = stop + (1f - stop) * .1f
        val stop3 = stop + (1f - stop) * .36f
        val stop4 = stop + (1f - stop) * .64f
        val stop5 = stop + (1f - stop) * .9f
        val colors = intArrayOf(backgroundCircleColor, speedometerColor, backgroundCircleColor, backgroundCircleColor, speedometerColor, speedometerColor)
        val radialGradient = RadialGradient(size * .5f, size * .5f, sizePa * .5f, colors, floatArrayOf(stop, stop2, stop3, stop4, stop5, 1f), Shader.TileMode.CLAMP)
        ringPaint.shader = radialGradient
    }

    private fun initDraw() {
        ringPaint.strokeWidth = speedometerWidth
        customMarkPaint.color = markColor
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
        customMarkPaint.strokeWidth = markH / 5f

        val triangleHeight = viewSizePa / 20f
        initTickPadding = triangleHeight

        trianglesPath.reset()
        trianglesPath.moveTo(size * .5f, padding + viewSizePa / 20f)
        val triangleWidth = viewSize / 20f
        trianglesPath.lineTo(size * .5f - triangleWidth / 2f, padding.toFloat())
        trianglesPath.lineTo(size * .5f + triangleWidth / 2f, padding.toFloat())

        val risk = speedometerWidth * .5f + padding
        speedometerRect.set(risk, risk, size - risk, size - risk)
        c.drawArc(speedometerRect, 0f, 360f, false, ringPaint)

        drawCustomMarks(c)
        drawMarks(c)
        drawTicks(c)
    }

    protected fun drawCustomMarks(c: Canvas) {
        val range = getEndDegree() - getStartDegree()
        ticks.forEachIndexed { index, t ->
            val d = getStartDegree() + range * t
            c.save()
            c.rotate(d + 90f, size * .5f, size * .5f)

            c.drawPath(trianglesPath, trianglesPaint)
            if (index + 1 != tickNumber) {
                c.save()
                val d2 = getStartDegree() + range * ticks[index + 1]
                val eachDegree = d2 - d
                for (j in 1..9) {
                    c.rotate(eachDegree * .1f, size * .5f, size * .5f)
                    if (j == 5)
                        customMarkPaint.strokeWidth = size.toFloat() / 22f / 5f
                    else
                        customMarkPaint.strokeWidth = size.toFloat() / 22f / 9f
                    c.drawPath(markPath, customMarkPaint)
                }
                c.restore()
            }
            c.restore()
        }
    }

    fun getSpeedometerColor(): Int {
        return speedometerColor
    }

    fun setSpeedometerColor(speedometerColor: Int) {
        this.speedometerColor = speedometerColor
        updateGradient()
        invalidateGauge()
    }
}
