package org.wit.webappproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DestinationAdapter(private val destinations: List<DestinationModel>) :
    RecyclerView.Adapter<DestinationAdapter.MainHolder>() {

    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardName: TextView = itemView.findViewById(R.id.cardName)
        val cardDescription: TextView = itemView.findViewById(R.id.cardDescription)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.destination_card, parent, false)
        return MainHolder(view)
    }

    override fun getItemCount(): Int = destinations.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val destination = destinations[position]

        holder.cardName.text = destination.name
        holder.cardDescription.text = destination.description
    }
}