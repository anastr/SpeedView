package com.github.anastr.speedviewlib.components

import android.os.Parcel
import android.os.Parcelable
import com.github.anastr.speedviewlib.Gauge

/**
 * Created by Anas Altair on 10/25/2019.
 */
class Section @JvmOverloads constructor(startOffset: Float, endOffset: Float, color: Int, width: Float = 0f, style: Style = Style.BUTT): Parcelable {

    private var gauge: Gauge? = null

    var width: Float = width
        set(value) {
            field = value
            gauge?.invalidateGauge()
        }
    var padding: Float = 0f
        set(value) {
            field = value
            gauge?.invalidateGauge()
        }

    constructor(section: Section) : this(section.startOffset, section.endOffset, section.color, section.width, section.style) {
        padding = section.padding
    }

    constructor(parcel: Parcel) : this(parcel.readFloat(), parcel.readFloat(), parcel.readInt(), parcel.readFloat(), parcel.readSerializable() as Style) {
        padding = parcel.readFloat()
    }

    private var _startOffset: Float = startOffset
    private var _endOffset: Float = endOffset

    /**
     * start percent value to section range [0, 1]
     * 0 means 0%
     * 1 means 100%
     * @throws IllegalArgumentException if [startOffset] is invalid.
     */
    var startOffset
        get() = _startOffset
        set(value) = setStartEndOffset(value, endOffset)

    /**
     * end percent value to section range [0, 1]
     * 0 means 0%
     * 1 means 100%
     * @throws IllegalArgumentException if [endOffset] is invalid.
     */
    var endOffset
        get() = _endOffset
        set(value) = setStartEndOffset(startOffset, value)

    /**
     * section color, for speedometer family (not for all speedometer).
     */
    var color: Int = color
        set(value) {
            field = value
            gauge?.invalidateGauge()
        }

    /**
     * style to ths section.
     */
    var style: Style = style
        set(value) {
            field = value
            gauge?.invalidateGauge()
        }

    /**
     * change both offsets at once.
     * @param startOffset start of the section [0, 1)
     * @param endOffset end of the section (0, 1]
     * @throws IllegalArgumentException if [startOffset] or [endOffset] are invalid.
     */
    fun setStartEndOffset(startOffset: Float, endOffset: Float) {
        _startOffset = startOffset
        _endOffset = endOffset
        gauge?.checkSection(this)
        gauge?.invalidateGauge()
    }

    /**
     * add gauge to this section to attache to, **a section can attache to one gauge**.
     */
    internal fun inGauge(gauge: Gauge): Section {
        this.gauge = gauge
        return this
    }

    /**
     * remove gauge that this section attached to,
     * to avoid call `invalidate()` from this section.
     */
    internal fun clearGauge() { gauge = null }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(startOffset)
        parcel.writeFloat(endOffset)
        parcel.writeInt(color)
        parcel.writeFloat(width)
        parcel.writeSerializable(style.ordinal)
        parcel.writeFloat(padding)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Section> {

        override fun createFromParcel(parcel: Parcel): Section {
            return Section(parcel)
        }

        override fun newArray(size: Int): Array<Section?> {
            return arrayOfNulls(size)
        }
    }

}