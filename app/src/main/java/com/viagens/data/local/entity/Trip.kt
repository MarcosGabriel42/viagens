package com.viagens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val destination: String,
    val type: String, // "Lazer" ou "Negócios"
    val startDate: Long,
    val endDate: Long,
    val budget: Double,
    val userId: Int,
    val totalSpent: Double = 0.0 // Nova informação solicitada
)
