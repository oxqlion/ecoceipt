package com.example.ecoceipt.repository

import android.util.Log
import com.example.ecoceipt.models.ItemModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ItemRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val itemsRef = db.collection("items")

    suspend fun addItem(item: ItemModel) {
        val docRef = itemsRef.document()
        val itemWithId = item.copy(id = docRef.id)
        docRef.set(itemWithId).await()
    }

    suspend fun getItems(): List<ItemModel> {
        val snapshot = itemsRef.orderBy("name").get().await()
        return snapshot.toObjects(ItemModel::class.java)
    }

    suspend fun updateItem(item: ItemModel) {
        itemsRef.document(item.id).set(item).await()
    }

    suspend fun deleteItem(itemId: String) {
        itemsRef.document(itemId).delete().await()
    }
}