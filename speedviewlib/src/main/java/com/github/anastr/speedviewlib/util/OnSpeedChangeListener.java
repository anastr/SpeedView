package com.github.anastr.speedviewlib.util;

import com.github.anastr.speedviewlib.Gauge;

/**
 * <p>
 * A callback that notifies clients when the speed has been
 * changed (just when speed change in integer).
 * </p>
 *
 * this Library build By <b>Anas Altair</b>
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public interface OnSpeedChangeListener {
    /**
     * Notification that the speed has changed.
     *
     * @param gauge the gauge who change.
     * @param isSpeedUp if speed increase.
     * @param isByTremble true if speed has changed by Tremble.
     */
    void onSpeedChange(Gauge gauge, boolean isSpeedUp, boolean isByTremble);
}
