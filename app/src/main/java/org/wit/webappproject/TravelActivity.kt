package org.wit.webappproject

/*
 * References:
 *
 * Android Activity Lifecycle:
 * https://developer.android.com/guide/components/activities/activity-lifecycle
 *
 * Activity Result API:
 * https://developer.android.com/training/basics/intents/result
 *
 * RecyclerView Documentation:
 * https://developer.android.com/guide/topics/ui/layout/recyclerview
 *
 * RecyclerView LayoutManager:
 * https://developer.android.com/reference/androidx/recyclerview/widget/LinearLayoutManager
 *
 * ViewBinding Documentation:
 * https://developer.android.com/topic/libraries/view-binding
 *
 * Google Maps Android SDK:
 * https://developers.google.com/maps/documentation/android-sdk
 *
 * Google Maps Marker Documentation:
 * https://developers.google.com/android/reference/com/google/android/gms/maps/model/MarkerOptions
 *
 * Gson JSON Serialization Library:
 * https://github.com/google/gson
 *
 * Android Internal Storage:
 * https://developer.android.com/training/data-storage/app-specific
 *
 * Material Design Components:
 * https://m3.material.io/
 *
 * DatePickerDialog Documentation:
 * https://developer.android.com/reference/android/app/DatePickerDialog
 *
 * Android Intents:
 * https://developer.android.com/reference/android/content/Intent
 *
 * Timber Logging Library:
 * https://github.com/JakeWharton/timber
 */

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

// Main activity responsible for managing the travel journal user interface and destination CRUD operations.
class TravelActivity : AppCompatActivity() {

    // Stores the currently selected image URI chosen from the device gallery.
    private var selectedImageUri: Uri? = null

    // Stores selected map coordinates returned from MapActivity
    private var selectedLocation = Location()

    // ViewBinding reference used instead of findViewById for safer UI access.
    private lateinit var binding: ActivityTravelBinding

    // Main in-memory list of all saved destinations.
    private val destinations = ArrayList<DestinationModel>()

    // RecyclerView adapter responsible for rendering destination cards.
    private lateinit var adapter: DestinationAdapter

    // JSON file used for local persistence
    private val JSON_FILE = "destinations.json"

    // // Gson library used for object serialization.
    private val gson = Gson()

    // Activity result launcher used to select destination images from device storage.
    // OpenDocument() implementation based on Android activity result API documentation : https://developer.android.com/training/basics/intents/result
    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            uri?.let {

                // Persist image permissions so selected images remain accessible after app restarts.
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                selectedImageUri = it

                binding.destinationPhoto.setImageURI(it)
            }
        }

    // Receives selected map coordinates from MapActivity using the Activity
    private val mapLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                result.data?.let {

                    selectedLocation =
                        if (
                            android.os.Build.VERSION.SDK_INT >=
                            android.os.Build.VERSION_CODES.TIRAMISU
                        ) {

                            it.getSerializableExtra(
                                "location",
                                Location::class.java
                            ) ?: Location()

                        } else {

                            @Suppress("DEPRECATION")

                            it.getSerializableExtra(
                                "location"
                            ) as? Location ?: Location()
                        }

                    binding.txtLocation.text =
                        "Lat: ${selectedLocation.lat}, " +
                                "Lng: ${selectedLocation.lng}"

                    Toast.makeText(
                        this,
                        "Location Selected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding =
            ActivityTravelBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        binding.destinationList.layoutManager =
            LinearLayoutManager(this)

        binding.btnViewMap.setOnClickListener {

            val intent = Intent(
                this,
                AllDestinationsMapActivity::class.java
            )

            intent.putParcelableArrayListExtra(
                "destinations",
                destinations
            )

            startActivity(intent)
        }

        // RecyclerView adapter handling: destination display, delete functionality, edit functionality, map navigation
        adapter = DestinationAdapter(

            destinations,

            // Removes destination from the list, updates JSON storage and refreshes UI
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

            // Populates form fields with an existing destination allowing users to edit and resave updated information
            { position ->

                val destination = destinations[position]

                binding.destinationName.setText(
                    destination.name
                )

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

                // RESTORE IMAGE
                if (destination.imageUri.isNotEmpty()) {

                    selectedImageUri =
                        Uri.parse(destination.imageUri)

                    binding.destinationPhoto.setImageURI(
                        selectedImageUri
                    )
                }

                // RESTORE LOCATION
                selectedLocation =
                    destination.location

                binding.txtLocation.text =
                    "Lat: ${selectedLocation.lat}, " +
                            "Lng: ${selectedLocation.lng}"

                // REMOVE OLD ITEM
                destinations.removeAt(position)

                saveDestinations()

                adapter.notifyItemRemoved(position)

                Toast.makeText(
                    this,
                    "Edit destination then press Add Destination",
                    Toast.LENGTH_LONG
                ).show()
            },

            // SHOW EDIT BUTTON
            true,

            // Opens Google Maps screen displaying the saved location of the destination
            { position ->

                val destination =
                    destinations[position]

                val intent =
                    Intent(this, MapActivity::class.java)

                intent.putExtra(
                    "location",
                    destination.location
                )

                startActivity(intent)
            }
        )

        binding.destinationList.adapter = adapter

        // LOAD SAVED DESTINATIONS
        loadDestinations()

        adapter.notifyDataSetChanged()

        val calendar = Calendar.getInstance()

        // Displays DatePickerDialog allowing users to select arrival date
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

        // DEPARTURE DATE
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

        // IMAGE BUTTON
        binding.btnAddImage.setOnClickListener {

            showImagePicker()
        }

        // LOCATION BUTTON
        binding.btnSetLocation.setOnClickListener {

            val intent =
                Intent(this, MapActivity::class.java)

            // IMPORTANT
            intent.putExtra(
                "selecting",
                true
            )

            mapLauncher.launch(intent)
        }

        // VIEW SAVED DESTINATIONS
        binding.btnViewDestinations.setOnClickListener {

            val intent =
                Intent(
                    this,
                    DestinationListActivity::class.java
                )

            intent.putParcelableArrayListExtra(
                "userDestinations",
                destinations
            )

            startActivity(intent)
        }

        // Validates user input, creates a new DestinationModel object and persists the destination to local JSON storage.
        binding.btnAddDestination.setOnClickListener {

            val name =
                binding.destinationName.text
                    .toString()
                    .trim()

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

            // VALIDATION
            if (name.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter a destination name",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Saving ${selectedLocation.lat}, ${selectedLocation.lng}",
                Toast.LENGTH_LONG
            ).show()

            // CREATE DESTINATION
            val destination = DestinationModel(
                name,
                description,
                activities,
                rating,
                arrived,
                left,
                selectedImageUri?.toString() ?: "",
                selectedLocation
            )

            // ADD TO LIST
            destinations.add(destination)

            saveDestinations()

            adapter.notifyDataSetChanged()

            Timber.i("Destination added: $destination")

            // CLEAR FORM
            binding.destinationName.text?.clear()

            binding.destinationDescription.text?.clear()

            binding.destinationActivities.text?.clear()

            binding.destinationRating.rating = 0f

            binding.dateArrived.text?.clear()

            binding.dateLeft.text?.clear()

            // RESET IMAGE
            selectedImageUri = null

            binding.destinationPhoto.setImageResource(
                R.drawable.world_icon
            )

            // RESET LOCATION
            selectedLocation = Location()

            binding.txtLocation.text =
                "No Location Selected"

            Toast.makeText(
                this,
                "$name added",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // IMAGE PICKER
    private fun showImagePicker() {

        imagePicker.launch(arrayOf("image/*"))
    }

    // Serializes all destination objects into JSON and writes them into internal device storage
    private fun saveDestinations() {

        val type =
            object : TypeToken<
                    ArrayList<DestinationModel>
                    >() {}.type

        val jsonString =
            gson.toJson(destinations, type)

        write(this, JSON_FILE, jsonString)

        Timber.i("Destinations saved")
    }

    // Reads saved JSON data from internal storage, converts JSON back into DestinationModel objects and restores the destination list.
    private fun loadDestinations() {

        try {

            if (exists(this, JSON_FILE)) {

                val jsonString =
                    read(this, JSON_FILE)

                val type =
                    object : TypeToken<
                            ArrayList<DestinationModel>
                            >() {}.type

                val loadedDestinations:
                        ArrayList<DestinationModel> =
                    gson.fromJson(jsonString, type)
                        ?: arrayListOf()

                destinations.clear()

                destinations.addAll(
                    loadedDestinations
                )

                Timber.i("Destinations loaded")
            }

        } catch (e: Exception) {

            Timber.e("LOAD ERROR: ${e.message}")

            Toast.makeText(
                this,
                "Error loading destinations",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}