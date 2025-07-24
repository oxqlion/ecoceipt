package com.example.ecoceipt.models

data class UserModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val businessName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)