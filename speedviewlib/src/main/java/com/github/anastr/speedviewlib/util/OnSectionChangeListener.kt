package com.github.anastr.speedviewlib.util

import com.github.anastr.speedviewlib.components.Section

/**
 * A callback that notifies clients when
 * the indicator move to new section.
 *
 * this Library build By **Anas Altair**
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
interface OnSectionChangeListener {
    /**
     * Notification that the section has changed.
     *
     * @param previousSection where speed value came from, or null if there is no previous section.
     * @param newSection where speed value moved to, or null if there is no section where speed moved to.
     */
    fun onSectionChangeListener(previousSection: Section?, newSection: Section?)
}
