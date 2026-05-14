package com.viagens.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.viagens.data.local.database.AppDatabase
import com.viagens.data.local.entity.Trip
import com.viagens.data.local.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val tripDao = db.tripDao()
    private val userDao = db.userDao()
    private val sessionManager = SessionManager(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips

    private val _currentTrip = MutableStateFlow<Trip?>(null)
    val currentTrip: StateFlow<Trip?> = _currentTrip

    private val _currentCity = MutableStateFlow<String?>(null)
    val currentCity: StateFlow<String?> = _currentCity

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            val email = sessionManager.getUser()
            if (email != null) {
                val user = userDao.getUserByEmail(email)
                if (user != null) {
                    tripDao.getTripsByUser(user.id).collectLatest {
                        _trips.value = it
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationAndSearchTrip() {
        val cts = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cts.token
        ).addOnSuccessListener { location: Location? ->
            location?.let {
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                try {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val city = addresses[0].locality
                        _currentCity.value = city
                        city?.let { cityName ->
                            searchCurrentTrip(cityName)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun searchCurrentTrip(city: String) {
        viewModelScope.launch {
            val email = sessionManager.getUser()
            if (email != null) {
                val user = userDao.getUserByEmail(email)
                if (user != null) {
                    val currentTime = System.currentTimeMillis()
                    val trip = tripDao.findTripByCityAndDate(user.id, city, currentTime)
                    _currentTrip.value = trip
                }
            }
        }
    }

    suspend fun getTripById(id: Int): Trip? {
        return tripDao.getTripById(id)
    }

    fun saveTrip(
        id: Int = 0,
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
}
