package com.studentnest.app.ui.listings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentnest.app.data.model.Listing
import com.studentnest.app.data.repository.ListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingsViewModel @Inject constructor(
    private val repository: ListingRepository
) : ViewModel() {

    private val _listings = MutableLiveData<List<Listing>>()
    val listings: LiveData<List<Listing>> = _listings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _reservationEvent = MutableLiveData<String?>()
    val reservationEvent: LiveData<String?> = _reservationEvent

    init {
        loadAllListings()
    }

    fun loadAllListings() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                repository.allListings.collect { listings ->
                    _listings.postValue(listings)
                    _isLoading.postValue(false)
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
            }
        }
    }

    fun applyFilters(location: String? = null, maxPrice: Double? = null, minDate: Long? = null) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                repository.filterListings(location, maxPrice, minDate).collect { listings ->
                    _listings.postValue(listings)
                    _isLoading.postValue(false)
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
            }
        }
    }
}
