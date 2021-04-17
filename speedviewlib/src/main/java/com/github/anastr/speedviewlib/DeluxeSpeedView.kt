package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.components.indicators.Indicator
import com.github.anastr.speedviewlib.components.indicators.NormalSmallIndicator
import com.github.anastr.speedviewlib.util.getRoundAngle

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
open class DeluxeSpeedView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : Speedometer(context, attrs, defStyleAttr) {

    private val smallMarkPath = Path()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val smallMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    private var withEffects = true

    var isWithEffects: Boolean
        get() = withEffects
        set(withEffects) {
            this.withEffects = withEffects
            if (isInEditMode)
                return
            indicator.withEffects(withEffects)
            if (withEffects) {
                markPaint.maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID)
                speedBackgroundPaint.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.SOLID)
                circlePaint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID)
            } else {
                markPaint.maskFilter = null
                speedBackgroundPaint.maskFilter = null
                circlePaint.maskFilter = null
            }
            invalidateGauge()
        }

    var speedBackgroundColor: Int
        get() = speedBackgroundPaint.color
        set(speedBackgroundColor) {
            speedBackgroundPaint.color = speedBackgroundColor
            invalidateGauge()
        }

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

    override fun defaultGaugeValues() {
        super.textColor = 0xFFFFFFFF.toInt()
        sections[0].color = 0xff37872f.toInt()
        sections[1].color = 0xffa38234.toInt()
        sections[2].color = 0xff9b2020.toInt()
    }

    override fun defaultSpeedometerValues() {
        indicator = NormalSmallIndicator(context)
        indicator.color = 0xff00ffec.toInt()
        super.backgroundCircleColor = 0xff212121.toInt()
        super.marksNumber = 8
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        smallMarkPaint.style = Paint.Style.STROKE
        speedBackgroundPaint.color = 0xFFFFFFFF.toInt()
        circlePaint.color = 0xffe0e0e0.toInt()

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        isWithEffects = withEffects
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            initAttributeValue()
            return
        }
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DeluxeSpeedView, 0, 0)

        speedBackgroundPaint.color = a.getColor(R.styleable.DeluxeSpeedView_sv_speedBackgroundColor, speedBackgroundPaint.color)
        withEffects = a.getBoolean(R.styleable.DeluxeSpeedView_sv_withEffects, withEffects)
        circlePaint.color = a.getColor(R.styleable.DeluxeSpeedView_sv_centerCircleColor, circlePaint.color)
        centerCircleRadius = a.getDimension(R.styleable.DeluxeSpeedView_sv_centerCircleRadius, centerCircleRadius)
        val styleIndex = a.getInt(R.styleable.DeluxeSpeedView_sv_sectionStyle, -1)
        if (styleIndex != -1)
            sections.forEach { it.style = Style.values()[styleIndex] }
        a.recycle()
        isWithEffects = withEffects
        initAttributeValue()
    }

    private fun initAttributeValue() {}


    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        updateBackgroundBitmap()
    }

    private fun initDraw() {
        speedometerPaint.strokeWidth = speedometerWidth
        smallMarkPaint.color = markColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val speedBackgroundRect = getSpeedUnitTextBounds()
        speedBackgroundRect.left -= 2f
        speedBackgroundRect.right += 2f
        speedBackgroundRect.bottom += 2f
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint)

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)
        canvas.drawCircle(size * .5f, size * .5f, centerCircleRadius, circlePaint)
        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        initDraw()

        val smallMarkH = viewSizePa / 20f
        smallMarkPath.reset()
        smallMarkPath.moveTo(size * .5f, speedometerWidth + padding)
        smallMarkPath.lineTo(size * .5f, speedometerWidth + padding.toFloat() + smallMarkH)
        smallMarkPaint.strokeWidth = 3f

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

        c.save()
        c.rotate(90f + getStartDegree(), size * .5f, size * .5f)
        var i = getStartDegree().toFloat()
        while (i < getEndDegree() - 10f) {
            c.rotate(10f, size * .5f, size * .5f)
            c.drawPath(smallMarkPath, smallMarkPaint)
            i += 10f
        }
        c.restore()

        drawMarks(c)

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }

    override fun setIndicator(indicator: Indicator.Indicators) {
        super.setIndicator(indicator)
        this.indicator.withEffects(withEffects)
    }
}
