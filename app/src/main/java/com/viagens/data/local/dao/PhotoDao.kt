package com.viagens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.viagens.data.local.entity.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE tripId = :tripId")
    fun getPhotosByTrip(tripId: Int): Flow<List<Photo>>

    @Insert
    suspend fun insert(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)
}
