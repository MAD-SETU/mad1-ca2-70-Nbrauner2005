package org.wit.webappproject

import java.io.Serializable

data class Location(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 15f
) : Serializable