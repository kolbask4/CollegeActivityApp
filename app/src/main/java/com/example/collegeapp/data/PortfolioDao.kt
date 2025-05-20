package com.example.collegeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PortfolioDao {
    @Insert
    suspend fun insertItem(item: PortfolioItem)

    @Update
    suspend fun updateItem(item: PortfolioItem)

    @Query("SELECT * FROM portfolio_items WHERE userIin = :userIin")
    suspend fun getItemsByUser(userIin: String): List<PortfolioItem>

    @Query("SELECT * FROM portfolio_items WHERE userIin = :userIin AND type = :type")
    suspend fun getItemsByType(userIin: String, type: PortfolioType): List<PortfolioItem>

    @Query("DELETE FROM portfolio_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int)
} 