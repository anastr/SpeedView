package com.github.anastr.speedviewlib.util;

/**
 * Created by Anas Altair.
 */

public interface OnPrintTickLabel {
    /**
     * @param tickPosition position of ticks, start from 0.
     * @param tick speed value at the tick.
     * @return label to draw.
     */
    String getTickLabel(int tickPosition, int tick);
}
