package com.example.ecoceipt.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.repository.ReceiptRepository
import com.example.ecoceipt.utils.ExtractTextFromImageUseCase
import kotlinx.coroutines.launch

class OCRViewModel constructor(
    private val extractTextFromImageUseCase: ExtractTextFromImageUseCase
) : ViewModel() {
    var extractedText by mutableStateOf<String?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun processCapturedImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            val result = extractTextFromImageUseCase(uri, context)
            result
                .onSuccess { receiptModel ->
                    extractedText = receiptModel.fullText
                    error = null
                }
                .onFailure {
                    error = it.localizedMessage ?: "Unknown error"
                }
        }
    }
}

class OCRViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = ReceiptRepository()
        val useCase = ExtractTextFromImageUseCase(repository)
        return OCRViewModel(useCase) as T
    }
}
