package com.studentnest.app.ui.details

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
class DetailViewModel @Inject constructor(
    private val repository: ListingRepository
) : ViewModel() {

    private val _listing = MutableLiveData<Listing?>()
    val listing: LiveData<Listing?> get() = _listing

    /**
     * Fetches a single listing from the repository by its ID.
     */
    fun getListingById(id: Int) {
        viewModelScope.launch {
            // We ask the repository for the specific house
            val result = repository.getListingById(id)
            _listing.postValue(result)
        }
    }
}