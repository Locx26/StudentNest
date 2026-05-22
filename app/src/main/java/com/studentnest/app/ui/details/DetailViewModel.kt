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
    val listing: LiveData<Listing?> = _listing

    fun getListingById(id: Int) {
        viewModelScope.launch {
            val result = repository.getListingById(id)
            _listing.postValue(result)
        }
    }
}
