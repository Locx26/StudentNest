package com.studentnest.app.ui.listings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.studentnest.app.R
import com.studentnest.app.data.model.Listing
import com.studentnest.app.databinding.ItemListingBinding

class ListingsAdapter(
    private val onClick: (Listing) -> Unit
) : ListAdapter<Listing, ListingsAdapter.ListingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = ItemListingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListingViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ListingViewHolder(
        private val binding: ItemListingBinding,
        private val onClick: (Listing) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listing: Listing) {
            binding.apply {
                tvTitle.text = listing.title
                tvLocation.text = listing.location
                tvPrice.text = "BWP \u0050${String.format("%,.0f", listing.priceBWP)} / month"

                Glide.with(ivImage.context)
                    .load(listing.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(ivImage)

                if (listing.isReserved) {
                    tvStatus.text = "Occupied"
                    tvStatus.setBackgroundColor(tvStatus.context.getColor(R.color.red_500))
                } else {
                    tvStatus.text = "Available"
                    tvStatus.setBackgroundColor(tvStatus.context.getColor(R.color.green_500))
                }

                root.setOnClickListener {
                    onClick(listing)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Listing>() {
        override fun areItemsTheSame(oldItem: Listing, newItem: Listing) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Listing, newItem: Listing) = oldItem == newItem
    }
}
