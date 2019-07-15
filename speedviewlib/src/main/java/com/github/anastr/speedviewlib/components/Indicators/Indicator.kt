package com.github.anastr.speedviewlib.components.Indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint

import com.github.anastr.speedviewlib.Speedometer

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
abstract class Indicator<I : Indicator<I>> protected constructor(context: Context) {

    protected var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val density: Float
    private var indicatorWidth: Float = 0.toFloat()
    private var viewSize: Float = 0.toFloat()
    var speedometerWidth: Float = 0.toFloat()
        private set
    private var indicatorColor = -0xde690d
    var padding: Int = 0
        private set
    var isInEditMode: Boolean = false
        private set
    protected abstract val defaultIndicatorWidth: Float

    /**  @return Y position of indicator
     */
    open fun getTop(): Float = padding.toFloat()
    /** @return Bottom Y position of indicator
     */
    open fun getBottom(): Float = getCenterY()
    /** @return down point after center
     */
    fun getLightBottom(): Float = if (getCenterY() > getBottom()) getBottom() else getCenterY()

    /**
     * @return x center of speedometer.
     */
    fun getCenterX(): Float = viewSize / 2f

    /**
     * @return y center of speedometer.
     */
    fun getCenterY(): Float = viewSize / 2f

    init {
        this.density = context.resources.displayMetrics.density
        init()
    }

    private fun init() {
        indicatorPaint.color = indicatorColor
        indicatorWidth = defaultIndicatorWidth
    }

    abstract fun draw(canvas: Canvas, degree: Float)
    /** called when size change or color, width  */
    protected abstract fun updateIndicator()

    /** @param withEffects if indicator have effects like BlurMaskFilter
     */
    protected abstract fun setWithEffects(withEffects: Boolean)

    /**
     * must call in `speedometer.onSizeChanged()`
     * @param speedometer target speedometer.
     */
    fun onSizeChange(speedometer: Speedometer) {
        setTargetSpeedometer(speedometer)
    }

    /**
     * to change indicator's data,
     * this method called by the library.
     * @param speedometer target speedometer.
     */
    fun setTargetSpeedometer(speedometer: Speedometer) {
        updateData(speedometer)
        updateIndicator()
    }

    private fun updateData(speedometer: Speedometer) {
        this.viewSize = speedometer.size.toFloat()
        this.speedometerWidth = speedometer.getSpeedometerWidth()
        this.padding = speedometer.padding
        this.isInEditMode = speedometer.isInEditMode
    }

    fun dpTOpx(dp: Float): Float {
        return dp * density
    }

    fun getIndicatorWidth(): Float {
        return indicatorWidth
    }

    fun setIndicatorWidth(indicatorWidth: Float): I {
        this.indicatorWidth = indicatorWidth
        return this as I
    }

    /**
     * @return size of Speedometer View without padding.
     */
    fun getViewSize(): Float {
        return viewSize - padding * 2f
    }

    fun getIndicatorColor(): Int {
        return indicatorColor
    }

    fun setIndicatorColor(indicatorColor: Int): I {
        this.indicatorColor = indicatorColor
        return this as I
    }

    fun noticeIndicatorWidthChange(indicatorWidth: Float) {
        this.indicatorWidth = indicatorWidth
        updateIndicator()
    }

    fun noticeIndicatorColorChange(indicatorColor: Int) {
        this.indicatorColor = indicatorColor
        updateIndicator()
    }

    fun noticeSpeedometerWidthChange(speedometerWidth: Float) {
        this.speedometerWidth = speedometerWidth
        updateIndicator()
    }

    fun noticePaddingChange(newPadding: Int) {
        this.padding = newPadding
        updateIndicator()
    }

    fun withEffects(withEffects: Boolean) {
        setWithEffects(withEffects)
        updateIndicator()
    }

    /** indicator's shape  */
    enum class Indicators {
        NoIndicator, NormalIndicator, NormalSmallIndicator, TriangleIndicator, SpindleIndicator, LineIndicator, HalfLineIndicator, QuarterLineIndicator, KiteIndicator, NeedleIndicator
    }

    companion object {

        /**
         * create new [Indicator] with default values.
         * @param context required.
         * @param indicator new indicator (Enum value).
         * @return new indicator object.
         */
        fun createIndicator(context: Context, indicator: Indicators): Indicator<*> {
            when (indicator) {
                Indicator.Indicators.NoIndicator -> return NoIndicator(context)
                Indicator.Indicators.NormalIndicator -> return NormalIndicator(context)
                Indicator.Indicators.NormalSmallIndicator -> return NormalSmallIndicator(context)
                Indicator.Indicators.TriangleIndicator -> return TriangleIndicator(context)
                Indicator.Indicators.SpindleIndicator -> return SpindleIndicator(context)
                Indicator.Indicators.LineIndicator -> return LineIndicator(context, LineIndicator.LINE)
                Indicator.Indicators.HalfLineIndicator -> return LineIndicator(context, LineIndicator.HALF_LINE)
                Indicator.Indicators.QuarterLineIndicator -> return LineIndicator(context, LineIndicator.QUARTER_LINE)
                Indicator.Indicators.KiteIndicator -> return KiteIndicator(context)
                Indicator.Indicators.NeedleIndicator -> return NeedleIndicator(context)
                else -> return NormalIndicator(context)
            }
        }
    }
}
