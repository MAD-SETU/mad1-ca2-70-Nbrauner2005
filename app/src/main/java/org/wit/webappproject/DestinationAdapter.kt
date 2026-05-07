package org.wit.webappproject

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DestinationAdapter(
    private val destinations: MutableList<DestinationModel>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<DestinationAdapter.MainHolder>() {

    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardName: TextView =
            itemView.findViewById(R.id.cardName)

        val cardDescription: TextView =
            itemView.findViewById(R.id.cardDescription)

        val cardImage: ImageView =
            itemView.findViewById(R.id.cardImage)

        val btnDelete: Button =
            itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.destination_card, parent, false)

        return MainHolder(view)
    }

    override fun getItemCount(): Int = destinations.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {

        val destination = destinations[position]

        holder.cardName.text = destination.name

        holder.cardDescription.text = destination.description

        // Load image safely
        try {

            if (destination.imageUri.isNotEmpty()) {

                holder.cardImage.setImageURI(
                    Uri.parse(destination.imageUri)
                )
            }

        } catch (e: Exception) {

            holder.cardImage.setImageResource(
                R.drawable.world_icon
            )
        }

        // Delete button click
        holder.btnDelete.setOnClickListener {

            onDeleteClick(position)
        }
    }
}