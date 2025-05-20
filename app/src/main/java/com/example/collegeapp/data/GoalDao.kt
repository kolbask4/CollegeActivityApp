package com.example.collegeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GoalDao {
    @Insert
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE userIin = :userIin")
    suspend fun getGoalsByUser(userIin: String): List<Goal>

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoal(goalId: Int)
} 