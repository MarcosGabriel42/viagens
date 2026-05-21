package com.viagens.viewmodel

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.viagens.data.local.database.AppDatabase
import com.viagens.data.local.entity.Trip
import com.viagens.data.local.session.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val tripDao = AppDatabase.getDatabase(application).tripDao()
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val sessionManager = SessionManager(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _currentTrip = MutableStateFlow<Trip?>(null)
    val currentTrip: StateFlow<Trip?> = _currentTrip.asStateFlow()

    private val _currentCity = MutableStateFlow<String?>(null)
    val currentCity: StateFlow<String?> = _currentCity.asStateFlow()

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            val email = sessionManager.getUser()
            if (email != null) {
                val user = userDao.getUserByEmail(email)
                if (user != null) {
                    tripDao.getTripsByUser(user.id).collect {
                        _trips.value = it
                    }
                }
            }
        }
    }

    suspend fun getTripById(id: Int): Trip? {
        return tripDao.getTripById(id)
    }

    fun saveTrip(
        id: Int,
        destination: String,
        type: String,
        startDate: Long,
        endDate: Long,
        budget: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val email = sessionManager.getUser()
            if (email == null) {
                onError("Usuário não logado")
                return@launch
            }

            val user = userDao.getUserByEmail(email)
            if (user == null) {
                onError("Usuário não encontrado")
                return@launch
            }

            val trip = Trip(
                id = id,
                destination = destination,
                type = type,
                startDate = startDate,
                endDate = endDate,
                budget = budget,
                userId = user.id
            )

            if (id == 0) {
                tripDao.insert(trip)
            } else {
                tripDao.update(trip)
            }
            onSuccess()
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            tripDao.delete(trip)
        }
    }

    fun requestLocationAndSearchTrip() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        searchTripByLocation(location.latitude, location.longitude)
                    }
                }
        } catch (e: SecurityException) {
            // Permissão não concedida, tratado na UI
        }
    }

    private fun searchTripByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality
                    _currentCity.value = city
                    
                    val email = sessionManager.getUser()
                    if (email != null && city != null) {
                        val user = userDao.getUserByEmail(email)
                        if (user != null) {
                            val trip = tripDao.findTripByCityAndDate(
                                user.id,
                                city,
                                System.currentTimeMillis()
                            )
                            _currentTrip.value = trip
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
