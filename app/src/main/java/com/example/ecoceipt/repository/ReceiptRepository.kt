package com.example.ecoceipt.repository

import android.util.Log
import com.example.ecoceipt.models.ReceiptModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class ReceiptRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val receiptsRef = db.collection("receipts")

    suspend fun addReceipt(receipt: ReceiptModel): Boolean {
        return try {
            receiptsRef.document(receipt.id).set(receipt).await()
            true
        } catch (e: Exception) {
            Log.e("ReceiptRepository", "Error adding receipt", e)
            false
        }
    }

    suspend fun getReceipt(receiptId: String): ReceiptModel? {
        return try {
            receiptsRef.document(receiptId).get().await()
                .toObject(ReceiptModel::class.java)
        } catch (e: Exception) {
            Log.e("ReceiptRepository", "Error getting receipt", e)
            null
        }
    }

    suspend fun getReceiptsForUser(userId: String): List<ReceiptModel> {
        return try {
            val snapshot = receiptsRef
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.toObjects(ReceiptModel::class.java)
        } catch (e: Exception) {
            Log.e("ReceiptRepository", "Error fetching receipts", e)
            emptyList()
        }
    }
}