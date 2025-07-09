package edu.vt.mobiledev.dailyh2o

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val waterDao: WaterDao = AppDatabase.getDatabase(application).waterDao()
    private val userProfileDao: UserProfileDao = AppDatabase.getDatabase(application).userProfileDao()

    private val _currentDayTotalWater = MutableStateFlow(0f)
    val currentDayTotalWater: StateFlow<Float> = _currentDayTotalWater.asStateFlow()

    private val _currentDayEntries = MutableStateFlow<List<WaterEntry>>(emptyList())
    val currentDayEntries: StateFlow<List<WaterEntry>> = _currentDayEntries.asStateFlow()

    val allWaterEntries: Flow<List<WaterEntry>> = waterDao.getAllWaterEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    val dailyGoal: StateFlow<Float> = userProfileDao.getUserProfile()
        .map { profile ->
            profile?.dailyGoal ?: 64f
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 64f
        )

    init {
        viewModelScope.launch {
            userProfileDao.getUserProfile().collect { profile ->
                _userProfile.value = profile
            }
        }

        viewModelScope.launch {
            val today = ZonedDateTime.now(ZoneId.systemDefault())
            val startOfDay = today.truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
            val endOfDay = today.plusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli() - 1

            waterDao.getTotalWaterIntakeForDay(startOfDay, endOfDay).collect { total ->
                _currentDayTotalWater.value = total ?: 0f
            }

            waterDao.getWaterEntriesForDay(startOfDay, endOfDay).collect { entries ->
                _currentDayEntries.value = entries
            }
        }
    }

    fun addWater(amount: Float) {
        viewModelScope.launch {
            val newEntry = WaterEntry(amount = amount, timestamp = System.currentTimeMillis())
            waterDao.insert(newEntry)
        }
    }

    fun deleteWaterEntry(waterEntry: WaterEntry) {
        viewModelScope.launch {
            waterDao.delete(waterEntry)
        }
    }

    fun saveUserProfile(weight: Float, activityLevel: String) {
        viewModelScope.launch {
            val currentProfile = userProfile.value
            val calculatedGoal = calculateDailyGoal(weight, activityLevel)

            val newProfile = currentProfile?.copy(
                weight = weight,
                activityLevel = activityLevel,
                dailyGoal = calculatedGoal
            ) ?: UserProfile(
                weight = weight,
                activityLevel = activityLevel,
                dailyGoal = calculatedGoal
            )
            userProfileDao.insert(newProfile)
        }
    }

    private fun calculateDailyGoal(weight: Float, activityLevel: String): Float {
        var goal = weight * 0.5f

        when (activityLevel.lowercase()) {
            "moderate" -> goal += 16f
            "high" -> goal += 32f
        }
        return goal
    }
}