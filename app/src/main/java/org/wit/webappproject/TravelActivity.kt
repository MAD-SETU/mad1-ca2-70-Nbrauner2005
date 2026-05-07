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

class TravelActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    private var selectedLocation = Location()

    private lateinit var binding: ActivityTravelBinding

    private val destinations = ArrayList<DestinationModel>()

    private lateinit var adapter: DestinationAdapter

    private val JSON_FILE = "destinations.json"

    private val gson = Gson()

    // IMAGE PICKER
    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            uri?.let {

                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                selectedImageUri = it

                binding.destinationPhoto.setImageURI(it)
            }
        }

    // MAP RESULT
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

        // ADAPTER
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

            // OPEN SAVED MAP
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

        // ARRIVAL DATE
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

        // ADD DESTINATION
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

    // SAVE JSON
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

    // LOAD JSON
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