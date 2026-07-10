package com.viagens.viewmodel

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.viagens.data.local.database.AppDatabase
import com.viagens.data.local.entity.Trip
import com.viagens.data.local.entity.Photo
import com.viagens.data.local.entity.Itinerary
import com.viagens.data.local.session.SessionManager
import com.viagens.data.repository.GeminiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val tripDao = db.tripDao()
    private val userDao = db.userDao()
    private val photoDao = db.photoDao()
    private val itineraryDao = db.itineraryDao()
    
    private val geminiRepository = GeminiRepository()
    private val sessionManager = SessionManager(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _nearestTrip = MutableStateFlow<Trip?>(null)
    val nearestTrip: StateFlow<Trip?> = _nearestTrip.asStateFlow()

    private val _currentCity = MutableStateFlow<String?>("Buscando localização...")
    val currentCity: StateFlow<String?> = _currentCity.asStateFlow()

    private val _isGeneratingAI = MutableStateFlow(false)
    val isGeneratingAI: StateFlow<Boolean> = _isGeneratingAI.asStateFlow()

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            val email = sessionManager.getUser()
            if (email != null) {
                val user = userDao.getUserByEmail(email)
                if (user != null) {
                    tripDao.getTripsByUser(user.id).collect { tripList ->
                        _trips.value = tripList
                        calculateNearestTrip(tripList)
                    }
                }
            }
        }
    }

    private fun calculateNearestTrip(tripList: List<Trip>) {
        val now = System.currentTimeMillis()
        val current = tripList.find { now in it.startDate..it.endDate }
        if (current != null) {
            _nearestTrip.value = current
            return
        }
        val future = tripList.filter { it.startDate > now }.minByOrNull { it.startDate }
        _nearestTrip.value = future ?: tripList.maxByOrNull { it.endDate }
    }

    suspend fun getTripById(id: Int): Trip? = tripDao.getTripById(id)

    fun saveTrip(
        id: Int,
        destination: String,
        type: String,
        startDate: Long,
        endDate: Long,
        budget: Double,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val email = sessionManager.getUser() ?: return@launch onError("Usuário não logado")
            val user = userDao.getUserByEmail(email) ?: return@launch onError("Usuário não encontrado")

            val trip = Trip(id = id, destination = destination, type = type, startDate = startDate, endDate = endDate, budget = budget, userId = user.id)

            try {
                if (id == 0) {
                    val newId = tripDao.insert(trip)
                    onSuccess(newId.toInt())
                } else {
                    tripDao.update(trip)
                    onSuccess(id)
                }
            } catch (e: Exception) {
                onError("Erro ao salvar: ${e.message}")
            }
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch { tripDao.delete(trip) }
    }

    // IA Gemini: Gera 5 itens para o checklist
    fun generateAIRoute(tripId: Int, destination: String, type: String) {
        viewModelScope.launch {
            _isGeneratingAI.value = true
            val contexto = if (type == "Lazer") "turismo e lazer" else "trabalho e networking"
            val prompt = "Crie 5 objetivos curtos para uma viagem a $destination. Perfil: $contexto. Retorne apenas os itens, um por linha."

            val result = geminiRepository.generateItinerary(prompt)
            result?.lines()?.filter { it.isNotBlank() }?.take(5)?.forEach { title ->
                val cleanTitle = title.replace(Regex("^[*\\-•\\d.]+\\s*"), "").trim()
                if (cleanTitle.isNotEmpty()) {
                    addItineraryItem(tripId, cleanTitle)
                }
            }
            _isGeneratingAI.value = false
        }
    }

    // --- Fotos ---
    fun getTripImages(tripId: Int): Flow<List<Photo>> = photoDao.getPhotosByTrip(tripId)
    fun addTripImage(tripId: Int, uri: String) {
        viewModelScope.launch { photoDao.insert(Photo(tripId = tripId, imageUri = uri)) }
    }

    // --- Roteiro (Checklist) ---
    fun getTripItinerary(tripId: Int): Flow<List<Itinerary>> = itineraryDao.getItineraryByTrip(tripId)
    fun addItineraryItem(tripId: Int, title: String) {
        viewModelScope.launch { itineraryDao.insert(Itinerary(tripId = tripId, title = title)) }
    }
    fun toggleTask(itinerary: Itinerary) {
        viewModelScope.launch { itineraryDao.update(itinerary.copy(isCompleted = !itinerary.isCompleted)) }
    }
    fun deleteTask(itinerary: Itinerary) {
        viewModelScope.launch { itineraryDao.delete(itinerary) }
    }

    fun requestLocationAndSearchTrip() {
        _currentCity.value = "Buscando localização..."
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) getCityFromLocation(location.latitude, location.longitude)
                }
        } catch (e: SecurityException) { }
    }

    private fun getCityFromLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    _currentCity.value = "Você está em ${addr.locality} - ${addr.adminArea}"
                }
            } catch (e: Exception) { }
        }
    }
}
