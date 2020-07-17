package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.indicators.Indicator

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
open class RaySpeedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val markPath = Path()
    private val ray1Path = Path()
    private val ray2Path = Path()
    private val rayMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val activeMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rayPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var withEffects = true

    private var degreeBetweenMark = 5

    var isWithEffects: Boolean
        get() = withEffects
        set(withEffects) {
            this.withEffects = withEffects
            if (isInEditMode)
                return
            indicator.withEffects(withEffects)
            if (withEffects) {
                rayPaint.maskFilter = BlurMaskFilter(3f, BlurMaskFilter.Blur.SOLID)
                activeMarkPaint.maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID)
                speedBackgroundPaint.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.SOLID)
            } else {
                rayPaint.maskFilter = null
                activeMarkPaint.maskFilter = null
                speedBackgroundPaint.maskFilter = null
            }
            invalidateGauge()
        }

    var speedBackgroundColor: Int
        get() = speedBackgroundPaint.color
        set(speedBackgroundColor) {
            speedBackgroundPaint.color = speedBackgroundColor
            invalidateGauge()
        }

    var rayMarkWidth: Float
        get() = rayMarkPaint.strokeWidth
        set(markRayWidth) {
            rayMarkPaint.strokeWidth = markRayWidth
            activeMarkPaint.strokeWidth = markRayWidth
            if (isAttachedToWindow)
                invalidate()
        }

    var rayColor: Int
        get() = rayPaint.color
        set(rayColor) {
            rayPaint.color = rayColor
            invalidateGauge()
        }

//    /**
//     * this Speedometer doesn't use this method.
//     * @return `Color.TRANSPARENT` always.
//     */
//    override var indicatorColor: Int
//        @Deprecated("")
//        get() = 0
//        @Deprecated("")
//        set(indicatorColor) {
//        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        super.textColor = 0xFFFFFFFF.toInt()
    }

    override fun defaultSpeedometerValues() {
        super.backgroundCircleColor = 0xff212121.toInt()
        super.markColor = 0xFF000000.toInt()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.RaySpeedometer, 0, 0)

        rayPaint.color = a.getColor(R.styleable.RaySpeedometer_sv_rayColor, rayPaint.color)
        val degreeBetweenMark = a.getInt(R.styleable.RaySpeedometer_sv_degreeBetweenMark, this.degreeBetweenMark)
        val rayMarkWidth = a.getDimension(R.styleable.RaySpeedometer_sv_rayMarkWidth, rayMarkPaint.strokeWidth)
        rayMarkPaint.strokeWidth = rayMarkWidth
        activeMarkPaint.strokeWidth = rayMarkWidth
        speedBackgroundPaint.color = a.getColor(R.styleable.RaySpeedometer_sv_speedBackgroundColor, speedBackgroundPaint.color)
        withEffects = a.getBoolean(R.styleable.RaySpeedometer_sv_withEffects, withEffects)
        a.recycle()
        isWithEffects = withEffects
        if (degreeBetweenMark in 1..20)
            this.degreeBetweenMark = degreeBetweenMark
    }

    private fun init() {
        rayMarkPaint.style = Paint.Style.STROKE
        rayMarkPaint.strokeWidth = dpTOpx(3f)
        activeMarkPaint.style = Paint.Style.STROKE
        activeMarkPaint.strokeWidth = dpTOpx(3f)
        rayPaint.style = Paint.Style.STROKE
        rayPaint.strokeWidth = dpTOpx(1.8f)
        rayPaint.color = 0xFFFFFFFF.toInt()
        speedBackgroundPaint.color = 0xFFFFFFFF.toInt()

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        isWithEffects = withEffects
    }


    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateMarkPath()
        updateBackgroundBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.rotate(getStartDegree() + 90f, size * .5f, size * .5f)
        var i = getStartDegree()
        while (i < getEndDegree()) {
            if (degree <= i) {
                rayMarkPaint.color = markColor
                canvas.drawPath(markPath, rayMarkPaint)
                canvas.rotate(degreeBetweenMark.toFloat(), size * .5f, size * .5f)
                i += degreeBetweenMark
                continue
            }
            if (currentSection != null)
                activeMarkPaint.color = currentSection!!.color
            else
                activeMarkPaint.color = 0 // transparent color
            canvas.drawPath(markPath, activeMarkPaint)
            canvas.rotate(degreeBetweenMark.toFloat(), size * .5f, size / 2f)
            i += degreeBetweenMark
        }
        canvas.restore()

        val speedBackgroundRect = getSpeedUnitTextBounds()
        speedBackgroundRect.left -= 2f
        speedBackgroundRect.right += 2f
        speedBackgroundRect.bottom += 2f
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint)

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)
        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()

        updateMarkPath()

        ray1Path.reset()
        ray1Path.moveTo(size / 2f, size / 2f)
        ray1Path.lineTo(size / 2f, sizePa / 3.2f + padding)
        ray1Path.moveTo(size / 2f, sizePa / 3.2f + padding)
        ray1Path.lineTo(size / 2.2f, sizePa / 3f + padding)
        ray1Path.moveTo(size / 2.2f, sizePa / 3f + padding)
        ray1Path.lineTo(size / 2.1f, sizePa / 4.5f + padding)

        ray2Path.reset()
        ray2Path.moveTo(size / 2f, size / 2f)
        ray2Path.lineTo(size / 2f, sizePa / 3.2f + padding)
        ray2Path.moveTo(size / 2f, sizePa / 3.2f + padding)
        ray2Path.lineTo(size / 2.2f, sizePa / 3.8f + padding)
        ray2Path.moveTo(size / 2f, sizePa / 3.2f + padding)
        ray2Path.lineTo(size / 1.8f, sizePa / 3.8f + padding)

        c.save()
        for (i in 0..5) {
            c.rotate(58f, size * .5f, size * .5f)
            if (i % 2 == 0)
                c.drawPath(ray1Path, rayPaint)
            else
                c.drawPath(ray2Path, rayPaint)
        }
        c.restore()

        drawMarks(c)

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }

    private fun updateMarkPath() {
        markPath.reset()
        markPath.moveTo(size * .5f, padding.toFloat())
        markPath.lineTo(size * .5f, speedometerWidth + padding)
    }

    override fun setIndicator(indicator: Indicator.Indicators) {
        super.setIndicator(indicator)
        this.indicator.withEffects(withEffects)
    }

    fun getDegreeBetweenMark(): Int {
        return degreeBetweenMark
    }

    /**
     * The spacing between the marks
     *
     * it should be between (0-20] ,else well be ignore.
     *
     * @param degreeBetweenMark degree between two marks.
     */
    fun setDegreeBetweenMark(degreeBetweenMark: Int) {
        if (degreeBetweenMark <= 0 || degreeBetweenMark > 20)
            return
        this.degreeBetweenMark = degreeBetweenMark
        if (isAttachedToWindow)
            invalidate()
    }
}
