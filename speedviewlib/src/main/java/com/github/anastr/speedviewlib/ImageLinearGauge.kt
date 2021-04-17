package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
open class ImageLinearGauge @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : LinearGauge(context, attrs, defStyleAttr) {

    private var image: Drawable? = null

    private var backColor = 0xffd6d7d7.toInt()

    init {
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        super.speedTextPosition = Position.CENTER
        super.unitUnderSpeedText = true
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ImageLinearGauge, 0, 0)

        backColor = a.getColor(R.styleable.ImageLinearGauge_sv_speedometerBackColor, backColor)
        image = a.getDrawable(R.styleable.ImageLinearGauge_sv_image)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = measuredWidth
        val h = measuredHeight
        if (image == null || w == 0 || h == 0
                || image!!.intrinsicWidth <= 0 || image!!.intrinsicHeight <= 0)
            return
        val imageW = image!!.intrinsicWidth.toFloat()
        val imageH = image!!.intrinsicHeight.toFloat()
        val view_w_to_h = (w / h).toFloat()
        val image_w_to_h = imageW / imageH

        if (image_w_to_h > view_w_to_h)
            setMeasuredDimension(w, (w * imageH / imageW).toInt())
        else
            setMeasuredDimension((h * imageW / imageH).toInt(), h)
    }

    override fun updateFrontAndBackBitmaps() {
        val canvasBack = createBackgroundBitmapCanvas()
        val canvasFront = createForegroundBitmapCanvas()

        if (image != null) {
            image!!.setBounds(padding, padding, width - padding, height - padding)
            image!!.setColorFilter(backColor, PorterDuff.Mode.SRC_IN)
            image!!.draw(canvasBack)

            image!!.colorFilter = null
            image!!.draw(canvasFront)
        }
    }
}
