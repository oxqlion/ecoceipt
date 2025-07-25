package com.example.ecoceipt.utils

import android.content.Context
import android.net.Uri
import com.example.ecoceipt.models.ReceiptModel
import com.example.ecoceipt.repository.OCRRepository

class ExtractTextFromImageUseCase(
    private val repository: OCRRepository
) {
    suspend operator fun invoke(imageUri: Uri, context: Context): Result<ReceiptModel> {
        return repository.extractTextFromImage(imageUri, context)
            .map { extractedText ->
                parseReceiptData(extractedText.rawText)
            }
    }

    private fun parseReceiptData(text: String): ReceiptModel {
        return ReceiptModel(
            fullText = text,
        )
    }
}