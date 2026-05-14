package com.viagens.data.local.dao

import androidx.room.*
import com.viagens.data.local.entity.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userId = :userId")
    fun getTripsByUser(userId: Int): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: Int): Trip?

    @Query("SELECT * FROM trips WHERE userId = :userId AND LOWER(destination) = LOWER(:city) AND :currentTime >= startDate AND :currentTime <= endDate LIMIT 1")
    suspend fun findTripByCityAndDate(userId: Int, city: String, currentTime: Long): Trip?

    @Insert
    suspend fun insert(trip: Trip)

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)
}
