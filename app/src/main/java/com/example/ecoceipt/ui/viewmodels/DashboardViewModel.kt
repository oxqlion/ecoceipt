package com.example.ecoceipt.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.models.AIResultModel
import com.example.ecoceipt.models.ReceiptModel
import com.example.ecoceipt.repository.LLMRepository
import com.example.ecoceipt.repository.ReceiptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import java.time.*
import java.time.temporal.WeekFields
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val receiptRepository: ReceiptRepository = ReceiptRepository(),
    private val llmRepository: LLMRepository = LLMRepository()
) : ViewModel() {

    private val _receipts = MutableStateFlow<List<ReceiptModel>>(emptyList())
    val receipts: StateFlow<List<ReceiptModel>> = _receipts

    private val _recommendation = MutableStateFlow<AIResultModel?>(null)
    val recommendation: StateFlow<AIResultModel?> = _recommendation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadReceipts()
        fetchRecommendation()
    }

    private fun loadReceipts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = receiptRepository.getReceiptsForUser(userId = " 3s8mnAExkbJHYOVnVrfQ ")
                _receipts.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchRecommendation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = llmRepository.getAIResultByUserId(" 3s8mnAExkbJHYOVnVrfQ ")
                _recommendation.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getWeeklyRevenue(): List<Double> {
        val now = LocalDate.now()
        val firstDayOfWeek = now.with(DayOfWeek.MONDAY)
        val lastDayOfWeek = now.with(DayOfWeek.SUNDAY)

        val revenuePerDay = MutableList(7) { 0.0 }

        _receipts.value.forEach { receipt ->
            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
            if (date in firstDayOfWeek..lastDayOfWeek) {
                val dayIndex = date.dayOfWeek.value % 7
                revenuePerDay[dayIndex] += receipt.totalAmount
            }
        }

        return revenuePerDay
    }

    fun getMonthlyRevenue(): List<Double> {
        val now = LocalDate.now()
        val currentYearMonth = YearMonth.from(now)

        val weekFields = WeekFields.of(Locale.getDefault())
        val revenuePerWeek = mutableMapOf<Int, Double>()

        _receipts.value.forEach { receipt ->
            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
            val receiptMonth = YearMonth.from(date)

            if (receiptMonth == currentYearMonth) {
                val weekOfMonth = date.get(weekFields.weekOfMonth())
                revenuePerWeek[weekOfMonth] = revenuePerWeek.getOrDefault(weekOfMonth, 0.0) + receipt.totalAmount
            }
        }

        return (1..5).map { revenuePerWeek.getOrDefault(it, 0.0) }
    }

    fun getYearlyRevenue(): List<Double> {
        val now = LocalDate.now()
        val currentYear = now.year

        val revenuePerMonth = MutableList(12) { 0.0 }

        _receipts.value.forEach { receipt ->
            val date = Instant.ofEpochMilli(receipt.date).atZone(ZoneId.systemDefault()).toLocalDate()
            if (date.year == currentYear) {
                val monthIndex = date.monthValue - 1
                revenuePerMonth[monthIndex] += receipt.totalAmount
            }
        }

        return revenuePerMonth
    }


}