package org.wit.webappproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.webappproject.databinding.ActivityMapBinding

// Activity responsible for displaying all saved destinations together on one map.
class AllDestinationsMapActivity :
    AppCompatActivity(),
    OnMapReadyCallback {

    // Google map object reference
    private lateinit var mMap: GoogleMap

    // ViewBinding reference for accessing layout views.
    private lateinit var binding: ActivityMapBinding

    // Stores all destinations passed from TravelActivity.
    private val destinations =
        ArrayList<DestinationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding =
            ActivityMapBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Retrieves SupportMapFragment and prepares the Google Map asynchronously.
        val mapFragment =
            supportFragmentManager.findFragmentById(
                R.id.map
            ) as SupportMapFragment

        mapFragment.getMapAsync(this)

        // Receive saved destinations from TravelActivity
        val receivedDestinations =
            intent.getParcelableArrayListExtra<DestinationModel>(
                "destinations"
            )

        // Add all received destinations into local list
        if (receivedDestinations != null) {

            destinations.addAll(
                receivedDestinations
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        // Enable built in Google Maps controls for improved user interaction
        mMap.uiSettings.isZoomControlsEnabled =
            true

        mMap.uiSettings.isCompassEnabled =
            true

        mMap.uiSettings.isMapToolbarEnabled =
            true

        // Loop through all destinations and create a map marker for each saved location.
        destinations.forEach { destination ->

            val latLng = LatLng(
                destination.location.lat,
                destination.location.lng
            )

            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(destination.name)
            )
        }

        // Automatically move camera to the first destination in the list.
        if (destinations.isNotEmpty()) {

            val firstLocation = LatLng(
                destinations[0].location.lat,
                destinations[0].location.lng
            )

            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    firstLocation,
                    5f
                )
            )
        }
    }
}