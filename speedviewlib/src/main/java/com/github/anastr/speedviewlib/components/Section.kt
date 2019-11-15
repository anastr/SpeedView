package com.github.anastr.speedviewlib.components

import android.os.Parcel
import android.os.Parcelable
import com.github.anastr.speedviewlib.Gauge
import java.util.*

/**
 * Created by Anas Altair on 10/25/2019.
 */
class Section(speedOffset: Float, color: Int): Observable(), Parcelable {

    constructor(section: Section) : this(section.speedOffset, section.color)

    constructor(parcel: Parcel) : this(parcel.readFloat(), parcel.readInt())

    /**
     * percent value to section range [0, 1]
     * 0 means 0%
     * 1 means 100%
     * @throws NullPointerException if [speedOffset] out of range.
     */
    var speedOffset: Float = speedOffset
        set(value) {
            field = value
            setChanged()
            checkPercent()
            notifyObservers(true)
        }

    /**
     * section color, for speedometer family (not for all speedometer).
     */
    var color: Int = color
        set(value) {
            field = value
            setChanged()
            notifyObservers(false)
        }

    /**
     * add Observer to this section, **only one gauge can observe the section**.
     */
    fun inGauge(gauge: Gauge): Section {
        deleteObservers()
        addObserver(gauge)
        return this
    }


    private fun checkPercent() {
        require(!(speedOffset > 1f || speedOffset < 0f)) { "speedOffset must be between [0, 1]" }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(speedOffset)
        parcel.writeInt(color)
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