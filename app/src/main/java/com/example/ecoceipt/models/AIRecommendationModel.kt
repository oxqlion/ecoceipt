package com.example.ecoceipt.models

data class AIRecommendationModel (
    val item: ItemModel = ItemModel(),
    val recommendation: String = ""
)