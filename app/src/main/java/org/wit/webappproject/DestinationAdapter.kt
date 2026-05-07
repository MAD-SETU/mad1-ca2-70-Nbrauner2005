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
    private val onDeleteClick: (Int) -> Unit,
    private val onEditClick: (Int) -> Unit,
    private val showEditButton: Boolean = true,
    private val onCardClick: (Int) -> Unit
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

        val btnEdit: Button =
            itemView.findViewById(R.id.btnEdit)
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

        holder.btnDelete.setOnClickListener {

            onDeleteClick(position)
        }

        if (showEditButton) {

            holder.btnEdit.visibility = View.VISIBLE

            holder.btnEdit.setOnClickListener {

                onEditClick(position)
            }

        } else {

            holder.btnEdit.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {

            onCardClick(position)
        }
    }
}