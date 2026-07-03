package com.viagens.viewmodel

import android.app.Application
import android.location.Geocoder
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.viagens.data.local.database.AppDatabase
import com.viagens.data.local.entity.Photo
import com.viagens.data.local.entity.Trip
import com.viagens.data.repository.PhotoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class TripDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val tripDao = AppDatabase.getDatabase(application).tripDao()
    private val photoRepository = PhotoRepository(AppDatabase.getDatabase(application).photoDao())

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location.asStateFlow()

    fun loadTripDetails(tripId: Int) {
        viewModelScope.launch {
            val tripDetails = tripDao.getTripById(tripId)
            _trip.value = tripDetails
            
            tripDetails?.let {
                updateLocation(it.destination)
            }

            photoRepository.getPhotosByTrip(tripId).collect {
                _photos.value = it
            }
        }
    }

    private fun updateLocation(destination: String) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                val addresses = geocoder.getFromLocationName(destination, 1)
                if (!addresses.isNullOrEmpty()) {
                    _location.value = LatLng(addresses[0].latitude, addresses[0].longitude)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addPhoto(tripId: Int, uri: Uri) {
        viewModelScope.launch {
            val photo = Photo(tripId = tripId, imageUri = uri.toString())
            photoRepository.insertPhoto(photo)
        }
    }
}
