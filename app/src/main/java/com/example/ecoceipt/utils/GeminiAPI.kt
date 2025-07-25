package com.example.ecoceipt.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.ecoceipt.models.GeminiRequest
import com.example.ecoceipt.models.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GeminiApi {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    @Headers(
        "Content-Type: application/json",
        "x-goog-api-key: YOUR API KEY HERE" // DO NOT PUSH BRO
    )
    suspend fun generateContent(@Body request: GeminiRequest): GeminiResponse
}

fun encodeImageToBase64(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes() ?: return ""
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}