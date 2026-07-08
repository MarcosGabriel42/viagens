package com.viagens.viewmodel

import android.app.Application
import android.location.Geocoder
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import org.osmdroid.util.GeoPoint
import com.viagens.data.local.database.AppDatabase
import com.viagens.data.local.entity.Photo
import com.viagens.data.local.entity.Trip
import com.viagens.data.local.entity.Itinerary
import com.viagens.data.repository.PhotoRepository
import com.viagens.data.repository.ItineraryRepository
import com.viagens.data.repository.GeminiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class TripDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val tripDao = AppDatabase.getDatabase(application).tripDao()
    private val photoRepository = PhotoRepository(AppDatabase.getDatabase(application).photoDao())
    private val itineraryRepository = ItineraryRepository(AppDatabase.getDatabase(application).itineraryDao())
    private val geminiRepository = GeminiRepository()

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _location = MutableStateFlow<GeoPoint?>(null)
    val location: StateFlow<GeoPoint?> = _location.asStateFlow()

    private val _itinerary = MutableStateFlow<Itinerary?>(null)
    val itinerary: StateFlow<Itinerary?> = _itinerary.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

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
        
        viewModelScope.launch {
            itineraryRepository.getItineraryByTrip(tripId).collect {
                _itinerary.value = it
                // Gera automaticamente se não houver roteiro salvo
                if (it == null && _trip.value != null && !_isGenerating.value) {
                    generateItinerary(tripId, "")
                }
            }
        }
    }

    fun generateItinerary(tripId: Int, interests: String) {
        val currentTrip = _trip.value ?: return
        
        viewModelScope.launch {
            _isGenerating.value = true
            val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val duration = (currentTrip.endDate - currentTrip.startDate) / (1000 * 60 * 60 * 24) + 1
            
            val preferencesPart = if (interests.isNotBlank()) "focado em $interests" else "com foco em pontos turísticos gerais"
            
            val prompt = """
                Crie um roteiro turístico de $duration dias para ${currentTrip.destination} 
                $preferencesPart com orçamento de R$ ${String.format(Locale.getDefault(), "%.2f", currentTrip.budget)} 
                e tipo de viagem ${currentTrip.type}.
                
                O roteiro deve começar em ${df.format(Date(currentTrip.startDate))} e terminar em ${df.format(Date(currentTrip.endDate))}.
                
                IMPORTANTE: Responda usando EXATAMENTE este formato para cada dia:
                DIA X
                - Horário: Atividade (Sugestão de Local)
                - Dica: Uma dica rápida
                
                Exemplo:
                DIA 1
                - 09:00: Visita ao Teatro Amazonas
                - 12:00: Almoço no Mercado Municipal
                - Dica: Leve protetor solar.
                
                Use uma formatação limpa e amigável.
            """.trimIndent()

            val result = geminiRepository.generateItinerary(prompt)
            if (result != null) {
                val itinerary = Itinerary(tripId = tripId, generatedText = result)
                itineraryRepository.saveItinerary(itinerary)
            }
            _isGenerating.value = false
        }
    }

    private fun updateLocation(destination: String) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                val addresses = geocoder.getFromLocationName(destination, 1)
                if (!addresses.isNullOrEmpty()) {
                    _location.value = GeoPoint(addresses[0].latitude, addresses[0].longitude)
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
