package com.github.anastr.speedviewlib.components

import android.os.Parcel
import android.os.Parcelable
import com.github.anastr.speedviewlib.Gauge

/**
 * Created by Anas Altair on 10/25/2019.
 */
class Section(startOffset: Float, endOffset: Float, color: Int, style: Style): Parcelable {

    var gauge: Gauge? = null

    constructor(section: Section) : this(section.startOffset, section.endOffset, section.color, section.style)

    constructor(parcel: Parcel) : this(parcel.readFloat(), parcel.readFloat(), parcel.readInt(), parcel.readSerializable() as Style)

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
        set(value) {
            _startOffset = value
            gauge?.checkSection(this)
            gauge?.invalidateGauge()
        }

    /**
     * end percent value to section range [0, 1]
     * 0 means 0%
     * 1 means 100%
     * @throws IllegalArgumentException if [endOffset] is invalid.
     */
    var endOffset
        get() = _endOffset
        set(value) {
            _endOffset = value
            gauge?.checkSection(this)
            gauge?.invalidateGauge()
        }

    /**
     * section color, for speedometer family (not for all speedometer).
     */
    var color: Int = color
        set(value) {
            field = value
            gauge?.invalidateGauge()
        }

    var style: Style = style
        set(value) {
            field = value
            gauge?.invalidateGauge()
        }

    fun setStartEndOffset(startOffset: Float, endOffset: Float) {
        _startOffset = startOffset
        _endOffset = endOffset
        gauge?.checkSection(this)
        gauge?.invalidateGauge()
    }

    /**
     * add Observer to this section, **only one gauge can observe the section**.
     */
    internal fun inGauge(gauge: Gauge): Section {
        this.gauge = gauge
        return this
    }

    internal fun clearGauge() { gauge = null }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(startOffset)
        parcel.writeFloat(endOffset)
        parcel.writeInt(color)
        parcel.writeSerializable(style.ordinal)
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

    enum class Style {
        SQUARE, ROUND
    }

}