package com.github.anastr.speedviewlib.components.note

import android.content.Context
import android.graphics.*

/**
 * small popup dialog with simple note shown with limited or unlimited time.
 *
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class Note<out N : Note<N>> protected constructor(context: Context) {

    private val density: Float = context.resources.displayMetrics.density

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paddingLeft: Float = 0.toFloat()
    private var paddingTop: Float = 0.toFloat()
    private var paddingRight: Float = 0.toFloat()
    private var paddingBottom: Float = 0.toFloat()
    private var backgroundBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private var position = Position.CenterIndicator
    private var align = Align.Top
    private var noteW = 0
    private var noteH = 0
    private var containsW = 0
    private var containsH = 0
    private var cornersRound = 5f
    /** dialog's triangle Height  */
    private var triangleHeight: Float = 0.toFloat()

    val backgroundColor: Int
        get() = backgroundPaint.color

    init {
        triangleHeight = dpTOpx(12f)
        backgroundPaint.color = 0xffd6d7d7.toInt()
        setPadding(dpTOpx(7f), dpTOpx(7f), dpTOpx(7f), dpTOpx(7f))
    }

    fun dpTOpx(dp: Float): Float {
        return dp * density
    }

    /**
     * draw inside note's dialog.
     * @param canvas canvas to draw.
     * @param leftX left x position to start drawing.
     * @param topY top y position to start drawing.
     */
    protected abstract fun drawContains(canvas: Canvas, leftX: Float, topY: Float)

    /**
     * called by speedometer after create the Note.<br></br>
     * it must call [.noticeContainsSizeChange] at the End.
     * @param viewWidth Speedometer View Width.
     */
    abstract fun build(viewWidth: Int)

    /**
     * this must call when contains size change or padding or [.triangleHeight].
     * @param containsW contains width.
     * @param containsH contains height.
     */
    protected fun noticeContainsSizeChange(containsW: Int, containsH: Int) {
        this.containsW = containsW
        this.containsH = containsH
        if (align == Align.Top || align == Align.Bottom) {
            this.noteW = (containsW.toFloat() + paddingLeft + paddingRight).toInt()
            this.noteH = (containsH.toFloat() + paddingTop + paddingBottom + triangleHeight).toInt()
        } else {
            this.noteW = (containsW.toFloat() + paddingLeft + paddingRight + triangleHeight).toInt()
            this.noteH = (containsH.toFloat() + paddingTop + paddingBottom).toInt()
        }
        updateBackgroundBitmap()
    }

    /**
     * notice that background dialog changed (color, size, Corners Round ....).
     */
    private fun updateBackgroundBitmap() {
        if (noteW > 0 && noteH > 0) {
            backgroundBitmap = Bitmap.createBitmap(noteW, noteH, Bitmap.Config.ARGB_8888)
            val c = Canvas(backgroundBitmap)
            when (align) {
                Align.Left -> bitmapLeft(c)
                Align.Top -> bitmapTop(c)
                Align.Right -> bitmapRight(c)
                Align.Bottom -> bitmapBottom(c)
            }
        }
    }

    private fun bitmapLeft(c: Canvas) {
        val rectF = RectF(0f, 0f, noteW - triangleHeight, noteH.toFloat())
        val p = Path()
        p.moveTo(noteW.toFloat(), noteH / 2f)
        p.lineTo(rectF.right - 1, noteH / 2f - dpTOpx(9f))
        p.lineTo(rectF.right - 1, noteH / 2f + dpTOpx(9f))
        c.drawPath(p, backgroundPaint)
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint)
    }

    private fun bitmapTop(c: Canvas) {
        val rectF = RectF(0f, 0f, noteW.toFloat(), noteH - triangleHeight)
        val p = Path()
        p.moveTo(noteW / 2f, noteH.toFloat())
        p.lineTo(noteW / 2f - dpTOpx(9f), rectF.bottom - 1)
        p.lineTo(noteW / 2f + dpTOpx(9f), rectF.bottom - 1)
        c.drawPath(p, backgroundPaint)
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint)
    }

    private fun bitmapRight(c: Canvas) {
        val rectF = RectF(0f + triangleHeight, 0f, noteW.toFloat(), noteH.toFloat())
        val p = Path()
        p.moveTo(0f, noteH / 2f)
        p.lineTo(rectF.left + 1, noteH / 2f - dpTOpx(9f))
        p.lineTo(rectF.left + 1, noteH / 2f + dpTOpx(9f))
        c.drawPath(p, backgroundPaint)
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint)
    }

    private fun bitmapBottom(c: Canvas) {
        val rectF = RectF(0f, 0f + triangleHeight, noteW.toFloat(), noteH.toFloat())
        val p = Path()
        p.moveTo(noteW / 2f, 0f)
        p.lineTo(noteW / 2f - dpTOpx(9f), rectF.top + 1)
        p.lineTo(noteW / 2f + dpTOpx(9f), rectF.top + 1)
        c.drawPath(p, backgroundPaint)
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint)
    }

    fun draw(canvas: Canvas, posX: Float, posY: Float) {
        when (align) {
            Align.Left -> {
                canvas.drawBitmap(backgroundBitmap, posX - noteW, posY - noteH / 2f, paint)
                drawContains(canvas, posX - noteW + paddingLeft, posY - noteH / 2f + paddingTop)
            }
            Align.Top -> {
                canvas.drawBitmap(backgroundBitmap, posX - noteW / 2f, posY - noteH, paint)
                drawContains(canvas, posX - containsW / 2f, posY - noteH + paddingTop)
            }
            Align.Right -> {
                canvas.drawBitmap(backgroundBitmap, posX, posY - noteH / 2f, paint)
                drawContains(canvas, posX + triangleHeight + paddingLeft, posY - noteH / 2f + paddingTop)
            }
            Align.Bottom -> {
                canvas.drawBitmap(backgroundBitmap, posX - noteW / 2f, posY, paint)
                drawContains(canvas, posX - containsW / 2f, posY + triangleHeight + paddingTop)
            }
        }

    }

    /**
     * set dialog color.
     * @param backgroundColor new color.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    fun setBackgroundColor(backgroundColor: Int): N {
        backgroundPaint.color = backgroundColor
        return this as N
    }

    fun getCornersRound(): Float {
        return cornersRound
    }

    /**
     * change Corners Round for Dialog Rect.
     * @param cornersRound new Corners Round.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    fun setCornersRound(cornersRound: Float): N {
        require(cornersRound >= 0) { "cornersRound cannot be negative" }
        this.cornersRound = cornersRound
        return this as N
    }

    fun getAlign(): Align {
        return align
    }

    /**
     * change dialog Align (default : **Top**)
     * @param align Enm value new dialog Align.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    fun setAlign(align: Align): N {
        this.align = align
        return this as N
    }

    fun getPosition(): Position {
        return position
    }

    /**
     * set dialog position.
     * @param position Enm value new Position.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    fun setPosition(position: Position): N {
        this.position = position
        return this as N
    }

    /**
     * set padding inside dialog.
     * @param left Left Padding.
     * @param top Top Padding.
     * @param right Right Padding.
     * @param bottom Bottom Padding.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    fun setPadding(left: Float, top: Float, right: Float, bottom: Float): N {
        paddingLeft = left
        paddingTop = top
        paddingRight = right
        paddingBottom = bottom
        noticeContainsSizeChange(containsW, containsH)
        return this as N
    }

    enum class Position {
        /**
         * the top of the indicator at current speed.
         */
        TopIndicator,
        /**
         * center between [top fo indicator, bottom of indicator] at current speed.
         */
        CenterIndicator,
        /**
         * the bottom of the indicator at current speed.
         */
        BottomIndicator,
        /**
         * the top of the speedometer at current speed.
         */
        TopSpeedometer,
        /**
         * center of the speedometer (current speed ignored).
         */
        CenterSpeedometer,
        /**
         * quarter of the speedometer between [top fo speedometer, center of speedometer] at current speed.
         */
        QuarterSpeedometer
    }

    enum class Align {
        Left, Top, Right, Bottom
    }

    companion object {

        /** This value used with the `speedometer.addNote(Note, int)` property
         * to keep the note on the speedometer.
         *
         * but it well be removed when call `speedometer.removeAllNotes()`. */
        const val INFINITE = -1
    }
}
