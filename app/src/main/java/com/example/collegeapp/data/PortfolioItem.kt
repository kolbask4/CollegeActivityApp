package com.example.collegeapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class PortfolioType {
    PROJECT, CERTIFICATE, DIPLOMA
}

@Entity(
    tableName = "portfolio_items",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["iin"],
            childColumns = ["userIin"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PortfolioItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userIin: String,
    val title: String,
    val description: String,
    val type: PortfolioType,
    val imageUrl: String,
    val date: Long, // timestamp
    val tags: List<String>
) 