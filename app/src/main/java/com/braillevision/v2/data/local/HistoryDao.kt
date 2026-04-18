package com.braillevision.v2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>
    
    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getById(id: Long): HistoryEntity?
    
    @Query("SELECT COUNT(*) FROM history")
    suspend fun getCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity): Long
    
    @Delete
    suspend fun delete(history: HistoryEntity)
    
    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
