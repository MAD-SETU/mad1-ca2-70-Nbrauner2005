package org.wit.webappproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Activity responsible for displaying all saved destinations in a list
class DestinationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    // Mutable list that will store destinations removed from TravelActivity
    private val destinations = mutableListOf<DestinationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Connects this activity to the destination list layout
        setContentView(R.layout.activity_destination_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialise RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Retrieve the list of destinations passed through the Intent
        val userDestinations =
            intent.getParcelableArrayListExtra<DestinationModel>("userDestinations")

        // If destinations exist, add them to the list
        if (userDestinations != null) {
            destinations.addAll(userDestinations)
        }

        // Set the adapter to display the destinations
        recyclerView.adapter = DestinationAdapter(destinations)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}