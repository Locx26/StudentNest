package com.studentnest.app.ui.listings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.studentnest.app.R
import com.studentnest.app.data.model.Listing
import com.studentnest.app.databinding.ItemListingBinding

class ListingsAdapter(
    private var listings: List<Listing>,
    private val onClick: (Listing) -> Unit
) : RecyclerView.Adapter<ListingsAdapter.ListingViewHolder>() {

    inner class ListingViewHolder(private val binding: ItemListingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listing: Listing) {
            val context = binding.root.context

            // 1. SET TEXT DATA
            binding.tvTitle.text = listing.title
            binding.tvLocation.text = listing.location

            // FIX: Changed listing.price to listing.priceBWP (Requirement B)
            binding.tvPrice.text = context.getString(R.string.currency_format, listing.priceBWP)

            // 2. PROFESSIONAL IMAGE LOADING (Glide)
            Glide.with(context)
                .load(listing.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(binding.ivListingImage)

            // 3. DYNAMIC STATUS LOGIC
            if (listing.isReserved) {
                // Requirement D: Visual feedback for reserved rooms
                binding.tvStatus.text = context.getString(R.string.status_occupied)
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_pill_occupied)
            } else {
                binding.tvStatus.text = context.getString(R.string.status_available)
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_pill)
            }

            // 4. CLICK LISTENER
            binding.root.setOnClickListener { onClick(listing) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = ItemListingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(listings[position])
    }

    override fun getItemCount() = listings.size

    fun updateData(newList: List<Listing>) {
        listings = newList
        notifyDataSetChanged()
    }
}
