package com.hien.le.dkvfinder.feature.evcharging.poi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hien.le.dkvfinder.feature.evcharging.R
import com.hien.le.dkvfinder.feature.evcharging.databinding.ItemPoiBinding

interface PoiItemClickListener {
    fun onFavoriteClicked(poi: PoiItemUiState)
    fun onItemClicked(poi: PoiItemUiState)
}

class PoiAdapter(
    private val clickListener: PoiItemClickListener
) : ListAdapter<PoiItemUiState, PoiAdapter.PoiViewHolder>(PoiDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val binding = ItemPoiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoiViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        val poiItem = getItem(position)
        holder.bind(poiItem)
    }

    class PoiViewHolder(
        private val binding: ItemPoiBinding,
        private val clickListener: PoiItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(poi: PoiItemUiState) {
            binding.textViewPoiTitle.text = poi.title ?: "N/A"
            binding.textViewPoiAddress.text = poi.address ?: "Address not available"
            binding.textViewPoiTown.text = poi.town ?: ""

            if (poi.telephone != null) {
                binding.textViewPoiTelephone.text = poi.telephone
                binding.textViewPoiTelephone.visibility = android.view.View.VISIBLE
            } else {
                binding.textViewPoiTelephone.visibility = android.view.View.GONE
            }

            if (poi.distance != null) {
                val distanceUnitString = when (poi.distanceUnit) {
                    0 -> binding.root.context.getString(R.string.distance_unit_km)
                    1 -> binding.root.context.getString(R.string.distance_unit_miles)
                    else -> ""
                }
                // Get the formatted string from resources
                binding.textViewPoiDistance.text = binding.root.context.getString(
                    R.string.poi_distance_format,
                    poi.distance,
                    distanceUnitString
                )
                binding.textViewPoiDistance.visibility = android.view.View.VISIBLE
            } else {
                binding.textViewPoiDistance.visibility = android.view.View.GONE
            }

            // Update favorite button state
            if (poi.isFavorite) {
                binding.imageButtonFavorite.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                binding.imageButtonFavorite.setImageResource(R.drawable.ic_favorite_border)
            }

            // Set click listener for the favorite button
            binding.imageButtonFavorite.setOnClickListener {
                clickListener.onFavoriteClicked(poi)
            }

            // Set click listener for the whole item view
            binding.root.setOnClickListener {
                clickListener.onItemClicked(poi)
            }
        }
    }

    // DiffUtil helps ListAdapter determine how the list has changed
    class PoiDiffCallback : DiffUtil.ItemCallback<PoiItemUiState>() {
        override fun areItemsTheSame(oldItem: PoiItemUiState, newItem: PoiItemUiState): Boolean {
            // Check if the items represent the same object (e.g., by unique ID)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PoiItemUiState, newItem: PoiItemUiState): Boolean {
            // Check if the data within the items is the same
            return oldItem == newItem
        }
    }
}