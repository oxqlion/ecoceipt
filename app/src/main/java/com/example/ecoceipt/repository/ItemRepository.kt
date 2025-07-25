package com.example.ecoceipt.repository

import android.util.Log
import com.example.ecoceipt.models.ItemModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ItemRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val itemsRef = db.collection("items")

    suspend fun addItem(item: ItemModel): Boolean {
        return try {
            val docRef = itemsRef.document()
            val itemWithId = item.copy(id = docRef.id)
            docRef.set(itemWithId).await()
            true
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error adding item", e)
            false
        }
    }

    suspend fun getItems(): List<ItemModel> {
        return try {
            val snapshot = itemsRef.get().await()
            snapshot.toObjects(ItemModel::class.java).sortedBy { it.name }
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error fetching items", e)
            emptyList()
        }
    }

    suspend fun updateItem(item: ItemModel): Boolean {
        if (item.id.isBlank()) {
            Log.e("ItemRepository", "Error: Attempted to update an item with no ID.")
            return false
        }
        return try {
            itemsRef.document(item.id).set(item, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error updating item", e)
            false
        }
    }

    suspend fun deleteItem(itemId: String): Boolean {
        return try {
            itemsRef.document(itemId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error deleting item", e)
            false
        }
    }
}