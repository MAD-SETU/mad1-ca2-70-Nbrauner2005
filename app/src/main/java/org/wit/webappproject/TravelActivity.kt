
package org.wit.webappproject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import java.util.Calendar

// Screen where users add destinations and see them in a list
class TravelActivity : AppCompatActivity() {

    private val destinations = ArrayList<DestinationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        Timber.plant(Timber.DebugTree())
        Timber.i("TravelActivity started")

        val addButton = findViewById<Button>(R.id.btnAddDestination)
        val viewButton = findViewById<Button>(R.id.btnViewDestinations)

        val nameInput = findViewById<EditText>(R.id.destinationName)
        val descriptionInput = findViewById<EditText>(R.id.destinationDescription)
        val activitiesInput = findViewById<EditText>(R.id.destinationActivities)

        val ratingBar = findViewById<RatingBar>(R.id.destinationRating)

        val dateArrived = findViewById<EditText>(R.id.dateArrived)
        val dateLeft = findViewById<EditText>(R.id.dateLeft)

        val recyclerView = findViewById<RecyclerView>(R.id.destinationList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = DestinationAdapter(destinations)
        recyclerView.adapter = adapter

        val calendar = Calendar.getInstance()

        // Arrival Date Picker
        dateArrived.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    dateArrived.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Departure Date Picker
        dateLeft.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    dateLeft.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Open the list screen
        viewButton.setOnClickListener {
            val intent = Intent(this, DestinationListActivity::class.java)
            intent.putParcelableArrayListExtra("userDestinations", destinations)
            startActivity(intent)
        }

        // Add destination button
        addButton.setOnClickListener {

            val name = nameInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val activities = activitiesInput.text.toString().trim()
            val rating = ratingBar.rating
            val arrived = dateArrived.text.toString()
            val left = dateLeft.text.toString()

            if (name.isEmpty()) {
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

            Timber.i("Name: $name")
            Timber.i("Description: $description")
            Timber.i("Activities: $activities")
            Timber.i("Rating: $rating")
            Timber.i("Arrived: $arrived")
            Timber.i("Left: $left")
            Timber.i("Destination added: $destination")

            nameInput.text.clear()
            descriptionInput.text.clear()
            activitiesInput.text.clear()
            ratingBar.rating = 0f
            dateArrived.text.clear()
            dateLeft.text.clear()

            Toast.makeText(this, "$name added", Toast.LENGTH_SHORT).show()
        }
    }
}

