package org.wit.webappproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.webappproject.databinding.ActivityDestinationListBinding

// Activity responsible for displaying all saved destinations in a list
class DestinationListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinationListBinding

    // Mutable list storing destinations received from TravelActivity
    private val destinations = mutableListOf<DestinationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout using ViewBinding
        binding = ActivityDestinationListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Toolbar back button
        binding.topToolbar.setNavigationOnClickListener {

            finish()
        }

        // RecyclerView setup
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)

        // Receive destinations from TravelActivity
        val userDestinations =
            intent.getParcelableArrayListExtra<DestinationModel>(
                "userDestinations"
            )

        // Add received destinations to list
        if (userDestinations != null) {

            destinations.addAll(userDestinations)
        }

        // Attach adapter
        binding.recyclerView.adapter =

            DestinationAdapter(

                destinations,

                // Delete functionality
                { position ->

                    destinations.removeAt(position)

                    binding.recyclerView.adapter
                        ?.notifyItemRemoved(position)
                },

                // Empty edit callback
                {},

                // Hide edit button on this screen
                false
            )
    }
}