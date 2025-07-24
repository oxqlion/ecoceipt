package com.example.ecoceipt.repository

import android.util.Log
import com.example.ecoceipt.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersRef = db.collection("users")

    suspend fun addUser(user: UserModel): Boolean {
        return try {
            usersRef.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user", e)
            false
        }
    }

    suspend fun getUser(userId: String): UserModel? {
        return try {
            val snapshot = usersRef.document(userId).get().await()
            snapshot.toObject(UserModel::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user", e)
            null
        }
    }

    suspend fun updateUser(user: UserModel): Boolean {
        return try {
            usersRef.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user", e)
            false
        }
    }
}
