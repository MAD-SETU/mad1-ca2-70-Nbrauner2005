package org.wit.webappproject

/*
* References:
*
* Parcelable Documentation:
* https://developer.android.com/reference/android/os/Parcelable
*
* Parcel Documentation:
* https://developer.android.com/reference/android/os/Parcel
*
* Android Intent Object Passing:
* https://developer.android.com/guide/components/activities/parcelables-and-bundles
*
* Kotlin Data Classes:
* https://kotlinlang.org/docs/data-classes.html
*/

import android.os.Parcel
import android.os.Parcelable

// Main data model used throughout the app to represent saved travel destinations.
data class DestinationModel(
    val name: String,
    val description: String,
    val activities: String = "",
    val rating: Float = 0f,
    val dateArrived: String = "",
    val dateLeft: String = "",
    var imageUri: String = "",
    var location: Location = Location() // Saved Google Maps location coordinates
) : Parcelable {

    // Reconstructs DestinationModel object from parcel data when passed between activites
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readSerializable() as? Location ?: Location() // Deserialize saved location object
    )

    // Writes DestinationModel properties into parcel object for activity transfer.
    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(name)

        parcel.writeString(description)

        parcel.writeString(activities)

        parcel.writeFloat(rating)

        parcel.writeString(dateArrived)

        parcel.writeString(dateLeft)

        parcel.writeString(imageUri)

        parcel.writeSerializable(location) // Serialize map location object
    }

    // Standard parcelable implementation
    override fun describeContents(): Int = 0

    // Companion object required for Parcelable object recreation by android framework
    companion object CREATOR : Parcelable.Creator<DestinationModel> {

        // Creates DestinationModel from Parcel.
        override fun createFromParcel(
            parcel: Parcel
        ): DestinationModel {

            return DestinationModel(parcel)
        }

        // Creates Parcelable array
        override fun newArray(
            size: Int
        ): Array<DestinationModel?> {

            return arrayOfNulls(size)
        }
    }
}