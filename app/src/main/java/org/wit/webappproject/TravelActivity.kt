package org.wit.webappproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class TravelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        Timber.plant(Timber.DebugTree())
        Timber.i("TravelActivity started")

        val addButton = findViewById<Button>(R.id.btnAddDestination)
        val nameInput = findViewById<EditText>(R.id.destinationName)
        val descriptionInput = findViewById<EditText>(R.id.destinationDescription)

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a destination name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Timber.i("Adding destination: $name - $description")

            nameInput.text.clear()
            descriptionInput.text.clear()

            Toast.makeText(this, "$name added!", Toast.LENGTH_SHORT).show()
        }
    }
}