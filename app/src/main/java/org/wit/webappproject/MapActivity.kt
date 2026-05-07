package org.wit.webappproject

/*
* References:
*
* Google Maps Android SDK:
* https://developers.google.com/maps/documentation/android-sdk
*
* GoogleMap Documentation:
* https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap
*
* SupportMapFragment Documentation:
* https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment
*
* LatLng Documentation:
* https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLng
*
* MarkerOptions Documentation:
* https://developers.google.com/android/reference/com/google/android/gms/maps/model/MarkerOptions
*
* CameraUpdateFactory Documentation:
* https://developers.google.com/android/reference/com/google/android/gms/maps/CameraUpdateFactory
*
* Android Intent Documentation:
* https://developer.android.com/reference/android/content/Intent
*
* Activity Result API:
* https://developer.android.com/training/basics/intents/result
*/

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.webappproject.databinding.ActivityMapBinding

// Activity responsible for displaying Google Maps, viewing saved locations and selecting new coordinates.
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    // Google Map object reference
    private lateinit var mMap: GoogleMap

    // ViewBinding reference for accessing XML layout views.
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Inflate XML layout using ViewBinding. // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityMapBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Retrieve Google Maps fragment from layout.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        // Asynchronously prepare Google Map.
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        // Enable built in Google Maps controls for improved user interaction.
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.uiSettings.isCompassEnabled = true

        mMap.uiSettings.isMapToolbarEnabled = true

        // Determine whether Activity was opened for location selection or viewing only
        val selectingLocation =
            intent.getBooleanExtra(
                "selecting",
                false
            )

        // Retrieve saved destination location passed through Intent extras.
        val location =
            if (
                android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.TIRAMISU
            ) {

                // Android 13+ Serializable implementation.
                intent.getSerializableExtra(
                    "location",
                    Location::class.java
                ) ?: Location(-34.0, 151.0, 5f)

            } else {

                // Backwards compatibility for older android versions
                @Suppress("DEPRECATION")

                intent.getSerializableExtra(
                    "location"
                ) as? Location
                    ?: Location(53.3498, -6.2603, 6f)
            }

        // Convert custom location object into Google Maps LatLng coordinates
        val destinationLatLng =
            LatLng(location.lat, location.lng)

        // Move camera to destination location
        mMap.addMarker(
            MarkerOptions()
                .position(destinationLatLng)
                .title("Saved Destination")
        )

        // Move camera to destination location
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                destinationLatLng,
                location.zoom
            )
        )

        // Allow location selection only when opened from "Set Location" button.
        if (selectingLocation) {

            // Google Maps click listener. https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnMapClickListener
            mMap.setOnMapClickListener { latLng ->

                // Remove previous markers
                mMap.clear()

                // Add marker at selected location.
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                )

                // Create new location object using selected coordinates.
                val newLocation = Location(
                    latLng.latitude,
                    latLng.longitude,
                    mMap.cameraPosition.zoom
                )

                //Return selected location back to TravelActivity
                val resultIntent = Intent()

                resultIntent.putExtra(
                    "location",
                    newLocation
                )

                setResult(
                    RESULT_OK,
                    resultIntent
                )

                // Close MapActivity and return result.
                finish()
            }
        }
    }
}