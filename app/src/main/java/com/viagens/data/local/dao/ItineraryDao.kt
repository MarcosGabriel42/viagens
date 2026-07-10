package com.viagens.data.local.dao

import androidx.room.*
import com.viagens.data.local.entity.Itinerary
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM itineraries WHERE tripId = :tripId ORDER BY createdAt ASC")
    fun getItineraryByTrip(tripId: Int): Flow<List<Itinerary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itinerary: Itinerary)

    @Update
    suspend fun update(itinerary: Itinerary)

    @Delete
    suspend fun delete(itinerary: Itinerary)

    @Query("DELETE FROM itineraries WHERE tripId = :tripId")
    suspend fun deleteByTrip(tripId: Int)
}
