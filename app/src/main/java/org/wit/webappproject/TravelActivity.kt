package org.wit.webappproject

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.wit.webappproject.databinding.ActivityTravelBinding
import timber.log.Timber
import java.util.Calendar

// Screen where users add destinations and see them in a list
class TravelActivity : AppCompatActivity() {

    // Stores selected image URI
    private var selectedImageUri: Uri? = null

    // ViewBinding object
    private lateinit var binding: ActivityTravelBinding

    // List storing all destinations
    private val destinations = ArrayList<DestinationModel>()

    // RecyclerView adapter
    private lateinit var adapter: DestinationAdapter

    // JSON file name
    private val JSON_FILE = "destinations.json"

    // Gson object for JSON conversion
    private val gson = Gson()

    // Image picker launcher
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->

            uri?.let {

                // Keep image permission after app restart
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                selectedImageUri = it

                // Display selected image
                binding.destinationPhoto.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout using ViewBinding
        binding = ActivityTravelBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        Timber.i("TravelActivity started")

        // RecyclerView setup
        binding.destinationList.layoutManager =
            LinearLayoutManager(this)

        adapter = DestinationAdapter(

            destinations,

            // DELETE
            { position ->

                destinations.removeAt(position)

                saveDestinations()

                adapter.notifyItemRemoved(position)

                Toast.makeText(
                    this,
                    "Destination deleted",
                    Toast.LENGTH_SHORT
                ).show()
            },

            // EDIT
            { position ->

                val destination = destinations[position]

                // Fill form fields
                binding.destinationName.setText(destination.name)

                binding.destinationDescription.setText(
                    destination.description
                )

                binding.destinationActivities.setText(
                    destination.activities
                )

                binding.destinationRating.rating =
                    destination.rating

                binding.dateArrived.setText(
                    destination.dateArrived
                )

                binding.dateLeft.setText(
                    destination.dateLeft
                )

                // Restore image
                if (destination.imageUri.isNotEmpty()) {

                    selectedImageUri =
                        Uri.parse(destination.imageUri)

                    binding.destinationPhoto.setImageURI(
                        selectedImageUri
                    )
                }

                // Remove old item temporarily
                destinations.removeAt(position)

                saveDestinations()

                adapter.notifyItemRemoved(position)

                Toast.makeText(
                    this,
                    "Edit the destination and press Add Destination to save changes",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        binding.destinationList.adapter = adapter

        // Load saved destinations
        loadDestinations()

        adapter.notifyDataSetChanged()

        val calendar = Calendar.getInstance()

        // Arrival Date Picker
        binding.dateArrived.setOnClickListener {

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->

                    binding.dateArrived.setText(
                        "$day/${month + 1}/$year"
                    )
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

                    binding.dateLeft.setText(
                        "$day/${month + 1}/$year"
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }

        // Open image picker
        binding.btnAddImage.setOnClickListener {

            showImagePicker()
        }

        // Open saved destinations screen
        binding.btnViewDestinations.setOnClickListener {

            val intent =
                Intent(this, DestinationListActivity::class.java)

            intent.putParcelableArrayListExtra(
                "userDestinations",
                destinations
            )

            startActivity(intent)
        }

        // Add destination button
        binding.btnAddDestination.setOnClickListener {

            val name =
                binding.destinationName.text.toString().trim()

            val description =
                binding.destinationDescription.text
                    .toString()
                    .trim()

            val activities =
                binding.destinationActivities.text
                    .toString()
                    .trim()

            val rating =
                binding.destinationRating.rating

            val arrived =
                binding.dateArrived.text.toString()

            val left =
                binding.dateLeft.text.toString()

            // Validation check
            if (name.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter a destination name",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Create destination object
            val destination = DestinationModel(
                name,
                description,
                activities,
                rating,
                arrived,
                left,
                selectedImageUri?.toString() ?: ""
            )

            // Add destination
            destinations.add(destination)

            // Save destinations
            saveDestinations()

            // Refresh RecyclerView
            adapter.notifyDataSetChanged()

            Timber.i("Destination added: $destination")

            // Clear form fields
            binding.destinationName.text.clear()

            binding.destinationDescription.text.clear()

            binding.destinationActivities.text.clear()

            binding.destinationRating.rating = 0f

            binding.dateArrived.text.clear()

            binding.dateLeft.text.clear()

            // Reset image
            selectedImageUri = null

            binding.destinationPhoto.setImageResource(
                R.drawable.world_icon
            )

            Toast.makeText(
                this,
                "$name added",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Opens image picker
    private fun showImagePicker() {

        imagePicker.launch(arrayOf("image/*"))
    }

    // Save destinations to JSON
    private fun saveDestinations() {

        val type =
            object : TypeToken<ArrayList<DestinationModel>>() {}.type

        val jsonString =
            gson.toJson(destinations, type)

        write(this, JSON_FILE, jsonString)

        Timber.i("Destinations saved to JSON")
    }

    // Load destinations from JSON
    private fun loadDestinations() {

        try {

            if (exists(this, JSON_FILE)) {

                val jsonString =
                    read(this, JSON_FILE)

                Timber.i("JSON Loaded: $jsonString")

                val type =
                    object : TypeToken<ArrayList<DestinationModel>>() {}.type

                val loadedDestinations:
                        ArrayList<DestinationModel> =
                    gson.fromJson(jsonString, type)
                        ?: arrayListOf()

                destinations.clear()

                destinations.addAll(loadedDestinations)

                Timber.i("Destinations loaded successfully")
            }

        } catch (e: Exception) {

            Timber.e("LOAD ERROR: ${e.message}")

            Toast.makeText(
                this,
                "Error loading saved destinations",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}