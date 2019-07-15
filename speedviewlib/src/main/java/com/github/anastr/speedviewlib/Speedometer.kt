package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.util.AttributeSet
import android.view.View
import com.github.anastr.speedviewlib.components.Indicators.ImageIndicator
import com.github.anastr.speedviewlib.components.Indicators.Indicator
import com.github.anastr.speedviewlib.components.Indicators.NoIndicator
import com.github.anastr.speedviewlib.components.note.Note
import com.github.anastr.speedviewlib.util.OnPrintTickLabel
import java.util.*

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
abstract class Speedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Gauge(context, attrs, defStyleAttr) {

    /** needle point to [currentSpeed], cannot be null  */
    private lateinit var indicator: Indicator<*>

    /**
     * light effect behind the [indicator].
     */
    var isWithIndicatorLight = false

    /**
     * indicator light's color.
     * @see isWithIndicatorLight
     */
    var indicatorLightColor = -0x4400a8de

    private val circleBackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indicatorLightPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var speedometerWidth = dpTOpx(30f)

    /**
     * change the color of all marks (if exist),
     * **this option is not available for all Speedometers**.
     */
    var markColor = -0x1
        set(markColor) {
            field = markColor
            if (!isAttachedToWindow)
                return
            invalidate()
        }
    private var lowSpeedColor = -0xff0100
    private var mediumSpeedColor = -0x100
    private var highSpeedColor = -0x10000
    /**
     * Circle Background Color,
     * you can set it `Color.TRANSPARENT`
     * to remove circle background.
     */
    public var backgroundCircleColor = -0x1
        set(backgroundCircleColor) {
            field = backgroundCircleColor
            circleBackPaint.color = backgroundCircleColor
            if (!isAttachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
    }

    private var startDegree = 135
    private var endDegree = 135 + 270

    /**
     * to rotate indicator
     * @return current degree where indicator must be.
     */
    protected var degree = startDegree.toFloat()
        private set

    /** array to contain all notes that will be draw  */
    private val notes = ArrayList<Note<*>>()

    /**
     * change speedometer shape, style and indicator position.<br></br>
     * this option will return [.startDegree] to the **minimum** value,
     * and [.endDegree] to the **maximum** value
     * if the speedometerMode doesn't equal to `Mode.NORMAL`.
     */
    var speedometerMode = Mode.NORMAL
        set(speedometerMode) {
            field = speedometerMode
            if (speedometerMode != Mode.NORMAL) {
                startDegree = speedometerMode.minDegree
                endDegree = speedometerMode.maxDegree
            }
            updateTranslated()
            cancelSpeedAnimator()
            degree = getDegreeAtSpeed(speed)
            indicator.onSizeChange(this)
            if (!isAttachedToWindow)
                return
            requestLayout()
            updateBackgroundBitmap()
            tremble()
            invalidate()
        }

    /** padding to fix speedometer cut when change [.speedometerMode]  */
    private var cutPadding = 0

    /** ticks values(speed values) to draw  */
    private val ticks = ArrayList<Float>()
    /** to rotate tick label  */
    private var tickRotation = true
    /**
     *  first padding, set by speedometer.
     *  this will not redraw background bitmap.
     */
    protected var initTickPadding = 0f
    private var tickPadding = (getSpeedometerWidth() + dpTOpx(3f)).toInt()
    private var onPrintTickLabel: OnPrintTickLabel? = null

    private var lastPercentSpeed = 0f

    /**
     * change indicator's color,
     * this option will be ignored when using [ImageIndicator].
     */
    var indicatorColor: Int
        get() = indicator.getIndicatorColor()
        set(indicatorColor) {
            indicator.noticeIndicatorColorChange(indicatorColor)
            if (!isAttachedToWindow)
                return
            invalidate()
        }

    /**
     * @return size of speedometer.
     */
    val size: Int
        get() {
            if (this.speedometerMode == Mode.NORMAL)
                return width
            return if (this.speedometerMode.isHalf) Math.max(width, height) else Math.max(width, height) * 2 - cutPadding * 2
        }

    /**
     * @return size of speedometer without padding.
     */
    val sizePa: Int
        get() = size - padding * 2

    /**
     * change indicator width in pixel, this value have several meaning
     * between [Indicator.Indicators], it will be ignored
     * when using [ImageIndicator].
     */
    var indicatorWidth: Float
        get() = indicator.getIndicatorWidth()
        set(indicatorWidth) {
            indicator.noticeIndicatorWidthChange(indicatorWidth)
            if (!isAttachedToWindow)
                return
            invalidate()
        }

    /**
     * number of tick points of speed value's label.
     *
     * to add speed value label at each tick point between [maxSpeed]
     * and [minSpeed].
     * @throws IllegalArgumentException if `tickNumber < 0`.
     */
    // tick each degree
    var tickNumber: Int
        get() = ticks.size
        set(tickNumber) {
            if (tickNumber < 0)
                throw IllegalArgumentException("tickNumber mustn't be negative")
            val ticks = ArrayList<Float>()
            val tickEach = if (tickNumber != 1) (endDegree - startDegree).toFloat() / (tickNumber - 1).toFloat() else endDegree + 1f
            for (i in 0 until tickNumber) {
                val tick = getSpeedAtDegree(tickEach * i + getStartDegree())
                ticks.add(tick)
            }
            setTicks(ticks)
        }

    /**
     * to make speed value's label rotate at each tick.
     */
    var isTickRotation: Boolean
        get() = tickRotation
        set(tickRotation) {
            this.tickRotation = tickRotation
            if (!isAttachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    /**
     * @return current position of center X to use in drawing.
     */
    protected val viewCenterX: Float
        get() {
            return when (this.speedometerMode) {
                Speedometer.Mode.LEFT, Speedometer.Mode.TOP_LEFT, Speedometer.Mode.BOTTOM_LEFT -> size * .5f - width * .5f
                Speedometer.Mode.RIGHT, Speedometer.Mode.TOP_RIGHT, Speedometer.Mode.BOTTOM_RIGHT -> size * .5f + width * .5f
                else -> size * .5f
            }
        }

    /**
     * @return current position of center Y to use in drawing.
     */
    protected val viewCenterY: Float
        get() {
            return when (this.speedometerMode) {
                Speedometer.Mode.TOP, Speedometer.Mode.TOP_LEFT, Speedometer.Mode.TOP_RIGHT -> size * .5f - height * .5f
                Speedometer.Mode.BOTTOM, Speedometer.Mode.BOTTOM_LEFT, Speedometer.Mode.BOTTOM_RIGHT -> size * .5f + height * .5f
                else -> size * .5f
            }
        }

    protected val viewLeft: Float
        get() = viewCenterX - width * .5f

    protected val viewTop: Float
        get() = viewCenterY - height * .5f

    protected val viewRight: Float
        get() = viewCenterX + width * .5f

    protected val viewBottom: Float
        get() = viewCenterY + height * .5f

    init {
        init()
        initAttributeSet(context, attrs)
        initAttributeValue()
    }

    private fun init() {
        indicatorLightPaint.style = Paint.Style.STROKE
        indicator = NoIndicator(context)
        defaultSpeedometerValues()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.Speedometer, 0, 0)

        val mode = a.getInt(R.styleable.Speedometer_sv_speedometerMode, -1)
        if (mode != -1 && mode != 0)
            speedometerMode = Mode.values()[mode]
        val ind = a.getInt(R.styleable.Speedometer_sv_indicator, -1)
        if (ind != -1)
            setIndicator(Indicator.Indicators.values()[ind])
        markColor = a.getColor(R.styleable.Speedometer_sv_markColor, markColor)
        lowSpeedColor = a.getColor(R.styleable.Speedometer_sv_lowSpeedColor, lowSpeedColor)
        mediumSpeedColor = a.getColor(R.styleable.Speedometer_sv_mediumSpeedColor, mediumSpeedColor)
        highSpeedColor = a.getColor(R.styleable.Speedometer_sv_highSpeedColor, highSpeedColor)
        backgroundCircleColor = a.getColor(R.styleable.Speedometer_sv_backgroundCircleColor, backgroundCircleColor)
        speedometerWidth = a.getDimension(R.styleable.Speedometer_sv_speedometerWidth, speedometerWidth)
        startDegree = a.getInt(R.styleable.Speedometer_sv_startDegree, startDegree)
        endDegree = a.getInt(R.styleable.Speedometer_sv_endDegree, endDegree)
        indicatorWidth = a.getDimension(R.styleable.Speedometer_sv_indicatorWidth, indicator.getIndicatorWidth())
        cutPadding = a.getDimension(R.styleable.Speedometer_sv_cutPadding, cutPadding.toFloat()).toInt()
        tickNumber = a.getInteger(R.styleable.Speedometer_sv_tickNumber, ticks.size)
        tickRotation = a.getBoolean(R.styleable.Speedometer_sv_tickRotation, tickRotation)
        tickPadding = a.getDimension(R.styleable.Speedometer_sv_tickPadding, tickPadding.toFloat()).toInt()
        indicatorColor = a.getColor(R.styleable.Speedometer_sv_indicatorColor, indicator.getIndicatorColor())
        isWithIndicatorLight = a.getBoolean(R.styleable.Speedometer_sv_withIndicatorLight, isWithIndicatorLight)
        indicatorLightColor = a.getColor(R.styleable.Speedometer_sv_indicatorLightColor, indicatorLightColor)
        degree = startDegree.toFloat()
        a.recycle()
        checkStartAndEndDegree()
    }

    private fun initAttributeValue() {
        circleBackPaint.color = backgroundCircleColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val defaultSize = dpTOpx(250f).toInt()

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        val size: Int

        size = if (widthMode == View.MeasureSpec.EXACTLY)
            measuredWidth
        else if (heightMode == View.MeasureSpec.EXACTLY)
            measuredHeight
        else if (widthMode == View.MeasureSpec.UNSPECIFIED && heightMode == View.MeasureSpec.UNSPECIFIED)
            defaultSize
        else if (widthMode == View.MeasureSpec.AT_MOST && heightMode == View.MeasureSpec.AT_MOST)
            Math.min(defaultSize, Math.min(measuredWidth, measuredHeight))
        else {
            if (widthMode == View.MeasureSpec.AT_MOST)
                Math.min(defaultSize, measuredWidth)
            else
                Math.min(defaultSize, measuredHeight)
        }

        var newW = size / this.speedometerMode.divWidth
        var newH = size / this.speedometerMode.divHeight
        if (this.speedometerMode.isHalf) {
            if (this.speedometerMode.divWidth == 2)
                newW += cutPadding
            else
                newH += cutPadding
        }
        setMeasuredDimension(newW, newH)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        indicator.onSizeChange(this)
        updateTranslated()
    }

    private fun checkStartAndEndDegree() {
        if (startDegree < 0)
            throw IllegalArgumentException("StartDegree can\'t be Negative")
        if (endDegree < 0)
            throw IllegalArgumentException("EndDegree can\'t be Negative")
        if (startDegree >= endDegree)
            throw IllegalArgumentException("EndDegree must be bigger than StartDegree !")
        if (endDegree - startDegree > 360)
            throw IllegalArgumentException("(EndDegree - StartDegree) must be smaller than 360 !")
        if (startDegree < this.speedometerMode.minDegree)
            throw IllegalArgumentException("StartDegree must be bigger than " + this.speedometerMode.minDegree
                    + " in " + this.speedometerMode + " Mode !")
        if (endDegree > this.speedometerMode.maxDegree)
            throw IllegalArgumentException("EndDegree must be smaller than " + this.speedometerMode.maxDegree
                    + " in " + this.speedometerMode + " Mode !")
    }

    /**
     * add default values for Speedometer inside this method,
     * call super setting method to set default value,
     * Ex :
     *
     * `super.setBackgroundCircleColor(Color.TRANSPARENT);`
     */
    protected abstract fun defaultSpeedometerValues()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        degree = getDegreeAtSpeed(currentSpeed)
    }

    /**
     * draw indicator at current [degree],
     * this method must call in subSpeedometer's `onDraw` method.
     * @param canvas view canvas to draw.
     */
    protected fun drawIndicator(canvas: Canvas) {
        if (isWithIndicatorLight)
            drawIndicatorLight(canvas)
        indicator.draw(canvas, degree)
    }

    protected fun drawIndicatorLight(canvas: Canvas) {
        val MAX_LIGHT_SWEEP = 30f
        var sweep = Math.abs(getPercentSpeed() - lastPercentSpeed) * MAX_LIGHT_SWEEP
        lastPercentSpeed = getPercentSpeed()
        if (sweep > MAX_LIGHT_SWEEP)
            sweep = MAX_LIGHT_SWEEP
        val colors = intArrayOf(indicatorLightColor, 0x00FFFFFF)
        val lightSweep = SweepGradient(size * .5f, size * .5f, colors, floatArrayOf(0f, sweep / 360f))
        indicatorLightPaint.shader = lightSweep
        indicatorLightPaint.strokeWidth = indicator.getLightBottom() - indicator.getTop()

        val risk = indicator.getTop() + indicatorLightPaint.strokeWidth * .5f
        val speedometerRect = RectF(risk, risk, size - risk, size - risk)
        canvas.save()
        canvas.rotate(degree, size * .5f, size * .5f)
        if (isSpeedIncrease)
            canvas.scale(1f, -1f, size * .5f, size * .5f)
        canvas.drawArc(speedometerRect, 0f, sweep, false, indicatorLightPaint)
        canvas.restore()
    }

    /**
     * draw Notes,
     * every Speedometer must call this method at End of it's `onDraw()` method.
     * @param canvas view canvas to draw notes.
     */
    protected fun drawNotes(canvas: Canvas) {
        for (note in notes) {
            if (note.getPosition() === Note.Position.CenterSpeedometer)
                note.draw(canvas, width * .5f, height * .5f)
            else {
                val y = when (note.getPosition()) {
                    Note.Position.TopIndicator -> indicator.getTop()
                    Note.Position.CenterIndicator -> (indicator.getTop() + indicator.getBottom()) * .5f
                    Note.Position.BottomIndicator -> indicator.getBottom()
                    Note.Position.TopSpeedometer -> padding.toFloat()
                    Note.Position.QuarterSpeedometer -> heightPa * .25f + padding
                    Note.Position.CenterSpeedometer -> viewCenterY
                }
                canvas.save()
                canvas.rotate(90f + degree, width * .5f, height * .5f)
                canvas.rotate(-(90f + degree), width * .5f, y)
                note.draw(canvas, width * .5f, y)
                canvas.restore()
            }
        }
    }

    /**
     * create canvas to draw [backgroundBitmap].
     * @return [backgroundBitmap]'s canvas.
     */
    override fun createBackgroundBitmapCanvas(): Canvas {
        if (width == 0 || height == 0)
            return Canvas()
        backgroundBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(backgroundBitmap!!)
        canvas.drawCircle(size * .5f, size * .5f, size * .5f - padding, circleBackPaint)

        // to fix preview mode issue
        canvas.clipRect(0, 0, size, size)

        return canvas
    }

    /**
     * @param speed to know the degree at it.
     * @return current Degree at that speed.
     */
    protected fun getDegreeAtSpeed(speed: Float): Float {
        return (speed - getMinSpeed()) * (endDegree - startDegree) / (getMaxSpeed() - getMinSpeed()) + startDegree
    }

    /**
     * @param degree to know the speed at it.
     * @return current speed at that degree.
     */
    protected fun getSpeedAtDegree(degree: Float): Float {
        return (degree - startDegree) * (getMaxSpeed() - getMinSpeed()) / (endDegree - startDegree) + getMinSpeed()
    }

    open fun getLowSpeedColor(): Int {
        return lowSpeedColor
    }

    /**
     * change the color of Low Section.
     * @param lowSpeedColor new color.
     */
    open fun setLowSpeedColor(lowSpeedColor: Int) {
        this.lowSpeedColor = lowSpeedColor
        if (!isAttachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    open fun getMediumSpeedColor(): Int {
        return mediumSpeedColor
    }

    /**
     * change the color of Medium Section.
     * @param mediumSpeedColor new color.
     */
    open fun setMediumSpeedColor(mediumSpeedColor: Int) {
        this.mediumSpeedColor = mediumSpeedColor
        if (!isAttachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    open fun getHighSpeedColor(): Int {
        return highSpeedColor
    }

    /**
     * change the color of High Section.
     * @param highSpeedColor new color.
     */
    open fun setHighSpeedColor(highSpeedColor: Int) {
        this.highSpeedColor = highSpeedColor
        if (!isAttachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    fun getSpeedometerWidth(): Float {
        return speedometerWidth
    }

    /**
     * change the width of speedometer's bar.
     * @param speedometerWidth new width in pixel.
     */
    open fun setSpeedometerWidth(speedometerWidth: Float) {
        this.speedometerWidth = speedometerWidth
        if (!isAttachedToWindow)
            return
        indicator.noticeSpeedometerWidthChange(speedometerWidth)
        updateBackgroundBitmap()
        invalidate()
    }

    protected fun getStartDegree(): Int {
        return startDegree
    }

    /**
     * change the start of speedometer (at [minSpeed]).<br></br>
     * this method will recreate ticks, and if you have set custom tick,
     * it will be removed, by calling [tickNumber] method.
     * @param startDegree the start of speedometer.
     * @throws IllegalArgumentException if `startDegree` negative.
     * @throws IllegalArgumentException if `startDegree >= endDegree`.
     * @throws IllegalArgumentException if the difference between `endDegree and startDegree` bigger than 360.
     */
    fun setStartDegree(startDegree: Int) {
        setStartEndDegree(startDegree, endDegree)
    }

    protected fun getEndDegree(): Int {
        return endDegree
    }

    /**
     * change the end of speedometer (at [maxSpeed]).<br></br>
     * this method will recreate ticks, and if you have set custom tick,
     * it will be removed, by calling [tickNumber] method.
     * @param endDegree the end of speedometer.
     * @throws IllegalArgumentException if `endDegree` negative.
     * @throws IllegalArgumentException if `endDegree <= startDegree`.
     * @throws IllegalArgumentException if the difference between `endDegree and startDegree` bigger than 360.
     */
    fun setEndDegree(endDegree: Int) {
        setStartEndDegree(startDegree, endDegree)
    }

    /**
     * change start and end of speedometer.<br></br>
     * this method will recreate ticks, and if you have set custom tick,
     * it will be removed, by calling [tickNumber] method.
     * @param startDegree the start of speedometer.
     * @param endDegree the end of speedometer.
     * @throws IllegalArgumentException if `startDegree OR endDegree` negative.
     * @throws IllegalArgumentException if `startDegree >= endDegree`.
     * @throws IllegalArgumentException if the difference between `endDegree and startDegree` bigger than 360.
     */
    fun setStartEndDegree(startDegree: Int, endDegree: Int) {
        this.startDegree = startDegree
        this.endDegree = endDegree
        checkStartAndEndDegree()
        if (ticks.size != 0)
            tickNumber = ticks.size
        cancelSpeedAnimator()
        degree = getDegreeAtSpeed(speed)
        if (!isAttachedToWindow)
            return
        updateBackgroundBitmap()
        tremble()
        invalidate()
    }

    /**
     * Display new [Note](https://github.com/anastr/SpeedView/wiki/Notes)
     * for custom seconds.
     * @param note to display.
     * @param showTimeMillisecond time to remove Note, 3 sec by default.
     */
    fun addNote(note: Note<*>, showTimeMillisecond: Long = 3000) {
        note.build(width)
        notes.add(note)
        if (showTimeMillisecond == Note.INFINITE.toLong())
            return
        postDelayed({
            if (isAttachedToWindow) {
                notes.remove(note)
                postInvalidate()
            }
        }, showTimeMillisecond)
        invalidate()
    }

    /**
     * remove All [Notes](https://github.com/anastr/SpeedView/wiki/Notes).
     */
    fun removeAllNotes() {
        notes.clear()
        invalidate()
    }

    /**
     * draw minSpeedText and maxSpeedText at default Position.
     * @param c canvas to draw.
     */
    protected fun drawDefMinMaxSpeedPosition(c: Canvas) {
        textPaint.textAlign = when {
            getStartDegree() % 360 <= 90 -> Paint.Align.RIGHT
            getStartDegree() % 360 <= 180 -> Paint.Align.LEFT
            getStartDegree() % 360 <= 270 -> Paint.Align.CENTER
            else -> Paint.Align.RIGHT
        }
        c.save()
        c.rotate(getStartDegree() + 90f, size * .5f, size * .5f)
        c.rotate(-(getStartDegree() + 90f), sizePa * .5f - textPaint.textSize + padding, textPaint.textSize + padding)
        c.drawText(getMinSpeedText(), sizePa * .5f - textPaint.textSize + padding, textPaint.textSize + padding, textPaint)
        c.restore()
        textPaint.textAlign = when {
            getEndDegree() % 360 <= 90 -> Paint.Align.RIGHT
            getEndDegree() % 360 <= 180 -> Paint.Align.LEFT
            getEndDegree() % 360 <= 270 -> Paint.Align.CENTER
            else -> Paint.Align.RIGHT
        }
        c.save()
        c.rotate(getEndDegree() + 90f, size * .5f, size * .5f)
        c.rotate(-(getEndDegree() + 90f), sizePa * .5f + textPaint.textSize + padding.toFloat(), textPaint.textSize + padding)
        c.drawText(getMaxSpeedText(), sizePa * .5f + textPaint.textSize + padding.toFloat(), textPaint.textSize + padding, textPaint)
        c.restore()
    }

    /**
     * draw speed value at each tick point.
     * @param c canvas to draw.
     */
    protected fun drawTicks(c: Canvas) {
        if (ticks.size == 0)
            return

        textPaint.textAlign = Paint.Align.LEFT

        for (i in ticks.indices) {
            val d = getDegreeAtSpeed(ticks[i]) + 90f
            c.save()
            c.rotate(d, size * .5f, size * .5f)
            if (!tickRotation)
                c.rotate(-d, size * .5f, initTickPadding + textPaint.textSize + padding.toFloat() + tickPadding.toFloat())

            var tick: CharSequence? = null
            if (onPrintTickLabel != null)
                tick = onPrintTickLabel!!.getTickLabel(i, ticks[i])

            // if onPrintTickLabel == null, or getTickLabel() return null.
            if (tick == null)
                tick = if (tickTextFormat == Gauge.FLOAT_FORMAT.toInt())
                    "%.1f".format(locale, ticks[i])
//                    String.format(locale, "%.1f", ticks[i])
                else
                    "%d".format(locale, ticks[i].toInt())
//                    String.format(locale, "%d", ticks[i].toInt())

            c.translate(0f, initTickPadding + padding.toFloat() + tickPadding.toFloat())
            StaticLayout(tick, textPaint, size, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)
                    .draw(c)

            c.restore()
        }
    }

    /**
     * call this method to apply/remove blur effect for indicator.
     * @param withEffects effect.
     */
    protected fun indicatorEffects(withEffects: Boolean) {
        indicator.withEffects(withEffects)
    }

    /**
     * change [indicator shape](https://github.com/anastr/SpeedView/wiki/Indicators).<br></br>
     * this method will get bach indicatorColor and indicatorWidth to default.
     * @param indicator new indicator (Enum value).
     */
    open fun setIndicator(indicator: Indicator.Indicators) {
        this.indicator = Indicator.createIndicator(context, indicator)
        if (!isAttachedToWindow)
            return
        this.indicator.setTargetSpeedometer(this)
        invalidate()
    }

    /**
     * add custom [indicator](https://github.com/anastr/SpeedView/wiki/Indicators).
     * @param indicator new indicator.
     */
    fun setIndicator(indicator: Indicator<*>) {
        this.indicator = indicator
        if (!isAttachedToWindow)
            return
        this.indicator.setTargetSpeedometer(this)
        invalidate()
    }

    /**
     * @return ticks values as list, don't edit the list.
     */
    fun getTicks(): List<Float> {
        return ticks
    }

    /**
     * to add custom speed value label at each tick point between [maxSpeed]
     * and [minSpeed].
     * @param ticks custom ticks values (speed values).
     * @throws IllegalArgumentException if one of [ticks] out of range [[minSpeed], [maxSpeed]].
     * @throws IllegalArgumentException If [ticks] are not ascending.
     */
    fun setTicks(vararg ticks: Float) {
        setTicks(ticks.asList())
    }

    /**
     * to add custom speed value label at each tick point between [maxSpeed]
     * and [minSpeed].
     * @param ticks custom ticks values (speed values).
     * @throws IllegalArgumentException if one of [ticks] out of range [[minSpeed], [maxSpeed]].
     * @throws IllegalArgumentException If [ticks] are not ascending.
     */
    fun setTicks(ticks: List<Float>) {
        this.ticks.clear()
        this.ticks.addAll(ticks)
        checkTicks()
        if (!isAttachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    private fun checkTicks() {
        var lastTick = getMinSpeed() - 1f
        for (tick in ticks) {
            if (lastTick == tick)
                throw IllegalArgumentException("you mustn't have double ticks")
            if (lastTick > tick)
                throw IllegalArgumentException("ticks must be ascending order")
            if (tick < getMinSpeed() || tick > getMaxSpeed())
                throw IllegalArgumentException("ticks must be between [minSpeed, maxSpeed] !!")
            lastTick = tick
        }
    }

    /**
     * @return tick label's padding.
     */
    fun getTickPadding(): Int {
        return tickPadding
    }

    /**
     * @param tickPadding tick label's padding.
     */
    fun setTickPadding(tickPadding: Int) {
        this.tickPadding = tickPadding
        if (!isAttachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    /**
     * create custom Tick label.
     * @param onPrintTickLabel maybe null, The callback that will run.
     */
    fun setOnPrintTickLabel(onPrintTickLabel: OnPrintTickLabel) {
        this.onPrintTickLabel = onPrintTickLabel
        if (!isAttachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    private fun updateTranslated() {
        translatedDx = if (this.speedometerMode.isRight) -size * .5f + cutPadding else 0f
        translatedDy = if (this.speedometerMode.isBottom) -size * .5f + cutPadding else 0f
    }

    enum class Mode(internal val minDegree: Int, internal val maxDegree: Int, val isHalf: Boolean, internal val divWidth: Int, internal val divHeight: Int) {
        NORMAL(0, 360 * 2, false, 1, 1), LEFT(90, 270, true, 2, 1), TOP(180, 360, true, 1, 2), RIGHT(270, 450, true, 2, 1), BOTTOM(0, 180, true, 1, 2), TOP_LEFT(180, 270, false, 1, 1), TOP_RIGHT(270, 360, false, 1, 1), BOTTOM_RIGHT(0, 90, false, 1, 1), BOTTOM_LEFT(90, 180, false, 1, 1);

        val isLeft: Boolean
            get() = this == LEFT || this == TOP_LEFT || this == BOTTOM_LEFT

        val isTop: Boolean
            get() = this == TOP || this == TOP_LEFT || this == TOP_RIGHT

        val isRight: Boolean
            get() = this == RIGHT || this == TOP_RIGHT || this == BOTTOM_RIGHT

        val isBottom: Boolean
            get() = this == BOTTOM || this == BOTTOM_LEFT || this == BOTTOM_RIGHT

        val isQuarter: Boolean
            get() = !isHalf && this != NORMAL
    }
}

