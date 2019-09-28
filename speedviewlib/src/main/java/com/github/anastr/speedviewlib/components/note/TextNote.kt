package com.github.anastr.speedviewlib.components.note

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import kotlin.math.max

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class TextNote
/**
 * @param context you can use `getApplicationContext()` method.
 * @param noteText text to display, support SpannableString and multi-lines.
 */
(context: Context, private val noteText: CharSequence?) : Note<TextNote>(context) {
    private val notePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var textSize = notePaint.textSize
    private var textLayout: StaticLayout? = null

    val textColor: Int
        get() = notePaint.color

    init {
        requireNotNull(noteText) { "noteText cannot be null." }
        notePaint.textAlign = Paint.Align.LEFT
    }

    override fun build(viewWidth: Int) {
        textLayout = StaticLayout(noteText, notePaint, viewWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true)
        var w = 0
        for (i in 0 until textLayout!!.lineCount)
            w = max(w.toFloat(), textLayout!!.getLineWidth(i)).toInt()
        noticeContainsSizeChange(w, textLayout!!.height)
    }

    override fun drawContains(canvas: Canvas, leftX: Float, topY: Float) {
        canvas.save()
        canvas.translate(leftX, topY)
        textLayout!!.draw(canvas)
        canvas.restore()
    }

    fun getTextSize(): Float {
        return textSize
    }

    /**
     * set Text size.
     * @param textSize in Pixel.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    fun setTextSize(textSize: Float): TextNote {
        this.textSize = textSize
        notePaint.textSize = textSize
        return this
    }

    /**
     * to change font or text style.
     * @param typeface new Typeface.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    fun setTextTypeFace(typeface: Typeface): TextNote {
        notePaint.typeface = typeface
        return this
    }

    /**
     * set text color.
     * @param textColor new color.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    fun setTextColor(textColor: Int): TextNote {
        notePaint.color = textColor
        return this
    }
}
