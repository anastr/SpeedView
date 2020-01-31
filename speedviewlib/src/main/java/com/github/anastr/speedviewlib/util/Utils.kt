package com.github.anastr.speedviewlib.util

import com.github.anastr.speedviewlib.Gauge
import com.github.anastr.speedviewlib.components.Section

/**
 *
 * this Library build By **Anas Altair**
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */

/**
 * callback to draw custom TickLabel for each Tick.
 *
 * [tickPosition] position of ticks, start from 0.
 *
 * [tick] speed value at the tick.
 * @return label to draw.
 */
typealias OnPrintTickLabelListener = (tickPosition :Int, tick :Float) -> CharSequence

/**
 * A callback that notifies clients when
 * the indicator move to new section.
 *
 * Notification that the section has changed.
 *
 * [previousSection] where speed value came from, or null if there is no previous section.
 *
 * [newSection] where speed value moved to, or null if there is no section where speed moved to.
 */
typealias OnSectionChangeListener = (previousSection :Section?, newSection : Section?) -> Unit


/**
 * A callback that notifies clients when the speed has been
 * changed (just when speed change in integer).
 *
 * Notification that the speed has changed.
 *
 * [gauge] the gauge who change.
 *
 * [isSpeedUp] if speed increase.
 *
 * [isByTremble] true if speed has changed by Tremble.
 */
typealias OnSpeedChangeListener = (gauge: Gauge, isSpeedUp: Boolean, isByTremble: Boolean) -> Unit