package com.github.anastr.speedviewlib.util

/**
 * A callback that notifies clients when
 * the the indicator move to new section.
 *
 * this Library build By **Anas Altair**
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
interface OnSectionChangeListener {
    /**
     * Notification that the section has changed.
     *
     * @param oldSection where speed value came from.
     * @param newSection where speed value moved to.
     */
    fun onSectionChangeListener(oldSection: Byte, newSection: Byte)
}
