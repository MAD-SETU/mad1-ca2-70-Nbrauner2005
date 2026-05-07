package org.wit.webappproject

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

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.uiSettings.isCompassEnabled = true

        mMap.uiSettings.isMapToolbarEnabled = true

        // CHECK IF USER IS SELECTING LOCATION
        val selectingLocation =
            intent.getBooleanExtra(
                "selecting",
                false
            )

        // GET SAVED LOCATION
        val location =
            if (
                android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.TIRAMISU
            ) {

                intent.getSerializableExtra(
                    "location",
                    Location::class.java
                ) ?: Location(-34.0, 151.0, 5f)

            } else {

                @Suppress("DEPRECATION")

                intent.getSerializableExtra(
                    "location"
                ) as? Location
                    ?: Location(53.3498, -6.2603, 6f)
            }

        val destinationLatLng =
            LatLng(location.lat, location.lng)

        // SHOW SAVED MARKER
        mMap.addMarker(
            MarkerOptions()
                .position(destinationLatLng)
                .title("Saved Destination")
        )

        // MOVE CAMERA
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                destinationLatLng,
                location.zoom
            )
        )

        // ONLY ALLOW LOCATION SELECTION
        // WHEN OPENED FROM SET LOCATION BUTTON
        if (selectingLocation) {

            mMap.setOnMapClickListener { latLng ->

                mMap.clear()

                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                )

                val newLocation = Location(
                    latLng.latitude,
                    latLng.longitude,
                    mMap.cameraPosition.zoom
                )

                val resultIntent = Intent()

                resultIntent.putExtra(
                    "location",
                    newLocation
                )

                setResult(
                    RESULT_OK,
                    resultIntent
                )

                finish()
            }
        }
    }
}