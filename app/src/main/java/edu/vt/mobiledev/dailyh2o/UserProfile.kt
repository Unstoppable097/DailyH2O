package edu.vt.mobiledev.dailyh2o

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val userId: Int = 1,
    val weight: Float,
    val activityLevel: String,
    val dailyGoal: Float
)