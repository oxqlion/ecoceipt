package com.example.ecoceipt.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.models.AIRecommendationModel
import com.example.ecoceipt.models.AIResultModel
import com.example.ecoceipt.models.Content
import com.example.ecoceipt.models.GeminiRequest
import com.example.ecoceipt.models.ItemModel
import com.example.ecoceipt.models.Part
import com.example.ecoceipt.models.ReceiptModel
import com.example.ecoceipt.repository.LLMRepository
import com.example.ecoceipt.repository.ReceiptRepository
import com.example.ecoceipt.utils.GeminiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class ReceiptsViewModel(
    val repository: ReceiptRepository = ReceiptRepository(),
    private val llmRepository: LLMRepository = LLMRepository()
) : ViewModel() {

    private val _aiResult = MutableStateFlow<AIResultModel?>(null)
//    val aiResult: StateFlow<AIResultModel?> = _aiResult

    fun addReceipt(receipt: ReceiptModel) {
        viewModelScope.launch {
            repository.addReceipt(receipt)
        }
    }

    fun getRecommendation(userId: String) {
        viewModelScope.launch {
            try {
                val receipts = repository.getReceiptTextsByUser(userId)
                Log.d("ReceiptsViewModel", "Fetched receipts: $receipts")

                val combinedText = receipts.take(5).joinToString("\n\n")
                Log.d("ReceiptsViewModel", "Combined text: $combinedText")

                val resultText = recommendationReceipts(combinedText)
                Log.d("ReceiptsViewModel", "Result from Gemini: $resultText")

                if (resultText != null && resultText.contains("Recommended items to restock:") && resultText.contains("Summary:")) {
                    val itemsRaw = resultText
                        .substringAfter("Recommended items to restock:")
                        .substringBefore("Summary:")
                        .trim()
                        .removePrefix("[")
                        .removeSuffix("]")
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    val recommendationItems = itemsRaw.map { itemName ->
                        AIRecommendationModel(
                            item = ItemModel(
                                name = itemName,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            ),
                            recommendation = "Recommended for restock based on frequent sales"
                        )
                    }

                    val summary = resultText.substringAfter("Summary:").trim()

                    val result = AIResultModel(
                        id = UUID.randomUUID().toString(),
                        receiptId = "", // can be filled later if linked to a specific receipt
                        userId = userId,
                        recommendationSummary = summary,
                        recommendationItems = recommendationItems,
                        createdAt = System.currentTimeMillis()
                    )

                    // Save to Firestore
                    llmRepository.upsertAIResultByUserId(result)
                    _aiResult.value = result

                } else {
                    Log.d("ReceiptsViewModel", "No valid result from Gemini.")
                    _aiResult.value = null
                }
            } catch (e: Exception) {
                Log.e("ReceiptsViewModel", "Error in getRecommendation: ${e.message}", e)
                _aiResult.value = null
            }
        }
    }

    private suspend fun recommendationReceipts(receiptText: String): String? {
        if (receiptText.isBlank()) {
            Log.d("GeminiDebug", "Receipt text is blank.")
            return null
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(GeminiApi::class.java)

        val prompt = """
        You are a smart menu and inventory assistant.
        Below is a list of raw receipt texts from a printed or handwritten receipt. These show what has been sold in the last 7 days:

        "$receiptText"

        Your task is to analyze the sold items and recommend a menu restock plan that:
        - Prioritizes items that are frequently sold or in high quantity
        - Identifies popular items or combos
        - Suggests what to restock smartly **to prevent overstocking and reduce food waste**

        Return the result in the following format:
        - Recommended items to restock:
        [item name 1, item name 2, item name 3, ...]

        Summary:
        <1-2 sentence summary of why these items are recommended and general insight>

        If this is not a receipt or is too unclear to process, return only:
        **"cannot detect receipt"**

        Do not explain or add anything else. Follow the format exactly.
        """.trimIndent()

        Log.d("GeminiDebug", "Prompt: $prompt")

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            )
        )

        return try {
            val response = api.generateContent(request)
            Log.d("GeminiDebug", "Gemini response: $response")
            val content = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (content.isNullOrBlank()) {
                Log.d("GeminiDebug", "Gemini response is blank or null")
            } else {
                Log.d("GeminiDebug", "Gemini response: $content")
            }

            content
        } catch (e: Exception) {
            Log.e("GeminiDebug", "Error in Gemini API: ${e.message}", e)
            null
        }
    }
}
