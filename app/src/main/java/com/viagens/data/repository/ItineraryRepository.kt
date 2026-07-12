package com.viagens.data.repository

import com.viagens.data.local.dao.ItineraryDao
import com.viagens.data.local.entity.Itinerary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItineraryRepository(private val itineraryDao: ItineraryDao) {
    fun getItineraryByTrip(tripId: Int): Flow<Itinerary?> = 
        itineraryDao.getItineraryByTrip(tripId).map { it.firstOrNull { item -> item.generatedText != null } }

    suspend fun saveItinerary(itinerary: Itinerary) {
        itineraryDao.insert(itinerary)
    }
}
