package com.example.ecoceipt.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.models.Content
import com.example.ecoceipt.models.GeminiRequest
import com.example.ecoceipt.models.InlineData
import com.example.ecoceipt.models.Part
import com.example.ecoceipt.repository.OCRRepository
import com.example.ecoceipt.utils.ExtractTextFromImageUseCase
import com.example.ecoceipt.utils.GeminiApi
import com.example.ecoceipt.utils.encodeImageToBase64
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OCRViewModel(
    private val extractTextFromImageUseCase: ExtractTextFromImageUseCase
) : ViewModel() {
    var extractedText by mutableStateOf<String?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var _ocrResult by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun processCapturedImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d("OCRViewModel", "Processing captured image: $uri")
                val result = extractTextFromImageUseCase(uri, context)
                val base64 = encodeImageToBase64(context, uri)
                val gemini_result = callGeminiWithImage(base64)
                _ocrResult = gemini_result ?: "Failed to extract"
                result
                    .onSuccess { receiptModel ->
                        extractedText = _ocrResult
                        error = null
                        Log.d("OCRViewModel", "Success di OCRViewModel: $extractedText")
                    }
                    .onFailure {
                        error = it.localizedMessage ?: "Unknown error"
                    }
            } catch(e: Exception) {
                Log.d("OCRViewModelError", "Error di OCRViewModel: $e")
            } finally {
                isLoading = false
            }
        }
    }
}

class OCRViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = OCRRepository()
        val useCase = ExtractTextFromImageUseCase(repository)
        return OCRViewModel(useCase) as T
    }
}

suspend fun callGeminiWithImage(base64Image: String): String? {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(GeminiApi::class.java)

    val request = GeminiRequest(
        contents = listOf(
            Content(
                parts = listOf(
                    Part(
                        inline_data = InlineData(
                            mime_type = "image/jpeg",
                            data = base64Image
                        )
                    ),
                    Part(text = "You are a smart receipt reader that extracts structured data from images of printed or handwritten receipts. Read the text from this image, and return a structured summary with the following fields:\n" +
                            " - store name : <store name or best guess>\n" +
                            "- date and time : <exact or estimated date/time with the format of DD/MM/YYYY>\n" +
                            "- items : [\n" +
                            "[item name 1, quantity,price],\n" +
                            "[item name 2, quantity,price],\n" +
                            "...\n" +
                            "]\n" +
                            "- subtotal : <subtotal amount>\n" +
                            "\n" +
                            "If some parts are unclear or partially unreadable, make believable guesses based on common receipt patterns. If you're not confident, still return a result that looks reasonable.\n" +
                            "\n" +
                            "If the image clearly does not contain a receipt or is completely unreadable, return **only** the following text:  \n" +
                            "**\"cannot detect receipt\"**\n" +
                            "\n" +
                            "Do not explain, apologize, or add anything else. Follow the format exactly as shown.")
                )
            )
        )
    )

    return try {
        val response = api.generateContent(request)
        response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
