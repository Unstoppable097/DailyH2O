package edu.vt.mobiledev.dailyh2o

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_entries")
data class WaterEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Float,
    val timestamp: Long,
    val drinkType: String = "Water"
)