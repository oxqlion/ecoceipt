package com.example.ecoceipt.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecoceipt.models.PurchasedItemModel
import com.example.ecoceipt.ui.viewmodels.ReceiptsViewModel
import com.example.ecoceipt.utils.parseToReceiptModel
import com.example.tim_sam_2.utils.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OCRSummaryView(
    extractedText: String,
    navController: NavController,
    viewModel: ReceiptsViewModel = viewModel()
) {
    val parsedReceipt = parseToReceiptModel(extractedText)
    val purchasedItems = remember { mutableStateListOf<PurchasedItemModel>() }

    LaunchedEffect(parsedReceipt) {
        parsedReceipt?.items?.let {
            purchasedItems.clear()
            purchasedItems.addAll(it)
        }
    }

    var total by remember { mutableDoubleStateOf(parsedReceipt?.totalAmount ?: 0.0) }

    fun recalculateTotal() {
        total = purchasedItems.sumOf { it.item.price * it.quantity }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        parsedReceipt?.date?.let { date ->
            val formattedDate = try {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
            } catch (_: Exception) {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            }

            Text("Transaction Date: $formattedDate", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text("Edit Items:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        purchasedItems.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = item.item.name,
                    onValueChange = { newName ->
                        purchasedItems[index] = item.copy(
                            item = item.item.copy(name = newName)
                        )
                    },
                    label = { Text("Item") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = item.quantity.toString(),
                    onValueChange = { qtyStr ->
                        val qty = qtyStr.toIntOrNull() ?: 1
                        purchasedItems[index] = item.copy(quantity = qty)
                        recalculateTotal()
                    },
                    label = { Text("Qty") },
                    modifier = Modifier.width(80.dp)
                )

                OutlinedTextField(
                    value = item.item.price.toInt().toString(),
                    onValueChange = { priceStr ->
                        val price = priceStr.toDoubleOrNull() ?: 0.0
                        purchasedItems[index] = item.copy(
                            item = item.item.copy(price = price)
                        )
                        recalculateTotal()
                    },
                    label = { Text("Price") },
                    modifier = Modifier.width(120.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: Rp${total.toInt()}", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (parsedReceipt != null) {
                    viewModel.addReceipt(parsedReceipt)

                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Receipt")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                    navController.navigate(Screen.Scan.route) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                        launchSingleTop = true
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Rescan Receipt")
        }
    }
}
