package org.wit.webappproject

/*
* References:
*
* Google Maps Android SDK:
* https://developers.google.com/maps/documentation/android-sdk
*
* Serializable Interface:
* https://developer.android.com/reference/java/io/Serializable
*
* Kotlin Data Classes:
* https://kotlinlang.org/docs/data-classes.html
*
* LatLng Reference:
* https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLng
*/

import java.io.Serializable

// Stores geographical coordinates and map zoom level for destination locations selected on Google Maps.
data class Location(
    var lat: Double = 0.0, // Latitude
    var lng: Double = 0.0, // Longitude
    var zoom: Float = 15f // Camera zoom level
) : Serializable