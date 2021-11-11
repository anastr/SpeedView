package com.github.anastr.speedviewlib

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.github.anastr.speedviewlib.components.Section
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.util.OnSectionChangeListener
import com.github.anastr.speedviewlib.util.OnSpeedChangeListener
import com.github.anastr.speedviewlib.util.doOnSections
import java.util.*
import kotlin.math.abs
import kotlin.math.max


typealias SpeedTextListener = (speed: Float) -> CharSequence

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class Gauge constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), Observer {

    private val speedUnitTextBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    protected var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val speedTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val unitTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    /** Unit text, the text after speed text. */
    var unit = "Km/h"
        set(unit) {
            field = unit
            if (attachedToWindow)
                invalidate()
        }

    /**
     * Automatically increase and decrease speed value around the [speed]
     *
     * **If true** : the speed value automatically will be increases and decreases
     * by [trembleDegree] around last speed you set,
     * used to add some reality to the gauge.
     *
     * **If false** : nothing will happen.
     *
     * @see .setTrembleData
     */
    var withTremble = true
        set(withTremble) {
            field = withTremble
            tremble()
        }

    private var _minSpeed = 0f
    private var _maxSpeed = 100f

    /**
     * The minimum value of gauge, `default = 0`.
     *
     * Any change will move [currentSpeed] to its new position
     * immediately without animation.
     *
     * @throws IllegalArgumentException if `minSpeed >= maxSpeed`.
     */
    var minSpeed: Float
        get() = _minSpeed
        set(value) = setMinMaxSpeed(value, maxSpeed)

    /**
     * The maximum value of gauge, `default = 100`.
     *
     * any change will move [currentSpeed] to its new position
     * immediately without animation.
     *
     * @throws IllegalArgumentException if `minSpeed >= maxSpeed`.
     */
    var maxSpeed: Float
        get() = _maxSpeed
        set(value) = setMinMaxSpeed(minSpeed, value)

    /**
     * @return The last speed which you set by [speedTo]
     * or [speedTo] or [speedPercentTo],
     * or if you stop the gauge By [stop] method.
     *
     * @see currentSpeed
     */
    var speed = minSpeed
        private set

    /**
     * What speed is right now in **Integer**.
     * A safe way to handle all speed values in [onSpeedChangeListener].
     *
     * @return Current speed in Integer.
     * @see currentSpeed
     */
    var currentIntSpeed = 0
        private set

    /**
     * What speed is right now in **float**.
     * It will give different results if [withTremble] is running.
     *
     * @return Current speed.
     * @see withTremble
     * @see speed
     */
    var currentSpeed = minSpeed
        private set(value) {
            field = value
            checkSpeedIntChange()
            checkSectionChange()
        }

    /**
     * Given a state of speed changes if it's increase or decrease.
     * @return is speed increased in the last change or not.
     */
    var isSpeedIncrease = false
        private set

    /**
     * A degree to increases and decreases speed value around [speed].
     * Default : 4 speed value.
     * @throws IllegalArgumentException If trembleDegree is Negative.
     */
    var trembleDegree = 4f
        set(trembleDegree) {
            field = trembleDegree
            checkTrembleData()
        }
    /**
     * Tremble animation duration in millisecond.
     * Default : 1000 millisecond.
     * @throws IllegalArgumentException If trembleDuration is Negative.
     */
    var trembleDuration = 1000
        set(trembleDuration) {
            field = trembleDuration
            checkTrembleData()
        }

    private var speedAnimator: ValueAnimator? = null
    private var trembleAnimator: ValueAnimator? = null
    private var realSpeedAnimator: ValueAnimator? = null
    private var canceled = false
    /**
     * Register a callback to be invoked when speed value changed (in integer).
     * maybe null.
     */
    var onSpeedChangeListener: OnSpeedChangeListener? = null
    /**
     * Register a callback to be invoked when
     * [section](https://github.com/anastr/SpeedView/wiki/Usage#control-division-of-the-speedometer) changed.
     * maybe null.
     */
    var onSectionChangeListener: OnSectionChangeListener? = null
    /** This animatorListener to call [tremble] method when animator done  */
    private val animatorListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            if (!canceled)
                tremble()
        }
    }

    /** To contain all drawing that don't change with speed value. */
    protected var backgroundBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private val backgroundBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var padding = 0
        private set
    /**
     * View width without padding.
     * @return View width without padding.
     */
    var widthPa = 0
        private set
    /**
     * View height without padding.
     * @return View height without padding.
     */
    var heightPa = 0
        private set

    private val _sections = mutableListOf<Section>()
    /** All sections. */
    val sections : List<Section> get() = _sections
    var currentSection: Section? = null
        private set

    /**
     * The width of speedometer's bar in pixel.
     */
    open var speedometerWidth = dpTOpx(30f)
        set(speedometerWidth) {
            field = speedometerWidth
            doOnSections { it.width = speedometerWidth }
            if (isAttachedToWindow)
                invalidateGauge()
        }

    /**
     * To support right-to-left text.
     */
    var speedometerTextRightToLeft = false
        set(speedometerTextRightToLeft) {
            field = speedometerTextRightToLeft
            invalidateGauge()
        }

    private var attachedToWindow = false

    /**
     * @return Canvas translate dx.
     */
    protected var translatedDx = 0f
    /**
     * @return Canvas translate dy.
     */
    protected var translatedDy = 0f

    /**
     * An object to set text digits locale.
     *
     * Set Locale to localizing digits to the given locale,
     * for speed Text and speedometer Text.
     * `null` value means no localization.
     */
    var locale: Locale = Locale.getDefault()
        set(locale) {
            field = locale
            if (attachedToWindow)
                invalidate()
        }

    /**
     * Number expresses the Acceleration, between (0, 1]
     *
     * Change accelerate, used by [realSpeedTo] [speedUp]
     * and [slowDown] methods.
     *
     * Must be between `(0, 1]`, default value 0.1f.
     * @throws IllegalArgumentException If `accelerate` out of range.
     */
    var accelerate = .1f
        set(accelerate) {
            field = accelerate
            checkAccelerate()
        }

    /**
     * Number expresses the Deceleration, between (0, 1]
     *
     * Change decelerate, used by [realSpeedTo] [speedUp]
     * and [slowDown] methods.
     *
     * Must be between `(0, 1]`, default value 0.1f.
     * @throws IllegalArgumentException If `decelerate` out of range.
     */
    var decelerate = .1f
        set(decelerate) {
            field = decelerate
            checkDecelerate()
        }

    /**
     * Change position of speed and Unit Text.
     */
    var speedTextPosition = Position.BOTTOM_CENTER
        set(speedTextPosition) {
            field = speedTextPosition
            invalidateGauge()
        }

    /** Space between speedText and unitText in pixel.  */
    private var unitSpeedInterval = dpTOpx(1f)
        set(unitSpeedInterval) {
            field = unitSpeedInterval
            invalidateGauge()
        }

    /**
     * Speed-Unit Text padding in pixel,
     * this value will be ignored if `[speedTextPosition] == Position.CENTER`.
     */
    private var speedTextPadding = dpTOpx(20f)
        set(speedTextPadding) {
            field = speedTextPadding
            if (attachedToWindow)
                invalidate()
        }

    /**
     * To make Unit Text under Speed Text.
     *
     * If true: Drawing unit text **under** speed text.
     * IF false: Drawing unit text and speed text **side by side**.
     */
    var unitUnderSpeedText = false
        set(unitUnderSpeedText) {
            field = unitUnderSpeedText
            if (unitUnderSpeedText) {
                speedTextPaint.textAlign = Paint.Align.CENTER
                unitTextPaint.textAlign = Paint.Align.CENTER
            } else {
                speedTextPaint.textAlign = Paint.Align.LEFT
                unitTextPaint.textAlign = Paint.Align.LEFT
            }
            invalidateGauge()
        }

    // just initialize to avoid NullPointerException
    private var speedUnitTextBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private var speedUnitTextCanvas: Canvas? = null

    /**
     * To change speed text's format by custom text.
     */
    var speedTextListener: SpeedTextListener = { speed -> "%.1f".format(locale, speed) }
        set(speedTextFormat) {
            field = speedTextFormat
            invalidateGauge()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    private fun init() {
        textPaint.color = 0xFF000000.toInt()
        textPaint.textSize = dpTOpx(10f)
        textPaint.textAlign = Paint.Align.CENTER
        speedTextPaint.color = 0xFF000000.toInt()
        speedTextPaint.textSize = dpTOpx(18f)
        unitTextPaint.color = 0xFF000000.toInt()
        unitTextPaint.textSize = dpTOpx(15f)
        _sections.add(Section(0f, .6f, 0xFF00FF00.toInt(), speedometerWidth).inGauge(this))
        _sections.add(Section(.6f, .87f, 0xFFFFFF00.toInt(), speedometerWidth).inGauge(this))
        _sections.add(Section(.87f, 1f, 0xFFFF0000.toInt(), speedometerWidth).inGauge(this))

        defaultGaugeValues()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.Gauge, 0, 0)

        maxSpeed = a.getFloat(R.styleable.Gauge_sv_maxSpeed, maxSpeed)
        minSpeed = a.getFloat(R.styleable.Gauge_sv_minSpeed, minSpeed)
        speed = minSpeed
        currentSpeed = minSpeed
        speedometerWidth = a.getDimension(R.styleable.Gauge_sv_speedometerWidth, speedometerWidth)
        _sections.forEach { it.width = speedometerWidth }
        withTremble = a.getBoolean(R.styleable.Gauge_sv_withTremble, withTremble)
        textPaint.color = a.getColor(R.styleable.Gauge_sv_textColor, textPaint.color)
        textPaint.textSize = a.getDimension(R.styleable.Gauge_sv_textSize, textPaint.textSize)
        speedTextPaint.color = a.getColor(R.styleable.Gauge_sv_speedTextColor, speedTextPaint.color)
        speedTextPaint.textSize = a.getDimension(R.styleable.Gauge_sv_speedTextSize, speedTextPaint.textSize)
        unitTextPaint.color = a.getColor(R.styleable.Gauge_sv_unitTextColor, unitTextPaint.color)
        unitTextPaint.textSize = a.getDimension(R.styleable.Gauge_sv_unitTextSize, unitTextPaint.textSize)
        val unit = a.getString(R.styleable.Gauge_sv_unit)
        this.unit = unit ?: this.unit
        trembleDegree = a.getFloat(R.styleable.Gauge_sv_trembleDegree, trembleDegree)
        trembleDuration = a.getInt(R.styleable.Gauge_sv_trembleDuration, trembleDuration)
        speedometerTextRightToLeft = a.getBoolean(R.styleable.Gauge_sv_textRightToLeft, speedometerTextRightToLeft)
        accelerate = a.getFloat(R.styleable.Gauge_sv_accelerate, accelerate)
        decelerate = a.getFloat(R.styleable.Gauge_sv_decelerate, decelerate)
        unitUnderSpeedText = a.getBoolean(R.styleable.Gauge_sv_unitUnderSpeedText, unitUnderSpeedText)
        unitSpeedInterval = a.getDimension(R.styleable.Gauge_sv_unitSpeedInterval, unitSpeedInterval)
        speedTextPadding = a.getDimension(R.styleable.Gauge_sv_speedTextPadding, speedTextPadding)
        val speedTypefacePath = a.getString(R.styleable.Gauge_sv_speedTextTypeface)
        if (speedTypefacePath != null && !isInEditMode)
            speedTextTypeface = Typeface.createFromAsset(context.assets, speedTypefacePath)
        val typefacePath = a.getString(R.styleable.Gauge_sv_textTypeface)
        if (typefacePath != null && !isInEditMode)
            textTypeface = Typeface.createFromAsset(context.assets, typefacePath)
        val position = a.getInt(R.styleable.Gauge_sv_speedTextPosition, -1)
        if (position != -1)
            speedTextPosition = Position.values()[position]
        val speedFormat = a.getInt(R.styleable.Gauge_sv_speedTextFormat, -1)
        if (speedFormat == 0)
            speedTextListener = { speed -> "%.0f".format(locale, speed) }
        else if (speedFormat == 1)
            speedTextListener = { speed -> "%.1f".format(locale, speed) }
        a.recycle()
        checkAccelerate()
        checkDecelerate()
        checkTrembleData()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        if (widthPa > 0 && heightPa > 0)
            speedUnitTextBitmap = Bitmap.createBitmap(widthPa, heightPa, Bitmap.Config.ARGB_8888)
        speedUnitTextCanvas = Canvas(speedUnitTextBitmap)
    }

    private fun checkAccelerate() {
        require(!(accelerate > 1f || accelerate <= 0)) { "accelerate must be between (0, 1]" }
    }

    private fun checkDecelerate() {
        require(!(decelerate > 1f || decelerate <= 0)) { "decelerate must be between (0, 1]" }
    }

    private fun checkTrembleData() {
        require(trembleDegree >= 0) { "trembleDegree  can't be Negative" }
        require(trembleDuration >= 0) { "trembleDuration  can't be Negative" }
    }

    internal fun checkSection(section: Section) {
        val i = _sections.indexOf(section)
        require(section.startOffset < section.endOffset) { "endOffset must be bigger than startOffset" }
        _sections.getOrNull(i-1)?.let {
            require(it.endOffset <= section.startOffset
                    && it.endOffset < section.endOffset) { "Section at index ($i) is conflicted with previous section" }
        }
        _sections.getOrNull(i+1)?.let {
            require(it.startOffset >= section.endOffset
                    && it.startOffset > section.startOffset) { "Section at index ($i) is conflicted with next section" }
        }
    }

    /**
     * Convert dp to **pixel**.
     * @param dp To convert.
     * @return Dimension in pixel.
     */
    fun dpTOpx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * Convert pixel to **dp**.
     * @param px To convert.
     * @return Dimension in dp.
     */
    fun pxTOdp(px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    /**
     * Add default values for the Gauge inside this method,
     * call super setting method to set default value,
     * Ex : `super.setBackgroundCircleColor(Color.TRANSPARENT)`
     */
    protected abstract fun defaultGaugeValues()

    /**
     * Notify to update [backgroundBitmap].
     */
    protected abstract fun updateBackgroundBitmap()

    /**
     * Notify that padding or size have been changed.
     */
    private fun updatePadding(left: Int, top: Int, right: Int, bottom: Int) {
        padding = max(max(left, right), max(top, bottom))
        widthPa = width - padding * 2
        heightPa = height - padding * 2
    }

    /**
     * Speed-unit text position and size.
     * @return speed-unit's rect.
     */
    protected fun getSpeedUnitTextBounds(): RectF {
        val left = widthPa * speedTextPosition.x - translatedDx + padding -
                getSpeedUnitTextWidth() * speedTextPosition.width + speedTextPadding * speedTextPosition.paddingH
        val top = heightPa * speedTextPosition.y - translatedDy + padding -
                getSpeedUnitTextHeight() * speedTextPosition.height + speedTextPadding * speedTextPosition.paddingV
        return RectF(left, top, left + getSpeedUnitTextWidth(), top + getSpeedUnitTextHeight())
    }

    /**
     * @return The width of speed-unit text at runtime.
     */
    private fun getSpeedUnitTextWidth(): Float =
            if (unitUnderSpeedText)
                max(speedTextPaint.measureText(getSpeedText().toString()), unitTextPaint.measureText(unit))
            else
                speedTextPaint.measureText(getSpeedText().toString()) + unitTextPaint.measureText(unit) + unitSpeedInterval

    /**
     * @return The height of speed-unit text at runtime.
     */
    private fun getSpeedUnitTextHeight(): Float =
            if (unitUnderSpeedText)
                speedTextPaint.textSize + unitTextPaint.textSize + unitSpeedInterval
            else
                max(speedTextPaint.textSize, unitTextPaint.textSize)

    /**
     * Get current speed as string to **Draw**.
     */
    protected fun getSpeedText() = speedTextListener.invoke(currentSpeed)

    /**
     * Get current speed as **percent**.
     * @return Percent speed, between [0,100].
     */
    fun getPercentSpeed(): Float = (currentSpeed - minSpeed) * 100f / (maxSpeed - minSpeed)

    /**
     * @return Offset speed, between [0,1].
     */
    fun getOffsetSpeed(): Float = (currentSpeed - minSpeed) / (maxSpeed - minSpeed)

    /**
     * Change all text color without **speed, unit text**.
     *
     * @see speedTextColor
     * @see unitTextColor
     */
    var textColor: Int
        get() = textPaint.color
        set(textColor) {
            textPaint.color = textColor
            invalidateGauge()
        }

    /**
     * Change just speed text color.
     *
     * @see unitTextColor
     * @see textColor
     */
    var speedTextColor: Int
        get() = speedTextPaint.color
        set(speedTextColor) {
            speedTextPaint.color = speedTextColor
            if (attachedToWindow)
                invalidate()
        }

    /**
     * Change just unit text color.
     *
     * @see speedTextColor
     * @see textColor
     */
    var unitTextColor: Int
        get() = unitTextPaint.color
        set(unitTextColor) {
            unitTextPaint.color = unitTextColor
            if (attachedToWindow)
                invalidate()
        }

    /**
     * Change all text size without **speed and unit text**.
     *
     * @see dpTOpx
     * @see speedTextSize
     * @see unitTextSize
     */
    var textSize: Float
        get() = textPaint.textSize
        set(textSize) {
            textPaint.textSize = textSize
            if (attachedToWindow)
                invalidate()
        }

    /**
     * Change just speed text size.
     *
     * @see dpTOpx
     * @see textSize
     * @see unitTextSize
     */
    var speedTextSize: Float
        get() = speedTextPaint.textSize
        set(speedTextSize) {
            speedTextPaint.textSize = speedTextSize
            if (attachedToWindow)
                invalidate()
        }

    /**
     * Change just unit text size.
     *
     * @see dpTOpx
     * @see speedTextSize
     * @see textSize
     */
    var unitTextSize: Float
        get() = unitTextPaint.textSize
        set(unitTextSize) {
            unitTextPaint.textSize = unitTextSize
            invalidateGauge()
        }

    val viewSize: Int
        get() = max(width, height)

    val viewSizePa: Int
        get() = max(widthPa, heightPa)

    /**
     * Maybe null, change typeface for **speed and unit** text.
     */
    var speedTextTypeface: Typeface?
        get() = speedTextPaint.typeface
        set(typeface) {
            speedTextPaint.typeface = typeface
            unitTextPaint.typeface = typeface
            invalidateGauge()
        }

    /**
     * Maybe null, change typeface for all texts without speed and unit text.
     */
    var textTypeface: Typeface?
        get() = textPaint.typeface
        set(typeface) {
            textPaint.typeface = typeface
            invalidateGauge()
        }

    internal fun checkSpeedIntChange() {
        // check onSpeedChangeEvent.
        val newSpeed = currentSpeed.toInt()
        if (newSpeed != currentIntSpeed && onSpeedChangeListener != null) {
            val byTremble = trembleAnimator?.isRunning == true
            val isSpeedUp = newSpeed > currentIntSpeed
            val update = if (isSpeedUp) 1 else -1
            // this loop to pass on all speed values,
            // to safe handle by call gauge.getCorrectIntSpeed().
            while (currentIntSpeed != newSpeed) {
                currentIntSpeed += update
                onSpeedChangeListener!!.invoke(this, isSpeedUp, byTremble)
            }
        }
        currentIntSpeed = newSpeed
    }

    internal fun checkSectionChange() {
        // check onSectionChangeEvent.
        val newSection = findSection()
        if (currentSection !== newSection) {
            onSectionChangeEvent(currentSection, newSection)
            currentSection = newSection
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(translatedDx, translatedDy)

        canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundBitmapPaint)
    }

    /**
     * Draw speed and unit text at [speedTextPosition],
     * this method must be called in subSpeedometer's `onDraw` method.
     * @param canvas View canvas to draw.
     */
    protected fun drawSpeedUnitText(canvas: Canvas) {
        val r = getSpeedUnitTextBounds()
        updateSpeedUnitTextBitmap(getSpeedText().toString())
        canvas.drawBitmap(speedUnitTextBitmap, r.left - speedUnitTextBitmap.width * .5f + r.width() * .5f
                , r.top - speedUnitTextBitmap.height * .5f + r.height() * .5f, speedUnitTextBitmapPaint)
    }

    /**
     * Clear [speedUnitTextBitmap] and draw speed and unit text.
     *
     * Taking into consideration [speedometerTextRightToLeft] and [unitUnderSpeedText].
     */
    private fun updateSpeedUnitTextBitmap(speedText: String) {
        speedUnitTextBitmap.eraseColor(0)

        if (unitUnderSpeedText) {
            speedUnitTextCanvas?.drawText(speedText, speedUnitTextBitmap.width * .5f, speedUnitTextBitmap.height * .5f - unitSpeedInterval * .5f, speedTextPaint)
            speedUnitTextCanvas?.drawText(unit, speedUnitTextBitmap.width * .5f, speedUnitTextBitmap.height * .5f + unitTextPaint.textSize + unitSpeedInterval * .5f, unitTextPaint)
        } else {
            val speedX: Float
            val unitX: Float
            if (speedometerTextRightToLeft) {
                unitX = speedUnitTextBitmap.width * .5f - getSpeedUnitTextWidth() * .5f
                speedX = unitX + unitTextPaint.measureText(unit) + unitSpeedInterval
            } else {
                speedX = speedUnitTextBitmap.width * .5f - getSpeedUnitTextWidth() * .5f
                unitX = speedX + speedTextPaint.measureText(speedText) + unitSpeedInterval
            }
            val h = speedUnitTextBitmap.height * .5f + getSpeedUnitTextHeight() * .5f
            speedUnitTextCanvas?.drawText(speedText, speedX, h, speedTextPaint)
            speedUnitTextCanvas?.drawText(unit, unitX, h, unitTextPaint)
        }
    }

    /**
     * Create canvas to draw [backgroundBitmap].
     * @return [backgroundBitmap]'s canvas.
     */
    protected open fun createBackgroundBitmapCanvas(): Canvas {
        if (width == 0 || height == 0)
            return Canvas()
        backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        return Canvas(backgroundBitmap)
    }

    /**
     * Implement this method to handle section change event.
     * @param previousSection Where speed value came from.
     * @param newSection Where speed value move to.
     */
    protected fun onSectionChangeEvent(previousSection: Section?, newSection: Section?) {
        onSectionChangeListener?.invoke(previousSection, newSection)
    }

    /**
     * Stop speedometer and run tremble if [withTremble] is true.
     * Use this method just when you wont to stop [speedTo] and [realSpeedTo].
     */
    fun stop() {
        if (speedAnimator?.isRunning == false && realSpeedAnimator?.isRunning == false)
            return
        speed = currentSpeed
        cancelSpeedAnimator()
        tremble()
    }

    /**
     * Cancel all animators without calling [tremble].
     */
    protected fun cancelSpeedAnimator() {
        cancelSpeedMove()
        cancelTremble()
    }

    private fun cancelTremble() {
        canceled = true
        trembleAnimator?.cancel()
        canceled = false
        trembleAnimator = null
    }

    private fun cancelSpeedMove() {
        canceled = true
        speedAnimator?.cancel()
        realSpeedAnimator?.cancel()
        canceled = false
    }

    /**
     * Move speed value to new speed without animation.
     * @param speed Current speed to move.
     */
    fun setSpeedAt(speed: Float) {
        var newSpeed = speed
        newSpeed = if (newSpeed > maxSpeed) maxSpeed else if (newSpeed < minSpeed) minSpeed else newSpeed
        isSpeedIncrease = newSpeed > currentSpeed
        this.speed = newSpeed
        this.currentSpeed = newSpeed
        cancelSpeedAnimator()
        invalidate()
        tremble()
    }

    /**
     * Move speed to percent value.
     * @param percent Percent value to move, must be between [0,100].
     * @param moveDuration The length of the animation, in milliseconds.
     * This value cannot be negative.
     *
     * @see speedTo
     * @see speedPercentTo
     * @see realSpeedTo
     */
    @JvmOverloads
    fun speedPercentTo(percent: Int, moveDuration: Long = 2000) {
        speedTo(getSpeedValue(percent.toFloat()), moveDuration)
    }

    /**
     * Move speed to current value smoothly with animation,
     * it should be between [[minSpeed], [maxSpeed]].
     *
     * If `speed > maxSpeed` speed value will move to [maxSpeed].
     *
     * If `speed < minSpeed` speed value will move to [minSpeed].
     *
     * @param speed Current speed to move.
     * @param moveDuration The length of animation, in milliseconds.
     * This value cannot be negative (2000 by default).
     *
     * @see speedTo
     * @see speedPercentTo
     * @see realSpeedTo
     */
    @JvmOverloads
    fun speedTo(speed: Float, moveDuration: Long = 2000) {
        var newSpeed = speed
        newSpeed = if (newSpeed > maxSpeed) maxSpeed else if (newSpeed < minSpeed) minSpeed else newSpeed
        if (newSpeed == this.speed)
            return
        this.speed = newSpeed

        isSpeedIncrease = newSpeed > currentSpeed

        cancelSpeedAnimator()
        speedAnimator = ValueAnimator.ofFloat(currentSpeed, newSpeed).apply {
            interpolator = DecelerateInterpolator()
            duration = moveDuration
            addUpdateListener { animation ->
                currentSpeed = animation.animatedValue as Float
                postInvalidate()
            }
            addListener(animatorListener)
        }
        speedAnimator?.start()
    }

    /**
     * This method use [realSpeedTo] to speed up
     * the speedometer to [maxSpeed].
     *
     * @see realSpeedTo
     * @see slowDown
     */
    fun speedUp() {
        realSpeedTo(maxSpeed)
    }

    /**
     * This method use [realSpeedTo] to slow down
     * the speedometer to [minSpeed].
     *
     * @see realSpeedTo
     * @see speedUp
     */
    fun slowDown() {
        realSpeedTo(0f)
    }

    /**
     * Move speed to percent value by using [realSpeedTo] method.
     * @param percent percent value to move, must be between [0,100].
     */
    fun realSpeedPercentTo(percent: Float) {
        realSpeedTo(getSpeedValue(percent))
    }

    /**
     * To make speedometer some real.
     *
     * When **speed up** : speed value will increase *slowly* by [accelerate].
     *
     * When **slow down** : speed value will decrease *rapidly* by [decelerate].
     * @param speed current speed to move.
     *
     * @see speedTo
     * @see speedPercentTo
     * @see speedUp
     * @see slowDown
     */
    fun realSpeedTo(speed: Float) {
        var newSpeed = speed
        val oldIsSpeedUp = this.speed > currentSpeed
        newSpeed = if (newSpeed > maxSpeed) maxSpeed else if (newSpeed < minSpeed) minSpeed else newSpeed
        if (newSpeed == this.speed)
            return
        this.speed = newSpeed

        isSpeedIncrease = newSpeed > currentSpeed
        if (realSpeedAnimator?.isRunning == true && oldIsSpeedUp == isSpeedIncrease)
            return

        cancelSpeedAnimator()
        realSpeedAnimator = ValueAnimator.ofInt(currentSpeed.toInt(), newSpeed.toInt()).apply {
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = abs(((newSpeed - currentSpeed) * 10).toLong())
            val finalSpeed = newSpeed
            addUpdateListener {
                if (isSpeedIncrease) {
                    val per = 100.005f - getPercentSpeed()
                    currentSpeed += accelerate * 10f * per * .01f
                    if (currentSpeed > finalSpeed)
                        currentSpeed = finalSpeed
                } else {
                    val per = getPercentSpeed() + .005f
                    currentSpeed -= decelerate * 10f * per * .01f + .1f
                    if (currentSpeed < finalSpeed)
                        currentSpeed = finalSpeed
                }
                postInvalidate()
                if (finalSpeed == currentSpeed)
                    stop()
            }
            addListener(animatorListener)
        }
        realSpeedAnimator?.start()
    }

    /**
     * Check if [withTremble] true, and run tremble.
     */
    protected fun tremble() {
        cancelTremble()
        if (!withTremble)
            return
        val random = Random()
        var mad = trembleDegree * random.nextFloat() * (if (random.nextBoolean()) -1 else 1).toFloat()
        mad = when {
            speed + mad > maxSpeed -> maxSpeed - speed
            speed + mad < minSpeed -> minSpeed - speed
            else -> mad
        }
        trembleAnimator = ValueAnimator.ofFloat(currentSpeed, speed + mad).apply {
            interpolator = DecelerateInterpolator()
            duration = trembleDuration.toLong()
            addUpdateListener { animation ->
                isSpeedIncrease = animation.animatedValue as Float > currentSpeed
                currentSpeed = animation.animatedValue as Float
                postInvalidate()
            }
            addListener(animatorListener)
        }
        trembleAnimator?.start()
    }

    /**
     * @param percentSpeed Between [0, 100].
     * @return Speed value at current percentSpeed.
     */
    private fun getSpeedValue(percentSpeed: Float): Float = when {
        percentSpeed > 100f -> maxSpeed
        percentSpeed < 0f -> minSpeed
        else -> percentSpeed * (maxSpeed - minSpeed) * .01f + minSpeed
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (speedAnimator?.isRunning == false && realSpeedAnimator?.isRunning == false) {
            if (isVisible) tremble() else cancelTremble()
        }
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (trembleAnimator?.isRunning == true) {
            cancelTremble()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
        if (!isInEditMode) {
            updateBackgroundBitmap()
            invalidate()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelSpeedAnimator()
        attachedToWindow = false
    }

//    override fun onSaveInstanceState(): Parcelable? {
//        val bundle = Bundle()
//        bundle.putParcelable("superState", super.onSaveInstanceState())
//        bundle.putFloat("speed", speed)
//        bundle.putParcelableArrayList("sections", sections)
//        return bundle
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable?) {
//        val bundle = state as Bundle
//        speed = bundle.getFloat("speed")
//        sections.clear()
//        addSections(bundle.getParcelableArrayList("sections")!!)
//        super.onRestoreInstanceState(bundle.getParcelable("superState"))
//        setSpeedAt(speed)
//    }

    /**
     * Tremble control.
     * @param trembleDegree A speed value to increases and decreases current [speed] around.
     * @param trembleDuration Tremble Animation duration in millisecond.
     *
     * @throws IllegalArgumentException If trembleDegree or trembleDuration is negative.
     */
    fun setTrembleData(trembleDegree: Float, trembleDuration: Int) {
        this.trembleDegree = trembleDegree
        this.trembleDuration = trembleDuration
    }

    /**
     * Change [minSpeed] and [maxSpeed].
     *
     * This method will move [currentSpeed] to its new position
     * immediately without animation.
     *
     * @param minSpeed New min speed value.
     * @param maxSpeed New max speed value.
     *
     * @throws IllegalArgumentException If `minSpeed >= maxSpeed`
     */
    fun setMinMaxSpeed(minSpeed: Float, maxSpeed: Float) {
        require(minSpeed < maxSpeed) { "minSpeed must be smaller than maxSpeed !!" }
        cancelSpeedAnimator()
        _minSpeed = minSpeed
        _maxSpeed = maxSpeed
        checkSectionChange()
        invalidateGauge()
        if (attachedToWindow)
            setSpeedAt(speed)
    }

    /**
     * Add list of sections to the gauge.
     * @throws IllegalArgumentException If [Section.startOffset] or [Section.endOffset] are invalid.
     */
    fun addSections(vararg sections: Section) {
        addSections(sections.asList())
    }

    /**
     * Add list of sections to the gauge.
     * @throws IllegalArgumentException If [Section.startOffset] or [Section.endOffset] are invalid.
     */
    fun addSections(sections: List<Section>) {
        sections.forEach {
            this._sections.add(it.inGauge(this))
            checkSection(it)
        }
        invalidateGauge()
    }

    /**
     * Clear old [sections],
     * and add [numberOfSections] equal to each others.
     */
    fun makeSections(numberOfSections: Int, color: Int, style: Style) {
        _sections.forEach { it.clearGauge() }
        _sections.clear()
        var prevPart = 0f
        var part = 1f / numberOfSections
        for (i in 0 until numberOfSections) {
            _sections.add(Section(prevPart, part, color, speedometerWidth, style).inGauge(this))
            prevPart = part
            part += (1f / numberOfSections)
        }
        invalidateGauge()
    }

    /**
     * Remove section from this gauge.
     */
    fun removeSection(section: Section?) {
        section?.clearGauge()
        _sections.remove(section)
        invalidateGauge()
    }

    /**
     * Remove all sections.
     */
    fun clearSections() {
        _sections.forEach { it.clearGauge() }
        _sections.clear()
        invalidateGauge()
    }

    /**
     * Notification that an section has changed.
     */
    override fun update(section: Observable?, isPercentChanged: Any?) {
        invalidateGauge()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        updatePadding(left, top, right, bottom)
        super.setPadding(padding, padding, padding, padding)
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        updatePadding(start, top, end, bottom)
        super.setPaddingRelative(padding, padding, padding, padding)
    }

    /**
     * @return Calculate current section.
     */
    private fun findSection(): Section? {
        _sections.forEach {
            if ((maxSpeed - minSpeed) * it.startOffset + minSpeed <= currentSpeed
                    && (maxSpeed - minSpeed) * it.endOffset + minSpeed >= currentSpeed)
                return it
        }
        return null
    }

    /**
     * @return Whether this view attached to layout or not.
     */
    override fun isAttachedToWindow(): Boolean {
        return attachedToWindow
    }

    /**
     * Redraw the Gauge.
     */
    fun invalidateGauge() {
        if (attachedToWindow) {
            updateBackgroundBitmap()
            invalidate()
        }
    }

    /**
     * Position of speed-unit text.
     */
    enum class Position constructor(internal val x: Float, internal val y: Float
                                    , internal val width: Float, internal val height: Float
                                    , internal val paddingH: Int // horizontal padding
                                    , internal val paddingV: Int // vertical padding
    ) {
        TOP_LEFT     (0f, 0f, 0f, 0f, 1, 1),
        TOP_CENTER   (.5f, 0f, .5f, 0f, 0, 1),
        TOP_RIGHT    (1f, 0f, 1f, 0f, -1, 1),
        LEFT         (0f, .5f, 0f, .5f, 1, 0),
        CENTER       (.5f, .5f, .5f, .5f, 0, 0),
        RIGHT        (1f, .5f, 1f, .5f, -1, 0),
        BOTTOM_LEFT  (0f, 1f, 0f, 1f, 1, -1),
        BOTTOM_CENTER(.5f, 1f, .5f, 1f, 0, -1),
        BOTTOM_RIGHT (1f, 1f, 1f, 1f, -1, -1)
    }
}
