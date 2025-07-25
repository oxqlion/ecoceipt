package com.example.ecoceipt.repository

import android.util.Log
import com.example.ecoceipt.models.AIResultModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LLMRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val resultsRef = db.collection("ai_results")

    suspend fun addAIResult(result: AIResultModel): Boolean {
        return try {
            resultsRef.document(result.id).set(result).await()
            true
        } catch (e: Exception) {
            Log.e("LLMRepository", "Error adding AI result", e)
            false
        }
    }

    suspend fun getAIResultByUserId(userId: String): AIResultModel? {
        return try {
            val snapshot = resultsRef
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toObject(AIResultModel::class.java)
        } catch (e: Exception) {
            Log.e("LLMRepository", "Failed to fetch AI result", e)
            null
        }
    }

    suspend fun upsertAIResultByUserId(result: AIResultModel): Boolean {
        return try {
            val snapshot = resultsRef
                .whereEqualTo("userId", result.userId)
                .limit(1)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                // Document exists → update existing one
                val docId = snapshot.documents.first().id
                resultsRef.document(docId).set(result).await()
            } else {
                // Not found → create new
                resultsRef.document(result.id).set(result).await()
            }

            true
        } catch (e: Exception) {
            Log.e("LLMRepository", "Error updating or creating AI result", e)
            false
        }
    }
}