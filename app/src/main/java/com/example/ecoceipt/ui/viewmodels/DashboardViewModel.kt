package com.example.ecoceipt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.models.ReceiptModel
import com.example.ecoceipt.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UPDATED: Added aiRecommendation to the state
data class DashboardUiState(
    val userName: String = "",
    val aiRecommendation: String = "", // New property for the AI summary
    val selectedPeriod: String = "Weekly",
    val revenueData: List<Pair<String, Double>> = emptyList(),
    val totalRevenue: Double = 0.0,
    val receipts: List<ReceiptModel> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadAiRecommendation() // Load the recommendation
        updatePeriod("Weekly")
    }

    private fun loadUserData() {
        val dummyUser = UserModel(name = "Samuel Lie")
        _uiState.update { it.copy(userName = dummyUser.name) }
    }

    // New function to load the AI recommendation
    private fun loadAiRecommendation() {
        // In the future, this will be fetched from Firebase.
        // For now, we use a dummy string.
        val dummyRecommendation = "Your sales for 'Nasi Goreng' are trending up this week. Consider promoting it!"
        _uiState.update { it.copy(aiRecommendation = dummyRecommendation) }
    }

    fun updatePeriod(period: String) {
        viewModelScope.launch {
            val (revenueData, totalRevenue) = when (period) {
                "Monthly" -> getMonthlyData()
                "Yearly" -> getYearlyData()
                else -> getWeeklyData()
            }

            _uiState.update { currentState ->
                currentState.copy(
                    selectedPeriod = period,
                    revenueData = revenueData,
                    totalRevenue = totalRevenue,
                    isLoading = false
                )
            }
        }
    }

    // --- Dummy Data Generation Functions remain the same ---
    private fun getWeeklyData(): Pair<List<Pair<String, Double>>, Double> {
        val data = listOf(
            "Mon" to 250000.0, "Tue" to 310000.0, "Wed" to 280000.0,
            "Thu" to 350000.0, "Fri" to 450000.0, "Sat" to 600000.0, "Sun" to 550000.0
        )
        return data to data.sumOf { it.second }
    }

    private fun getMonthlyData(): Pair<List<Pair<String, Double>>, Double> {
        val data = listOf(
            "Week 1" to 2790000.0, "Week 2" to 3100000.0,
            "Week 3" to 2500000.0, "Week 4" to 3500000.0
        )
        return data to data.sumOf { it.second }
    }

    private fun getYearlyData(): Pair<List<Pair<String, Double>>, Double> {
        val data = listOf(
            "Jan" to 10500000.0, "Feb" to 9800000.0, "Mar" to 12300000.0,
            "Apr" to 11500000.0, "May" to 13000000.0, "Jun" to 14200000.0,
            "Jul" to 13800000.0, "Aug" to 15000000.0, "Sep" to 14500000.0,
            "Oct" to 16000000.0, "Nov" to 15500000.0, "Dec" to 18000000.0
        )
        return data to data.sumOf { it.second }
    }
}