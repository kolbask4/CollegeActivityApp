package com.example.collegeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val iin: String,
    val name: String,
    val password: String,
    val course: Int
) 