package com.github.anastr.speedviewlib.util

import com.github.anastr.speedviewlib.Gauge
import com.github.anastr.speedviewlib.components.Section

/**
 *
 * this Library build By **Anas Altair**
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */

/**
 * A callback that notifies clients when the speed has been
 * changed (just when speed change in integer).
 *
 * Notification that the speed has changed.
 *
 * @param [gauge] the gauge who change.
 * @param [isSpeedUp] if speed increase.
 * @param [isByTremble] true if speed has changed by Tremble.
 */
typealias OnSpeedChangeListener = (gauge: Gauge, isSpeedUp: Boolean, isByTremble: Boolean) -> Unit

/**
 * A callback that notifies clients when
 * the indicator move to new section.
 *
 * Notification that the section has changed.
 *
 * @param [previousSection] where speed value came from, or null if there is no previous section.
 * @param [newSection] where speed value moved to, or null if there is no section where speed moved to.
 */
typealias OnSectionChangeListener = (previousSection :Section?, newSection : Section?) -> Unit

/**
 * callback to draw custom TickLabel for each Tick.
 *
 * @param [tickPosition] position of ticks, start from 0.
 * @param [tick] speed value at the tick.
 * @return label to draw.
 */
typealias OnPrintTickLabelListener = (tickPosition :Int, tick :Float) -> CharSequence?


/**
 * do an action on all [Gauge.sections], with
 * only one redraw (after complete) to avoid redrawing
 * the speedometer on every change.
 * @param [action] an action to invoke for every section.
 */
fun Gauge.doOnSections(action: (section: Section) -> Unit) {
    val sections = ArrayList(this.sections)
    // this will also clear observers.
    this.clearSections()
    sections.forEach { action.invoke(it) }
    this.addSections(sections)
}

/**
 * here we calculate the extra length when strokeCap = ROUND.
 *
 * round angle padding =         A       * 360 / (           D             *   PI   )
 * @param [a] Arc Length, the extra length that taken ny ROUND stroke in one side.
 * @param [d] Diameter of circle.
 */
fun getRoundAngle(a: Float, d: Float): Float {
    return (a * .5f * 360 / (d  * Math.PI)).toFloat()
}