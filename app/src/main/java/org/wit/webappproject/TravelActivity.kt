
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

    // ArrayList used to temporarily store destinations added by the user
    // This data is kept in memory and passed to another activity when viewing the list
    private val destinations = ArrayList<DestinationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Links this activity to the activity_travel.xml layout
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

        // RecyclerView is used instead of ListView because it is more efficient for
        // displaying dynamic lists of data. It reuses view items as the user scrolls,
        // which improves performance when handling larger datasets.
        recyclerView.layoutManager = LinearLayoutManager(this)

        // The Adapter acts as a bridge between the destination data and the RecyclerView UI.
        // It takes the list of DestinationModel objects and binds each item to a card layout
        // so it can be displayed in the list.
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

        // Intent is used to navigate from TravelActivity to DestinationListActivity.
        // The list of destinations is passed using Parcelable so the data can be transferred
        // between activities
        viewButton.setOnClickListener {
            val intent = Intent(this, DestinationListActivity::class.java)
            intent.putParcelableArrayListExtra("userDestinations", destinations)
            startActivity(intent)
        }

        // When the user pressed the Add Destination button
        addButton.setOnClickListener {


            // Read the values entered by the user
            val name = nameInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val activities = activitiesInput.text.toString().trim()
            val rating = ratingBar.rating
            val arrived = dateArrived.text.toString()
            val left = dateLeft.text.toString()

            // Validate that the destination name is not empty
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a destination name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new DestinationModel object with the user input
            val destination = DestinationModel(
                name,
                description,
                activities,
                rating,
                arrived,
                left
            )

            // Add the destination to the list
            destinations.add(destination)

            // Notify RecyclerView that the data has changed so it refreshes
            // the displayed list and shows the newly added destination.
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

