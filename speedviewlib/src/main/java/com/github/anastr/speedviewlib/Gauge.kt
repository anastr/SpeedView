package com.github.anastr.speedviewlib

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.github.anastr.speedviewlib.util.OnSectionChangeListener
import com.github.anastr.speedviewlib.util.OnSpeedChangeListener
import java.util.*

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
abstract class Gauge constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val speedUnitTextBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    protected var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val speedTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val unitTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    /** the text after speedText  */
    private var unit = "Km/h"

    /**
     * automatically increase and decrease speed value around the [speed]
     *
     * **if true** : the speed value automatically will be increases and decreases
     * by [trembleDegree] around last speed you set,
     * used to add some reality to speedometer.<br></br>
     * **if false** : nothing will done.
     *
     * @see .setTrembleData
     */
    var withTremble = true
        set(withTremble) {
            field = withTremble
            tremble()
        }

    /** the max range in speedometer, `default = 100`  */
    private var maxSpeed = 100f
    /** the min range in speedometer, `default = 0`  */
    private var minSpeed = 0f

    /**
     * @return the last speed which you set by [speedTo]
     * or [speedTo] or [speedPercentTo],
     * or if you stop speedometer By [stop] method.
     *
     * @see currentSpeed
     */
    var speed = minSpeed
        private set

    /**
     * what is speed now in **integer**.
     * safe method to handle all speed values in [onSpeedChangeListener].
     *
     * @return current speed in Integer
     * @see currentSpeed
     */
    var currentIntSpeed = 0
        private set

    /**
     * what is speed now in **float**.
     * It will give different results if [withTremble] is running.
     *
     * @return current speed now.
     * @see withTremble
     * @see speed
     */
    var currentSpeed = minSpeed
        private set

    /**
     * given a state of the speed change if it's increase or decrease.
     * @return is speed increase in the last change or not.
     */
    var isSpeedIncrease = false
        private set

    /**
     * a degree to increases and decreases speed value around [speed]
     * default : 4 speed value.
     * @throws IllegalArgumentException If trembleDegree is Negative.
     */
    var trembleDegree = 4f
        set(trembleDegree) {
            setTrembleData(trembleDegree, trembleDuration)
        }
    /**
     * tremble Animation duration in millisecond.
     * default : 1000 millisecond.
     * @throws IllegalArgumentException If trembleDuration is Negative.
     */
    var trembleDuration = 1000
        set(trembleDuration) {
            setTrembleData(trembleDegree, trembleDuration)
        }

    private lateinit var speedAnimator: ValueAnimator
    private lateinit var trembleAnimator: ValueAnimator
    private lateinit var realSpeedAnimator: ValueAnimator
    private var canceled = false
    private var onSpeedChangeListener: OnSpeedChangeListener? = null
    private var onSectionChangeListener: OnSectionChangeListener? = null
    /** this animatorListener to call [.tremble] method when animator done  */
    private lateinit var animatorListener: Animator.AnimatorListener

    /** to contain all drawing that doesn't change  */
    protected var backgroundBitmap: Bitmap? = null
    private val backgroundBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var padding = 0
        private set
    /**
     * view width without padding
     * @return View width without padding.
     */
    var widthPa = 0
        private set
    /**
     * View height without padding
     * @return View height without padding.
     */
    var heightPa = 0
        private set

    /** low speed area  */
    private var lowSpeedPercent = 60
    /** medium speed area  */
    private var mediumSpeedPercent = 87
    private var section = LOW_SECTION

    /**
     * to support Right To Left Text.
     */
    var speedometerTextRightToLeft = false
        set(speedometerTextRightToLeft) {
            field = speedometerTextRightToLeft
            if (!attachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    private var attachedToWindow = false

    /**
     * @return canvas translate dx.
     */
    protected var translatedDx = 0f
    /**
     * @return canvas translate dy.
     */
    protected var translatedDy = 0f

    /**
     * object to set text digits locale.
     *
     * set Locale to localizing digits to the given locale,
     * for speed Text and speedometer Text.
     * `null` value means no localization.
     */
    var locale: Locale = Locale.getDefault()
        set(locale) {
            field = locale
            if (!attachedToWindow)
                return
            invalidate()
        }

    /**
     * Number expresses the Acceleration, between (0, 1]
     *
     * change accelerate, used by [realSpeedTo] [speedUp]
     * and [slowDown] methods.<br></br>
     * must be between `(0, 1]`, default value 0.1f.
     * @throws IllegalArgumentException if `accelerate` out of range.
     */
    var accelerate = .1f
        set(accelerate) {
            field = accelerate
            checkAccelerate()
        }

    /**
     * Number expresses the Deceleration, between (0, 1]
     *
     * change decelerate, used by [realSpeedTo] [speedUp]
     * and [slowDown] methods.<br></br>
     * must be between `(0, 1]`, default value 0.1f.
     * @throws IllegalArgumentException if `decelerate` out of range.
     */
    var decelerate = .1f
        set(decelerate) {
            field = decelerate
            checkDecelerate()
        }

    private var speedTextPosition = Position.BOTTOM_CENTER
    /** space between unitText and speedText  */
    private var unitSpeedInterval = dpTOpx(1f)
    private var speedTextPadding = dpTOpx(20f)

    /**
     * to make Unit Text under Speed Text.
     *
     * if true: drawing unit text **under** speed text.
     * false: drawing unit text and speed text **side by side**.
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
            if (!attachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    // just initialize to avoid NullPointerException
    private var speedUnitTextBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private var speedUnitTextCanvas: Canvas? = null

    /**
     * number of decimal places
     *
     * change speed text's format [[INTEGER_FORMAT] or [FLOAT_FORMAT]]
     * or number of decimal places you want.
     */
    var speedTextFormat = FLOAT_FORMAT.toInt()
        set(speedTextFormat) {
            field = speedTextFormat
            if (!attachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    /**
     * number of decimal places
     *
     * change tick text's format [[INTEGER_FORMAT] or [FLOAT_FORMAT]]
     * or number of decimal places you want.
     */
    var tickTextFormat = INTEGER_FORMAT.toInt()
        set(tickTextFormat) {
            field = tickTextFormat
            if (!attachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    private fun init() {
        textPaint.color = -0x1000000
        textPaint.textSize = dpTOpx(10f)
        textPaint.textAlign = Paint.Align.CENTER
        speedTextPaint.color = -0x1000000
        speedTextPaint.textSize = dpTOpx(18f)
        unitTextPaint.color = -0x1000000
        unitTextPaint.textSize = dpTOpx(15f)

        if (Build.VERSION.SDK_INT >= 11) {
            speedAnimator = ValueAnimator.ofFloat(0f, 1f)
            trembleAnimator = ValueAnimator.ofFloat(0f, 1f)
            realSpeedAnimator = ValueAnimator.ofFloat(0f, 1f)
            animatorListener = object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    if (!canceled)
                        tremble()
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            }
        }
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
        lowSpeedPercent = a.getInt(R.styleable.Gauge_sv_lowSpeedPercent, lowSpeedPercent)
        mediumSpeedPercent = a.getInt(R.styleable.Gauge_sv_mediumSpeedPercent, mediumSpeedPercent)
        speedometerTextRightToLeft = a.getBoolean(R.styleable.Gauge_sv_textRightToLeft, speedometerTextRightToLeft)
        accelerate = a.getFloat(R.styleable.Gauge_sv_accelerate, accelerate)
        decelerate = a.getFloat(R.styleable.Gauge_sv_decelerate, decelerate)
        unitUnderSpeedText = a.getBoolean(R.styleable.Gauge_sv_unitUnderSpeedText, unitUnderSpeedText)
        unitSpeedInterval = a.getDimension(R.styleable.Gauge_sv_unitSpeedInterval, unitSpeedInterval)
        speedTextPadding = a.getDimension(R.styleable.Gauge_sv_speedTextPadding, speedTextPadding)
        val speedTypefacePath = a.getString(R.styleable.Gauge_sv_speedTextTypeface)
        if (speedTypefacePath != null)
            speedTextTypeface = Typeface.createFromAsset(getContext().assets, speedTypefacePath)
        val typefacePath = a.getString(R.styleable.Gauge_sv_textTypeface)
        if (typefacePath != null)
            textTypeface = Typeface.createFromAsset(getContext().assets, typefacePath)
        val position = a.getInt(R.styleable.Gauge_sv_speedTextPosition, -1)
        if (position != -1)
            setSpeedTextPosition(Position.values()[position])
        val speedFormat = a.getInt(R.styleable.Gauge_sv_speedTextFormat, -1)
        if (speedFormat != -1)
            speedTextFormat = speedFormat
        val tickFormat = a.getInt(R.styleable.Gauge_sv_tickTextFormat, -1)
        if (tickFormat != -1)
            tickTextFormat = tickFormat
        a.recycle()
        checkSpeedometerPercent()
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

    private fun checkSpeedometerPercent() {
        if (lowSpeedPercent > mediumSpeedPercent)
            throw IllegalArgumentException("lowSpeedPercent must be smaller than mediumSpeedPercent")
        if (lowSpeedPercent > 100 || lowSpeedPercent < 0)
            throw IllegalArgumentException("lowSpeedPercent must be between [0, 100]")
        if (mediumSpeedPercent > 100 || mediumSpeedPercent < 0)
            throw IllegalArgumentException("mediumSpeedPercent must be between [0, 100]")
    }

    private fun checkAccelerate() {
        if (accelerate > 1f || accelerate <= 0)
            throw IllegalArgumentException("accelerate must be between (0, 1]")
    }

    private fun checkDecelerate() {
        if (decelerate > 1f || decelerate <= 0)
            throw IllegalArgumentException("decelerate must be between (0, 1]")
    }

    private fun checkTrembleData() {
        if (trembleDegree < 0)
            throw IllegalArgumentException("trembleDegree  can't be Negative")
        if (trembleDuration < 0)
            throw IllegalArgumentException("trembleDuration  can't be Negative")
    }

    /**
     * convert dp to **pixel**.
     * @param dp to convert.
     * @return Dimension in pixel.
     */
    fun dpTOpx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * convert pixel to **dp**.
     * @param px to convert.
     * @return Dimension in dp.
     */
    fun pxTOdp(px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    /**
     * add default values for Gauge inside this method,
     * call super setting method to set default value,
     * Ex : `super.setBackgroundCircleColor(Color.TRANSPARENT);`
     */
    protected abstract fun defaultGaugeValues()

    /**
     * notice that [backgroundBitmap] must recreate.
     */
    protected abstract fun updateBackgroundBitmap()

    /**
     * notice that padding or size have changed.
     */
    private fun updatePadding(left: Int, top: Int, right: Int, bottom: Int) {
        padding = Math.max(Math.max(left, right), Math.max(top, bottom))
        widthPa = width - padding * 2
        heightPa = height - padding * 2
    }

    /**
     * speed-unit text position and size.
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
     * @return the width of speed & unit text at runtime.
     */
    private fun getSpeedUnitTextWidth(): Float =
        if (unitUnderSpeedText)
            Math.max(speedTextPaint.measureText(getSpeedText()), unitTextPaint.measureText(getUnit()))
        else
            speedTextPaint.measureText(getSpeedText()) + unitTextPaint.measureText(getUnit()) + unitSpeedInterval

    /**
     * @return the height of speed & unit text at runtime.
     */
    private fun getSpeedUnitTextHeight(): Float =
        if (unitUnderSpeedText)
            speedTextPaint.textSize + unitTextPaint.textSize + unitSpeedInterval
        else
            Math.max(speedTextPaint.textSize, unitTextPaint.textSize)

    /**
     * get current speed as string to **Draw**.
     */
    protected fun getSpeedText() = "%.${speedTextFormat}f".format(locale, currentSpeed)

    /**
     * get Max speed as string to **Draw**.
     */
    protected fun getMaxSpeedText() = "%.${tickTextFormat}f".format(locale, maxSpeed)

    /**
     * get Min speed as string to **Draw**.
     */
    protected fun getMinSpeedText() = "%.${tickTextFormat}f".format(locale, minSpeed)

    /**
     * get current speed as **percent**.
     * @return percent speed, between [0,100].
     */
    fun getPercentSpeed(): Float = (currentSpeed - minSpeed) * 100f / (maxSpeed - minSpeed)

    /**
     * @return offset speed, between [0,1].
     */
    fun getOffsetSpeed(): Float = (currentSpeed - minSpeed) / (maxSpeed - minSpeed)

    /**
     * change all text color without **speed, unit text**.
     *
     * @see speedTextColor
     * @see unitTextColor
     */
    var textColor: Int
        get() = textPaint.color
        set(textColor) {
            textPaint.color = textColor
            if (!attachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    /**
     * change just speed text color.
     *
     * @see unitTextColor
     * @see textColor
     */
    var speedTextColor: Int
        get() = speedTextPaint.color
        set(speedTextColor) {
            speedTextPaint.color = speedTextColor
            if (!attachedToWindow)
                return
            invalidate()
        }

    /**
     * change just unit text color.
     *
     * @see speedTextColor
     * @see textColor
     */
    var unitTextColor: Int
        get() = unitTextPaint.color
        set(unitTextColor) {
            unitTextPaint.color = unitTextColor
            if (!attachedToWindow)
                return
            invalidate()
        }

    /**
     * change all text size without **speed and unit text**.
     *
     * @see dpTOpx
     * @see speedTextSize
     * @see unitTextSize
     */
    var textSize: Float
        get() = textPaint.textSize
        set(textSize) {
            textPaint.textSize = textSize
            if (!attachedToWindow)
                return
            invalidate()
        }

    /**
     * change just speed text size.
     *
     * @see dpTOpx
     * @see textSize
     * @see unitTextSize
     */
    var speedTextSize: Float
        get() = speedTextPaint.textSize
        set(speedTextSize) {
            speedTextPaint.textSize = speedTextSize
            if (!attachedToWindow)
                return
            invalidate()
        }

    /**
     * change just unit text size.
     *
     * @see dpTOpx
     * @see speedTextSize
     * @see textSize
     */
    var unitTextSize: Float
        get() = unitTextPaint.textSize
        set(unitTextSize) {
            unitTextPaint.textSize = unitTextSize
            if (!attachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    /**
     * @return the long of low speed area (low section) as Offset [0, 1].
     */
    fun getLowSpeedOffset(): Float = lowSpeedPercent * .01f

    /**
     * @return the long of Medium speed area (Medium section) as Offset [0, 1].
     */
    fun getMediumSpeedOffset(): Float = mediumSpeedPercent * .01f

    val viewSize: Int
        get() = Math.max(width, height)

    val viewSizePa: Int
        get() = Math.max(widthPa, heightPa)

    /**
     * @return true if current speed in Low Section.
     *
     * @see setLowSpeedPercent
     */
    fun isInLowSection() = (maxSpeed - minSpeed) * getLowSpeedOffset() + minSpeed >= currentSpeed

    /**
     * @return true if current speed in Medium Section
     * , and it is not in Low Section.
     *
     * @see setMediumSpeedPercent
     */
    fun isInMediumSection() = (maxSpeed - minSpeed) * getMediumSpeedOffset() + minSpeed >= currentSpeed && !isInLowSection()

    /**
     * @return true if current speed in High Section
     * , and it is not in Low Section or Medium Section.
     */
    fun isInHighSection() = currentSpeed > (maxSpeed - minSpeed) * getMediumSpeedOffset() + minSpeed

    /**
     * Maybe null, change typeface for **speed and unit** text.
     */
    var speedTextTypeface: Typeface?
        get() = speedTextPaint.typeface
        set(typeface) {
            speedTextPaint.typeface = typeface
            unitTextPaint.typeface = typeface
            if (!attachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    /**
     * Maybe null, change typeface for all texts without speed and unit text.
     */
    var textTypeface: Typeface?
        get() = textPaint.typeface
        set(typeface) {
            textPaint.typeface = typeface
            if (!attachedToWindow)
                return
            updateBackgroundBitmap()
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(translatedDx, translatedDy)

        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap!!, 0f, 0f, backgroundBitmapPaint)

        // check onSpeedChangeEvent.
        val newSpeed = currentSpeed.toInt()
        if (newSpeed != currentIntSpeed && onSpeedChangeListener != null) {
            val byTremble = Build.VERSION.SDK_INT >= 11 && trembleAnimator.isRunning
            val isSpeedUp = newSpeed > currentIntSpeed
            val update = if (isSpeedUp) 1 else -1
            // this loop to pass on all speed values,
            // to safe handle by call gauge.getCorrectIntSpeed().
            while (currentIntSpeed != newSpeed) {
                currentIntSpeed += update
                onSpeedChangeListener!!.onSpeedChange(this, isSpeedUp, byTremble)
            }
        }
        currentIntSpeed = newSpeed

        // check onSectionChangeEvent.
        val newSection = getSection()
        if (section != newSection)
            onSectionChangeEvent(section, newSection)
        section = newSection
    }

    /**
     * draw speed and unit text at [speedTextPosition],
     * this method must call in subSpeedometer's `onDraw` method.
     * @param canvas view canvas to draw.
     */
    protected fun drawSpeedUnitText(canvas: Canvas) {
        val r = getSpeedUnitTextBounds()
        updateSpeedUnitTextBitmap(getSpeedText())
        canvas.drawBitmap(speedUnitTextBitmap, r.left - speedUnitTextBitmap.width * .5f + r.width() * .5f
                , r.top - speedUnitTextBitmap.height * .5f + r.height() * .5f, speedUnitTextBitmapPaint)
    }

    /**
     * clear [speedUnitTextBitmap] and draw speed and unit Text
     * taking into consideration [speedometerTextRightToLeft] and [unitUnderSpeedText].
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
     * create canvas to draw [backgroundBitmap].
     * @return [backgroundBitmap]'s canvas.
     */
    protected open fun createBackgroundBitmapCanvas(): Canvas {
        if (width == 0 || height == 0)
            return Canvas()
        backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        return Canvas(backgroundBitmap!!)
    }

    /**
     * Implement this method to handle section change event.
     * @param oldSection where speed value came from.
     * @param newSection where speed value move to.
     */
    protected fun onSectionChangeEvent(oldSection: Byte, newSection: Byte) {
        onSectionChangeListener?.onSectionChangeListener(oldSection, newSection)
    }

    /**
     * stop speedometer and run tremble if [withTremble] is true.
     * use this method just when you wont to stop `speedTo and realSpeedTo`.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun stop() {
        if (Build.VERSION.SDK_INT < 11)
            return
        if (!speedAnimator.isRunning && !realSpeedAnimator.isRunning)
            return
        speed = currentSpeed
        cancelSpeedAnimator()
        tremble()
    }

    /**
     * cancel all animators without call [tremble].
     */
    protected fun cancelSpeedAnimator() {
        cancelSpeedMove()
        cancelTremble()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun cancelTremble() {
        if (Build.VERSION.SDK_INT < 11)
            return
        canceled = true
        trembleAnimator.cancel()
        canceled = false
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun cancelSpeedMove() {
        if (Build.VERSION.SDK_INT < 11)
            return
        canceled = true
        speedAnimator.cancel()
        realSpeedAnimator.cancel()
        canceled = false
    }

    /**
     * move speed value to new speed without animation.
     * @param speed current speed to move.
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
     * move speed to percent value.
     * @param percent percent value to move, must be between [0,100].
     * @param moveDuration The length of the animation, in milliseconds.
     * This value cannot be negative.
     *
     * @see speedTo
     * @see speedTo
     * @see speedPercentTo
     * @see realSpeedTo
     */
    @JvmOverloads
    fun speedPercentTo(percent: Int, moveDuration: Long = 2000) {
        speedTo(getSpeedValue(percent.toFloat()), moveDuration)
    }

    /**
     * move speed to current value smoothly,
     * it should be between [[minSpeed], [maxSpeed]].<br></br>
     * <br></br>
     * if `speed > maxSpeed` speed value will move to [maxSpeed],<br></br>
     * if `speed < minSpeed` speed value will move to [minSpeed].<br></br>
     *
     * it is the same [speedTo]
     * with default `moveDuration = 2000`.
     *
     * @param speed current speed to move.
     *
     * @see speedTo
     * @see speedPercentTo
     * @see realSpeedTo
     */
    fun speedTo(speed: Float) {
        speedTo(speed, 2000)
    }

    /**
     * move speed to current value smoothly with animation duration,
     * it should be between [[minSpeed], [maxSpeed]].<br></br>
     * <br></br>
     * if `speed > maxSpeed` speed value will move to [maxSpeed],<br></br>
     * if `speed < minSpeed` speed value will move to [minSpeed].
     *
     * @param speed current speed to move.
     * @param moveDuration The length of animation, in milliseconds.
     * This value cannot be negative.
     *
     * @see speedTo
     * @see speedPercentTo
     * @see realSpeedTo
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun speedTo(speed: Float, moveDuration: Long) {
        var newSpeed = speed
        newSpeed = if (newSpeed > maxSpeed) maxSpeed else if (newSpeed < minSpeed) minSpeed else newSpeed
        if (newSpeed == this.speed)
            return
        this.speed = newSpeed

        if (Build.VERSION.SDK_INT < 11) {
            setSpeedAt(newSpeed)
            return
        }

        isSpeedIncrease = newSpeed > currentSpeed

        cancelSpeedAnimator()
        speedAnimator = ValueAnimator.ofFloat(currentSpeed, newSpeed)
        speedAnimator.interpolator = DecelerateInterpolator()
        speedAnimator.duration = moveDuration
        speedAnimator.addUpdateListener {
            currentSpeed = speedAnimator.animatedValue as Float
            postInvalidate()
        }
        speedAnimator.addListener(animatorListener)
        speedAnimator.start()
    }

    /**
     * this method use [realSpeedTo] to speed up
     * the speedometer to [maxSpeed].
     *
     * @see realSpeedTo
     * @see slowDown
     */
    fun speedUp() {
        realSpeedTo(getMaxSpeed())
    }

    /**
     * this method use [realSpeedTo] to slow down
     * the speedometer to [minSpeed].
     *
     * @see realSpeedTo
     * @see speedUp
     */
    fun slowDown() {
        realSpeedTo(0f)
    }

    /**
     * move speed to percent value by using [realSpeedTo] method.
     * @param percent percent value to move, must be between [0,100].
     */
    fun realSpeedPercentTo(percent: Float) {
        realSpeedTo(getSpeedValue(percent))
    }

    /**
     * to make speedometer some real.
     * <br></br>
     * when **speed up** : speed value will increase *slowly* by [accelerate].
     * <br></br>
     * when **slow down** : speed value will decrease *rapidly* by [decelerate].
     * @param speed current speed to move.
     *
     * @see speedTo
     * @see speedTo
     * @see speedPercentTo
     * @see speedUp
     * @see slowDown
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun realSpeedTo(speed: Float) {
        var newSpeed = speed
        val oldIsSpeedUp = this.speed > currentSpeed
        newSpeed = if (newSpeed > maxSpeed) maxSpeed else if (newSpeed < minSpeed) minSpeed else newSpeed
        if (newSpeed == this.speed)
            return
        this.speed = newSpeed

        if (Build.VERSION.SDK_INT < 11) {
            setSpeedAt(newSpeed)
            return
        }
        isSpeedIncrease = newSpeed > currentSpeed
        if (realSpeedAnimator.isRunning && oldIsSpeedUp == isSpeedIncrease)
            return

        cancelSpeedAnimator()
        realSpeedAnimator = ValueAnimator.ofInt(currentSpeed.toInt(), newSpeed.toInt())
        realSpeedAnimator.repeatCount = ValueAnimator.INFINITE
        realSpeedAnimator.interpolator = LinearInterpolator()
        realSpeedAnimator.duration = Math.abs(((newSpeed - currentSpeed) * 10).toLong())
        val finalSpeed = newSpeed
        realSpeedAnimator.addUpdateListener {
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
        realSpeedAnimator.addListener(animatorListener)
        realSpeedAnimator.start()
    }

    /**
     * check if [withTremble] true, and run tremble.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected fun tremble() {
        cancelTremble()
        if (!withTremble || Build.VERSION.SDK_INT < 11)
            return
        val random = Random()
        var mad = trembleDegree * random.nextFloat() * (if (random.nextBoolean()) -1 else 1).toFloat()
        mad = when {
            speed + mad > maxSpeed -> maxSpeed - speed
            speed + mad < minSpeed -> minSpeed - speed
            else -> mad
        }
        trembleAnimator = ValueAnimator.ofFloat(currentSpeed, speed + mad)
        trembleAnimator.interpolator = DecelerateInterpolator()
        trembleAnimator.duration = trembleDuration.toLong()
        trembleAnimator.addUpdateListener {
            isSpeedIncrease = trembleAnimator.animatedValue as Float > currentSpeed
            currentSpeed = trembleAnimator.animatedValue as Float
            postInvalidate()
        }
        trembleAnimator.addListener(animatorListener)
        trembleAnimator.start()
    }

    /**
     * @param percentSpeed between [0, 100].
     * @return speed value at current percentSpeed.
     */
    private fun getSpeedValue(percentSpeed: Float): Float {
        var percentSpeed_ = percentSpeed
        percentSpeed_ = when {
            percentSpeed_ > 100 -> 100f
            percentSpeed_ < 0 -> 0f
            else -> percentSpeed_
        }
        return percentSpeed_ * (maxSpeed - minSpeed) * .01f + minSpeed
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

    override fun onSaveInstanceState(): Parcelable? {
        super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putFloat("speed", speed)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var state_ = state
        val bundle = state_ as Bundle?
        speed = bundle!!.getFloat("speed")
        state_ = bundle.getParcelable("superState")
        super.onRestoreInstanceState(state_)
        setSpeedAt(speed)
    }

    /**
     * tremble control.
     * @param trembleDegree a speed value to increases and decreases current around [speed].
     * @param trembleDuration tremble Animation duration in millisecond.
     *
     * @throws IllegalArgumentException If trembleDegree OR trembleDuration is Negative.
     */
    fun setTrembleData(trembleDegree: Float, trembleDuration: Int) {
        this.trembleDegree = trembleDegree
        this.trembleDuration = trembleDuration
        checkTrembleData()
    }

    /**
     * get max speed in speedometer, default max speed is 100.
     * @return max speed.
     *
     * @see getMinSpeed
     * @see setMaxSpeed
     */
    fun getMaxSpeed(): Float {
        return maxSpeed
    }

    /**
     * change max speed.<br></br>
     * this method will move [currentSpeed] to its new position
     * immediately without animation.
     *
     * @param maxSpeed new MAX Speed.
     *
     * @throws IllegalArgumentException if `minSpeed >= maxSpeed`
     */
    fun setMaxSpeed(maxSpeed: Float) {
        setMinMaxSpeed(minSpeed, maxSpeed)
    }

    /**
     * get min speed in speedometer, default min speed is 0.
     * @return min speed.
     *
     * @see .getMaxSpeed
     * @see .setMinSpeed
     */
    fun getMinSpeed(): Float {
        return minSpeed
    }

    /**
     * change min speed.<br></br>
     * this method will move [currentSpeed] to its new position
     * immediately without animation.
     *
     * @param minSpeed new MIN Speed.
     *
     * @throws IllegalArgumentException if `minSpeed >= maxSpeed`
     */
    fun setMinSpeed(minSpeed: Float) {
        setMinMaxSpeed(minSpeed, maxSpeed)
    }

    /**
     * change Min and Max speed.<br></br>
     * this method will move [currentSpeed] to its new position
     * immediately without animation.
     *
     * @param minSpeed new MIN Speed.
     * @param maxSpeed new MAX Speed.
     *
     * @throws IllegalArgumentException if `minSpeed >= maxSpeed`
     */
    fun setMinMaxSpeed(minSpeed: Float, maxSpeed: Float) {
        if (minSpeed >= maxSpeed)
            throw IllegalArgumentException("minSpeed must be smaller than maxSpeed !!")
        cancelSpeedAnimator()
        this.minSpeed = minSpeed
        this.maxSpeed = maxSpeed
        if (!attachedToWindow)
            return
        updateBackgroundBitmap()
        setSpeedAt(speed)
    }

    /**
     * @return unit text.
     */
    fun getUnit(): String {
        return unit
    }

    /**
     * unit text, the text after speed text.
     * @param unit unit text.
     */
    fun setUnit(unit: String) {
        this.unit = unit
        if (!attachedToWindow)
            return
        invalidate()
    }

    /**
     * Register a callback to be invoked when speed value changed (in integer).
     * @param onSpeedChangeListener maybe null, The callback that will run.
     */
    fun setOnSpeedChangeListener(onSpeedChangeListener: OnSpeedChangeListener) {
        this.onSpeedChangeListener = onSpeedChangeListener
    }

    /**
     * Register a callback to be invoked when
     * [section](https://github.com/anastr/SpeedView/wiki/Usage#control-division-of-the-speedometer) changed.
     * @param onSectionChangeListener maybe null, The callback that will run.
     */
    fun setOnSectionChangeListener(onSectionChangeListener: OnSectionChangeListener) {
        this.onSectionChangeListener = onSectionChangeListener
    }

    /**
     * @return the long of low speed area (low section) as percent.
     */
    fun getLowSpeedPercent(): Int {
        return lowSpeedPercent
    }

    /**
     * to change low speed area (low section).
     * @param lowSpeedPercent the long of low speed area as percent,
     * must be between `[0,100]`.
     * @throws IllegalArgumentException if `lowSpeedPercent` out of range.
     * @throws IllegalArgumentException if `lowSpeedPercent > mediumSpeedPercent`.
     */
    fun setLowSpeedPercent(lowSpeedPercent: Int) {
        this.lowSpeedPercent = lowSpeedPercent
        checkSpeedometerPercent()
        if (!attachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    /**
     * @return the long of Medium speed area (Medium section) as percent.
     */
    fun getMediumSpeedPercent(): Int {
        return mediumSpeedPercent
    }

    /**
     * to change medium speed area (medium section).
     * @param mediumSpeedPercent the long of medium speed area as percent,
     * must be between `[0,100]`.
     * @throws IllegalArgumentException if `mediumSpeedPercent` out of range.
     * @throws IllegalArgumentException if `mediumSpeedPercent < lowSpeedPercent`.
     */
    fun setMediumSpeedPercent(mediumSpeedPercent: Int) {
        this.mediumSpeedPercent = mediumSpeedPercent
        checkSpeedometerPercent()
        if (!attachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
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
     * @return current section,
     * used in condition : `if (speedometer.getSection() == speedometer.LOW_SECTION)`.
     */
    fun getSection(): Byte {
        return when {
            isInLowSection() -> LOW_SECTION
            isInMediumSection() -> MEDIUM_SECTION
            else -> HIGH_SECTION
        }
    }

    /**
     * @return whether this view attached to Layout or not.
     */
    override fun isAttachedToWindow(): Boolean {
        return attachedToWindow
    }

    /**
     * @return the space between Speed Text and Unit Text.
     */
    fun getUnitSpeedInterval(): Float {
        return unitSpeedInterval
    }

    /**
     * change space between speedText and UnitText.
     * @param unitSpeedInterval new space in pixel.
     */
    fun setUnitSpeedInterval(unitSpeedInterval: Float) {
        this.unitSpeedInterval = unitSpeedInterval
        if (!attachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    /**
     * @return Speed-Unit Text padding.
     */
    fun getSpeedTextPadding(): Float {
        return speedTextPadding
    }

    /**
     * change the Speed-Unit Text padding,
     * this value will ignore if `{ #speedTextPosition} == Position.CENTER`.
     * @param speedTextPadding padding in pixel.
     */
    fun setSpeedTextPadding(speedTextPadding: Float) {
        this.speedTextPadding = speedTextPadding
        if (!attachedToWindow)
            return
        invalidate()
    }

    /**
     * change position of speed and Unit Text.
     * @param position new Position (enum value).
     */
    fun setSpeedTextPosition(position: Position) {
        this.speedTextPosition = position
        if (!attachedToWindow)
            return
        updateBackgroundBitmap()
        invalidate()
    }

    /**
     * position of Speed-Unit Text.
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

    companion object {

        const val LOW_SECTION: Byte = 1
        const val MEDIUM_SECTION: Byte = 2
        const val HIGH_SECTION: Byte = 3

        /** draw speed text as **integer** . */
        const val INTEGER_FORMAT: Byte = 0
        /** draw speed text as **.1 float**.  */
        const val FLOAT_FORMAT: Byte = 1
    }
}
