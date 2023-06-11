package com.github.anastr.speedviewlib_compose.utils

import androidx.compose.ui.geometry.Size

/**
 * here we calculate the extra length when strokeCap = ROUND.
 *
 * round angle padding =         A       * 360 / (           D             *   PI   )
 * @param [a] Arc Length, the extra length that taken ny ROUND stroke in one side.
 * @param [d] Diameter of circle.
 */
internal fun getRoundAngle(a: Float, d: Float): Float {
    return (a * .5f * 360 / (d  * Math.PI)).toFloat()
}

/**
 * Helper method to offset the provided size with the offset in box width and height
 */
internal fun Size.offsetSize(offset: Float): Size =
    Size(this.width - offset, this.height - offset)
