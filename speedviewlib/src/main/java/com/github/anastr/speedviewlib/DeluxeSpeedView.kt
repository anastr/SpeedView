package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.indicators.Indicator
import com.github.anastr.speedviewlib.components.indicators.NormalSmallIndicator

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class DeluxeSpeedView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val markPath = Path()
    private val smallMarkPath = Path()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)
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
     * change the color of the center circle (if exist),
     * **this option is not available for all Speedometers**.
     */
    var centerCircleColor: Int
        get() = circlePaint.color
        set(centerCircleColor) {
            circlePaint.color = centerCircleColor
            if (isAttachedToWindow)
                invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        super.textColor = -0x1
        sections[0].color = -0xc878d1
        sections[1].color = -0x5c7dcc
        sections[2].color = -0x64dfe0
    }

    override fun defaultSpeedometerValues() {
        indicator = NormalSmallIndicator(context)
        indicator.color = -0xff0014
        super.backgroundCircleColor = -0xdededf
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        markPaint.style = Paint.Style.STROKE
        smallMarkPaint.style = Paint.Style.STROKE
        speedBackgroundPaint.color = -0x1
        circlePaint.color = -0x1f1f20

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
        markPaint.color = markColor
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
        canvas.drawCircle(size * .5f, size * .5f, widthPa / 12f, circlePaint)
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

        val markH = viewSizePa / 28f
        markPath.reset()
        markPath.moveTo(size * .5f, padding.toFloat())
        markPath.lineTo(size * .5f, markH + padding)
        markPaint.strokeWidth = markH / 3f

        val risk = speedometerWidth * .5f + padding
        speedometerRect.set(risk, risk, size - risk, size - risk)

        for (i in sections.size-1 downTo 0) {
            speedometerPaint.color = sections[i].color
            c.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()) * sections[i].speedOffset, false, speedometerPaint)
        }

        c.save()
        c.rotate(90f + getStartDegree(), size * .5f, size * .5f)
        val everyDegree = (getEndDegree() - getStartDegree()) * .111f
        run {
            var i = getStartDegree().toFloat()
            while (i < getEndDegree() - 2f * everyDegree) {
                c.rotate(everyDegree, size * .5f, size * .5f)
                c.drawPath(markPath, markPaint)
                i += everyDegree
            }
        }
        c.restore()

        c.save()
        c.rotate(90f + getStartDegree(), size * .5f, size * .5f)
        var i = getStartDegree().toFloat()
        while (i < getEndDegree() - 10f) {
            c.rotate(10f, size * .5f, size * .5f)
            c.drawPath(smallMarkPath, smallMarkPaint)
            i += 10f
        }
        c.restore()

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
