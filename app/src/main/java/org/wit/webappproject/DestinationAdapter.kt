package org.wit.webappproject

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter used to bind destination data into RecyclerView destination cards.
class DestinationAdapter(
    // Main list of destination objects
    private val destinations: MutableList<DestinationModel>,
    // Callback triggered when delete button is pressed
    private val onDeleteClick: (Int) -> Unit,
    // Callback triggered when edit button is pressed
    private val onEditClick: (Int) -> Unit,
    // Controls edit button visibility depending on which screen is displaying the adapter
    private val showEditButton: Boolean = true,
    // Callback triggered when destination card is selected.
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<DestinationAdapter.MainHolder>() {

    // ViewHolder pattern improves RecyclerView performance by recycling existing card views.
    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Destination title text
        val cardName: TextView =
            itemView.findViewById(R.id.cardName)

        // Destination description preview
        val cardDescription: TextView =
            itemView.findViewById(R.id.cardDescription)

        // Destination image preview
        val cardImage: ImageView =
            itemView.findViewById(R.id.cardImage)

        // Delete destination button
        val btnDelete: Button =
            itemView.findViewById(R.id.btnDelete)

        // Edit destination button
        val btnEdit: Button =
            itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainHolder {

        // Inflates destination card XML layout into RecyclerView rows.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.destination_card, parent, false)

        return MainHolder(view)
    }

    // Returns total number of destinations.
    override fun getItemCount(): Int = destinations.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {

        val destination = destinations[position]

        // Bind destination name and description into the destination card UI
        holder.cardName.text = destination.name

        holder.cardDescription.text = destination.description

        // Attempts to load saved image URI. Falls back to default image if loading fails.
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

        // Delete button callback.
        holder.btnDelete.setOnClickListener {

            onDeleteClick(position)
        }

        // Conditionally display edit button depending on Activity requirements.
        if (showEditButton) {

            holder.btnEdit.visibility = View.VISIBLE

            holder.btnEdit.setOnClickListener {

                onEditClick(position)
            }

        } else {

            holder.btnEdit.visibility = View.GONE
        }

        // Opens destination map location when destination card is selcted
        holder.itemView.setOnClickListener {

            onCardClick(position)
        }
    }
}