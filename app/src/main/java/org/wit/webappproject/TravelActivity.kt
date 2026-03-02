package org.wit.webappproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

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

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()

            if (name.isEmpty()) { // name must not be empty
                Toast.makeText(this, "Please enter a destination name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Log the added destination for debugging
            Timber.i("Adding destination: $name - $description")

            // Clear the input fields after adding the destination
            nameInput.text.clear()
            descriptionInput.text.clear()

            // Display a toast to confirm the destination was added
            Toast.makeText(this, "$name added!", Toast.LENGTH_SHORT).show()
        }
    }
}