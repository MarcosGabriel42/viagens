package com.viagens.data.repository

import com.viagens.data.local.dao.PhotoDao
import com.viagens.data.local.entity.Photo
import kotlinx.coroutines.flow.Flow

class PhotoRepository(private val photoDao: PhotoDao) {
    fun getPhotosByTrip(tripId: Int): Flow<List<Photo>> = photoDao.getPhotosByTrip(tripId)

    suspend fun insertPhoto(photo: Photo) {
        photoDao.insert(photo)
    }

    suspend fun deletePhoto(photo: Photo) {
        photoDao.delete(photo)
    }
}
