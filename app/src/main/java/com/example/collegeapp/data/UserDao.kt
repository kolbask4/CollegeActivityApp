package com.example.collegeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE iin = :iin AND password = :password")
    suspend fun getUser(iin: String, password: String): User?

    @Query("SELECT * FROM users WHERE iin = :iin")
    suspend fun getUserByIin(iin: String): User?
} 