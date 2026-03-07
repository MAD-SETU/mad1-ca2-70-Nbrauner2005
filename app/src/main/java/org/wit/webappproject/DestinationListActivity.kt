package org.wit.webappproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DestinationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val destinations = mutableListOf<DestinationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userDestinations =
            intent.getParcelableArrayListExtra<DestinationModel>("userDestinations")

        if (userDestinations != null) {
            destinations.addAll(userDestinations)
        }

        recyclerView.adapter = DestinationAdapter(destinations)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}