package com.example.ecoceipt.utils

import android.util.Log
import com.example.ecoceipt.models.ItemModel
import com.example.ecoceipt.models.PurchasedItemModel
import com.example.ecoceipt.models.ReceiptModel
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

fun parseToReceiptModel(rawText: String): ReceiptModel? {
    return try {
        val cleaned = rawText
            .replace("```json", "")
            .replace("```", "")
            .trim()

        Log.d("ReceiptParser", "Cleaned JSON:\n$cleaned")

        val gson = GsonBuilder()
            .registerTypeAdapter(IntermediateReceipt::class.java, IntermediateReceiptDeserializer())
            .create()

        val parsed = gson.fromJson(cleaned, IntermediateReceipt::class.java)

        val parsedDate = parseDateOnly(parsed.dateAndTime)
        val receiptTimestamp = parsedDate ?: System.currentTimeMillis()

        val items = parsed.items.map {
            val fixedPrice = if (it.price >= 0) it.price else 0.0
            val fixedQuantity = if (it.quantity > 0) it.quantity else 1

            PurchasedItemModel(
                item = ItemModel(
                    name = it.name ?: "Unnamed",
                    price = fixedPrice,
                    createdAt = receiptTimestamp,
                    updatedAt = receiptTimestamp
                ),
                quantity = fixedQuantity
            )
        }

        val receipt = ReceiptModel(
            id = UUID.randomUUID().toString(),
            userId = "3s8mnAExkbJHYOVnVrfQ",
            date = receiptTimestamp,
            items = items,
            totalAmount = parsed.subtotal.takeIf { it >= 0 } ?: 0.0,
            taxAmount = parsed.taxAmount ?: 0.0,
            discountAmount = parsed.discountAmount ?: 0.0,
            fullText = rawText,
            currency = "IDR",
            createdAt = receiptTimestamp,
            updatedAt = receiptTimestamp
        )

        receipt
    } catch (e: Exception) {
        Log.e("ReceiptParser", "Parsing failed", e)
        null
    }
}

private class IntermediateReceiptDeserializer : JsonDeserializer<IntermediateReceipt> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): IntermediateReceipt {
        val obj = json.asJsonObject

        val itemsArray = obj.getAsJsonArray("items")
        val items = itemsArray?.mapNotNull {
            try {
                val arr = it.asJsonArray
                val name = arr[0].asString
                val quantity = arr[1].asInt
                val price = arr[2].asDouble
                IntermediateItem(name, quantity, price)
            } catch (e: Exception) {
                Log.e("ReceiptParser", "Invalid item format: $it", e)
                null
            }
        } ?: emptyList()

        return IntermediateReceipt(
            storeName = obj.get("store name")?.asString ?: "Unknown Store",
            dateAndTime = obj.get("date and time")?.asString ?: "",
            items = items,
            subtotal = obj.get("subtotal")?.asDouble ?: 0.0,
            taxAmount = obj.get("taxAmount")?.asDouble ?: 0.0,
            discountAmount = obj.get("discountAmount")?.asDouble ?: 0.0
        )
    }
}

private data class IntermediateReceipt(
    val storeName: String,
    val dateAndTime: String,
    val items: List<IntermediateItem>,
    val subtotal: Double,
    val taxAmount: Double?,
    val discountAmount: Double?
)

private data class IntermediateItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

fun parseDateOnly(dateStr: String?): Long? {
    if (dateStr.isNullOrBlank()) return null

    val formats = listOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd")
    for (format in formats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.US)
            sdf.isLenient = false
            return sdf.parse(dateStr)?.time
        } catch (_: Exception) {}
    }
    return null
}
