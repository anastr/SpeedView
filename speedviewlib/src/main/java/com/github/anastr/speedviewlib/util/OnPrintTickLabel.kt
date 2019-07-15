package com.github.anastr.speedviewlib.util

/**
 *
 *
 * callback to draw custom TickLabel for each Tick.
 *
 * this Library build By **Anas Altair**
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */

interface OnPrintTickLabel {
    /**
     * @param tickPosition position of ticks, start from 0.
     * @param tick speed value at the tick.
     * @return label to draw.
     */
    fun getTickLabel(tickPosition: Int, tick: Float): CharSequence
}
