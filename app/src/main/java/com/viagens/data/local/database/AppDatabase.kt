package com.viagens.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.viagens.data.local.dao.UserDao
import com.viagens.data.local.dao.TripDao
import com.viagens.data.local.dao.PhotoDao
import com.viagens.data.local.entity.User
import com.viagens.data.local.entity.Trip
import com.viagens.data.local.entity.Photo

@Database(entities = [User::class, Trip::class, Photo::class], version = 11, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "viagens_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
