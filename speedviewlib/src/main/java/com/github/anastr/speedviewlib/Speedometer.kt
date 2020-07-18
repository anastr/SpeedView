package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.components.indicators.Indicator
import com.github.anastr.speedviewlib.components.indicators.NoIndicator
import com.github.anastr.speedviewlib.components.note.Note
import com.github.anastr.speedviewlib.util.OnPrintTickLabelListener
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.text.Typography.degree

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
abstract class Speedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Gauge(context, attrs, defStyleAttr) {

    /**
     * needle point to [currentSpeed], cannot be null
     *
     * add custom [indicator](https://github.com/anastr/SpeedView/wiki/Indicators).
     */
    var indicator: Indicator<*> = NoIndicator(context)
        set(indicator) {
            field.deleteObservers()
            indicator.setTargetSpeedometer(this)
            field = indicator
            if (isAttachedToWindow) {
                this.indicator.setTargetSpeedometer(this)
                invalidate()
            }
        }

    /**
     * light effect behind the [indicator].
     */
    var isWithIndicatorLight = false

    /**
     * indicator light's color.
     * @see isWithIndicatorLight
     */
    var indicatorLightColor = 0xBBFF5722.toInt()

    private val circleBackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indicatorLightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    protected val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override var speedometerWidth
        get() = super.speedometerWidth
        set(speedometerWidth) {
            super.speedometerWidth = speedometerWidth
            if (isAttachedToWindow)
                indicator.updateIndicator()
        }

    private val markPath = Path()
    var marksNumber = 0
        set(marksNumber) {
            field = marksNumber
            invalidateGauge()
        }
    /**
     * change the color of all marks (if exist),
     * **this option is not available for all Speedometers**.
     */
    var markColor
        get() = markPaint.color
        set(markColor) {
            markPaint.color = markColor
        }
    var marksPadding = 0f
        set(marksPadding) {
            field = marksPadding
            invalidateGauge()
        }
    var markHeight = dpTOpx(9f)
        set(markHeight) {
            field = markHeight
            invalidateGauge()
        }
    var markWidth
        get() = markPaint.strokeWidth
        set(markWidth) {
            markPaint.strokeWidth = markWidth
            invalidateGauge()
        }
    var markStyle
        get() = if (markPaint.strokeCap == Paint.Cap.ROUND) Style.ROUND else Style.BUTT
        set(markStyle) {
            if (markStyle == Style.ROUND)
                markPaint.strokeCap = Paint.Cap.ROUND
            else
                markPaint.strokeCap = Paint.Cap.BUTT
            invalidateGauge()
        }

    /**
     * Circle Background Color,
     * you can set it `Color.TRANSPARENT`
     * to remove circle background.
     */
    var backgroundCircleColor = 0xFFFFFFFF.toInt()
        set(backgroundCircleColor) {
            field = backgroundCircleColor
            circleBackPaint.color = backgroundCircleColor
            invalidateGauge()
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
            indicator.updateIndicator()
            if (isAttachedToWindow) {
                requestLayout()
                invalidateGauge()
                tremble()
            }
        }

    /** padding to fix speedometer cut when change [.speedometerMode]  */
    private var cutPadding = 0

    /**
     * ticks values ([0, 1f] scale) to draw -**not editable**-.
     *
     * the value of each tick is point to a percent value of speed,
     * for Ex:
     *
     * if [minSpeed] = 0 and [maxSpeed] = 100
     * and your ticks (.1f, .4f, .8f, 1f),
     * then you will see ticks at(10, 40, 80, 100) speed value.
     *
     * order isn't important.
     *
     * @throws IllegalArgumentException if one of [ticks] out of range [0f, 1f].
     */
    var ticks = ArrayList<Float>()
        set(ticks) {
            field.clear()
            field.addAll(ticks)
            checkTicks()
            invalidateGauge()
        }

    /** to rotate tick label  */
    private var tickRotation = true
    /**
     *  first padding, set by speedometer.
     *  this will not redraw background bitmap.
     */
    protected var initTickPadding = 0f
    /**
     * tick label's padding in pixel.
     */
    var tickPadding = (speedometerWidth + dpTOpx(3f)).toInt()
        set(tickPadding) {
            field = tickPadding
            invalidateGauge()
        }

    /**
     * create custom Tick label,
     * maybe null.
     */
    var onPrintTickLabel: OnPrintTickLabelListener? = null
        set(onPrintTickLabel) {
            field = onPrintTickLabel
            invalidateGauge()
        }

    private var lastPercentSpeed = 0f

//    /**
//     * change indicator's color,
//     * this option will be ignored when using [ImageIndicator].
//     */
//    var indicatorColor: Int
//        get() = indicator.getIndicatorColor()
//        set(indicatorColor) {
//            indicator.noticeIndicatorColorChange(indicatorColor)
//            if (isAttachedToWindow)
//                invalidate()
//        }

    /**
     * @return size of speedometer.
     */
    val size: Int
        get() {
            if (this.speedometerMode == Mode.NORMAL)
                return width
            return if (this.speedometerMode.isHalf) max(width, height) else max(width, height) * 2 - cutPadding * 2
        }

    /**
     * @return size of speedometer without padding.
     */
    val sizePa: Int
        get() = size - padding * 2

//    /**
//     * change indicator width in pixel, this value have several meaning
//     * between [Indicator.Indicators], it will be ignored
//     * when using [ImageIndicator].
//     */
//    var indicatorWidth: Float
//        get() = indicator.getIndicatorWidth()
//        set(indicatorWidth) {
//            indicator.noticeIndicatorWidthChange(indicatorWidth)
//            if (isAttachedToWindow)
//                invalidate()
//        }

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
            require(tickNumber >= 0) { "tickNumber mustn't be negative" }
            val ticks = ArrayList<Float>()
            val tickEach = if (tickNumber == 1) 0f else 1f / (tickNumber - 1).toFloat()
            for (i in 0 until tickNumber)
                ticks.add(tickEach * i)
            this.ticks = ticks
        }

    /**
     * to make speed value's label rotate at each tick.
     */
    var isTickRotation: Boolean
        get() = tickRotation
        set(tickRotation) {
            this.tickRotation = tickRotation
            invalidateGauge()
        }

    /**
     * @return current position of center X to use in drawing.
     */
    protected val viewCenterX: Float
        get() {
            return when (this.speedometerMode) {
                Mode.LEFT, Mode.TOP_LEFT, Mode.BOTTOM_LEFT -> size * .5f - width * .5f
                Mode.RIGHT, Mode.TOP_RIGHT, Mode.BOTTOM_RIGHT -> size * .5f + width * .5f
                else -> size * .5f
            }
        }

    /**
     * @return current position of center Y to use in drawing.
     */
    protected val viewCenterY: Float
        get() {
            return when (this.speedometerMode) {
                Mode.TOP, Mode.TOP_LEFT, Mode.TOP_RIGHT -> size * .5f - height * .5f
                Mode.BOTTOM, Mode.BOTTOM_LEFT, Mode.BOTTOM_RIGHT -> size * .5f + height * .5f
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
        markPaint.style = Paint.Style.STROKE
        markColor = 0xFFFFFFFF.toInt()
        markWidth = dpTOpx(3f)
        markStyle = Style.BUTT
//        indicator = NoIndicator(context)
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
        marksNumber = a.getInt(R.styleable.Speedometer_sv_marksNumber, marksNumber)
        marksPadding = a.getDimension(R.styleable.Speedometer_sv_marksPadding, marksPadding)
        markHeight = a.getDimension(R.styleable.Speedometer_sv_markHeight, markHeight)
        markWidth = a.getDimension(R.styleable.Speedometer_sv_markWidth, markWidth)
        markColor = a.getColor(R.styleable.Speedometer_sv_markColor, markColor)
        val markStyleIndex = a.getInt(R.styleable.Speedometer_sv_markStyle, -1)
        if (markStyleIndex != -1)
            markStyle = Style.values()[markStyleIndex]
        backgroundCircleColor = a.getColor(R.styleable.Speedometer_sv_backgroundCircleColor, backgroundCircleColor)
        startDegree = a.getInt(R.styleable.Speedometer_sv_startDegree, startDegree)
        endDegree = a.getInt(R.styleable.Speedometer_sv_endDegree, endDegree)
        indicator.width = a.getDimension(R.styleable.Speedometer_sv_indicatorWidth, indicator.width)
        cutPadding = a.getDimension(R.styleable.Speedometer_sv_cutPadding, cutPadding.toFloat()).toInt()
        tickNumber = a.getInteger(R.styleable.Speedometer_sv_tickNumber, ticks.size)
        tickRotation = a.getBoolean(R.styleable.Speedometer_sv_tickRotation, tickRotation)
        tickPadding = a.getDimension(R.styleable.Speedometer_sv_tickPadding, tickPadding.toFloat()).toInt()
        indicator.color = a.getColor(R.styleable.Speedometer_sv_indicatorColor, indicator.color)
        isWithIndicatorLight = a.getBoolean(R.styleable.Speedometer_sv_withIndicatorLight, isWithIndicatorLight)
        indicatorLightColor = a.getColor(R.styleable.Speedometer_sv_indicatorLightColor, indicatorLightColor)
        val tickFormat = a.getInt(R.styleable.Speedometer_sv_tickTextFormat, -1)
        if (tickFormat == 0)
            onPrintTickLabel = { _, speed -> "%.0f".format(locale, speed) }
        else if (tickFormat == 1)
            onPrintTickLabel = { _, speed -> "%.1f".format(locale, speed) }
        degree = startDegree.toFloat()
        a.recycle()
        checkStartAndEndDegree()
    }

    private fun initAttributeValue() {
        circleBackPaint.color = backgroundCircleColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultSize = dpTOpx(250f).toInt()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        var size = if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY)
            min(w, h)
        else if (widthMode == MeasureSpec.EXACTLY)
            w
        else if (heightMode == MeasureSpec.EXACTLY)
            h
        else if ((widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED)
                || (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST))
            min(defaultSize, min(w, h))
        else {
            if (widthMode == MeasureSpec.AT_MOST)
                min(defaultSize, w)
            else
                min(defaultSize, h)
        }

        size = max(size, max(suggestedMinimumWidth, suggestedMinimumHeight))

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
        indicator.updateIndicator()
        updateTranslated()
    }

    private fun checkStartAndEndDegree() {
        require(startDegree >= 0) { "StartDegree can\'t be Negative" }
        require(endDegree >= 0) { "EndDegree can\'t be Negative" }
        require(startDegree < endDegree) { "EndDegree must be bigger than StartDegree !" }
        require(endDegree - startDegree <= 360) { "(EndDegree - StartDegree) must be smaller than 360 !" }
        require(startDegree >= speedometerMode.minDegree) {
            "StartDegree must be bigger than ${speedometerMode.minDegree} in $speedometerMode Mode !" }
        require(endDegree <= speedometerMode.maxDegree) {
            "EndDegree must be smaller than ${speedometerMode.maxDegree} in $speedometerMode Mode !" }
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
     * draw marks depending on [marksNumber],
     * this method must be called in subSpeedometer's [updateBackgroundBitmap] method.
     * @param canvas marks should be drawn on [backgroundBitmap].
     */
    protected fun drawMarks(canvas: Canvas) {
        markPath.reset()
        markPath.moveTo(size * .5f, marksPadding + padding)
        markPath.lineTo(size * .5f, marksPadding + markHeight + padding)

        canvas.save()
        canvas.rotate(90f + getStartDegree(), size * .5f, size * .5f)
        val everyDegree = (getEndDegree() - getStartDegree()) / (marksNumber + 1f)
        for (i in 1..marksNumber) {
            canvas.rotate(everyDegree, size * .5f, size * .5f)
            canvas.drawPath(markPath, markPaint)
        }
        canvas.restore()
    }

    /**
     * draw indicator at current [degree],
     * this method must be called in subSpeedometer's `onDraw` method.
     * @param canvas view canvas to draw.
     */
    protected fun drawIndicator(canvas: Canvas) {
        if (isWithIndicatorLight)
            drawIndicatorLight(canvas)
        indicator.draw(canvas, degree)
    }

    protected fun drawIndicatorLight(canvas: Canvas) {
        val MAX_LIGHT_SWEEP = 30f
        var sweep = abs(getPercentSpeed() - lastPercentSpeed) * MAX_LIGHT_SWEEP
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
        if (size == 0)
            return Canvas()
        backgroundBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(backgroundBitmap)
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
        return (speed - minSpeed) * (endDegree - startDegree) / (maxSpeed - minSpeed) + startDegree
    }

    /**
     * @param degree to know the speed at it.
     * @return current speed at that degree.
     */
    protected fun getSpeedAtDegree(degree: Float): Float {
        return (degree - startDegree) * (maxSpeed - minSpeed) / (endDegree - startDegree) + minSpeed
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
        cancelSpeedAnimator()
        degree = getDegreeAtSpeed(speed)
        if (isAttachedToWindow){
            invalidateGauge()
            tremble()
        }
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
            startDegree % 360 <= 90 -> Paint.Align.RIGHT
            startDegree % 360 <= 180 -> Paint.Align.LEFT
            startDegree % 360 <= 270 -> Paint.Align.CENTER
            else -> Paint.Align.RIGHT
        }
        var tickStart: CharSequence? = null
        if (onPrintTickLabel != null)
            tickStart = onPrintTickLabel!!.invoke(0, minSpeed)
        // if (onPrintTickLabel is null or it returns null)
        if (tickStart == null)
            tickStart = "%.0f".format(locale, minSpeed)
        c.save()
        c.rotate(startDegree + 90f, size * .5f, size * .5f)
        c.rotate(-(startDegree + 90f), sizePa * .5f - textPaint.textSize + padding, textPaint.textSize + padding)
        c.drawText(tickStart.toString(), sizePa * .5f - textPaint.textSize + padding, textPaint.textSize + padding, textPaint)
        c.restore()
        textPaint.textAlign = when {
            endDegree % 360 <= 90 -> Paint.Align.RIGHT
            endDegree % 360 <= 180 -> Paint.Align.LEFT
            endDegree % 360 <= 270 -> Paint.Align.CENTER
            else -> Paint.Align.RIGHT
        }
        var tickEnd: CharSequence? = null
        if (onPrintTickLabel != null)
            tickEnd = onPrintTickLabel!!.invoke(1, maxSpeed)
        // if (onPrintTickLabel is null or it returns null)
        if (tickEnd == null)
            tickEnd = "%.0f".format(locale, maxSpeed)
        c.save()
        c.rotate(endDegree + 90f, size * .5f, size * .5f)
        c.rotate(-(endDegree + 90f), sizePa * .5f + textPaint.textSize + padding.toFloat(), textPaint.textSize + padding)
        c.drawText(tickEnd.toString(), sizePa * .5f + textPaint.textSize + padding.toFloat(), textPaint.textSize + padding, textPaint)
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

        val range = endDegree - startDegree
        ticks.forEachIndexed { index, t ->
            val d = startDegree + range * t
            c.save()
            c.rotate(d + 90f, size * .5f, size * .5f)
            if (!tickRotation)
                c.rotate(-(d + 90f), size * .5f, initTickPadding + textPaint.textSize + padding.toFloat() + tickPadding.toFloat())

            var tick: CharSequence? = null
            if (onPrintTickLabel != null)
                tick = onPrintTickLabel!!.invoke(index, getSpeedAtDegree(d))
            // if (onPrintTickLabel is null or it returns null)
            if (tick == null)
                tick = "%.0f".format(locale, getSpeedAtDegree(d))

            c.translate(0f, initTickPadding + padding.toFloat() + tickPadding.toFloat())
            StaticLayout(tick, textPaint, size, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
                    .draw(c)

            c.restore()
        }
    }

    /**
     * change [indicator shape](https://github.com/anastr/SpeedView/wiki/Indicators).<br></br>
     * this method will get bach indicatorColor and indicatorWidth to default.
     * @param indicator new indicator (Enum value).
     */
    open fun setIndicator(indicator: Indicator.Indicators) {
        this.indicator = Indicator.createIndicator(context, this, indicator)
    }

    private fun checkTicks() {
        for (tick in ticks)
            require(!(tick < 0f || tick > 1f)) { "ticks must be between [0f, 1f] !!" }
    }

    private fun updateTranslated() {
        translatedDx = if (this.speedometerMode.isRight) -size * .5f + cutPadding else 0f
        translatedDy = if (this.speedometerMode.isBottom) -size * .5f + cutPadding else 0f
    }

    enum class Mode(internal val minDegree: Int, internal val maxDegree: Int, val isHalf: Boolean, internal val divWidth: Int, internal val divHeight: Int) {
        NORMAL(0, 360 * 2, false, 1, 1)
        , LEFT(90, 270, true, 2, 1)
        , TOP(180, 360, true, 1, 2)
        , RIGHT(270, 450, true, 2, 1)
        , BOTTOM(0, 180, true, 1, 2)
        , TOP_LEFT(180, 270, false, 1, 1)
        , TOP_RIGHT(270, 360, false, 1, 1)
        , BOTTOM_RIGHT(0, 90, false, 1, 1)
        , BOTTOM_LEFT(90, 180, false, 1, 1);

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

