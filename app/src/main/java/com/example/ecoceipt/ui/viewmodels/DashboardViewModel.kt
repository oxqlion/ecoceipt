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


//package com.example.ecoceipt.ui.viewmodels
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.ecoceipt.models.AIResultModel
//import com.example.ecoceipt.models.ReceiptModel
//import com.example.ecoceipt.repository.LLMRepository
//import com.example.ecoceipt.repository.ReceiptRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//import java.time.*
//import java.time.temporal.WeekFields
//import java.util.Locale
//
//@RequiresApi(Build.VERSION_CODES.O)
//class DashboardViewModel(
//    private val receiptRepository: ReceiptRepository = ReceiptRepository(),
//    private val llmRepository: LLMRepository = LLMRepository()
//) : ViewModel() {
//
//    private val _receipts = MutableStateFlow<List<ReceiptModel>>(emptyList())
//    val receipts: StateFlow<List<ReceiptModel>> = _receipts
//
//    private val _recommendation = MutableStateFlow<AIResultModel?>(null)
//    val recommendation: StateFlow<AIResultModel?> = _recommendation
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage
//
//    init {
//        loadReceipts()
//        fetchRecommendation()
//    }
//
//    private fun loadReceipts() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val result = receiptRepository.getReceiptsForUser(userId = " 3s8mnAExkbJHYOVnVrfQ ")
//                _receipts.value = result
//                _errorMessage.value = null
//            } catch (e: Exception) {
//                _errorMessage.value = e.message
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    private fun fetchRecommendation() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val result = llmRepository.getAIResultByUserId(" 3s8mnAExkbJHYOVnVrfQ ")
//                _recommendation.value = result
//                _errorMessage.value = null
//            } catch (e: Exception) {
//                _errorMessage.value = e.message
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun getWeeklyRevenue(): List<Double> {
//        val now = LocalDate.now()
//        val firstDayOfWeek = now.with(DayOfWeek.MONDAY)
//        val lastDayOfWeek = now.with(DayOfWeek.SUNDAY)
//
//        val revenuePerDay = MutableList(7) { 0.0 }
//
//        _receipts.value.forEach { receipt ->
//            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
//            if (date in firstDayOfWeek..lastDayOfWeek) {
//                val dayIndex = date.dayOfWeek.value % 7
//                revenuePerDay[dayIndex] += receipt.totalAmount
//            }
//        }
//
//        return revenuePerDay
//    }
//
//    fun getMonthlyRevenue(): List<Double> {
//        val now = LocalDate.now()
//        val currentYearMonth = YearMonth.from(now)
//
//        val weekFields = WeekFields.of(Locale.getDefault())
//        val revenuePerWeek = mutableMapOf<Int, Double>()
//
//        _receipts.value.forEach { receipt ->
//            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
//            val receiptMonth = YearMonth.from(date)
//
//            if (receiptMonth == currentYearMonth) {
//                val weekOfMonth = date.get(weekFields.weekOfMonth())
//                revenuePerWeek[weekOfMonth] = revenuePerWeek.getOrDefault(weekOfMonth, 0.0) + receipt.totalAmount
//            }
//        }
//
//        return (1..5).map { revenuePerWeek.getOrDefault(it, 0.0) }
//    }
//
//    fun getYearlyRevenue(): List<Double> {
//        val now = LocalDate.now()
//        val currentYear = now.year
//
//        val revenuePerMonth = MutableList(12) { 0.0 }
//
//        _receipts.value.forEach { receipt ->
//            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
//            if (date.year == currentYear) {
//                val monthIndex = date.monthValue - 1
//                revenuePerMonth[monthIndex] += receipt.totalAmount
//            }
//        }
//
//        return revenuePerMonth
//    }
//
//
//}