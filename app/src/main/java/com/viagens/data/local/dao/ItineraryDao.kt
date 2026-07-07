package com.viagens.data.local.dao

import androidx.room.*
import com.viagens.data.local.entity.Itinerary
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM itineraries WHERE tripId = :tripId ORDER BY createdAt DESC LIMIT 1")
    fun getItineraryByTrip(tripId: Int): Flow<Itinerary?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itinerary: Itinerary)

    @Query("DELETE FROM itineraries WHERE tripId = :tripId")
    suspend fun deleteByTrip(tripId: Int)
}
