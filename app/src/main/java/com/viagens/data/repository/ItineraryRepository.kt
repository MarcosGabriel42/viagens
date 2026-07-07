package com.viagens.data.repository

import com.viagens.data.local.dao.ItineraryDao
import com.viagens.data.local.entity.Itinerary
import kotlinx.coroutines.flow.Flow

class ItineraryRepository(private val itineraryDao: ItineraryDao) {
    fun getItineraryByTrip(tripId: Int): Flow<Itinerary?> = itineraryDao.getItineraryByTrip(tripId)

    suspend fun saveItinerary(itinerary: Itinerary) {
        itineraryDao.insert(itinerary)
    }
}
