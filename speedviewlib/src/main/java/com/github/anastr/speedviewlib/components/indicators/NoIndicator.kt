package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.Canvas

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class NoIndicator(context: Context) : Indicator<NoIndicator>(context) {

    override fun draw(canvas: Canvas) {}

    override fun updateIndicator() {}

    override fun setWithEffects(withEffects: Boolean) {}
}
