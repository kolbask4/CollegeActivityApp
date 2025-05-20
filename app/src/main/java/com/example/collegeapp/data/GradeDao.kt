package com.example.collegeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GradeDao {
    @Insert
    suspend fun insertGrade(grade: Grade)

    @Query("SELECT * FROM grades WHERE userIin = :userIin")
    suspend fun getGradesByUser(userIin: String): List<Grade>

    @Query("DELETE FROM grades WHERE userIin = :userIin")
    suspend fun deleteGradesByUser(userIin: String)

    @Query("UPDATE grades SET score = :newScore WHERE id = :gradeId")
    suspend fun updateGradeScore(gradeId: Int, newScore: Int)
} 