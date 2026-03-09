package org.wit.webappproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// RecyclerView Adapter responsible for binding destination data to the card layout
class DestinationAdapter(private val destinations: List<DestinationModel>) :
    RecyclerView.Adapter<DestinationAdapter.MainHolder>() {

    // ViewHolder class holds references to the views inside each card
    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // TextViews that display the destination information
        val cardName: TextView = itemView.findViewById(R.id.cardName)
        val cardDescription: TextView = itemView.findViewById(R.id.cardDescription)

    }

    // Called when RecyclerView needs to create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {

        // Inflate the destination_card.xml layout for each item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.destination_card, parent, false)
        return MainHolder(view)
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = destinations.size

    // Binds the data from DestinationModel to the UI components in the card
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val destination = destinations[position]

        holder.cardName.text = destination.name
        holder.cardDescription.text = destination.description
    }
}