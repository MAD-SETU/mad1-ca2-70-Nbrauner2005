package org.wit.webappproject

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import java.util.Calendar

// Layout for the screen where users can add a new destination and view there already saved destination
class TravelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        Timber.plant(Timber.DebugTree())
        Timber.i("TravelActivity started")

        val addButton = findViewById<Button>(R.id.btnAddDestination) // Button for submitting the new destination
        val nameInput = findViewById<EditText>(R.id.destinationName) // EditText where the user enters the destination name
        val descriptionInput = findViewById<EditText>(R.id.destinationDescription) // EditText for the destination description

        val activitiesInput = findViewById<EditText>(R.id.destinationActivities)
        val ratingBar = findViewById<RatingBar>(R.id.destinationRating)
        val dateArrived = findViewById<EditText>(R.id.dateArrived)
        val dateLeft = findViewById<EditText>(R.id.dateLeft)

        val calendar = Calendar.getInstance()

// Date picker for arrival date
        dateArrived.setOnClickListener {
            val datePicker = DatePickerDialog(this,
                { _, year, month, day ->
                    dateArrived.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Date picker for leaving date
        dateLeft.setOnClickListener {
            val datePicker = DatePickerDialog(this,
                { _, year, month, day ->
                    dateLeft.setText("$day/${month + 1}/$year")
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }


        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val activities = activitiesInput.text.toString().trim()
            val rating = ratingBar.rating
            val arrived = dateArrived.text.toString()
            val left = dateLeft.text.toString()

            if (name.isEmpty()) { // name must not be empty
                Toast.makeText(this, "Please enter a destination name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Timber.i("Description: $name")
            Timber.i("Description: $description")
            Timber.i("Activities: $activities")
            Timber.i("Rating: $rating")
            Timber.i("Arrived: $arrived")
            Timber.i("Left: $left")

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