package com.github.anastr.speedviewlib.util;

/**
 * A callback that notifies clients when
 * the the indicator move to new section.
 * <p>
 * this Library build By <b>Anas Altair</b>
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public interface OnSectionChangeListener {
    /**
     * Notification that the section has changed.
     *
     * @param oldSection where indicator came from.
     * @param newSection where indicator move to.
     */
    void onSectionChangeListener(byte oldSection, byte newSection);
}
