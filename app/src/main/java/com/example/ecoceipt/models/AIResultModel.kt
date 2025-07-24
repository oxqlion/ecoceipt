package com.example.ecoceipt.models

data class AIResultModel(
    val id: String = "",
    val receiptId: String = "",
    val userId: String = "",
    val recommendationSummary: String = "",
    val recommendationItems: List<AIRecommendationModel> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
