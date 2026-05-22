package com.studentnest.app.data.repository

import com.studentnest.app.data.dao.ListingDao
import com.studentnest.app.data.model.Listing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListingRepository @Inject constructor(
    private val listingDao: ListingDao
) {
    val allListings: Flow<List<Listing>> = listingDao.getAllListings()

    suspend fun getListingById(id: Int): Listing? {
        return listingDao.getListingById(id)
    }

    fun filterListings(
        location: String? = null,
        maxPrice: Double? = null,
        minDate: Long? = null
    ): Flow<List<Listing>> {
        return when {
            location != null && location != "All Areas" && maxPrice != null && minDate != null -> {
                filterByLocationPriceDate(location, maxPrice, minDate)
            }
            location != null && location != "All Areas" && maxPrice != null -> {
                filterByLocationPrice(location, maxPrice)
            }
            location != null && location != "All Areas" && minDate != null -> {
                filterByLocationDate(location, minDate)
            }
            maxPrice != null && minDate != null -> {
                filterByPriceDate(maxPrice, minDate)
            }
            location != null && location != "All Areas" -> {
                listingDao.getListingsByLocation(location)
            }
            maxPrice != null -> {
                listingDao.getListingsByMaxPrice(maxPrice)
            }
            minDate != null -> {
                listingDao.getListingsByAvailabilityDate(minDate)
            }
            else -> allListings
        }
    }

    private fun filterByLocationPriceDate(
        location: String,
        maxPrice: Double,
        minDate: Long
    ): Flow<List<Listing>> {
        return listingDao.getListingsByLocation(location).map { listings ->
            listings.filter { it.priceBWP <= maxPrice && it.availabilityDate >= minDate }
        }
    }

    private fun filterByLocationPrice(location: String, maxPrice: Double): Flow<List<Listing>> {
        return listingDao.getListingsByLocation(location).map { listings ->
            listings.filter { it.priceBWP <= maxPrice }
        }
    }

    private fun filterByLocationDate(location: String, minDate: Long): Flow<List<Listing>> {
        return listingDao.getListingsByLocation(location).map { listings ->
            listings.filter { it.availabilityDate >= minDate }
        }
    }

    private fun filterByPriceDate(maxPrice: Double, minDate: Long): Flow<List<Listing>> {
        return allListings.map { listings ->
            listings.filter { it.priceBWP <= maxPrice && it.availabilityDate >= minDate }
        }
    }
}
