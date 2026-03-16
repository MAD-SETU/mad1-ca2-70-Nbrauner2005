package org.wit.webappproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.webappproject.databinding.ActivityDestinationListBinding

// Activity responsible for displaying all saved destinations in a list
class DestinationListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinationListBinding

    // Mutable list that will store destinations removed from TravelActivity
    private val destinations = mutableListOf<DestinationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDestinationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val userDestinations =
            intent.getParcelableArrayListExtra<DestinationModel>("userDestinations")

        if (userDestinations != null) {
            destinations.addAll(userDestinations)
        }

        binding.recyclerView.adapter = DestinationAdapter(destinations)

    }
}