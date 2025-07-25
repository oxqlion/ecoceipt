package com.example.ecoceipt.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecoceipt.models.PurchasedItemModel
import com.example.ecoceipt.ui.viewmodels.ReceiptsViewModel
import com.example.ecoceipt.utils.parseToReceiptModel
import com.example.tim_sam_2.utils.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRSummaryView(
    extractedText: String,
    navController: NavController,
    viewModel: ReceiptsViewModel = viewModel()
) {
    val parsedReceipt = remember { parseToReceiptModel(extractedText) }
    val purchasedItems = remember { mutableStateListOf<PurchasedItemModel>() }
    var total by remember { mutableDoubleStateOf(0.0) }

    fun recalculateTotal() {
        total = purchasedItems.sumOf { it.item.price * it.quantity }
    }

    LaunchedEffect(parsedReceipt) {
        parsedReceipt?.items?.let {
            purchasedItems.clear()
            purchasedItems.addAll(it)
            recalculateTotal()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Receipt", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                TransactionDateHeader(date = parsedReceipt?.date?.let { Date(it) })
                Spacer(modifier = Modifier.height(24.dp))
                ItemsEditCard(
                    items = purchasedItems,
                    onItemChange = { index, updatedItem ->
                        purchasedItems[index] = updatedItem
                        recalculateTotal()
                    },
                    onItemDelete = { index ->
                        purchasedItems.removeAt(index)
                        recalculateTotal()
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                TotalDisplay(total = total)
            }

            ActionButtons(
                onSaveClick = {
                    if (parsedReceipt != null) {
                        val finalReceipt = parsedReceipt.copy(
                            items = purchasedItems.toList(),
                            totalAmount = total
                        )
                        viewModel.addReceipt(finalReceipt)

                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onRescanClick = {
                    navController.navigate(Screen.Scan.route) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun TransactionDateHeader(date: Date?) {
    val formattedDate = remember(date) {
        try {
            date?.let { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it) }
                ?: "Date not found"
        } catch (_: Exception) {
            "Invalid Date"
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Transaction Date",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ItemsEditCard(
    items: List<PurchasedItemModel>,
    onItemChange: (Int, PurchasedItemModel) -> Unit,
    onItemDelete: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Edit Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            items.forEachIndexed { index, item ->
                EditableItemRow(
                    item = item,
                    onValueChange = { updatedItem -> onItemChange(index, updatedItem) },
                    onDelete = { onItemDelete(index) }
                )
                if (index < items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun EditableItemRow(
    item: PurchasedItemModel,
    onValueChange: (PurchasedItemModel) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = item.item.name,
                onValueChange = { newName ->
                    onValueChange(item.copy(item = item.item.copy(name = newName)))
                },
                label = { Text("Item") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete Item", tint = MaterialTheme.colorScheme.error)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = item.quantity.toString(),
                onValueChange = { qtyStr ->
                    val qty = qtyStr.filter { it.isDigit() }.toIntOrNull() ?: 1
                    onValueChange(item.copy(quantity = qty))
                },
                label = { Text("Qty") },
                modifier = Modifier.weight(0.5f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = item.item.price.toInt().toString(),
                onValueChange = { priceStr ->
                    val price = priceStr.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                    onValueChange(item.copy(item = item.item.copy(price = price)))
                },
                label = { Text("Price") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
private fun TotalDisplay(total: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Total",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal
        )
        Text(
            // UPDATED: Inlined the currency formatting
            text = "Rp${total.toInt()}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ActionButtons(onSaveClick: () -> Unit, onRescanClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Receipt", modifier = Modifier.padding(vertical = 8.dp))
        }
        OutlinedButton(
            onClick = onRescanClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Rescan Receipt", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}