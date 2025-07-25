package com.example.ecoceipt.models

import android.graphics.Rect

data class ReceiptModel(
    val id: String = "",
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val currency: String = "IDR",
    val items: List<PurchasedItemModel> = emptyList(),
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val fullText: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ExtractedText(
    val rawText: String,
    val confidence: Float,
    val boundingBoxes: List<TextBlock>
)

data class TextBlock(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float
)