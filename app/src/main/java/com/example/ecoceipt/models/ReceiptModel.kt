package com.example.ecoceipt.models

data class ReceiptModel(
    val id: String = "",
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val currency: String = "IDR",
    val items: List<ItemModel> = emptyList(),
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)