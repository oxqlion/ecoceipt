package com.example.ecoceipt.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.models.ReceiptModel
import com.example.ecoceipt.repository.LLMRepository
import com.example.ecoceipt.repository.ReceiptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale

data class DashboardUiState(
    val userName: String = "Samuel Lie", // Default name, can be fetched later
    val aiRecommendation: String = "",
    val selectedPeriod: String = "Weekly",
    val revenueData: List<Pair<String, Double>> = emptyList(),
    val totalRevenue: Double = 0.0,
    val receipts: List<ReceiptModel> = emptyList(), // All fetched receipts
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    // Repositories are injected for real data access
    private val receiptRepository: ReceiptRepository = ReceiptRepository(),
    private val llmRepository: LLMRepository = LLMRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // Load all necessary data when the ViewModel is created.
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // In a real app, this would come from your auth service.
                val userId = "3s8mnAExkbJHYOVnVrfQ"

                // Fetch receipts and AI recommendation from the real repositories.
                val receipts = receiptRepository.getReceiptsForUser(userId)
                val recommendationResult = llmRepository.getAIResultByUserId(userId)

                _uiState.update { currentState ->
                    currentState.copy(
                        receipts = receipts,
                        // Access the correct field from the new AIResultModel
                        aiRecommendation = recommendationResult?.recommendationSummary ?: "No recommendations available at the moment.",
                    )
                }
                // After fetching, process the data for the default view ("Weekly").
                processRevenueData("Weekly")

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error loading data: ${e.message}", isLoading = false) }
            }
        }
    }

    fun updatePeriod(period: String) {
        processRevenueData(period)
    }

    private fun processRevenueData(period: String) {
        val allReceipts = _uiState.value.receipts
        val (revenueData, totalRevenue) = when (period) {
            "Monthly" -> processMonthlyRevenue(allReceipts)
            "Yearly" -> processYearlyRevenue(allReceipts)
            else -> processWeeklyRevenue(allReceipts)
        }

        _uiState.update {
            it.copy(
                selectedPeriod = period,
                revenueData = revenueData,
                totalRevenue = totalRevenue,
                isLoading = false
            )
        }
    }

    private fun processWeeklyRevenue(receipts: List<ReceiptModel>): Pair<List<Pair<String, Double>>, Double> {
        val now = LocalDate.now()
        val firstDayOfWeek = now.with(DayOfWeek.MONDAY)
        val revenuePerDay = MutableList(7) { 0.0 }

        receipts.forEach { receipt ->
            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
            if (!date.isBefore(firstDayOfWeek) && date.isBefore(firstDayOfWeek.plusWeeks(1))) {
                val dayIndex = date.dayOfWeek.value - 1
                revenuePerDay[dayIndex] += receipt.totalAmount
            }
        }
        val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        return labels.zip(revenuePerDay) to revenuePerDay.sum()
    }

    private fun processMonthlyRevenue(receipts: List<ReceiptModel>): Pair<List<Pair<String, Double>>, Double> {
        val now = LocalDate.now()
        val currentYearMonth = YearMonth.from(now)
        val weekFields = WeekFields.of(Locale.getDefault())
        val revenuePerWeek = mutableMapOf<Int, Double>()

        receipts.forEach { receipt ->
            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
            if (YearMonth.from(date) == currentYearMonth) {
                val weekOfMonth = date.get(weekFields.weekOfMonth())
                revenuePerWeek[weekOfMonth] = revenuePerWeek.getOrDefault(weekOfMonth, 0.0) + receipt.totalAmount
            }
        }
        val weeklyTotals = (1..5).map { revenuePerWeek.getOrDefault(it, 0.0) }
        val labels = (1..5).map { "Week $it" }
        return labels.zip(weeklyTotals) to weeklyTotals.sum()
    }

    private fun processYearlyRevenue(receipts: List<ReceiptModel>): Pair<List<Pair<String, Double>>, Double> {
        val now = LocalDate.now()
        val revenuePerMonth = MutableList(12) { 0.0 }

        receipts.forEach { receipt ->
            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
            if (date.year == now.year) {
                val monthIndex = date.monthValue - 1
                revenuePerMonth[monthIndex] += receipt.totalAmount
            }
        }
        val labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return labels.zip(revenuePerMonth) to revenuePerMonth.sum()
    }
}