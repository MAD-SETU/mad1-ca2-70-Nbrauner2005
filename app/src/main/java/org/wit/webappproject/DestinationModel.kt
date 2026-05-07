package org.wit.webappproject

import android.os.Parcel
import android.os.Parcelable

// Data model representing a travel destination
// Parcelable allows objects to be passed between activities using Intents

data class DestinationModel(
    val name: String,
    val description: String,
    val activities: String = "",
    val rating: Float = 0f,
    val dateArrived: String = "",
    val dateLeft: String = "",
    var imageUri: String = ""
) : Parcelable {

    // Read data back from Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "" // imageUri
    )

    // Write data into Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(activities)
        parcel.writeFloat(rating)
        parcel.writeString(dateArrived)
        parcel.writeString(dateLeft)
        parcel.writeString(imageUri)
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