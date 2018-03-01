package com.github.anastr.speedviewlib.util;

/**
 * <p>
 *     callback to draw custom TickLabel for each Tick.
 * </p>
 * this Library build By <b>Anas Altair</b>
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */

public interface OnPrintTickLabel {
    /**
     * @param tickPosition position of ticks, start from 0.
     * @param tick speed value at the tick.
     * @return label to draw.
     */
    CharSequence getTickLabel(int tickPosition, float tick);
}
