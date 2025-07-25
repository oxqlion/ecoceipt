package com.example.ecoceipt.repository

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import com.example.ecoceipt.models.ExtractedText
import com.example.ecoceipt.models.TextBlock
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import android.util.Log


class OCRRepository {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractTextFromImage(imageUri: Uri, context: Context): Result<ExtractedText> {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).await()

            Result.success(
                ExtractedText(
                    rawText = result.text,
                    confidence = 0.0f,
                    boundingBoxes = result.textBlocks.map { block: com.google.mlkit.vision.text.Text.TextBlock ->
                        TextBlock(
                            text = block.text,
                            boundingBox = block.boundingBox ?: Rect(),
                            confidence = 0.0f
                        )
                    }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}