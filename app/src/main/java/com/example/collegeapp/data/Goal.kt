package com.example.collegeapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["iin"],
            childColumns = ["userIin"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userIin: String,
    val title: String,
    val description: String,
    val progress: Int,
    val deadline: Long,
    val mentorComment: String? = null,
    val isCompleted: Boolean = false
)

data class GoalStep(
    val id: Int,
    val goalId: Int,
    val title: String,
    val isCompleted: Boolean
) 