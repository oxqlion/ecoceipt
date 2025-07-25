package com.example.ecoceipt.models

data class PurchasedItemModel(
    val item: ItemModel = ItemModel(),
    val quantity: Int = 0
)