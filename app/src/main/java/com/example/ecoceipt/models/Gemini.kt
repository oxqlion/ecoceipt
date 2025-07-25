package com.example.ecoceipt.models

data class GeminiRequest(val contents: List<Content>)
data class Content(val parts: List<Part>)
data class Part(
    val inline_data: InlineData? = null,
    val text: String? = null
)
data class InlineData(
    val mime_type: String,
    val data: String
)

data class GeminiResponse(val candidates: List<Candidate>)
data class Candidate(val content: Content)
