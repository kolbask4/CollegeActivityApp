package com.example.collegeapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "grades",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["iin"],
            childColumns = ["userIin"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Grade(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userIin: String,
    val score: Int,
    val course: Int
) 