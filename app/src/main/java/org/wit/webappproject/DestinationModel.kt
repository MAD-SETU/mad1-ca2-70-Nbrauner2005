package org.wit.webappproject

import android.os.Parcel
import android.os.Parcelable

data class DestinationModel(
    val name: String,
    val description: String,
    val activities: String = "",
    val rating: Float = 0f,
    val dateArrived: String = "",
    val dateLeft: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(activities)
        parcel.writeFloat(rating)
        parcel.writeString(dateArrived)
        parcel.writeString(dateLeft)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DestinationModel> {
        override fun createFromParcel(parcel: Parcel): DestinationModel {
            return DestinationModel(parcel)
        }

        override fun newArray(size: Int): Array<DestinationModel?> {
            return arrayOfNulls(size)
        }
    }
}