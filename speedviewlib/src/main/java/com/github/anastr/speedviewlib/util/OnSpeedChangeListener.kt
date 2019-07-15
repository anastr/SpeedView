package com.github.anastr.speedviewlib.util

import com.github.anastr.speedviewlib.Gauge

/**
 * A callback that notifies clients when the speed has been
 * changed (just when speed change in integer).
 *
 * this Library build By **Anas Altair**
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
interface OnSpeedChangeListener {
    /**
     * Notification that the speed has changed.
     *
     * @param gauge the gauge who change.
     * @param isSpeedUp if speed increase.
     * @param isByTremble true if speed has changed by Tremble.
     */
    fun onSpeedChange(gauge: Gauge, isSpeedUp: Boolean, isByTremble: Boolean)
}
