package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.github.anastr.speedviewlib.components.Indicators.Indicator
import com.github.anastr.speedviewlib.components.Indicators.NormalSmallIndicator

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
            indicatorEffects(withEffects)
            if (withEffects) {
                markPaint.maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID)
                speedBackgroundPaint.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.SOLID)
                circlePaint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID)
            } else {
                markPaint.maskFilter = null
                speedBackgroundPaint.maskFilter = null
                circlePaint.maskFilter = null
            }
            updateBackgroundBitmap()
            invalidate()
        }

    var speedBackgroundColor: Int
        get() = speedBackgroundPaint.color
        set(speedBackgroundColor) {
            speedBackgroundPaint.color = speedBackgroundColor
            updateBackgroundBitmap()
            invalidate()
        }

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

    override fun defaultGaugeValues() {
        super.textColor = -0x1
    }

    override fun defaultSpeedometerValues() {
        super.setIndicator(NormalSmallIndicator(context)
                .setIndicatorColor(-0xff0014))
        super.backgroundCircleColor = -0xdededf
        super.setLowSpeedColor(-0xc878d1)
        super.setMediumSpeedColor(-0x5c7dcc)
        super.setHighSpeedColor(-0x64dfe0)
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        markPaint.style = Paint.Style.STROKE
        smallMarkPaint.style = Paint.Style.STROKE
        speedBackgroundPaint.color = -0x1
        circlePaint.color = -0x1f1f20

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
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
        speedometerPaint.strokeWidth = getSpeedometerWidth()
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
        smallMarkPath.moveTo(size * .5f, getSpeedometerWidth() + padding)
        smallMarkPath.lineTo(size * .5f, getSpeedometerWidth() + padding.toFloat() + smallMarkH)
        smallMarkPaint.strokeWidth = 3f

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
        indicatorEffects(withEffects)
    }
}
