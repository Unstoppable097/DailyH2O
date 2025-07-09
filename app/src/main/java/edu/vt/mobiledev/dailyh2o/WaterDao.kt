package edu.vt.mobiledev.dailyh2o

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Insert
    suspend fun insert(waterEntry: WaterEntry)

    @Delete
    suspend fun delete(waterEntry: WaterEntry)

    @Query("SELECT * FROM water_entries ORDER BY timestamp DESC")
    fun getAllWaterEntries(): Flow<List<WaterEntry>>

    @Query("SELECT * FROM water_entries WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getWaterEntriesForDay(startTime: Long, endTime: Long): Flow<List<WaterEntry>>

    @Query("SELECT SUM(amount) FROM water_entries WHERE timestamp BETWEEN :startTime AND :endTime")
    fun getTotalWaterIntakeForDay(startTime: Long, endTime: Long): Flow<Float?>
}