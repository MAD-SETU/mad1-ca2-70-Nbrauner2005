package org.wit.webappproject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.webappproject.databinding.ActivityTravelBinding
import timber.log.Timber
import java.util.Calendar

// Screen where users add destinations and see them in a list
class TravelActivity : AppCompatActivity() {

    // ViewBinding object used instead of findViewById
    private lateinit var binding: ActivityTravelBinding

    // ArrayList used to temporarily store destinations added by the user
    // This data is kept in memory and passed to another activity when viewing the list
    private val destinations = ArrayList<DestinationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityTravelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        Timber.i("TravelActivity started")

        binding.destinationList.layoutManager = LinearLayoutManager(this)
        val adapter = DestinationAdapter(destinations)
        binding.destinationList.adapter = adapter

        val calendar = Calendar.getInstance()

        // Arrival Date Picker
        binding.dateArrived.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    binding.dateArrived.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Departure Date Picker
        binding.dateLeft.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    binding.dateLeft.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        //Open the list screen
        binding.btnViewDestinations.setOnClickListener {
            val intent = Intent(this, DestinationListActivity::class.java)
            intent.putParcelableArrayListExtra("userDestinations", destinations)
            startActivity(intent)
        }

        // Add destination button
        binding.btnAddDestination.setOnClickListener {
            val name = binding.destinationName.text.toString().trim()
            val description = binding.destinationDescription.text.toString().trim()
            val activities = binding.destinationActivities.text.toString().trim()
            val rating = binding.destinationRating.rating
            val arrived = binding.dateArrived.text.toString()
            val left = binding.dateLeft.text.toString()

            // Validate the name field
            if(name.isEmpty()) {
                Toast.makeText(this, "Please enter a destination name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val destination = DestinationModel(
                name,
                description,
                activities,
                rating,
                arrived,
                left
            )

            destinations.add(destination)

            adapter.notifyDataSetChanged()

            Timber.i("Destination added: $destination")

            binding.destinationName.text.clear()
            binding.destinationDescription.text.clear()
            binding.destinationActivities.text.clear()
            binding.destinationRating.rating = 0f
            binding.dateArrived.text.clear()
            binding.dateLeft.text.clear()

            Toast.makeText(this, "$name added", Toast.LENGTH_SHORT).show()
        }
    }
}

